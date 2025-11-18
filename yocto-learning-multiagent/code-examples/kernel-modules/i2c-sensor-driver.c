/*
 * I2C Sensor Driver Template for NVIDIA Jetson Platforms
 *
 * This driver demonstrates:
 * - I2C device driver registration
 * - I2C read/write operations
 * - Device tree integration
 * - Character device interface
 * - Sysfs attributes for sensor data
 * - Proper locking and error handling
 *
 * Example sensor: BME280 (Temperature, Humidity, Pressure)
 * Can be adapted for any I2C sensor
 *
 * Tested on: Jetson TX2, Xavier, Orin
 * Kernel: 5.10+
 * License: GPL-2.0
 */

#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/i2c.h>
#include <linux/fs.h>
#include <linux/cdev.h>
#include <linux/device.h>
#include <linux/slab.h>
#include <linux/uaccess.h>
#include <linux/delay.h>
#include <linux/of.h>
#include <linux/of_device.h>
#include <linux/mutex.h>

#define DRIVER_NAME "i2c_sensor"
#define DEVICE_NAME "sensor"

/* BME280 Register Definitions - adapt for your sensor */
#define BME280_REG_CHIPID       0xD0
#define BME280_REG_RESET        0xE0
#define BME280_REG_CTRL_HUM     0xF2
#define BME280_REG_CTRL_MEAS    0xF4
#define BME280_REG_CONFIG       0xF5
#define BME280_REG_DATA         0xF7
#define BME280_REG_TEMP_MSB     0xFA
#define BME280_REG_HUM_MSB      0xFD

#define BME280_CHIPID           0x60
#define BME280_RESET_CMD        0xB6

/* Driver data structure */
struct sensor_data {
    struct i2c_client *client;
    struct cdev cdev;
    dev_t dev_num;
    struct class *dev_class;
    struct device *device;
    struct mutex lock;

    /* Sensor readings */
    s32 temperature;  /* Temperature in 0.01 degrees C */
    u32 humidity;     /* Humidity in 0.01% */
    u32 pressure;     /* Pressure in Pa */

    /* Calibration data (sensor-specific) */
    u16 dig_T1;
    s16 dig_T2, dig_T3;
    /* Add more calibration parameters as needed */
};

/*
 * I2C Communication Functions
 */

/* Read single byte from I2C register */
static int sensor_read_byte(struct sensor_data *data, u8 reg, u8 *val)
{
    int ret;
    struct i2c_msg msgs[2];

    /* Write register address */
    msgs[0].addr = data->client->addr;
    msgs[0].flags = 0;
    msgs[0].len = 1;
    msgs[0].buf = &reg;

    /* Read data */
    msgs[1].addr = data->client->addr;
    msgs[1].flags = I2C_M_RD;
    msgs[1].len = 1;
    msgs[1].buf = val;

    ret = i2c_transfer(data->client->adapter, msgs, 2);
    if (ret != 2) {
        dev_err(&data->client->dev, "Failed to read register 0x%02X\n", reg);
        return ret < 0 ? ret : -EIO;
    }

    return 0;
}

/* Write single byte to I2C register */
static int sensor_write_byte(struct sensor_data *data, u8 reg, u8 val)
{
    int ret;
    u8 buf[2];
    struct i2c_msg msg;

    buf[0] = reg;
    buf[1] = val;

    msg.addr = data->client->addr;
    msg.flags = 0;
    msg.len = 2;
    msg.buf = buf;

    ret = i2c_transfer(data->client->adapter, &msg, 1);
    if (ret != 1) {
        dev_err(&data->client->dev, "Failed to write register 0x%02X\n", reg);
        return ret < 0 ? ret : -EIO;
    }

    return 0;
}

/* Read multiple bytes from I2C */
static int sensor_read_block(struct sensor_data *data, u8 reg, u8 *buf, int len)
{
    int ret;
    struct i2c_msg msgs[2];

    msgs[0].addr = data->client->addr;
    msgs[0].flags = 0;
    msgs[0].len = 1;
    msgs[0].buf = &reg;

    msgs[1].addr = data->client->addr;
    msgs[1].flags = I2C_M_RD;
    msgs[1].len = len;
    msgs[1].buf = buf;

    ret = i2c_transfer(data->client->adapter, msgs, 2);
    if (ret != 2) {
        dev_err(&data->client->dev, "Failed to read block from 0x%02X\n", reg);
        return ret < 0 ? ret : -EIO;
    }

    return 0;
}

/*
 * Sensor Operations
 */

/* Initialize sensor hardware */
static int sensor_init_device(struct sensor_data *data)
{
    int ret;
    u8 chip_id;

    /* Read and verify chip ID */
    ret = sensor_read_byte(data, BME280_REG_CHIPID, &chip_id);
    if (ret) {
        dev_err(&data->client->dev, "Failed to read chip ID\n");
        return ret;
    }

    if (chip_id != BME280_CHIPID) {
        dev_err(&data->client->dev, "Invalid chip ID: 0x%02X (expected 0x%02X)\n",
                chip_id, BME280_CHIPID);
        return -ENODEV;
    }

    dev_info(&data->client->dev, "Found sensor, chip ID: 0x%02X\n", chip_id);

    /* Reset sensor */
    ret = sensor_write_byte(data, BME280_REG_RESET, BME280_RESET_CMD);
    if (ret)
        return ret;

    msleep(10);  /* Wait for reset to complete */

    /* Configure sensor */
    /* Humidity oversampling x1 */
    ret = sensor_write_byte(data, BME280_REG_CTRL_HUM, 0x01);
    if (ret)
        return ret;

    /* Temperature and pressure oversampling x1, normal mode */
    ret = sensor_write_byte(data, BME280_REG_CTRL_MEAS, 0x27);
    if (ret)
        return ret;

    /* Standby time 0.5ms, filter off */
    ret = sensor_write_byte(data, BME280_REG_CONFIG, 0x00);
    if (ret)
        return ret;

    dev_info(&data->client->dev, "Sensor initialized successfully\n");
    return 0;
}

/* Read sensor data */
static int sensor_read_data(struct sensor_data *data)
{
    int ret;
    u8 buf[8];
    s32 adc_T, adc_P, adc_H;
    s32 var1, var2, t_fine;

    mutex_lock(&data->lock);

    /* Read all data registers */
    ret = sensor_read_block(data, BME280_REG_DATA, buf, 8);
    if (ret) {
        mutex_unlock(&data->lock);
        return ret;
    }

    /* Parse raw ADC values */
    adc_P = (buf[0] << 12) | (buf[1] << 4) | (buf[2] >> 4);
    adc_T = (buf[3] << 12) | (buf[4] << 4) | (buf[5] >> 4);
    adc_H = (buf[6] << 8) | buf[7];

    /* Temperature compensation (simplified) */
    /* In a real driver, load calibration data from sensor EEPROM */
    var1 = ((((adc_T >> 3) - (120000 << 1))) * 25000) >> 11;
    var2 = (((((adc_T >> 4) - 120000) * ((adc_T >> 4) - 120000)) >> 12) * (-7000)) >> 14;
    t_fine = var1 + var2;
    data->temperature = (t_fine * 5 + 128) >> 8;

    /* Store raw values for now (proper calibration needed) */
    data->pressure = adc_P;
    data->humidity = adc_H;

    mutex_unlock(&data->lock);

    dev_dbg(&data->client->dev, "Temp: %d, Pressure: %u, Humidity: %u\n",
            data->temperature, data->pressure, data->humidity);

    return 0;
}

/*
 * Character Device Operations
 */

static int sensor_dev_open(struct inode *inode, struct file *file)
{
    struct sensor_data *data = container_of(inode->i_cdev, struct sensor_data, cdev);
    file->private_data = data;
    return 0;
}

static int sensor_dev_release(struct inode *inode, struct file *file)
{
    return 0;
}

static ssize_t sensor_dev_read(struct file *file, char __user *buf,
                               size_t count, loff_t *ppos)
{
    struct sensor_data *data = file->private_data;
    char kbuf[128];
    int len, ret;

    if (*ppos > 0)
        return 0;

    /* Update sensor readings */
    ret = sensor_read_data(data);
    if (ret)
        return ret;

    /* Format data */
    mutex_lock(&data->lock);
    len = snprintf(kbuf, sizeof(kbuf),
                   "Temperature: %d.%02d C\n"
                   "Humidity: %u.%02u %%\n"
                   "Pressure: %u Pa\n",
                   data->temperature / 100, abs(data->temperature % 100),
                   data->humidity / 100, data->humidity % 100,
                   data->pressure);
    mutex_unlock(&data->lock);

    if (count < len)
        len = count;

    if (copy_to_user(buf, kbuf, len))
        return -EFAULT;

    *ppos += len;
    return len;
}

static const struct file_operations sensor_fops = {
    .owner = THIS_MODULE,
    .open = sensor_dev_open,
    .release = sensor_dev_release,
    .read = sensor_dev_read,
};

/*
 * Sysfs Attributes
 */

static ssize_t temperature_show(struct device *dev,
                                struct device_attribute *attr, char *buf)
{
    struct sensor_data *data = dev_get_drvdata(dev);
    int ret;

    ret = sensor_read_data(data);
    if (ret)
        return ret;

    return sprintf(buf, "%d.%02d\n",
                   data->temperature / 100,
                   abs(data->temperature % 100));
}

static ssize_t humidity_show(struct device *dev,
                            struct device_attribute *attr, char *buf)
{
    struct sensor_data *data = dev_get_drvdata(dev);
    int ret;

    ret = sensor_read_data(data);
    if (ret)
        return ret;

    return sprintf(buf, "%u.%02u\n",
                   data->humidity / 100,
                   data->humidity % 100);
}

static ssize_t pressure_show(struct device *dev,
                            struct device_attribute *attr, char *buf)
{
    struct sensor_data *data = dev_get_drvdata(dev);
    int ret;

    ret = sensor_read_data(data);
    if (ret)
        return ret;

    return sprintf(buf, "%u\n", data->pressure);
}

static DEVICE_ATTR_RO(temperature);
static DEVICE_ATTR_RO(humidity);
static DEVICE_ATTR_RO(pressure);

static struct attribute *sensor_attrs[] = {
    &dev_attr_temperature.attr,
    &dev_attr_humidity.attr,
    &dev_attr_pressure.attr,
    NULL,
};
ATTRIBUTE_GROUPS(sensor);

/*
 * I2C Driver Probe and Remove
 */

static int sensor_probe(struct i2c_client *client, const struct i2c_device_id *id)
{
    struct sensor_data *data;
    int ret;

    dev_info(&client->dev, "Probing I2C sensor driver\n");

    /* Check I2C functionality */
    if (!i2c_check_functionality(client->adapter, I2C_FUNC_I2C)) {
        dev_err(&client->dev, "I2C adapter doesn't support I2C_FUNC_I2C\n");
        return -ENODEV;
    }

    /* Allocate driver data */
    data = devm_kzalloc(&client->dev, sizeof(struct sensor_data), GFP_KERNEL);
    if (!data)
        return -ENOMEM;

    data->client = client;
    mutex_init(&data->lock);
    i2c_set_clientdata(client, data);

    /* Initialize sensor hardware */
    ret = sensor_init_device(data);
    if (ret)
        return ret;

    /* Allocate character device number */
    ret = alloc_chrdev_region(&data->dev_num, 0, 1, DEVICE_NAME);
    if (ret < 0) {
        dev_err(&client->dev, "Failed to allocate device number\n");
        return ret;
    }

    /* Initialize character device */
    cdev_init(&data->cdev, &sensor_fops);
    data->cdev.owner = THIS_MODULE;

    ret = cdev_add(&data->cdev, data->dev_num, 1);
    if (ret) {
        unregister_chrdev_region(data->dev_num, 1);
        return ret;
    }

    /* Create device class */
    data->dev_class = class_create(THIS_MODULE, "sensor_class");
    if (IS_ERR(data->dev_class)) {
        cdev_del(&data->cdev);
        unregister_chrdev_region(data->dev_num, 1);
        return PTR_ERR(data->dev_class);
    }

    /* Create device with sysfs attributes */
    data->dev_class->dev_groups = sensor_groups;
    data->device = device_create(data->dev_class, &client->dev,
                                  data->dev_num, data, DEVICE_NAME);
    if (IS_ERR(data->device)) {
        class_destroy(data->dev_class);
        cdev_del(&data->cdev);
        unregister_chrdev_region(data->dev_num, 1);
        return PTR_ERR(data->device);
    }

    dev_info(&client->dev, "Sensor driver probed successfully\n");
    dev_info(&client->dev, "Device: /dev/%s\n", DEVICE_NAME);
    dev_info(&client->dev, "Sysfs: /sys/class/sensor_class/%s/\n", DEVICE_NAME);

    return 0;
}

static int sensor_remove(struct i2c_client *client)
{
    struct sensor_data *data = i2c_get_clientdata(client);

    dev_info(&client->dev, "Removing I2C sensor driver\n");

    device_destroy(data->dev_class, data->dev_num);
    class_destroy(data->dev_class);
    cdev_del(&data->cdev);
    unregister_chrdev_region(data->dev_num, 1);

    return 0;
}

/* Device tree match table */
static const struct of_device_id sensor_of_match[] = {
    { .compatible = "bosch,bme280" },
    { .compatible = "custom,i2c-sensor" },
    { }
};
MODULE_DEVICE_TABLE(of, sensor_of_match);

/* I2C device ID table */
static const struct i2c_device_id sensor_id[] = {
    { "bme280", 0 },
    { "i2c-sensor", 0 },
    { }
};
MODULE_DEVICE_TABLE(i2c, sensor_id);

/* I2C driver structure */
static struct i2c_driver sensor_driver = {
    .driver = {
        .name = DRIVER_NAME,
        .of_match_table = sensor_of_match,
    },
    .probe = sensor_probe,
    .remove = sensor_remove,
    .id_table = sensor_id,
};

module_i2c_driver(sensor_driver);

MODULE_LICENSE("GPL v2");
MODULE_AUTHOR("Meta-Tegra Learning System");
MODULE_DESCRIPTION("I2C Sensor Driver for NVIDIA Jetson");
MODULE_VERSION("1.0");

/*
 * Usage Instructions:
 * ===================
 *
 * 1. Device Tree Configuration:
 *    Add to your DTS file:
 *
 *    &i2c1 {
 *        status = "okay";
 *
 *        sensor@76 {
 *            compatible = "bosch,bme280";
 *            reg = <0x76>;
 *        };
 *    };
 *
 * 2. Build the module:
 *    make
 *
 * 3. Load the module:
 *    sudo insmod i2c-sensor-driver.ko
 *
 * 4. Verify loading:
 *    dmesg | tail -20
 *    lsmod | grep i2c_sensor
 *
 * 5. Test character device:
 *    cat /dev/sensor
 *
 * 6. Test sysfs interface:
 *    cat /sys/class/sensor_class/sensor/temperature
 *    cat /sys/class/sensor_class/sensor/humidity
 *    cat /sys/class/sensor_class/sensor/pressure
 *
 * 7. Debug I2C communication:
 *    sudo i2cdetect -y -r 1  # Scan I2C bus 1
 *    sudo i2cdump -y 1 0x76  # Dump sensor registers
 *
 * 8. Unload module:
 *    sudo rmmod i2c_sensor
 */
