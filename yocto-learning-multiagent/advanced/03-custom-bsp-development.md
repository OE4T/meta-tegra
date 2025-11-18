# Custom BSP Development for Yocto & Meta-Tegra

## Overview

This module provides comprehensive guidance on creating custom Board Support Package (BSP) layers for Jetson-based hardware, covering board bring-up, hardware abstraction, vendor integration, and long-term maintenance strategies.

**Target Audience**: Hardware engineers and BSP developers
**Prerequisites**: Deep understanding of embedded Linux, device trees, and hardware architecture

---

## 1. Creating Custom BSP Layers

### 1.1 BSP Layer Structure

```bash
# Create custom BSP layer structure
mkdir -p meta-custom-jetson
cd meta-custom-jetson

# Layer directory structure
tree
.
├── conf
│   ├── layer.conf
│   ├── machine
│   │   ├── custom-jetson-xavier.conf
│   │   └── custom-jetson-orin.conf
│   └── distro
│       └── custom-distro.conf
├── recipes-bsp
│   ├── bootloader
│   │   └── u-boot-tegra_%.bbappend
│   ├── tegra-binaries
│   │   └── tegra-binaries_%.bbappend
│   └── device-tree
│       ├── device-tree.bb
│       └── files
│           ├── custom-hardware.dtsi
│           └── custom-pinmux.dtsi
├── recipes-kernel
│   └── linux
│       ├── linux-tegra_%.bbappend
│       └── files
│           ├── custom.cfg
│           └── patches
│               ├── 0001-add-custom-driver.patch
│               └── 0002-enable-custom-peripherals.patch
├── recipes-drivers
│   ├── custom-sensor-driver
│   │   └── custom-sensor-driver_1.0.bb
│   └── custom-fpga-interface
│       └── custom-fpga-interface_1.0.bb
├── recipes-core
│   └── images
│       └── custom-jetson-image.bb
├── classes
│   └── custom-flash.bbclass
├── wic
│   └── custom-jetson.wks.in
├── README.md
└── COPYING.MIT
```

### 1.2 Layer Configuration

```python
# conf/layer.conf

# Layer identity
BBPATH =. "${LAYERDIR}:"

# Additional recipes
BBFILES += "${LAYERDIR}/recipes*/*/*.bb \
            ${LAYERDIR}/recipes*/*/*.bbappend"

BBFILE_COLLECTIONS += "meta-custom-jetson"
BBFILE_PATTERN_meta-custom-jetson = "^${LAYERDIR}/"
BBFILE_PRIORITY_meta-custom-jetson = "10"

# Layer dependencies
LAYERDEPENDS_meta-custom-jetson = "core tegra"
LAYERSERIES_COMPAT_meta-custom-jetson = "kirkstone langdale mickledore"

# Additional configuration paths
BBPATH =. "${LAYERDIR}:"

# Custom machine configurations
BBMASK += " \
    meta-tegra/recipes-bsp/tegra-binaries/tegra-binaries-(?!.*32\.7\.4).* \
"

# Layer-specific variables
CUSTOM_JETSON_BSP_VERSION = "1.0"
CUSTOM_HARDWARE_REVISION = "A01"

# Additional image features
IMAGE_FEATURES_CUSTOM = "custom-drivers custom-firmware"
```

### 1.3 Machine Configuration

```python
# conf/machine/custom-jetson-xavier.conf

#@TYPE: Machine
#@NAME: Custom Jetson Xavier NX Carrier Board
#@DESCRIPTION: Custom carrier board based on Jetson Xavier NX

# Include base Jetson Xavier NX configuration
require conf/machine/jetson-xavier-nx-devkit.conf

# Custom machine identification
MACHINEOVERRIDES =. "custom-jetson:custom-carrier:"

# Machine-specific settings
MACHINE_FEATURES:append = " custom-peripherals can-bus dual-camera"
MACHINE_EXTRA_RRECOMMENDS:append = " \
    custom-sensor-driver \
    custom-fpga-interface \
    kernel-module-can \
"

# Custom kernel device tree
KERNEL_DEVICETREE = " \
    nvidia/tegra194-p3668-0001-custom-carrier.dtb \
"

# Bootloader configuration
UBOOT_MACHINE = "p3668-0001-p3509-0000-custom_defconfig"

# Flash layout customization
PARTITION_LAYOUT_TEMPLATE = "flash-custom-xavier.xml"
BOOTPART = "mmcblk0p1"
ROOTFSPART = "mmcblk0p${@rootpart_custom(d)}"

# Additional firmware
MACHINE_FIRMWARE:append = " \
    custom-fpga-firmware \
    custom-camera-firmware \
"

# Preferred providers
PREFERRED_PROVIDER_virtual/kernel = "linux-tegra-custom"
PREFERRED_VERSION_linux-tegra-custom = "5.10%"

# Serial console
SERIAL_CONSOLES = "115200;ttyTCU0"

# Custom BSP version
BSP_VERSION = "35.4.1-custom-${CUSTOM_HARDWARE_REVISION}"
```

---

## 2. Board Bring-Up Process

### 2.1 Hardware Validation Checklist

```bash
#!/bin/bash
# hardware-validation.sh - Automated hardware validation

echo "========================================="
echo "Custom Jetson Hardware Validation"
echo "========================================="

# Check power rails
echo "1. Checking power rails..."
for rail in VDD_IN VDD_CPU VDD_GPU VDD_SOC VDD_CV; do
    if [ -f "/sys/bus/i2c/drivers/ina3221x/1-0040/iio:device0/in_voltage_${rail}" ]; then
        voltage=$(cat "/sys/bus/i2c/drivers/ina3221x/1-0040/iio:device0/in_voltage_${rail}")
        echo "  ${rail}: ${voltage} mV"
    fi
done

# Check thermal sensors
echo ""
echo "2. Checking thermal sensors..."
for zone in /sys/class/thermal/thermal_zone*/temp; do
    name=$(cat $(dirname $zone)/type)
    temp=$(cat $zone)
    temp_c=$(echo "scale=1; $temp / 1000" | bc)
    echo "  ${name}: ${temp_c}°C"
done

# Check GPIO expanders
echo ""
echo "3. Checking GPIO expanders..."
for chip in /sys/class/gpio/gpiochip*; do
    if [ -d "$chip" ]; then
        label=$(cat $chip/label)
        base=$(cat $chip/base)
        ngpio=$(cat $chip/ngpio)
        echo "  ${label}: base=${base}, ngpio=${ngpio}"
    fi
done

# Check I2C buses
echo ""
echo "4. Checking I2C buses..."
for bus in /dev/i2c-*; do
    bus_num=${bus#/dev/i2c-}
    echo "  I2C Bus ${bus_num}:"
    i2cdetect -y $bus_num | grep -v "^     " | tail -n +2 | \
        awk '{printf "    "; for(i=2; i<=NF; i++) if($i != "--") printf "%s ", $i; printf "\n"}'
done

# Check CAN interfaces
echo ""
echo "5. Checking CAN interfaces..."
for can in can0 can1; do
    if ip link show $can 2>/dev/null | grep -q "$can"; then
        state=$(ip -details link show $can | grep -o "state [A-Z]*" | cut -d' ' -f2)
        echo "  ${can}: ${state}"
    fi
done

# Check camera interfaces
echo ""
echo "6. Checking camera interfaces..."
for video in /dev/video*; do
    if [ -c "$video" ]; then
        driver=$(udevadm info --query=property --name=$video | grep ID_V4L_PRODUCT= | cut -d= -f2)
        echo "  $video: ${driver:-Unknown}"
    fi
done

# Check NVME/Storage
echo ""
echo "7. Checking storage devices..."
lsblk -o NAME,SIZE,TYPE,MOUNTPOINT,MODEL | grep -v loop

# Check network interfaces
echo ""
echo "8. Checking network interfaces..."
ip -br link show

# Check GPU
echo ""
echo "9. Checking GPU..."
if command -v nvidia-smi >/dev/null 2>&1; then
    nvidia-smi --query-gpu=name,driver_version,memory.total --format=csv,noheader
else
    echo "  GPU: $(cat /sys/devices/gpu.0/devfreq/17000000.gv11b/cur_freq) Hz"
fi

# Generate validation report
REPORT_FILE="/tmp/hw-validation-$(date +%Y%m%d-%H%M%S).txt"
exec > >(tee -a "$REPORT_FILE")

echo ""
echo "========================================="
echo "Validation report saved to: $REPORT_FILE"
echo "========================================="
```

### 2.2 Device Tree Development

```dts
// custom-jetson-xavier-carrier.dts - Custom carrier board device tree

/dts-v1/;
/plugin/;

#include <dt-bindings/gpio/tegra194-gpio.h>
#include <dt-bindings/interrupt-controller/arm-gic.h>

/ {
    overlay-name = "Custom Jetson Xavier NX Carrier";
    compatible = "nvidia,p3668-0001-p3509-0000";
    nvidia,dtbbuildtime = __DATE__, __TIME__;

    fragment@0 {
        target-path = "/";
        __overlay__ {
            model = "Custom Jetson Xavier NX Carrier Board";

            /* Custom regulators */
            regulators {
                compatible = "simple-bus";
                #address-cells = <1>;
                #size-cells = <0>;

                /* 5V main power */
                vdd_5v0_sys: regulator@0 {
                    compatible = "regulator-fixed";
                    reg = <0>;
                    regulator-name = "vdd-5v0-sys";
                    regulator-min-microvolt = <5000000>;
                    regulator-max-microvolt = <5000000>;
                    regulator-always-on;
                    regulator-boot-on;
                };

                /* 3.3V for peripherals */
                vdd_3v3_periph: regulator@1 {
                    compatible = "regulator-fixed";
                    reg = <1>;
                    regulator-name = "vdd-3v3-periph";
                    regulator-min-microvolt = <3300000>;
                    regulator-max-microvolt = <3300000>;
                    gpio = <&gpio TEGRA194_MAIN_GPIO(H, 0) GPIO_ACTIVE_HIGH>;
                    enable-active-high;
                    regulator-boot-on;
                    vin-supply = <&vdd_5v0_sys>;
                };

                /* 12V for industrial I/O */
                vdd_12v0_io: regulator@2 {
                    compatible = "regulator-fixed";
                    reg = <2>;
                    regulator-name = "vdd-12v0-io";
                    regulator-min-microvolt = <12000000>;
                    regulator-max-microvolt = <12000000>;
                    gpio = <&gpio TEGRA194_MAIN_GPIO(H, 1) GPIO_ACTIVE_HIGH>;
                    enable-active-high;
                    vin-supply = <&vdd_5v0_sys>;
                };
            };

            /* GPIO keys for user buttons */
            gpio-keys {
                compatible = "gpio-keys";

                power-button {
                    label = "Power";
                    gpios = <&gpio TEGRA194_MAIN_GPIO(Q, 3) GPIO_ACTIVE_LOW>;
                    linux,code = <KEY_POWER>;
                    debounce-interval = <10>;
                    wakeup-source;
                };

                reset-button {
                    label = "Reset";
                    gpios = <&gpio TEGRA194_MAIN_GPIO(Q, 4) GPIO_ACTIVE_LOW>;
                    linux,code = <KEY_RESTART>;
                    debounce-interval = <10>;
                };
            };

            /* Status LEDs */
            leds {
                compatible = "gpio-leds";

                led-power {
                    label = "power-led";
                    gpios = <&gpio TEGRA194_MAIN_GPIO(A, 2) GPIO_ACTIVE_HIGH>;
                    default-state = "on";
                };

                led-status {
                    label = "status-led";
                    gpios = <&gpio TEGRA194_MAIN_GPIO(A, 3) GPIO_ACTIVE_HIGH>;
                    linux,default-trigger = "heartbeat";
                };

                led-error {
                    label = "error-led";
                    gpios = <&gpio TEGRA194_MAIN_GPIO(A, 4) GPIO_ACTIVE_HIGH>;
                    default-state = "off";
                };
            };
        };
    };

    /* CAN bus configuration */
    fragment@1 {
        target = <&mttcan0>;
        __overlay__ {
            status = "okay";
            pinctrl-names = "default";
            pinctrl-0 = <&mttcan0_default>;
        };
    };

    fragment@2 {
        target = <&mttcan1>;
        __overlay__ {
            status = "okay";
            pinctrl-names = "default";
            pinctrl-0 = <&mttcan1_default>;
        };
    };

    /* I2C bus for custom sensors */
    fragment@3 {
        target = <&gen8_i2c>;
        __overlay__ {
            status = "okay";
            clock-frequency = <400000>;

            /* Custom environmental sensor */
            env-sensor@48 {
                compatible = "custom,env-sensor";
                reg = <0x48>;
                interrupt-parent = <&gpio>;
                interrupts = <TEGRA194_MAIN_GPIO(X, 0) IRQ_TYPE_EDGE_FALLING>;
            };

            /* Custom IMU */
            imu@68 {
                compatible = "invensense,mpu9250";
                reg = <0x68>;
                interrupt-parent = <&gpio>;
                interrupts = <TEGRA194_MAIN_GPIO(X, 1) IRQ_TYPE_EDGE_RISING>;
                mount-matrix = "0", "-1", "0",
                              "-1", "0", "0",
                              "0", "0", "-1";
            };

            /* GPIO expander for additional I/O */
            gpio-expander@20 {
                compatible = "nxp,pca9555";
                reg = <0x20>;
                gpio-controller;
                #gpio-cells = <2>;
                interrupt-parent = <&gpio>;
                interrupts = <TEGRA194_MAIN_GPIO(X, 2) IRQ_TYPE_EDGE_FALLING>;
            };
        };
    };

    /* SPI bus for FPGA interface */
    fragment@4 {
        target = <&spi0>;
        __overlay__ {
            status = "okay";
            spi-max-frequency = <25000000>;

            fpga@0 {
                compatible = "custom,fpga-interface";
                reg = <0>;
                spi-max-frequency = <25000000>;
                interrupt-parent = <&gpio>;
                interrupts = <TEGRA194_MAIN_GPIO(Y, 0) IRQ_TYPE_EDGE_FALLING>;
                reset-gpios = <&gpio TEGRA194_MAIN_GPIO(Y, 1) GPIO_ACTIVE_LOW>;
            };
        };
    };

    /* Dual camera configuration */
    fragment@5 {
        target = <&vi_base>;
        __overlay__ {
            num-channels = <2>;

            ports {
                #address-cells = <1>;
                #size-cells = <0>;

                /* Camera Port 0 */
                vi_port0: port@0 {
                    reg = <0>;
                    vi_in0: endpoint {
                        port-index = <0>;
                        bus-width = <2>;
                        remote-endpoint = <&csi_out0>;
                    };
                };

                /* Camera Port 1 */
                vi_port1: port@1 {
                    reg = <1>;
                    vi_in1: endpoint {
                        port-index = <2>;
                        bus-width = <2>;
                        remote-endpoint = <&csi_out1>;
                    };
                };
            };
        };
    };

    /* PCIe configuration for expansion cards */
    fragment@6 {
        target = <&pcie_c5_rp>;
        __overlay__ {
            status = "okay";
            nvidia,max-speed = <4>;
            num-lanes = <4>;
        };
    };

    /* UART configuration for RS485 */
    fragment@7 {
        target = <&uartc>;
        __overlay__ {
            status = "okay";
            compatible = "nvidia,tegra194-hsuart-16550";
            linux,rs485-enabled-at-boot-time;
            rs485-rts-delay = <0 0>;
            rs485-rts-active-low;
            rts-gpio = <&gpio TEGRA194_MAIN_GPIO(Z, 0) GPIO_ACTIVE_HIGH>;
        };
    };

    /* Watchdog configuration */
    fragment@8 {
        target = <&tegra_wdt>;
        __overlay__ {
            status = "okay";
            nvidia,enable-on-init;
            nvidia,heartbeat-init = <120>;
        };
    };

    /* Pinmux configuration */
    fragment@9 {
        target = <&pinmux>;
        __overlay__ {
            pinctrl-names = "default";
            pinctrl-0 = <&custom_pinmux_config>;

            custom_pinmux_config: common {
                /* CAN0 */
                can0_dout_paa0 {
                    nvidia,pins = "can0_dout_paa0";
                    nvidia,function = "can0";
                    nvidia,pull = <TEGRA_PIN_PULL_NONE>;
                    nvidia,tristate = <TEGRA_PIN_DISABLE>;
                    nvidia,enable-input = <TEGRA_PIN_DISABLE>;
                };

                can0_din_paa1 {
                    nvidia,pins = "can0_din_paa1";
                    nvidia,function = "can0";
                    nvidia,pull = <TEGRA_PIN_PULL_UP>;
                    nvidia,tristate = <TEGRA_PIN_ENABLE>;
                    nvidia,enable-input = <TEGRA_PIN_ENABLE>;
                };

                /* SPI0 for FPGA */
                spi0_sck_pc0 {
                    nvidia,pins = "spi0_sck_pc0";
                    nvidia,function = "spi0";
                    nvidia,pull = <TEGRA_PIN_PULL_DOWN>;
                    nvidia,tristate = <TEGRA_PIN_DISABLE>;
                    nvidia,enable-input = <TEGRA_PIN_ENABLE>;
                };

                /* Additional pin configurations... */
            };
        };
    };
};
```

### 2.3 Custom Kernel Driver Template

```c
// custom-sensor-driver.c - Template for custom sensor driver

#include <linux/module.h>
#include <linux/i2c.h>
#include <linux/interrupt.h>
#include <linux/workqueue.h>
#include <linux/iio/iio.h>
#include <linux/iio/sysfs.h>
#include <linux/iio/events.h>
#include <linux/iio/trigger.h>
#include <linux/iio/buffer.h>
#include <linux/iio/triggered_buffer.h>
#include <linux/iio/trigger_consumer.h>

#define DRIVER_NAME "custom-sensor"
#define SENSOR_REG_WHOAMI    0x00
#define SENSOR_REG_CTRL      0x01
#define SENSOR_REG_STATUS    0x02
#define SENSOR_REG_DATA_X_L  0x03
#define SENSOR_REG_DATA_X_H  0x04
#define SENSOR_REG_DATA_Y_L  0x05
#define SENSOR_REG_DATA_Y_H  0x06
#define SENSOR_REG_DATA_Z_L  0x07
#define SENSOR_REG_DATA_Z_H  0x08

struct custom_sensor_data {
    struct i2c_client *client;
    struct iio_trigger *trig;
    struct mutex lock;
    struct work_struct work;
    int irq;
    bool triggered;

    /* Calibration data */
    s16 offset[3];
    u16 scale[3];
};

/* IIO channel specification */
static const struct iio_chan_spec custom_sensor_channels[] = {
    {
        .type = IIO_ACCEL,
        .modified = 1,
        .channel2 = IIO_MOD_X,
        .info_mask_separate = BIT(IIO_CHAN_INFO_RAW) |
                            BIT(IIO_CHAN_INFO_CALIBBIAS) |
                            BIT(IIO_CHAN_INFO_SCALE),
        .scan_index = 0,
        .scan_type = {
            .sign = 's',
            .realbits = 16,
            .storagebits = 16,
            .endianness = IIO_LE,
        },
    },
    {
        .type = IIO_ACCEL,
        .modified = 1,
        .channel2 = IIO_MOD_Y,
        .info_mask_separate = BIT(IIO_CHAN_INFO_RAW) |
                            BIT(IIO_CHAN_INFO_CALIBBIAS) |
                            BIT(IIO_CHAN_INFO_SCALE),
        .scan_index = 1,
        .scan_type = {
            .sign = 's',
            .realbits = 16,
            .storagebits = 16,
            .endianness = IIO_LE,
        },
    },
    {
        .type = IIO_ACCEL,
        .modified = 1,
        .channel2 = IIO_MOD_Z,
        .info_mask_separate = BIT(IIO_CHAN_INFO_RAW) |
                            BIT(IIO_CHAN_INFO_CALIBBIAS) |
                            BIT(IIO_CHAN_INFO_SCALE),
        .scan_index = 2,
        .scan_type = {
            .sign = 's',
            .realbits = 16,
            .storagebits = 16,
            .endianness = IIO_LE,
        },
    },
    IIO_CHAN_SOFT_TIMESTAMP(3),
};

/* I2C read/write helpers */
static int custom_sensor_read_reg(struct i2c_client *client, u8 reg, u8 *val)
{
    int ret;

    ret = i2c_smbus_read_byte_data(client, reg);
    if (ret < 0)
        return ret;

    *val = ret;
    return 0;
}

static int custom_sensor_write_reg(struct i2c_client *client, u8 reg, u8 val)
{
    return i2c_smbus_write_byte_data(client, reg, val);
}

/* Read sensor data */
static int custom_sensor_read_data(struct custom_sensor_data *data, s16 *buf)
{
    struct i2c_client *client = data->client;
    u8 raw[6];
    int ret, i;

    /* Read 6 bytes starting from DATA_X_L */
    ret = i2c_smbus_read_i2c_block_data(client, SENSOR_REG_DATA_X_L,
                                        sizeof(raw), raw);
    if (ret < 0)
        return ret;

    /* Convert to signed 16-bit values */
    for (i = 0; i < 3; i++) {
        buf[i] = (s16)((raw[i*2+1] << 8) | raw[i*2]);
        /* Apply calibration */
        buf[i] = (buf[i] - data->offset[i]) * data->scale[i] / 1000;
    }

    return 0;
}

/* IIO read_raw callback */
static int custom_sensor_read_raw(struct iio_dev *indio_dev,
                                 struct iio_chan_spec const *chan,
                                 int *val, int *val2, long mask)
{
    struct custom_sensor_data *data = iio_priv(indio_dev);
    s16 buf[3];
    int ret;

    switch (mask) {
    case IIO_CHAN_INFO_RAW:
        mutex_lock(&data->lock);
        ret = custom_sensor_read_data(data, buf);
        mutex_unlock(&data->lock);

        if (ret < 0)
            return ret;

        *val = buf[chan->scan_index];
        return IIO_VAL_INT;

    case IIO_CHAN_INFO_CALIBBIAS:
        *val = data->offset[chan->scan_index];
        return IIO_VAL_INT;

    case IIO_CHAN_INFO_SCALE:
        *val = data->scale[chan->scan_index];
        *val2 = 1000;
        return IIO_VAL_FRACTIONAL;

    default:
        return -EINVAL;
    }
}

/* IIO write_raw callback */
static int custom_sensor_write_raw(struct iio_dev *indio_dev,
                                   struct iio_chan_spec const *chan,
                                   int val, int val2, long mask)
{
    struct custom_sensor_data *data = iio_priv(indio_dev);

    switch (mask) {
    case IIO_CHAN_INFO_CALIBBIAS:
        data->offset[chan->scan_index] = val;
        return 0;

    case IIO_CHAN_INFO_SCALE:
        data->scale[chan->scan_index] = val;
        return 0;

    default:
        return -EINVAL;
    }
}

/* IIO info structure */
static const struct iio_info custom_sensor_info = {
    .read_raw = custom_sensor_read_raw,
    .write_raw = custom_sensor_write_raw,
};

/* Triggered buffer handler */
static irqreturn_t custom_sensor_trigger_handler(int irq, void *p)
{
    struct iio_poll_func *pf = p;
    struct iio_dev *indio_dev = pf->indio_dev;
    struct custom_sensor_data *data = iio_priv(indio_dev);
    s16 buf[4];  /* 3 channels + timestamp */
    int ret;

    ret = custom_sensor_read_data(data, buf);
    if (ret < 0)
        goto done;

    iio_push_to_buffers_with_timestamp(indio_dev, buf, iio_get_time_ns(indio_dev));

done:
    iio_trigger_notify_done(indio_dev->trig);
    return IRQ_HANDLED;
}

/* Interrupt handler */
static irqreturn_t custom_sensor_irq_handler(int irq, void *private)
{
    struct iio_dev *indio_dev = private;
    struct custom_sensor_data *data = iio_priv(indio_dev);

    data->triggered = true;
    schedule_work(&data->work);

    return IRQ_HANDLED;
}

/* Work queue handler */
static void custom_sensor_work_func(struct work_struct *work)
{
    struct custom_sensor_data *data =
        container_of(work, struct custom_sensor_data, work);
    struct iio_dev *indio_dev = i2c_get_clientdata(data->client);

    if (data->triggered) {
        iio_trigger_poll(indio_dev->trig);
        data->triggered = false;
    }
}

/* Device initialization */
static int custom_sensor_init(struct custom_sensor_data *data)
{
    struct i2c_client *client = data->client;
    u8 whoami;
    int ret;

    /* Verify device ID */
    ret = custom_sensor_read_reg(client, SENSOR_REG_WHOAMI, &whoami);
    if (ret < 0) {
        dev_err(&client->dev, "Failed to read WHO_AM_I\n");
        return ret;
    }

    if (whoami != 0x42) {  /* Expected device ID */
        dev_err(&client->dev, "Invalid device ID: 0x%02x\n", whoami);
        return -ENODEV;
    }

    /* Initialize sensor - set to active mode */
    ret = custom_sensor_write_reg(client, SENSOR_REG_CTRL, 0x01);
    if (ret < 0) {
        dev_err(&client->dev, "Failed to initialize sensor\n");
        return ret;
    }

    /* Set default calibration */
    data->offset[0] = data->offset[1] = data->offset[2] = 0;
    data->scale[0] = data->scale[1] = data->scale[2] = 1000;

    return 0;
}

/* Probe function */
static int custom_sensor_probe(struct i2c_client *client,
                               const struct i2c_device_id *id)
{
    struct iio_dev *indio_dev;
    struct custom_sensor_data *data;
    int ret;

    /* Allocate IIO device */
    indio_dev = devm_iio_device_alloc(&client->dev, sizeof(*data));
    if (!indio_dev)
        return -ENOMEM;

    data = iio_priv(indio_dev);
    data->client = client;
    i2c_set_clientdata(client, indio_dev);

    mutex_init(&data->lock);
    INIT_WORK(&data->work, custom_sensor_work_func);

    /* Initialize sensor hardware */
    ret = custom_sensor_init(data);
    if (ret < 0)
        return ret;

    /* Setup IIO device */
    indio_dev->dev.parent = &client->dev;
    indio_dev->name = DRIVER_NAME;
    indio_dev->modes = INDIO_DIRECT_MODE;
    indio_dev->channels = custom_sensor_channels;
    indio_dev->num_channels = ARRAY_SIZE(custom_sensor_channels);
    indio_dev->info = &custom_sensor_info;

    /* Setup triggered buffer */
    ret = devm_iio_triggered_buffer_setup(&client->dev, indio_dev,
                                         NULL,
                                         custom_sensor_trigger_handler,
                                         NULL);
    if (ret < 0) {
        dev_err(&client->dev, "Failed to setup triggered buffer\n");
        return ret;
    }

    /* Setup interrupt */
    if (client->irq) {
        ret = devm_request_threaded_irq(&client->dev, client->irq,
                                       custom_sensor_irq_handler,
                                       NULL,
                                       IRQF_TRIGGER_FALLING | IRQF_ONESHOT,
                                       DRIVER_NAME, indio_dev);
        if (ret < 0) {
            dev_err(&client->dev, "Failed to request IRQ\n");
            return ret;
        }
    }

    /* Register IIO device */
    ret = devm_iio_device_register(&client->dev, indio_dev);
    if (ret < 0) {
        dev_err(&client->dev, "Failed to register IIO device\n");
        return ret;
    }

    dev_info(&client->dev, "Custom sensor initialized successfully\n");
    return 0;
}

/* Device tree match table */
static const struct of_device_id custom_sensor_of_match[] = {
    { .compatible = "custom,env-sensor", },
    { }
};
MODULE_DEVICE_TABLE(of, custom_sensor_of_match);

/* I2C device ID table */
static const struct i2c_device_id custom_sensor_id[] = {
    { DRIVER_NAME, 0 },
    { }
};
MODULE_DEVICE_TABLE(i2c, custom_sensor_id);

/* I2C driver structure */
static struct i2c_driver custom_sensor_driver = {
    .driver = {
        .name = DRIVER_NAME,
        .of_match_table = custom_sensor_of_match,
    },
    .probe = custom_sensor_probe,
    .id_table = custom_sensor_id,
};

module_i2c_driver(custom_sensor_driver);

MODULE_AUTHOR("Your Name <you@example.com>");
MODULE_DESCRIPTION("Custom sensor driver for Jetson");
MODULE_LICENSE("GPL v2");
```

### 2.4 Kernel Driver Recipe

```python
# recipes-drivers/custom-sensor-driver/custom-sensor-driver_1.0.bb

SUMMARY = "Custom sensor driver"
DESCRIPTION = "Kernel driver for custom environmental sensor"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

inherit module

SRC_URI = "file://custom-sensor-driver.c \
           file://Makefile \
           file://COPYING \
          "

S = "${WORKDIR}"

RPROVIDES:${PN} += "kernel-module-custom-sensor"

# Kernel module dependencies
RDEPENDS:${PN} = "kernel-module-industrialio"

# Module will be auto-loaded via udev
KERNEL_MODULE_AUTOLOAD:append = " custom-sensor-driver"
```

---

## 3. Hardware Abstraction

### 3.1 Hardware Abstraction Layer (HAL)

```c
// hal/custom-hal.h - Hardware abstraction layer interface

#ifndef CUSTOM_HAL_H
#define CUSTOM_HAL_H

#include <stdint.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

/* HAL version */
#define CUSTOM_HAL_VERSION_MAJOR 1
#define CUSTOM_HAL_VERSION_MINOR 0

/* Hardware capabilities */
typedef struct {
    bool has_can_bus;
    bool has_dual_camera;
    bool has_fpga;
    bool has_gpio_expander;
    uint8_t num_analog_inputs;
    uint8_t num_digital_inputs;
    uint8_t num_digital_outputs;
} custom_hw_capabilities_t;

/* GPIO configuration */
typedef enum {
    GPIO_DIR_INPUT = 0,
    GPIO_DIR_OUTPUT = 1
} gpio_direction_t;

typedef enum {
    GPIO_PULL_NONE = 0,
    GPIO_PULL_UP = 1,
    GPIO_PULL_DOWN = 2
} gpio_pull_t;

/* CAN bus configuration */
typedef struct {
    uint32_t bitrate;
    bool loopback;
    bool listen_only;
    bool one_shot;
} can_config_t;

/* Camera configuration */
typedef struct {
    uint32_t width;
    uint32_t height;
    uint32_t framerate;
    uint32_t format;
} camera_config_t;

/* HAL initialization */
int custom_hal_init(void);
int custom_hal_deinit(void);
int custom_hal_get_capabilities(custom_hw_capabilities_t *caps);

/* GPIO functions */
int custom_hal_gpio_configure(uint8_t pin, gpio_direction_t dir, gpio_pull_t pull);
int custom_hal_gpio_write(uint8_t pin, bool value);
int custom_hal_gpio_read(uint8_t pin, bool *value);
int custom_hal_gpio_set_interrupt(uint8_t pin, void (*callback)(uint8_t, bool));

/* CAN bus functions */
int custom_hal_can_init(uint8_t bus, const can_config_t *config);
int custom_hal_can_send(uint8_t bus, uint32_t id, const uint8_t *data, uint8_t len);
int custom_hal_can_receive(uint8_t bus, uint32_t *id, uint8_t *data, uint8_t *len);
int custom_hal_can_set_filter(uint8_t bus, uint32_t id, uint32_t mask);

/* Camera functions */
int custom_hal_camera_init(uint8_t camera_id, const camera_config_t *config);
int custom_hal_camera_start(uint8_t camera_id);
int custom_hal_camera_stop(uint8_t camera_id);
int custom_hal_camera_capture(uint8_t camera_id, void *buffer, size_t *size);

/* ADC functions */
int custom_hal_adc_read(uint8_t channel, uint16_t *value);
int custom_hal_adc_read_voltage(uint8_t channel, float *voltage);

/* I2C functions */
int custom_hal_i2c_read(uint8_t bus, uint8_t addr, uint8_t reg, uint8_t *data, size_t len);
int custom_hal_i2c_write(uint8_t bus, uint8_t addr, uint8_t reg, const uint8_t *data, size_t len);

/* SPI functions */
int custom_hal_spi_transfer(uint8_t bus, const uint8_t *tx_data, uint8_t *rx_data, size_t len);

/* FPGA functions */
int custom_hal_fpga_init(void);
int custom_hal_fpga_load_bitstream(const void *bitstream, size_t size);
int custom_hal_fpga_read_register(uint32_t addr, uint32_t *value);
int custom_hal_fpga_write_register(uint32_t addr, uint32_t value);

/* Power management */
int custom_hal_set_power_mode(uint8_t mode);
int custom_hal_get_power_consumption(float *watts);

/* Thermal management */
int custom_hal_get_temperature(uint8_t sensor_id, float *temp_celsius);

#ifdef __cplusplus
}
#endif

#endif /* CUSTOM_HAL_H */
```

```python
# recipes-support/custom-hal/custom-hal_1.0.bb

SUMMARY = "Custom hardware abstraction layer"
DESCRIPTION = "HAL for custom Jetson carrier board"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=..."

SRC_URI = "file://custom-hal.c \
           file://custom-hal.h \
           file://custom-hal.pc.in \
           file://LICENSE \
          "

S = "${WORKDIR}"

inherit pkgconfig

DEPENDS = "libgpiod i2c-tools"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} -fPIC -shared -o libcustom-hal.so custom-hal.c -lgpiod
}

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    install -d ${D}${libdir}/pkgconfig

    install -m 0755 libcustom-hal.so ${D}${libdir}/
    install -m 0644 custom-hal.h ${D}${includedir}/

    # Generate and install pkg-config file
    sed -e 's:@PREFIX@:${prefix}:g' \
        -e 's:@LIBDIR@:${libdir}:g' \
        -e 's:@INCLUDEDIR@:${includedir}:g' \
        -e 's:@VERSION@:${PV}:g' \
        custom-hal.pc.in > custom-hal.pc
    install -m 0644 custom-hal.pc ${D}${libdir}/pkgconfig/
}

FILES:${PN} = "${libdir}/libcustom-hal.so"
FILES:${PN}-dev = "${includedir}/custom-hal.h ${libdir}/pkgconfig/custom-hal.pc"
```

---

## 4. Vendor Integration

### 4.1 Third-Party Binary Integration

```python
# recipes-bsp/vendor-binaries/vendor-firmware_1.0.bb

SUMMARY = "Vendor-specific firmware for custom hardware"
LICENSE = "CLOSED"

# Binary firmware from vendor
SRC_URI = " \
    file://camera_firmware.bin \
    file://fpga_bitstream.bit \
    file://vendor_config.json \
"

S = "${WORKDIR}"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

do_install() {
    install -d ${D}${base_libdir}/firmware/custom
    install -d ${D}${sysconfdir}/custom

    install -m 0644 camera_firmware.bin ${D}${base_libdir}/firmware/custom/
    install -m 0644 fpga_bitstream.bit ${D}${base_libdir}/firmware/custom/
    install -m 0644 vendor_config.json ${D}${sysconfdir}/custom/
}

FILES:${PN} = " \
    ${base_libdir}/firmware/custom/* \
    ${sysconfdir}/custom/* \
"

INSANE_SKIP:${PN} = "arch already-stripped"
```

### 4.2 Vendor SDK Wrapper

```python
# recipes-devtools/vendor-sdk/vendor-sdk_2.5.bb

SUMMARY = "Vendor SDK for custom hardware"
HOMEPAGE = "https://vendor.example.com"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE;md5=..."

SRC_URI = " \
    https://vendor.example.com/releases/sdk-${PV}.tar.gz \
    file://0001-fix-cross-compilation.patch \
    file://0002-use-system-libs.patch \
"

S = "${WORKDIR}/sdk-${PV}"

DEPENDS = "cuda-toolkit"
RDEPENDS:${PN} = "cuda-toolkit"

# Vendor SDK requires specific compiler flags
EXTRA_OECMAKE:append = " \
    -DCUDA_TOOLKIT_ROOT_DIR=${STAGING_DIR_NATIVE}/usr/local/cuda \
    -DCMAKE_BUILD_TYPE=Release \
    -DENABLE_TESTS=OFF \
"

inherit cmake

do_install:append() {
    # Install additional files not handled by cmake
    install -d ${D}${datadir}/vendor-sdk/examples
    cp -r ${S}/examples/* ${D}${datadir}/vendor-sdk/examples/
}

FILES:${PN} += "${datadir}/vendor-sdk/*"
FILES:${PN}-dev += "${includedir}/vendor/*"
```

---

## 5. Maintenance Strategies

### 5.1 BSP Version Management

```python
# conf/distro/custom-distro.conf

DISTRO = "custom-jetson"
DISTRO_NAME = "Custom Jetson Distribution"
DISTRO_VERSION = "1.0"
DISTRO_CODENAME = "genesis"

# BSP versioning
CUSTOM_BSP_VERSION = "1.0.0"
CUSTOM_BSP_BRANCH = "release/1.0"
META_TEGRA_VERSION = "35.4.1"

# Preferred versions
PREFERRED_VERSION_linux-tegra = "5.10%"
PREFERRED_VERSION_u-boot-tegra = "2021.07%"
PREFERRED_VERSION_tegra-binaries = "${META_TEGRA_VERSION}%"

# Distro features
DISTRO_FEATURES = " \
    systemd \
    pam \
    usrmerge \
    vulkan \
    opengl \
    wayland \
    ${DISTRO_FEATURES_LIBC} \
"

DISTRO_FEATURES:remove = "x11 alsa"

# Virtual providers
VIRTUAL-RUNTIME_init_manager = "systemd"
VIRTUAL-RUNTIME_initscripts = "systemd-compat-units"

# SDK configuration
SDK_NAME = "${DISTRO}-${TCLIBC}-${SDKMACHINE}-${IMAGE_BASENAME}-${TUNE_PKGARCH}-${MACHINE}"
SDK_VERSION = "${DISTRO_VERSION}"

# Maintain ABI compatibility
OLDEST_KERNEL = "5.10"
```

### 5.2 Automated Testing Framework

```python
# recipes-test/bsp-tests/bsp-tests_1.0.bb

SUMMARY = "BSP validation test suite"
LICENSE = "MIT"

SRC_URI = " \
    file://test_gpio.py \
    file://test_i2c.py \
    file://test_spi.py \
    file://test_can.py \
    file://test_camera.py \
    file://test_peripherals.py \
    file://run_all_tests.sh \
"

S = "${WORKDIR}"

RDEPENDS:${PN} = " \
    python3-pytest \
    python3-periphery \
    i2c-tools \
    can-utils \
"

do_install() {
    install -d ${D}${bindir}/bsp-tests

    install -m 0755 test_*.py ${D}${bindir}/bsp-tests/
    install -m 0755 run_all_tests.sh ${D}${bindir}/bsp-tests/
}

FILES:${PN} = "${bindir}/bsp-tests/*"
```

```bash
# files/run_all_tests.sh
#!/bin/bash

TEST_DIR="/usr/bin/bsp-tests"
REPORT_DIR="/var/log/bsp-tests"
TIMESTAMP=$(date +%Y%m%d-%H%M%S)
REPORT_FILE="${REPORT_DIR}/bsp-test-${TIMESTAMP}.txt"

mkdir -p "${REPORT_DIR}"

echo "========================================="
echo "BSP Validation Test Suite"
echo "Timestamp: ${TIMESTAMP}"
echo "========================================="
echo ""

cd "${TEST_DIR}"

# Run all tests with pytest
pytest -v --junit-xml="${REPORT_DIR}/junit-${TIMESTAMP}.xml" \
       --html="${REPORT_DIR}/report-${TIMESTAMP}.html" \
       --self-contained-html \
       test_*.py 2>&1 | tee "${REPORT_FILE}"

EXIT_CODE=$?

echo ""
echo "========================================="
if [ $EXIT_CODE -eq 0 ]; then
    echo "All tests PASSED"
else
    echo "Some tests FAILED"
fi
echo "Report saved to: ${REPORT_FILE}"
echo "========================================="

exit $EXIT_CODE
```

### 5.3 Continuous Integration Pipeline

```yaml
# .gitlab-ci.yml - CI/CD pipeline for BSP

variables:
  YOCTO_VERSION: "kirkstone"
  MACHINE: "custom-jetson-xavier"
  IMAGE: "custom-jetson-image"

stages:
  - build
  - test
  - deploy

build-image:
  stage: build
  tags:
    - yocto-builder
  script:
    - source /opt/poky/${YOCTO_VERSION}/environment-setup-x86_64-pokysdk-linux
    - cd build
    - bitbake ${IMAGE}
  artifacts:
    paths:
      - build/tmp/deploy/images/${MACHINE}/
    expire_in: 1 week

run-tests:
  stage: test
  dependencies:
    - build-image
  tags:
    - jetson-hardware
  script:
    - flash-image.sh build/tmp/deploy/images/${MACHINE}/${IMAGE}-${MACHINE}.tegraflash.tar.gz
    - wait-for-boot.sh
    - ssh root@jetson "/usr/bin/bsp-tests/run_all_tests.sh"
  artifacts:
    reports:
      junit: /var/log/bsp-tests/junit-*.xml
    paths:
      - /var/log/bsp-tests/
    when: always

deploy-production:
  stage: deploy
  dependencies:
    - build-image
    - run-tests
  only:
    - master
    - tags
  script:
    - upload-to-artifact-server.sh
    - generate-swupdate-package.sh
    - deploy-to-fleet.sh
  environment:
    name: production
```

---

## 6. Case Study: Industrial Controller BSP

**Project**: Custom BSP for industrial controller based on Jetson Xavier NX

**Hardware Features**:
- Dual Gigabit Ethernet (one with TSN)
- Dual CAN-FD bus
- 16 digital inputs (24V industrial)
- 8 digital outputs (relay)
- 4 analog inputs (0-10V)
- RS485 serial
- Industrial temperature range (-40°C to +85°C)

**Development Timeline**:
- Week 1-2: Hardware bring-up, power validation
- Week 3-4: Device tree development, driver integration
- Week 5-6: HAL development, API design
- Week 7-8: Testing, validation, certification prep

**Lessons Learned**:
1. Start with minimal device tree, add features incrementally
2. Hardware validation scripts save debugging time
3. HAL abstraction enables rapid application development
4. Automated testing is crucial for regression prevention
5. Maintain separate branches for hardware revisions

**Performance Results**:
- Boot time: 12 seconds (U-Boot to application)
- CAN bus latency: < 1ms
- GPIO response time: < 100μs
- Ethernet throughput: 940 Mbps (line rate)
- Power consumption: 8W idle, 15W full load

---

## Best Practices Summary

1. **Layer Organization**: Keep BSP layer separate from application layers
2. **Hardware Abstraction**: Provide clean APIs for hardware access
3. **Version Control**: Tag hardware revisions in BSP
4. **Testing**: Automate hardware validation tests
5. **Documentation**: Document pin assignments, schematics, errata
6. **Vendor Binaries**: Isolate in separate recipes with CLOSED license
7. **Maintenance**: Plan for long-term support and updates
8. **Collaboration**: Work closely with hardware team during development

---

**Next Steps**: Proceed to [Real-Time Systems](04-realtime-systems.md) to learn about RT-PREEMPT and deterministic behavior.
