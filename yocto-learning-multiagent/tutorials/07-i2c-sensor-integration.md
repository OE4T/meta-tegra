# Tutorial 07: I2C Sensor Integration
## Connecting and Programming I2C Devices on Jetson

---

## Learning Objectives

After completing this tutorial, you will be able to:
- Understand I2C protocol and addressing
- Configure I2C buses on Jetson
- Write I2C device drivers
- Integrate sensors via device tree
- Debug I2C communication issues
- Use IIO (Industrial I/O) framework
- Read sensor data from userspace

---

## Prerequisites Checklist

- [ ] Completed Tutorial 01-06 (Yocto through GPIO modules)
- [ ] Understanding of I2C protocol basics
- [ ] I2C sensor hardware (BME280, MPU6050, or similar)
- [ ] Jetson with custom image
- [ ] i2c-tools installed on target
- [ ] Logic analyzer or oscilloscope (optional, for debugging)
- [ ] Multimeter for voltage verification

---

## Estimated Duration

**Total Time**: 5-6 hours
- I2C theory and setup: 1 hour
- Hardware connection: 30 minutes
- Device tree configuration: 1 hour
- Driver development: 2 hours
- Testing and debugging: 1.5-2 hours

---

## Step-by-Step Instructions

### Step 1: Understand I2C on Jetson

Jetson Orin provides multiple I2C buses:

```bash
# On Jetson, list I2C buses
ls /dev/i2c-*

# Typical output for Orin AGX:
# /dev/i2c-0   - I2C Gen 1 (camera)
# /dev/i2c-1   - I2C Gen 2 (40-pin header pins 3,5)
# /dev/i2c-2   - I2C Gen 8 (40-pin header pins 27,28)
# /dev/i2c-7   - I2C Gen 7 (carrier board)
# /dev/i2c-8   - I2C Gen 3 (internal)

# View I2C controller information
cat /sys/class/i2c-dev/i2c-1/name

# Check device tree for I2C configuration
ls /proc/device-tree/i2c@*
```

**I2C Bus Mapping on 40-Pin Header**:
```
Pin 3  (GPIO03/SDA) -> I2C-1 SDA (gen2_i2c)
Pin 5  (GPIO05/SCL) -> I2C-1 SCL
Pin 27 (GPIO27/SDA) -> I2C-2 SDA (gen8_i2c)
Pin 28 (GPIO28/SCL) -> I2C-2 SCL
```

### Step 2: Hardware Setup and Wiring

Connect an I2C sensor to Jetson:

```
BME280 Environmental Sensor Wiring:
┌─────────────┐         ┌──────────────┐
│  Jetson     │         │  BME280      │
│             │         │              │
│  Pin 1 (3.3V)────────>│ VIN          │
│  Pin 6 (GND)─────────>│ GND          │
│  Pin 3 (SDA)─────────>│ SDA          │
│  Pin 5 (SCL)─────────>│ SCL          │
└─────────────┘         └──────────────┘

Important:
- Verify sensor voltage (3.3V or 5V)
- Check if pull-up resistors are needed
- Most breakout boards have built-in pull-ups
- Typical I2C addresses: 0x76 or 0x77 for BME280
```

**Verify connections before powering on**:
```bash
# Use multimeter to check:
# 1. No shorts between power and ground
# 2. SDA and SCL not shorted
# 3. Correct voltage on VIN
```

### Step 3: Scan I2C Bus for Devices

```bash
# Install i2c-tools if not present
opkg install i2c-tools

# Scan I2C bus 1 for devices
i2cdetect -y -r 1

# Expected output (BME280 at 0x76):
#      0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
# 00:          -- -- -- -- -- -- -- -- -- -- -- -- --
# 10: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
# 20: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
# 30: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
# 40: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
# 50: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
# 60: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
# 70: -- -- -- -- -- -- 76 --

# If address is UU, driver is already bound
# If --, no device detected (check wiring)

# Read specific register (e.g., chip ID)
# BME280 chip ID at register 0xD0 should be 0x60
i2cget -y 1 0x76 0xD0

# Output: 0x60 (confirms BME280 detected)
```

### Step 4: Create Device Tree Entry for I2C Sensor

Add sensor to device tree:

```bash
cd ~/yocto-jetson/meta-custom/recipes-kernel/dtb-overlays/files

cat > i2c-bme280-overlay.dts << 'EOF'
/dts-v1/;
/plugin/;

/ {
    compatible = "nvidia,p3737-0000+p3701-0000", "nvidia,tegra234";

    fragment@0 {
        target = <&gen2_i2c>;  /* I2C bus 1 */
        __overlay__ {
            status = "okay";
            clock-frequency = <400000>;  /* 400 kHz */

            bme280@76 {
                compatible = "bosch,bme280";
                reg = <0x76>;
                status = "okay";
            };
        };
    };
};
EOF

# Create recipe for overlay
cat > ../i2c-bme280-overlay_1.0.bb << 'EOF'
SUMMARY = "I2C BME280 sensor device tree overlay"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit devicetree

SRC_URI = "file://i2c-bme280-overlay.dts"
S = "${WORKDIR}"
DEPENDS = "virtual/kernel"
COMPATIBLE_MACHINE = "(tegra234)"

DTC_FLAGS += "-@"

do_compile() {
    dtc -@ -I dts -O dtb \
        -i ${STAGING_KERNEL_DIR}/include \
        -o ${B}/i2c-bme280-overlay.dtbo \
        ${WORKDIR}/i2c-bme280-overlay.dts
}

do_install() {
    install -d ${D}/boot/overlays
    install -m 0644 ${B}/i2c-bme280-overlay.dtbo ${D}/boot/overlays/
}

FILES:${PN} = "/boot/overlays/*.dtbo"
EOF
```

### Step 5: Write I2C Device Driver

Create a simple I2C driver for BME280:

```bash
cd ~/yocto-jetson/meta-custom/recipes-kernel
mkdir -p i2c-bme280-driver/files

cat > i2c-bme280-driver/files/bme280-driver.c << 'EOF'
/*
 * bme280-driver.c - I2C driver for BME280 environmental sensor
 * Demonstrates I2C communication and sensor reading
 */

#include <linux/module.h>
#include <linux/i2c.h>
#include <linux/delay.h>
#include <linux/sysfs.h>

#define BME280_REG_CHIP_ID      0xD0
#define BME280_REG_RESET        0xE0
#define BME280_REG_CTRL_HUM     0xF2
#define BME280_REG_STATUS       0xF3
#define BME280_REG_CTRL_MEAS    0xF4
#define BME280_REG_CONFIG       0xF5
#define BME280_REG_TEMP_MSB     0xFA

#define BME280_CHIP_ID          0x60

struct bme280_data {
    struct i2c_client *client;
    struct device *dev;

    /* Calibration data */
    u16 dig_T1;
    s16 dig_T2, dig_T3;
    u16 dig_P1;
    s16 dig_P2, dig_P3, dig_P4, dig_P5, dig_P6, dig_P7, dig_P8, dig_P9;
    u8  dig_H1, dig_H3;
    s16 dig_H2, dig_H4, dig_H5;
    s8  dig_H6;

    /* Latest readings */
    s32 temperature;  /* in 0.01 degrees C */
    u32 pressure;     /* in Pa */
    u32 humidity;     /* in 0.001 % RH */
};

/* I2C read/write helpers */
static int bme280_read_reg(struct i2c_client *client, u8 reg, u8 *val)
{
    int ret = i2c_smbus_read_byte_data(client, reg);
    if (ret < 0)
        return ret;

    *val = ret;
    return 0;
}

static int bme280_write_reg(struct i2c_client *client, u8 reg, u8 val)
{
    return i2c_smbus_write_byte_data(client, reg, val);
}

static int bme280_read_calibration(struct bme280_data *data)
{
    struct i2c_client *client = data->client;
    u8 buf[24];
    int ret;

    /* Read temperature and pressure calibration (0x88-0x9F) */
    ret = i2c_smbus_read_i2c_block_data(client, 0x88, 24, buf);
    if (ret < 0)
        return ret;

    data->dig_T1 = (u16)(buf[1] << 8 | buf[0]);
    data->dig_T2 = (s16)(buf[3] << 8 | buf[2]);
    data->dig_T3 = (s16)(buf[5] << 8 | buf[4]);

    data->dig_P1 = (u16)(buf[7] << 8 | buf[6]);
    data->dig_P2 = (s16)(buf[9] << 8 | buf[8]);
    data->dig_P3 = (s16)(buf[11] << 8 | buf[10]);
    data->dig_P4 = (s16)(buf[13] << 8 | buf[12]);
    data->dig_P5 = (s16)(buf[15] << 8 | buf[14]);
    data->dig_P6 = (s16)(buf[17] << 8 | buf[16]);
    data->dig_P7 = (s16)(buf[19] << 8 | buf[18]);
    data->dig_P8 = (s16)(buf[21] << 8 | buf[20]);
    data->dig_P9 = (s16)(buf[23] << 8 | buf[22]);

    /* Read humidity calibration */
    ret = i2c_smbus_read_byte_data(client, 0xA1);
    if (ret < 0)
        return ret;
    data->dig_H1 = ret;

    ret = i2c_smbus_read_i2c_block_data(client, 0xE1, 7, buf);
    if (ret < 0)
        return ret;

    data->dig_H2 = (s16)(buf[1] << 8 | buf[0]);
    data->dig_H3 = buf[2];
    data->dig_H4 = (s16)((buf[3] << 4) | (buf[4] & 0x0F));
    data->dig_H5 = (s16)((buf[5] << 4) | (buf[4] >> 4));
    data->dig_H6 = (s8)buf[6];

    return 0;
}

static s32 bme280_compensate_temp(struct bme280_data *data, s32 adc_T)
{
    s32 var1, var2, T;

    var1 = ((((adc_T >> 3) - ((s32)data->dig_T1 << 1))) * ((s32)data->dig_T2)) >> 11;
    var2 = (((((adc_T >> 4) - ((s32)data->dig_T1)) * ((adc_T >> 4) - ((s32)data->dig_T1))) >> 12) *
            ((s32)data->dig_T3)) >> 14;
    T = (var1 + var2) * 5 + 128) >> 8;

    return T;  /* Temperature in 0.01 degrees C */
}

static int bme280_read_measurements(struct bme280_data *data)
{
    struct i2c_client *client = data->client;
    u8 buf[8];
    s32 adc_T, adc_P, adc_H;
    int ret;

    /* Read all measurement registers (0xF7-0xFE) */
    ret = i2c_smbus_read_i2c_block_data(client, 0xF7, 8, buf);
    if (ret < 0)
        return ret;

    adc_P = (s32)((buf[0] << 12) | (buf[1] << 4) | (buf[2] >> 4));
    adc_T = (s32)((buf[3] << 12) | (buf[4] << 4) | (buf[5] >> 4));
    adc_H = (s32)((buf[6] << 8) | buf[7]);

    /* Compensate temperature */
    data->temperature = bme280_compensate_temp(data, adc_T);

    /* Pressure and humidity compensation omitted for brevity */
    /* Full implementation available in kernel drivers/iio/pressure/bmp280-core.c */

    return 0;
}

/* Sysfs attributes */
static ssize_t temperature_show(struct device *dev,
                                struct device_attribute *attr, char *buf)
{
    struct bme280_data *data = dev_get_drvdata(dev);
    int ret;

    ret = bme280_read_measurements(data);
    if (ret)
        return ret;

    return sprintf(buf, "%d.%02d\n",
                   data->temperature / 100,
                   abs(data->temperature % 100));
}

static DEVICE_ATTR_RO(temperature);

static struct attribute *bme280_attrs[] = {
    &dev_attr_temperature.attr,
    NULL,
};

ATTRIBUTE_GROUPS(bme280);

static int bme280_probe(struct i2c_client *client,
                        const struct i2c_device_id *id)
{
    struct bme280_data *data;
    u8 chip_id;
    int ret;

    dev_info(&client->dev, "Probing BME280 sensor\n");

    /* Allocate driver data */
    data = devm_kzalloc(&client->dev, sizeof(*data), GFP_KERNEL);
    if (!data)
        return -ENOMEM;

    data->client = client;
    data->dev = &client->dev;
    i2c_set_clientdata(client, data);

    /* Verify chip ID */
    ret = bme280_read_reg(client, BME280_REG_CHIP_ID, &chip_id);
    if (ret) {
        dev_err(&client->dev, "Failed to read chip ID\n");
        return ret;
    }

    if (chip_id != BME280_CHIP_ID) {
        dev_err(&client->dev, "Invalid chip ID: 0x%02x (expected 0x60)\n",
                chip_id);
        return -ENODEV;
    }

    dev_info(&client->dev, "BME280 chip detected (ID: 0x%02x)\n", chip_id);

    /* Read calibration data */
    ret = bme280_read_calibration(data);
    if (ret) {
        dev_err(&client->dev, "Failed to read calibration data\n");
        return ret;
    }

    /* Configure sensor: humidity oversampling x1 */
    ret = bme280_write_reg(client, BME280_REG_CTRL_HUM, 0x01);
    if (ret)
        return ret;

    /* Configure sensor: temp/pressure oversampling x1, normal mode */
    ret = bme280_write_reg(client, BME280_REG_CTRL_MEAS, 0x27);
    if (ret)
        return ret;

    /* Wait for first measurement */
    msleep(100);

    /* Create sysfs attributes */
    ret = sysfs_create_groups(&client->dev.kobj, bme280_groups);
    if (ret) {
        dev_err(&client->dev, "Failed to create sysfs attributes\n");
        return ret;
    }

    dev_info(&client->dev, "BME280 driver initialized successfully\n");
    return 0;
}

static int bme280_remove(struct i2c_client *client)
{
    dev_info(&client->dev, "Removing BME280 driver\n");
    sysfs_remove_groups(&client->dev.kobj, bme280_groups);
    return 0;
}

static const struct i2c_device_id bme280_id[] = {
    { "bme280", 0 },
    { }
};
MODULE_DEVICE_TABLE(i2c, bme280_id);

static const struct of_device_id bme280_of_match[] = {
    { .compatible = "bosch,bme280" },
    { }
};
MODULE_DEVICE_TABLE(of, bme280_of_match);

static struct i2c_driver bme280_driver = {
    .driver = {
        .name = "bme280",
        .of_match_table = bme280_of_match,
    },
    .probe = bme280_probe,
    .remove = bme280_remove,
    .id_table = bme280_id,
};

module_i2c_driver(bme280_driver);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Your Name");
MODULE_DESCRIPTION("BME280 I2C sensor driver");
MODULE_VERSION("1.0");
EOF
```

### Step 6: Create Makefile and Recipe

```bash
cat > i2c-bme280-driver/files/Makefile << 'EOF'
obj-m += bme280-driver.o

KERNELDIR ?= /lib/modules/$(shell uname -r)/build
PWD := $(shell pwd)

all:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) modules

clean:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) clean

install:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) modules_install

.PHONY: all clean install
EOF

cat > i2c-bme280-driver/bme280-driver_1.0.bb << 'EOF'
SUMMARY = "BME280 I2C sensor driver for Jetson"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

inherit module

SRC_URI = "\
    file://bme280-driver.c \
    file://Makefile \
    file://COPYING \
"

S = "${WORKDIR}"

RDEPENDS:${PN} = "kernel-module-i2c-tegra"
COMPATIBLE_MACHINE = "(tegra)"
EOF

# Create GPL license
cat > i2c-bme280-driver/files/COPYING << 'EOF'
GPL-2.0-only
EOF
```

### Step 7: Build and Test Driver

```bash
cd ~/yocto-jetson/builds/jetson-orin-agx

# Build the driver
bitbake bme280-driver

# Build overlay
bitbake i2c-bme280-overlay

# Add to image
echo 'IMAGE_INSTALL:append = " bme280-driver i2c-bme280-overlay"' >> conf/local.conf

# Deploy to Jetson
# After flashing or manually:

# On Jetson:
# Load overlay
cat /boot/overlays/i2c-bme280-overlay.dtbo > \
    /sys/kernel/config/device-tree/overlays/bme280/dtbo

# Load driver
modprobe bme280-driver

# Check driver loaded
lsmod | grep bme280

# Verify sensor bound
ls /sys/bus/i2c/devices/1-0076/

# Read temperature
cat /sys/bus/i2c/devices/1-0076/temperature
# Output: 23.45

# Check kernel messages
dmesg | grep -i bme280
```

### Step 8: Use Kernel's IIO Framework

For production, use the IIO framework:

```bash
# Kernel already has BME280 IIO driver
# Just enable it in kernel config

# On host:
cd ~/yocto-jetson/builds/jetson-orin-agx

# Create kernel config fragment
mkdir -p ~/yocto-jetson/meta-custom/recipes-kernel/linux/linux-tegra

cat > ~/yocto-jetson/meta-custom/recipes-kernel/linux/linux-tegra/iio-sensors.cfg << 'EOF'
# IIO (Industrial I/O) framework
CONFIG_IIO=y
CONFIG_IIO_BUFFER=y
CONFIG_IIO_TRIGGERED_BUFFER=y

# Environmental sensors
CONFIG_BME280=m
CONFIG_BME280_I2C=m

# IMU sensors
CONFIG_INV_MPU6050_I2C=m
CONFIG_INV_MPU6050_SPI=m

# Light sensors
CONFIG_TSL2563=m
CONFIG_BH1750=m

# Pressure sensors
CONFIG_MS5611=m
CONFIG_MS5611_I2C=m
EOF

# Create kernel append
cat > ~/yocto-jetson/meta-custom/recipes-kernel/linux/linux-tegra_%.bbappend << 'EOF'
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://iio-sensors.cfg"
EOF

# Rebuild kernel
bitbake virtual/kernel -c clean
bitbake virtual/kernel

# On Jetson after reboot:
# IIO devices appear in /sys/bus/iio/devices/
ls /sys/bus/iio/devices/

# Example: iio:device0 is BME280
cd /sys/bus/iio/devices/iio:device0

# Read temperature
cat in_temp_input
# Output: 23450 (23.45°C)

# Read pressure
cat in_pressure_input
# Output: 101325 (101.325 kPa)

# Read humidity
cat in_humidityrelative_input
# Output: 45000 (45.0% RH)
```

### Step 9: Create Userspace Reader Application

```bash
cat > ~/yocto-jetson/meta-custom/recipes-apps/sensor-reader/files/bme280-reader.c << 'EOF'
/*
 * bme280-reader.c - Read BME280 sensor via IIO
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <errno.h>

#define IIO_PATH "/sys/bus/iio/devices"

static int find_iio_device(const char *name)
{
    DIR *dir;
    struct dirent *ent;
    char path[256];
    char buf[256];
    FILE *fp;
    int device_num = -1;

    dir = opendir(IIO_PATH);
    if (!dir) {
        perror("opendir");
        return -1;
    }

    while ((ent = readdir(dir)) != NULL) {
        if (strncmp(ent->d_name, "iio:device", 10) != 0)
            continue;

        snprintf(path, sizeof(path), "%s/%s/name",
                 IIO_PATH, ent->d_name);

        fp = fopen(path, "r");
        if (!fp)
            continue;

        if (fgets(buf, sizeof(buf), fp) != NULL) {
            buf[strcspn(buf, "\n")] = 0;
            if (strcmp(buf, name) == 0) {
                sscanf(ent->d_name, "iio:device%d", &device_num);
                fclose(fp);
                break;
            }
        }

        fclose(fp);
    }

    closedir(dir);
    return device_num;
}

static int read_iio_value(int device, const char *channel)
{
    char path[256];
    FILE *fp;
    int value;

    snprintf(path, sizeof(path), "%s/iio:device%d/%s",
             IIO_PATH, device, channel);

    fp = fopen(path, "r");
    if (!fp) {
        fprintf(stderr, "Failed to open %s: %s\n",
                path, strerror(errno));
        return -1;
    }

    if (fscanf(fp, "%d", &value) != 1) {
        fclose(fp);
        return -1;
    }

    fclose(fp);
    return value;
}

int main(void)
{
    int device;
    int temp, pressure, humidity;

    printf("BME280 Sensor Reader\n");
    printf("====================\n\n");

    device = find_iio_device("bme280");
    if (device < 0) {
        fprintf(stderr, "BME280 not found\n");
        fprintf(stderr, "Make sure driver is loaded and sensor is connected\n");
        return 1;
    }

    printf("Found BME280 at iio:device%d\n\n", device);

    while (1) {
        temp = read_iio_value(device, "in_temp_input");
        pressure = read_iio_value(device, "in_pressure_input");
        humidity = read_iio_value(device, "in_humidityrelative_input");

        if (temp < 0 || pressure < 0 || humidity < 0) {
            fprintf(stderr, "Error reading sensor\n");
            return 1;
        }

        printf("\rTemp: %5.2f°C  Pressure: %6.2f kPa  Humidity: %5.1f%%    ",
               temp / 1000.0,
               pressure / 1000.0,
               humidity / 1000.0);
        fflush(stdout);

        usleep(1000000);  /* 1 second */
    }

    return 0;
}
EOF

# Create recipe
cat > ~/yocto-jetson/meta-custom/recipes-apps/sensor-reader/bme280-reader_1.0.bb << 'EOF'
SUMMARY = "BME280 sensor reader application"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://bme280-reader.c"

S = "${WORKDIR}"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} -o bme280-reader bme280-reader.c
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 bme280-reader ${D}${bindir}/
}
EOF
```

### Step 10: Advanced - Multi-Sensor System

Create a system with multiple I2C sensors:

```bash
cat > ~/yocto-jetson/meta-custom/recipes-kernel/dtb-overlays/files/multi-sensor-overlay.dts << 'EOF'
/dts-v1/;
/plugin/;

#include <dt-bindings/gpio/tegra234-gpio.h>
#include <dt-bindings/interrupt-controller/irq.h>

/ {
    compatible = "nvidia,p3737-0000+p3701-0000", "nvidia,tegra234";

    fragment@0 {
        target = <&gen2_i2c>;  /* I2C bus 1 */
        __overlay__ {
            status = "okay";
            clock-frequency = <400000>;

            /* BME280 environmental sensor */
            bme280@76 {
                compatible = "bosch,bme280";
                reg = <0x76>;
                status = "okay";
            };

            /* MPU6050 IMU */
            mpu6050@68 {
                compatible = "invensense,mpu6050";
                reg = <0x68>;
                interrupt-parent = <&tegra_main_gpio>;
                interrupts = <TEGRA234_MAIN_GPIO(H, 2) IRQ_TYPE_EDGE_RISING>;
                vdd-supply = <&vdd_3v3_sys>;
                vddio-supply = <&vdd_1v8_sys>;
                mount-matrix = "1", "0", "0",
                               "0", "1", "0",
                               "0", "0", "1";
                status = "okay";
            };
        };
    };

    fragment@1 {
        target = <&gen8_i2c>;  /* I2C bus 2 */
        __overlay__ {
            status = "okay";
            clock-frequency = <100000>;  /* 100 kHz for RTC */

            /* DS3231 RTC */
            rtc@68 {
                compatible = "maxim,ds3231";
                reg = <0x68>;
                status = "okay";
            };
        };
    };
};
EOF
```

---

## Troubleshooting Common Issues

### Issue 1: No I2C Device Detected

**Symptoms**: i2cdetect shows all `--`

**Solutions**:
```bash
# Check wiring
# - Verify 3.3V power
# - Check ground connection
# - Ensure SDA/SCL not swapped

# Check pull-up resistors
# Measure voltage on SDA/SCL when idle (should be 3.3V)
# If 0V, add 4.7kΩ pull-ups to 3.3V

# Try slower speed
# Edit device tree: clock-frequency = <100000>; /* 100 kHz */

# Check if bus is working
i2cdetect -y -r 1  # Use quick write mode

# Enable I2C debugging
echo "file drivers/i2c/* +p" > /sys/kernel/debug/dynamic_debug/control
dmesg -w
```

### Issue 2: Driver Doesn't Bind to Device

**Symptoms**: Device shows as UU in i2cdetect but driver not loaded

**Solutions**:
```bash
# Check if driver is loaded
lsmod | grep bme280

# Load driver manually
modprobe bme280-i2c

# Check device tree compatible string
cat /proc/device-tree/i2c@3160000/bme280@76/compatible

# Verify driver compatible string matches
modinfo bme280-i2c | grep alias

# Manual bind if needed
echo 1-0076 > /sys/bus/i2c/drivers/bme280/bind
```

### Issue 3: I2C Transmission Errors

**Symptoms**:
```
i2c i2c-1: Transfer error: -121
```

**Solutions**:
```bash
# Error -121 is -EREMOTEIO (I2C NAK)

# Possible causes:
# 1. Wrong device address
i2cdetect -y -r 1  # Verify actual address

# 2. Device not ready
# Add delay after power-on

# 3. Bus speed too fast
# Reduce to 100 kHz

# 4. Clock stretching issues
# Some devices need more time
# Enable in device tree:
# nvidia,clock-always-on;

# 5. Electrical issues
# Check with oscilloscope/logic analyzer
```

---

## Verification Checklist

- [ ] I2C bus visible in /dev/
- [ ] Sensor detected with i2cdetect
- [ ] Can read registers with i2cget
- [ ] Device tree overlay loads successfully
- [ ] Driver module compiles and loads
- [ ] Device bound to driver (check /sys/bus/i2c/devices/)
- [ ] Can read sensor values via sysfs
- [ ] IIO driver works correctly
- [ ] Userspace application reads sensor data
- [ ] No errors in dmesg
- [ ] Readings are reasonable and stable

---

## Next Steps

### Immediate Practice
1. Add more sensors (IMU, light, etc.)
2. Implement data logging
3. Create sensor fusion application

### Proceed to Next Tutorial
**Tutorial 08: Camera Driver Integration** - Work with camera sensors

### Advanced Topics
- I2C bus recovery mechanisms
- Multi-master I2C
- SMBus vs I2C protocols
- DMA for high-speed I2C

---

**Congratulations!** You can now integrate I2C sensors with Jetson, create device tree entries, write I2C drivers, and read sensor data from userspace.

---

*Tutorial created by the Yocto & Meta-Tegra Multi-Agent Learning System*
*Last updated: 2025-01-15*
