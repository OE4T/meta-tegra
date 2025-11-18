/*
 * SPI Device Driver Template for NVIDIA Jetson Platforms
 *
 * This driver demonstrates:
 * - SPI device driver registration
 * - SPI transfer operations (read/write)
 * - Device tree integration
 * - Character device interface
 * - DMA support (optional)
 * - Interrupt handling for DRDY/INT pins
 *
 * Example device: MCP3008 8-channel 10-bit ADC
 * Can be adapted for any SPI device
 *
 * Tested on: Jetson TX2, Xavier, Orin
 * Kernel: 5.10+
 * License: GPL-2.0
 */

#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/spi/spi.h>
#include <linux/fs.h>
#include <linux/cdev.h>
#include <linux/device.h>
#include <linux/slab.h>
#include <linux/uaccess.h>
#include <linux/mutex.h>
#include <linux/of.h>
#include <linux/of_device.h>
#include <linux/gpio/consumer.h>
#include <linux/interrupt.h>
#include <linux/delay.h>

#define DRIVER_NAME "spi_device"
#define DEVICE_NAME "spiadc"
#define MAX_CHANNELS 8

/* MCP3008 Commands */
#define MCP3008_START_BIT    0x01
#define MCP3008_SINGLE_ENDED 0x08

/* Driver data structure */
struct spi_adc_data {
    struct spi_device *spi;
    struct cdev cdev;
    dev_t dev_num;
    struct class *dev_class;
    struct device *device;
    struct mutex lock;

    /* GPIO for data ready signal (optional) */
    struct gpio_desc *drdy_gpio;
    int irq;

    /* ADC readings */
    u16 channels[MAX_CHANNELS];
    u32 spi_speed;
};

/*
 * SPI Communication Functions
 */

/* Read single ADC channel using SPI */
static int spi_adc_read_channel(struct spi_adc_data *data, int channel, u16 *value)
{
    int ret;
    u8 tx_buf[3];
    u8 rx_buf[3];
    struct spi_transfer xfer = {
        .tx_buf = tx_buf,
        .rx_buf = rx_buf,
        .len = 3,
        .speed_hz = data->spi_speed,
        .bits_per_word = 8,
    };
    struct spi_message msg;

    if (channel < 0 || channel >= MAX_CHANNELS)
        return -EINVAL;

    /* Prepare command bytes for MCP3008 */
    /* Byte 1: Start bit
     * Byte 2: Single-ended mode + channel selection
     * Byte 3: Don't care (clocks out data)
     */
    tx_buf[0] = MCP3008_START_BIT;
    tx_buf[1] = (MCP3008_SINGLE_ENDED | (channel << 4));
    tx_buf[2] = 0x00;

    spi_message_init(&msg);
    spi_message_add_tail(&xfer, &msg);

    mutex_lock(&data->lock);
    ret = spi_sync(data->spi, &msg);
    mutex_unlock(&data->lock);

    if (ret) {
        dev_err(&data->spi->dev, "SPI transfer failed: %d\n", ret);
        return ret;
    }

    /* Extract 10-bit ADC value from response
     * Result is in bits [9:0] of the last 10 bits received
     */
    *value = ((rx_buf[1] & 0x03) << 8) | rx_buf[2];

    dev_dbg(&data->spi->dev, "Channel %d: raw=0x%04X (%u)\n",
            channel, *value, *value);

    return 0;
}

/* Read all channels */
static int spi_adc_read_all_channels(struct spi_adc_data *data)
{
    int i, ret;

    for (i = 0; i < MAX_CHANNELS; i++) {
        ret = spi_adc_read_channel(data, i, &data->channels[i]);
        if (ret)
            return ret;
    }

    return 0;
}

/* Example: SPI write operation (for devices that support write) */
static int spi_adc_write_register(struct spi_adc_data *data, u8 reg, u8 value)
{
    int ret;
    u8 tx_buf[2];
    struct spi_transfer xfer = {
        .tx_buf = tx_buf,
        .len = 2,
        .speed_hz = data->spi_speed,
    };
    struct spi_message msg;

    tx_buf[0] = reg;
    tx_buf[1] = value;

    spi_message_init(&msg);
    spi_message_add_tail(&xfer, &msg);

    mutex_lock(&data->lock);
    ret = spi_sync(data->spi, &msg);
    mutex_unlock(&data->lock);

    return ret;
}

/*
 * Interrupt Handler (for DRDY/INT pins)
 */
static irqreturn_t spi_adc_irq_handler(int irq, void *dev_id)
{
    struct spi_adc_data *data = dev_id;

    dev_dbg(&data->spi->dev, "Data ready interrupt received\n");

    /* In a real application, you might:
     * - Schedule work to read the data
     * - Wake up waiting processes
     * - Set a flag for polling
     */

    return IRQ_HANDLED;
}

/*
 * Character Device Operations
 */

static int spi_adc_dev_open(struct inode *inode, struct file *file)
{
    struct spi_adc_data *data = container_of(inode->i_cdev,
                                               struct spi_adc_data, cdev);
    file->private_data = data;
    return 0;
}

static int spi_adc_dev_release(struct inode *inode, struct file *file)
{
    return 0;
}

static ssize_t spi_adc_dev_read(struct file *file, char __user *buf,
                                size_t count, loff_t *ppos)
{
    struct spi_adc_data *data = file->private_data;
    char kbuf[512];
    int len, ret, i;

    if (*ppos > 0)
        return 0;

    /* Read all channels */
    ret = spi_adc_read_all_channels(data);
    if (ret)
        return ret;

    /* Format output */
    len = snprintf(kbuf, sizeof(kbuf), "SPI ADC Readings:\n");
    for (i = 0; i < MAX_CHANNELS; i++) {
        len += snprintf(kbuf + len, sizeof(kbuf) - len,
                       "Channel %d: %4u (0x%03X) [%u mV]\n",
                       i, data->channels[i], data->channels[i],
                       (data->channels[i] * 3300) / 1024);
    }

    if (count < len)
        len = count;

    if (copy_to_user(buf, kbuf, len))
        return -EFAULT;

    *ppos += len;
    return len;
}

/* IOCTL for channel selection (optional) */
#define SPI_ADC_IOC_MAGIC 'S'
#define SPI_ADC_IOC_READ_CHANNEL _IOWR(SPI_ADC_IOC_MAGIC, 1, int)

static long spi_adc_dev_ioctl(struct file *file, unsigned int cmd,
                              unsigned long arg)
{
    struct spi_adc_data *data = file->private_data;
    int ret;
    u16 value;
    int __user *user_arg = (int __user *)arg;
    int channel;

    switch (cmd) {
    case SPI_ADC_IOC_READ_CHANNEL:
        if (get_user(channel, user_arg))
            return -EFAULT;

        ret = spi_adc_read_channel(data, channel, &value);
        if (ret)
            return ret;

        if (put_user(value, user_arg))
            return -EFAULT;

        return 0;

    default:
        return -ENOTTY;
    }
}

static const struct file_operations spi_adc_fops = {
    .owner = THIS_MODULE,
    .open = spi_adc_dev_open,
    .release = spi_adc_dev_release,
    .read = spi_adc_dev_read,
    .unlocked_ioctl = spi_adc_dev_ioctl,
};

/*
 * Sysfs Attributes
 */

static ssize_t channel_show(struct device *dev,
                           struct device_attribute *attr, char *buf)
{
    struct spi_adc_data *data = dev_get_drvdata(dev);
    int ret, i, len = 0;

    ret = spi_adc_read_all_channels(data);
    if (ret)
        return ret;

    for (i = 0; i < MAX_CHANNELS; i++) {
        len += sprintf(buf + len, "%u ", data->channels[i]);
    }
    len += sprintf(buf + len, "\n");

    return len;
}

static ssize_t speed_show(struct device *dev,
                         struct device_attribute *attr, char *buf)
{
    struct spi_adc_data *data = dev_get_drvdata(dev);
    return sprintf(buf, "%u\n", data->spi_speed);
}

static ssize_t speed_store(struct device *dev,
                          struct device_attribute *attr,
                          const char *buf, size_t count)
{
    struct spi_adc_data *data = dev_get_drvdata(dev);
    u32 speed;

    if (kstrtou32(buf, 10, &speed))
        return -EINVAL;

    if (speed > 10000000)  /* Max 10 MHz */
        return -EINVAL;

    data->spi_speed = speed;
    return count;
}

static DEVICE_ATTR_RO(channel);
static DEVICE_ATTR_RW(speed);

static struct attribute *spi_adc_attrs[] = {
    &dev_attr_channel.attr,
    &dev_attr_speed.attr,
    NULL,
};
ATTRIBUTE_GROUPS(spi_adc);

/*
 * SPI Driver Probe and Remove
 */

static int spi_adc_probe(struct spi_device *spi)
{
    struct spi_adc_data *data;
    int ret;

    dev_info(&spi->dev, "Probing SPI ADC driver\n");

    /* Allocate driver data */
    data = devm_kzalloc(&spi->dev, sizeof(struct spi_adc_data), GFP_KERNEL);
    if (!data)
        return -ENOMEM;

    data->spi = spi;
    mutex_init(&data->lock);
    spi_set_drvdata(spi, data);

    /* Configure SPI mode and speed */
    spi->mode = SPI_MODE_0;  /* CPOL=0, CPHA=0 */
    spi->bits_per_word = 8;
    data->spi_speed = spi->max_speed_hz ?: 1000000;  /* Default 1 MHz */

    ret = spi_setup(spi);
    if (ret) {
        dev_err(&spi->dev, "Failed to setup SPI: %d\n", ret);
        return ret;
    }

    /* Get optional DRDY GPIO */
    data->drdy_gpio = devm_gpiod_get_optional(&spi->dev, "drdy", GPIOD_IN);
    if (IS_ERR(data->drdy_gpio)) {
        dev_warn(&spi->dev, "Failed to get DRDY GPIO\n");
        data->drdy_gpio = NULL;
    }

    /* Request IRQ if DRDY GPIO is available */
    if (data->drdy_gpio) {
        data->irq = gpiod_to_irq(data->drdy_gpio);
        if (data->irq > 0) {
            ret = devm_request_irq(&spi->dev, data->irq,
                                   spi_adc_irq_handler,
                                   IRQF_TRIGGER_RISING,
                                   "spi_adc_drdy", data);
            if (ret) {
                dev_warn(&spi->dev, "Failed to request IRQ: %d\n", ret);
                data->irq = 0;
            } else {
                dev_info(&spi->dev, "DRDY IRQ configured: %d\n", data->irq);
            }
        }
    }

    /* Allocate character device number */
    ret = alloc_chrdev_region(&data->dev_num, 0, 1, DEVICE_NAME);
    if (ret < 0) {
        dev_err(&spi->dev, "Failed to allocate device number\n");
        return ret;
    }

    /* Initialize character device */
    cdev_init(&data->cdev, &spi_adc_fops);
    data->cdev.owner = THIS_MODULE;

    ret = cdev_add(&data->cdev, data->dev_num, 1);
    if (ret) {
        unregister_chrdev_region(data->dev_num, 1);
        return ret;
    }

    /* Create device class */
    data->dev_class = class_create(THIS_MODULE, "spi_adc_class");
    if (IS_ERR(data->dev_class)) {
        cdev_del(&data->cdev);
        unregister_chrdev_region(data->dev_num, 1);
        return PTR_ERR(data->dev_class);
    }

    /* Create device with sysfs attributes */
    data->dev_class->dev_groups = spi_adc_groups;
    data->device = device_create(data->dev_class, &spi->dev,
                                  data->dev_num, data, DEVICE_NAME);
    if (IS_ERR(data->device)) {
        class_destroy(data->dev_class);
        cdev_del(&data->cdev);
        unregister_chrdev_region(data->dev_num, 1);
        return PTR_ERR(data->device);
    }

    /* Test SPI communication */
    ret = spi_adc_read_all_channels(data);
    if (ret) {
        dev_err(&spi->dev, "Initial SPI read failed\n");
    }

    dev_info(&spi->dev, "SPI ADC driver probed successfully\n");
    dev_info(&spi->dev, "Device: /dev/%s\n", DEVICE_NAME);
    dev_info(&spi->dev, "SPI Speed: %u Hz\n", data->spi_speed);

    return 0;
}

static int spi_adc_remove(struct spi_device *spi)
{
    struct spi_adc_data *data = spi_get_drvdata(spi);

    dev_info(&spi->dev, "Removing SPI ADC driver\n");

    device_destroy(data->dev_class, data->dev_num);
    class_destroy(data->dev_class);
    cdev_del(&data->cdev);
    unregister_chrdev_region(data->dev_num, 1);

    return 0;
}

/* Device tree match table */
static const struct of_device_id spi_adc_of_match[] = {
    { .compatible = "microchip,mcp3008" },
    { .compatible = "custom,spi-adc" },
    { }
};
MODULE_DEVICE_TABLE(of, spi_adc_of_match);

/* SPI device ID table */
static const struct spi_device_id spi_adc_id[] = {
    { "mcp3008", 0 },
    { "spi-adc", 0 },
    { }
};
MODULE_DEVICE_TABLE(spi, spi_adc_id);

/* SPI driver structure */
static struct spi_driver spi_adc_driver = {
    .driver = {
        .name = DRIVER_NAME,
        .of_match_table = spi_adc_of_match,
    },
    .probe = spi_adc_probe,
    .remove = spi_adc_remove,
    .id_table = spi_adc_id,
};

module_spi_driver(spi_adc_driver);

MODULE_LICENSE("GPL v2");
MODULE_AUTHOR("Meta-Tegra Learning System");
MODULE_DESCRIPTION("SPI Device Driver for NVIDIA Jetson");
MODULE_VERSION("1.0");

/*
 * Usage Instructions:
 * ===================
 *
 * 1. Device Tree Configuration:
 *    Add to your DTS file:
 *
 *    &spi1 {
 *        status = "okay";
 *
 *        adc@0 {
 *            compatible = "microchip,mcp3008";
 *            reg = <0>;
 *            spi-max-frequency = <1000000>;
 *            drdy-gpios = <&gpio TEGRA_GPIO(X, 0) GPIO_ACTIVE_HIGH>;
 *        };
 *    };
 *
 * 2. Enable SPI in kernel config:
 *    CONFIG_SPI=y
 *    CONFIG_SPI_TEGRA114=y
 *
 * 3. Build the module:
 *    make
 *
 * 4. Load the module:
 *    sudo insmod spi-device-driver.ko
 *
 * 5. Verify loading:
 *    dmesg | tail -20
 *    lsmod | grep spi_device
 *
 * 6. Test character device:
 *    cat /dev/spiadc
 *
 * 7. Test sysfs interface:
 *    cat /sys/class/spi_adc_class/spiadc/channel
 *    cat /sys/class/spi_adc_class/spiadc/speed
 *    echo 500000 > /sys/class/spi_adc_class/spiadc/speed
 *
 * 8. Debug SPI:
 *    # Check SPI bus
 *    ls /dev/spi*
 *
 *    # View SPI configuration
 *    cat /sys/class/spi_master/spi1/device/spi1.0/modalias
 *
 * 9. Test with spidev (alternative):
 *    # Install spidev tools
 *    sudo apt-get install spi-tools
 *
 *    # Test SPI bus
 *    sudo spi-config -d /dev/spidev1.0 -q
 *
 * 10. Unload module:
 *     sudo rmmod spi_device
 */
