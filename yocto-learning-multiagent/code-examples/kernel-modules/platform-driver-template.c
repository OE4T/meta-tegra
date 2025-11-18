/*
 * Platform Driver Template for NVIDIA Jetson Platforms
 *
 * This driver demonstrates:
 * - Platform device driver registration
 * - Device tree integration and parsing
 * - Resource management (memory, IRQ, clocks, regulators)
 * - Power management (suspend/resume)
 * - Runtime PM
 * - Character device interface
 *
 * Use this template for SoC-integrated peripherals that are
 * not on discoverable buses (I2C, SPI, PCI, USB)
 *
 * Tested on: Jetson TX2, Xavier, Orin
 * Kernel: 5.10+
 * License: GPL-2.0
 */

#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/platform_device.h>
#include <linux/fs.h>
#include <linux/cdev.h>
#include <linux/device.h>
#include <linux/slab.h>
#include <linux/uaccess.h>
#include <linux/mutex.h>
#include <linux/of.h>
#include <linux/of_device.h>
#include <linux/of_address.h>
#include <linux/of_irq.h>
#include <linux/io.h>
#include <linux/ioport.h>
#include <linux/interrupt.h>
#include <linux/clk.h>
#include <linux/regulator/consumer.h>
#include <linux/pm_runtime.h>
#include <linux/delay.h>

#define DRIVER_NAME "platform_device"
#define DEVICE_NAME "pdev"

/* Hardware register definitions (example) */
#define REG_CONTROL     0x00
#define REG_STATUS      0x04
#define REG_DATA        0x08
#define REG_IRQ_ENABLE  0x0C
#define REG_IRQ_STATUS  0x10

/* Control register bits */
#define CTRL_ENABLE     BIT(0)
#define CTRL_RESET      BIT(1)
#define CTRL_IRQ_EN     BIT(2)

/* Status register bits */
#define STATUS_READY    BIT(0)
#define STATUS_ERROR    BIT(1)

/* Driver data structure */
struct pdev_data {
    struct platform_device *pdev;
    struct cdev cdev;
    dev_t dev_num;
    struct class *dev_class;
    struct device *device;
    struct mutex lock;

    /* Hardware resources */
    void __iomem *base;          /* Memory-mapped I/O base address */
    struct resource *mem_res;    /* Memory resource */
    int irq;                     /* IRQ number */
    struct clk *clk;             /* Clock */
    struct regulator *regulator; /* Power regulator */

    /* Device state */
    bool enabled;
    u32 data_value;

    /* Device tree properties */
    u32 max_speed;
    const char *mode;
};

/*
 * Hardware Access Functions
 */

static inline void pdev_writel(struct pdev_data *data, u32 val, unsigned int reg)
{
    writel(val, data->base + reg);
}

static inline u32 pdev_readl(struct pdev_data *data, unsigned int reg)
{
    return readl(data->base + reg);
}

/* Initialize hardware */
static int pdev_hw_init(struct pdev_data *data)
{
    u32 val;
    int timeout;

    dev_info(&data->pdev->dev, "Initializing hardware\n");

    /* Reset device */
    pdev_writel(data, CTRL_RESET, REG_CONTROL);
    msleep(10);

    /* Wait for device ready */
    timeout = 1000;
    while (timeout--) {
        val = pdev_readl(data, REG_STATUS);
        if (val & STATUS_READY)
            break;
        usleep_range(100, 200);
    }

    if (timeout <= 0) {
        dev_err(&data->pdev->dev, "Device not ready after reset\n");
        return -ETIMEDOUT;
    }

    /* Enable device */
    pdev_writel(data, CTRL_ENABLE | CTRL_IRQ_EN, REG_CONTROL);

    /* Enable interrupts */
    pdev_writel(data, 0xFFFFFFFF, REG_IRQ_ENABLE);

    data->enabled = true;

    dev_info(&data->pdev->dev, "Hardware initialized successfully\n");
    return 0;
}

/* Shutdown hardware */
static void pdev_hw_shutdown(struct pdev_data *data)
{
    dev_info(&data->pdev->dev, "Shutting down hardware\n");

    /* Disable interrupts */
    pdev_writel(data, 0, REG_IRQ_ENABLE);

    /* Disable device */
    pdev_writel(data, 0, REG_CONTROL);

    data->enabled = false;
}

/*
 * Interrupt Handler
 */
static irqreturn_t pdev_irq_handler(int irq, void *dev_id)
{
    struct pdev_data *data = dev_id;
    u32 irq_status;

    /* Read interrupt status */
    irq_status = pdev_readl(data, REG_IRQ_STATUS);

    if (!irq_status)
        return IRQ_NONE;  /* Not our interrupt */

    /* Handle interrupt */
    dev_dbg(&data->pdev->dev, "IRQ status: 0x%08X\n", irq_status);

    /* Read data */
    data->data_value = pdev_readl(data, REG_DATA);

    /* Clear interrupt */
    pdev_writel(data, irq_status, REG_IRQ_STATUS);

    return IRQ_HANDLED;
}

/*
 * Character Device Operations
 */

static int pdev_dev_open(struct inode *inode, struct file *file)
{
    struct pdev_data *data = container_of(inode->i_cdev,
                                          struct pdev_data, cdev);
    file->private_data = data;

    /* Enable runtime PM */
    pm_runtime_get_sync(&data->pdev->dev);

    return 0;
}

static int pdev_dev_release(struct inode *inode, struct file *file)
{
    struct pdev_data *data = file->private_data;

    /* Disable runtime PM */
    pm_runtime_put(&data->pdev->dev);

    return 0;
}

static ssize_t pdev_dev_read(struct file *file, char __user *buf,
                            size_t count, loff_t *ppos)
{
    struct pdev_data *data = file->private_data;
    char kbuf[128];
    int len;
    u32 status;

    if (*ppos > 0)
        return 0;

    mutex_lock(&data->lock);

    /* Read device status and data */
    status = pdev_readl(data, REG_STATUS);
    data->data_value = pdev_readl(data, REG_DATA);

    len = snprintf(kbuf, sizeof(kbuf),
                   "Device Status:\n"
                   "  Enabled: %s\n"
                   "  Status: 0x%08X\n"
                   "  Data: 0x%08X (%u)\n"
                   "  Mode: %s\n"
                   "  Max Speed: %u Hz\n",
                   data->enabled ? "Yes" : "No",
                   status,
                   data->data_value, data->data_value,
                   data->mode,
                   data->max_speed);

    mutex_unlock(&data->lock);

    if (count < len)
        len = count;

    if (copy_to_user(buf, kbuf, len))
        return -EFAULT;

    *ppos += len;
    return len;
}

static ssize_t pdev_dev_write(struct file *file, const char __user *buf,
                             size_t count, loff_t *ppos)
{
    struct pdev_data *data = file->private_data;
    char kbuf[32];
    u32 value;

    if (count > sizeof(kbuf) - 1)
        count = sizeof(kbuf) - 1;

    if (copy_from_user(kbuf, buf, count))
        return -EFAULT;

    kbuf[count] = '\0';

    /* Parse value */
    if (kstrtou32(kbuf, 0, &value))
        return -EINVAL;

    mutex_lock(&data->lock);

    /* Write value to device */
    pdev_writel(data, value, REG_DATA);
    dev_info(&data->pdev->dev, "Wrote value: 0x%08X\n", value);

    mutex_unlock(&data->lock);

    return count;
}

static const struct file_operations pdev_fops = {
    .owner = THIS_MODULE,
    .open = pdev_dev_open,
    .release = pdev_dev_release,
    .read = pdev_dev_read,
    .write = pdev_dev_write,
};

/*
 * Sysfs Attributes
 */

static ssize_t enabled_show(struct device *dev,
                           struct device_attribute *attr, char *buf)
{
    struct pdev_data *data = dev_get_drvdata(dev);
    return sprintf(buf, "%d\n", data->enabled);
}

static ssize_t data_show(struct device *dev,
                        struct device_attribute *attr, char *buf)
{
    struct pdev_data *data = dev_get_drvdata(dev);
    u32 value;

    mutex_lock(&data->lock);
    value = pdev_readl(data, REG_DATA);
    mutex_unlock(&data->lock);

    return sprintf(buf, "0x%08X\n", value);
}

static ssize_t status_show(struct device *dev,
                          struct device_attribute *attr, char *buf)
{
    struct pdev_data *data = dev_get_drvdata(dev);
    u32 status;

    mutex_lock(&data->lock);
    status = pdev_readl(data, REG_STATUS);
    mutex_unlock(&data->lock);

    return sprintf(buf, "0x%08X\n", status);
}

static DEVICE_ATTR_RO(enabled);
static DEVICE_ATTR_RO(data);
static DEVICE_ATTR_RO(status);

static struct attribute *pdev_attrs[] = {
    &dev_attr_enabled.attr,
    &dev_attr_data.attr,
    &dev_attr_status.attr,
    NULL,
};
ATTRIBUTE_GROUPS(pdev);

/*
 * Power Management
 */

#ifdef CONFIG_PM
static int pdev_suspend(struct device *dev)
{
    struct pdev_data *data = dev_get_drvdata(dev);

    dev_info(dev, "Suspending device\n");

    mutex_lock(&data->lock);
    if (data->enabled) {
        pdev_hw_shutdown(data);
    }
    mutex_unlock(&data->lock);

    /* Disable clock */
    if (data->clk)
        clk_disable_unprepare(data->clk);

    /* Disable regulator */
    if (data->regulator)
        regulator_disable(data->regulator);

    return 0;
}

static int pdev_resume(struct device *dev)
{
    struct pdev_data *data = dev_get_drvdata(dev);
    int ret;

    dev_info(dev, "Resuming device\n");

    /* Enable regulator */
    if (data->regulator) {
        ret = regulator_enable(data->regulator);
        if (ret) {
            dev_err(dev, "Failed to enable regulator\n");
            return ret;
        }
    }

    /* Enable clock */
    if (data->clk) {
        ret = clk_prepare_enable(data->clk);
        if (ret) {
            dev_err(dev, "Failed to enable clock\n");
            return ret;
        }
    }

    mutex_lock(&data->lock);
    if (data->enabled) {
        ret = pdev_hw_init(data);
        if (ret) {
            mutex_unlock(&data->lock);
            return ret;
        }
    }
    mutex_unlock(&data->lock);

    return 0;
}

static int pdev_runtime_suspend(struct device *dev)
{
    struct pdev_data *data = dev_get_drvdata(dev);

    dev_dbg(dev, "Runtime suspend\n");

    if (data->clk)
        clk_disable_unprepare(data->clk);

    return 0;
}

static int pdev_runtime_resume(struct device *dev)
{
    struct pdev_data *data = dev_get_drvdata(dev);
    int ret;

    dev_dbg(dev, "Runtime resume\n");

    if (data->clk) {
        ret = clk_prepare_enable(data->clk);
        if (ret) {
            dev_err(dev, "Failed to enable clock\n");
            return ret;
        }
    }

    return 0;
}
#endif /* CONFIG_PM */

static const struct dev_pm_ops pdev_pm_ops = {
    SET_SYSTEM_SLEEP_PM_OPS(pdev_suspend, pdev_resume)
    SET_RUNTIME_PM_OPS(pdev_runtime_suspend, pdev_runtime_resume, NULL)
};

/*
 * Parse Device Tree Properties
 */
static int pdev_parse_dt(struct platform_device *pdev, struct pdev_data *data)
{
    struct device_node *np = pdev->dev.of_node;
    int ret;

    if (!np)
        return -ENODEV;

    /* Read max-speed property */
    ret = of_property_read_u32(np, "max-speed", &data->max_speed);
    if (ret) {
        dev_warn(&pdev->dev, "max-speed not specified, using default\n");
        data->max_speed = 1000000;  /* Default 1 MHz */
    }

    /* Read mode property */
    ret = of_property_read_string(np, "mode", &data->mode);
    if (ret) {
        dev_warn(&pdev->dev, "mode not specified, using default\n");
        data->mode = "normal";
    }

    dev_info(&pdev->dev, "DT properties: max-speed=%u, mode=%s\n",
             data->max_speed, data->mode);

    return 0;
}

/*
 * Platform Driver Probe and Remove
 */

static int pdev_probe(struct platform_device *pdev)
{
    struct pdev_data *data;
    int ret;

    dev_info(&pdev->dev, "Probing platform device driver\n");

    /* Allocate driver data */
    data = devm_kzalloc(&pdev->dev, sizeof(struct pdev_data), GFP_KERNEL);
    if (!data)
        return -ENOMEM;

    data->pdev = pdev;
    mutex_init(&data->lock);
    platform_set_drvdata(pdev, data);

    /* Parse device tree */
    ret = pdev_parse_dt(pdev, data);
    if (ret)
        return ret;

    /* Get memory resource */
    data->mem_res = platform_get_resource(pdev, IORESOURCE_MEM, 0);
    if (!data->mem_res) {
        dev_err(&pdev->dev, "Failed to get memory resource\n");
        return -ENODEV;
    }

    /* Map memory */
    data->base = devm_ioremap_resource(&pdev->dev, data->mem_res);
    if (IS_ERR(data->base)) {
        dev_err(&pdev->dev, "Failed to map memory\n");
        return PTR_ERR(data->base);
    }

    dev_info(&pdev->dev, "Memory mapped at 0x%px (phys: 0x%llx)\n",
             data->base, (u64)data->mem_res->start);

    /* Get IRQ */
    data->irq = platform_get_irq(pdev, 0);
    if (data->irq < 0) {
        dev_warn(&pdev->dev, "No IRQ specified\n");
    } else {
        ret = devm_request_irq(&pdev->dev, data->irq, pdev_irq_handler,
                              IRQF_SHARED, DRIVER_NAME, data);
        if (ret) {
            dev_err(&pdev->dev, "Failed to request IRQ %d\n", data->irq);
            return ret;
        }
        dev_info(&pdev->dev, "IRQ %d registered\n", data->irq);
    }

    /* Get clock (optional) */
    data->clk = devm_clk_get(&pdev->dev, NULL);
    if (IS_ERR(data->clk)) {
        dev_warn(&pdev->dev, "Clock not available\n");
        data->clk = NULL;
    } else {
        ret = clk_prepare_enable(data->clk);
        if (ret) {
            dev_err(&pdev->dev, "Failed to enable clock\n");
            return ret;
        }
        dev_info(&pdev->dev, "Clock enabled: %lu Hz\n",
                 clk_get_rate(data->clk));
    }

    /* Get regulator (optional) */
    data->regulator = devm_regulator_get_optional(&pdev->dev, "vdd");
    if (IS_ERR(data->regulator)) {
        dev_warn(&pdev->dev, "Regulator not available\n");
        data->regulator = NULL;
    } else {
        ret = regulator_enable(data->regulator);
        if (ret) {
            dev_err(&pdev->dev, "Failed to enable regulator\n");
            goto err_clk;
        }
        dev_info(&pdev->dev, "Regulator enabled\n");
    }

    /* Initialize hardware */
    ret = pdev_hw_init(data);
    if (ret)
        goto err_regulator;

    /* Allocate character device number */
    ret = alloc_chrdev_region(&data->dev_num, 0, 1, DEVICE_NAME);
    if (ret < 0) {
        dev_err(&pdev->dev, "Failed to allocate device number\n");
        goto err_hw;
    }

    /* Initialize character device */
    cdev_init(&data->cdev, &pdev_fops);
    data->cdev.owner = THIS_MODULE;

    ret = cdev_add(&data->cdev, data->dev_num, 1);
    if (ret) {
        unregister_chrdev_region(data->dev_num, 1);
        goto err_hw;
    }

    /* Create device class */
    data->dev_class = class_create(THIS_MODULE, "pdev_class");
    if (IS_ERR(data->dev_class)) {
        cdev_del(&data->cdev);
        unregister_chrdev_region(data->dev_num, 1);
        ret = PTR_ERR(data->dev_class);
        goto err_hw;
    }

    /* Create device with sysfs attributes */
    data->dev_class->dev_groups = pdev_groups;
    data->device = device_create(data->dev_class, &pdev->dev,
                                  data->dev_num, data, DEVICE_NAME);
    if (IS_ERR(data->device)) {
        class_destroy(data->dev_class);
        cdev_del(&data->cdev);
        unregister_chrdev_region(data->dev_num, 1);
        ret = PTR_ERR(data->device);
        goto err_hw;
    }

    /* Enable runtime PM */
    pm_runtime_enable(&pdev->dev);

    dev_info(&pdev->dev, "Platform device driver probed successfully\n");
    dev_info(&pdev->dev, "Device: /dev/%s\n", DEVICE_NAME);

    return 0;

err_hw:
    pdev_hw_shutdown(data);
err_regulator:
    if (data->regulator)
        regulator_disable(data->regulator);
err_clk:
    if (data->clk)
        clk_disable_unprepare(data->clk);
    return ret;
}

static int pdev_remove(struct platform_device *pdev)
{
    struct pdev_data *data = platform_get_drvdata(pdev);

    dev_info(&pdev->dev, "Removing platform device driver\n");

    /* Disable runtime PM */
    pm_runtime_disable(&pdev->dev);

    /* Remove character device */
    device_destroy(data->dev_class, data->dev_num);
    class_destroy(data->dev_class);
    cdev_del(&data->cdev);
    unregister_chrdev_region(data->dev_num, 1);

    /* Shutdown hardware */
    pdev_hw_shutdown(data);

    /* Disable regulator */
    if (data->regulator)
        regulator_disable(data->regulator);

    /* Disable clock */
    if (data->clk)
        clk_disable_unprepare(data->clk);

    return 0;
}

/* Device tree match table */
static const struct of_device_id pdev_of_match[] = {
    { .compatible = "nvidia,tegra-custom-device" },
    { .compatible = "custom,platform-device" },
    { }
};
MODULE_DEVICE_TABLE(of, pdev_of_match);

/* Platform driver structure */
static struct platform_driver pdev_driver = {
    .driver = {
        .name = DRIVER_NAME,
        .of_match_table = pdev_of_match,
        .pm = &pdev_pm_ops,
    },
    .probe = pdev_probe,
    .remove = pdev_remove,
};

module_platform_driver(pdev_driver);

MODULE_LICENSE("GPL v2");
MODULE_AUTHOR("Meta-Tegra Learning System");
MODULE_DESCRIPTION("Platform Driver Template for NVIDIA Jetson");
MODULE_VERSION("1.0");

/*
 * Device Tree Example:
 * ====================
 *
 * custom_device: custom-device@0x70000000 {
 *     compatible = "nvidia,tegra-custom-device";
 *     reg = <0x0 0x70000000 0x0 0x1000>;
 *     interrupts = <GIC_SPI 100 IRQ_TYPE_LEVEL_HIGH>;
 *     clocks = <&bpmp_clks TEGRA194_CLK_CUSTOM>;
 *     clock-names = "custom";
 *     resets = <&bpmp_resets TEGRA194_RESET_CUSTOM>;
 *     reset-names = "custom";
 *     power-domains = <&bpmp TEGRA194_POWER_DOMAIN_CUSTOM>;
 *     vdd-supply = <&vdd_custom>;
 *     max-speed = <2000000>;
 *     mode = "high-performance";
 *     status = "okay";
 * };
 */
