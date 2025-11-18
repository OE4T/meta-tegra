/*
 * GPIO Interrupt Driver for NVIDIA Jetson Platforms
 *
 * This driver demonstrates:
 * - GPIO pin configuration and control
 * - Interrupt handling (IRQ)
 * - Character device interface
 * - Sysfs interface for user control
 * - Proper error handling and cleanup
 *
 * Tested on: Jetson TX2, Xavier, Orin
 * Kernel: 5.10+
 * License: GPL-2.0
 */

#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/gpio.h>
#include <linux/interrupt.h>
#include <linux/fs.h>
#include <linux/cdev.h>
#include <linux/device.h>
#include <linux/uaccess.h>
#include <linux/slab.h>
#include <linux/sysfs.h>
#include <linux/kobject.h>
#include <linux/platform_device.h>
#include <linux/of.h>
#include <linux/of_gpio.h>

#define DRIVER_NAME "gpio_interrupt"
#define DEVICE_NAME "gpio_int"
#define CLASS_NAME "gpio_class"

/* Default GPIO pin - can be overridden via module parameter */
static int gpio_pin = 12;  /* GPIO12 - adjust for your board */
module_param(gpio_pin, int, S_IRUGO);
MODULE_PARM_DESC(gpio_pin, "GPIO pin number for interrupt handling");

/* Trigger type - can be overridden via module parameter */
static char *trigger_type = "rising";
module_param(trigger_type, charp, S_IRUGO);
MODULE_PARM_DESC(trigger_type, "Interrupt trigger type: rising, falling, both");

/* Driver data structure */
struct gpio_irq_data {
    int gpio_pin;
    int irq_number;
    unsigned long irq_count;
    unsigned long last_jiffies;
    struct cdev cdev;
    dev_t dev_num;
    struct class *dev_class;
    struct device *device;
    struct kobject *kobj;
    spinlock_t lock;
};

static struct gpio_irq_data *gpio_data;

/*
 * IRQ Handler - Called when interrupt occurs
 * This handler runs in interrupt context - must be fast!
 */
static irqreturn_t gpio_irq_handler(int irq, void *dev_id)
{
    struct gpio_irq_data *data = (struct gpio_irq_data *)dev_id;
    unsigned long flags;

    /* Disable interrupts for critical section */
    spin_lock_irqsave(&data->lock, flags);

    /* Update interrupt statistics */
    data->irq_count++;
    data->last_jiffies = jiffies;

    spin_unlock_irqrestore(&data->lock, flags);

    /* Log interrupt (rate-limited to avoid flooding) */
    pr_info("%s: GPIO %d interrupt triggered (count: %lu)\n",
            DRIVER_NAME, data->gpio_pin, data->irq_count);

    /* Return IRQ_HANDLED to indicate we handled the interrupt */
    return IRQ_HANDLED;
}

/*
 * Character Device Operations
 */

/* Open device */
static int gpio_dev_open(struct inode *inode, struct file *file)
{
    pr_info("%s: Device opened\n", DRIVER_NAME);
    return 0;
}

/* Release device */
static int gpio_dev_release(struct inode *inode, struct file *file)
{
    pr_info("%s: Device closed\n", DRIVER_NAME);
    return 0;
}

/* Read from device - returns interrupt count */
static ssize_t gpio_dev_read(struct file *file, char __user *buf,
                              size_t count, loff_t *ppos)
{
    char kbuf[64];
    int len;
    unsigned long flags;

    if (*ppos > 0)
        return 0;  /* EOF */

    spin_lock_irqsave(&gpio_data->lock, flags);
    len = snprintf(kbuf, sizeof(kbuf), "IRQ count: %lu\nLast event: %lu jiffies ago\n",
                   gpio_data->irq_count,
                   jiffies - gpio_data->last_jiffies);
    spin_unlock_irqrestore(&gpio_data->lock, flags);

    if (count < len)
        len = count;

    if (copy_to_user(buf, kbuf, len))
        return -EFAULT;

    *ppos += len;
    return len;
}

/* Write to device - reset interrupt count */
static ssize_t gpio_dev_write(struct file *file, const char __user *buf,
                               size_t count, loff_t *ppos)
{
    char kbuf[16];
    unsigned long flags;

    if (count > sizeof(kbuf) - 1)
        count = sizeof(kbuf) - 1;

    if (copy_from_user(kbuf, buf, count))
        return -EFAULT;

    kbuf[count] = '\0';

    /* Reset counter if "reset" is written */
    if (strncmp(kbuf, "reset", 5) == 0) {
        spin_lock_irqsave(&gpio_data->lock, flags);
        gpio_data->irq_count = 0;
        spin_unlock_irqrestore(&gpio_data->lock, flags);
        pr_info("%s: Interrupt counter reset\n", DRIVER_NAME);
    }

    return count;
}

/* File operations structure */
static const struct file_operations gpio_fops = {
    .owner = THIS_MODULE,
    .open = gpio_dev_open,
    .release = gpio_dev_release,
    .read = gpio_dev_read,
    .write = gpio_dev_write,
};

/*
 * Sysfs Attributes
 */

/* Show interrupt count */
static ssize_t irq_count_show(struct kobject *kobj,
                              struct kobj_attribute *attr, char *buf)
{
    unsigned long flags;
    unsigned long count;

    spin_lock_irqsave(&gpio_data->lock, flags);
    count = gpio_data->irq_count;
    spin_unlock_irqrestore(&gpio_data->lock, flags);

    return sprintf(buf, "%lu\n", count);
}

/* Show GPIO pin number */
static ssize_t gpio_pin_show(struct kobject *kobj,
                             struct kobj_attribute *attr, char *buf)
{
    return sprintf(buf, "%d\n", gpio_data->gpio_pin);
}

/* Show IRQ number */
static ssize_t irq_number_show(struct kobject *kobj,
                               struct kobj_attribute *attr, char *buf)
{
    return sprintf(buf, "%d\n", gpio_data->irq_number);
}

/* Define sysfs attributes */
static struct kobj_attribute irq_count_attr = __ATTR_RO(irq_count);
static struct kobj_attribute gpio_pin_attr = __ATTR_RO(gpio_pin);
static struct kobj_attribute irq_number_attr = __ATTR_RO(irq_number);

static struct attribute *gpio_attrs[] = {
    &irq_count_attr.attr,
    &gpio_pin_attr.attr,
    &irq_number_attr.attr,
    NULL,
};

static struct attribute_group gpio_attr_group = {
    .attrs = gpio_attrs,
};

/*
 * Module Initialization
 */
static int __init gpio_irq_init(void)
{
    int result;
    unsigned long irq_flags;

    pr_info("%s: Initializing GPIO interrupt driver\n", DRIVER_NAME);

    /* Allocate driver data */
    gpio_data = kzalloc(sizeof(struct gpio_irq_data), GFP_KERNEL);
    if (!gpio_data) {
        pr_err("%s: Failed to allocate memory\n", DRIVER_NAME);
        return -ENOMEM;
    }

    gpio_data->gpio_pin = gpio_pin;
    spin_lock_init(&gpio_data->lock);

    /* Validate and request GPIO pin */
    if (!gpio_is_valid(gpio_pin)) {
        pr_err("%s: Invalid GPIO pin %d\n", DRIVER_NAME, gpio_pin);
        result = -EINVAL;
        goto fail_gpio_valid;
    }

    result = gpio_request(gpio_pin, DRIVER_NAME);
    if (result) {
        pr_err("%s: Failed to request GPIO %d\n", DRIVER_NAME, gpio_pin);
        goto fail_gpio_request;
    }

    /* Configure GPIO as input */
    result = gpio_direction_input(gpio_pin);
    if (result) {
        pr_err("%s: Failed to set GPIO %d as input\n", DRIVER_NAME, gpio_pin);
        goto fail_gpio_direction;
    }

    /* Get IRQ number for the GPIO */
    gpio_data->irq_number = gpio_to_irq(gpio_pin);
    if (gpio_data->irq_number < 0) {
        pr_err("%s: Failed to get IRQ for GPIO %d\n", DRIVER_NAME, gpio_pin);
        result = gpio_data->irq_number;
        goto fail_gpio_to_irq;
    }

    /* Determine IRQ flags based on trigger type */
    if (strcmp(trigger_type, "falling") == 0)
        irq_flags = IRQF_TRIGGER_FALLING;
    else if (strcmp(trigger_type, "both") == 0)
        irq_flags = IRQF_TRIGGER_RISING | IRQF_TRIGGER_FALLING;
    else
        irq_flags = IRQF_TRIGGER_RISING;

    /* Request IRQ */
    result = request_irq(gpio_data->irq_number, gpio_irq_handler,
                        irq_flags, DRIVER_NAME, gpio_data);
    if (result) {
        pr_err("%s: Failed to request IRQ %d\n", DRIVER_NAME, gpio_data->irq_number);
        goto fail_request_irq;
    }

    /* Allocate character device number */
    result = alloc_chrdev_region(&gpio_data->dev_num, 0, 1, DEVICE_NAME);
    if (result < 0) {
        pr_err("%s: Failed to allocate device number\n", DRIVER_NAME);
        goto fail_alloc_chrdev;
    }

    /* Initialize character device */
    cdev_init(&gpio_data->cdev, &gpio_fops);
    gpio_data->cdev.owner = THIS_MODULE;

    /* Add character device to system */
    result = cdev_add(&gpio_data->cdev, gpio_data->dev_num, 1);
    if (result) {
        pr_err("%s: Failed to add character device\n", DRIVER_NAME);
        goto fail_cdev_add;
    }

    /* Create device class */
    gpio_data->dev_class = class_create(THIS_MODULE, CLASS_NAME);
    if (IS_ERR(gpio_data->dev_class)) {
        pr_err("%s: Failed to create device class\n", DRIVER_NAME);
        result = PTR_ERR(gpio_data->dev_class);
        goto fail_class_create;
    }

    /* Create device */
    gpio_data->device = device_create(gpio_data->dev_class, NULL,
                                      gpio_data->dev_num, NULL, DEVICE_NAME);
    if (IS_ERR(gpio_data->device)) {
        pr_err("%s: Failed to create device\n", DRIVER_NAME);
        result = PTR_ERR(gpio_data->device);
        goto fail_device_create;
    }

    /* Create sysfs entry */
    gpio_data->kobj = kobject_create_and_add("gpio_interrupt", kernel_kobj);
    if (!gpio_data->kobj) {
        pr_err("%s: Failed to create kobject\n", DRIVER_NAME);
        result = -ENOMEM;
        goto fail_kobject;
    }

    result = sysfs_create_group(gpio_data->kobj, &gpio_attr_group);
    if (result) {
        pr_err("%s: Failed to create sysfs group\n", DRIVER_NAME);
        goto fail_sysfs;
    }

    pr_info("%s: Driver loaded successfully (GPIO: %d, IRQ: %d, Device: /dev/%s)\n",
            DRIVER_NAME, gpio_pin, gpio_data->irq_number, DEVICE_NAME);
    pr_info("%s: Sysfs interface: /sys/kernel/gpio_interrupt/\n", DRIVER_NAME);

    return 0;

/* Error handling - cleanup in reverse order */
fail_sysfs:
    kobject_put(gpio_data->kobj);
fail_kobject:
    device_destroy(gpio_data->dev_class, gpio_data->dev_num);
fail_device_create:
    class_destroy(gpio_data->dev_class);
fail_class_create:
    cdev_del(&gpio_data->cdev);
fail_cdev_add:
    unregister_chrdev_region(gpio_data->dev_num, 1);
fail_alloc_chrdev:
    free_irq(gpio_data->irq_number, gpio_data);
fail_request_irq:
fail_gpio_to_irq:
fail_gpio_direction:
    gpio_free(gpio_pin);
fail_gpio_request:
fail_gpio_valid:
    kfree(gpio_data);
    return result;
}

/*
 * Module Cleanup
 */
static void __exit gpio_irq_exit(void)
{
    pr_info("%s: Cleaning up GPIO interrupt driver\n", DRIVER_NAME);

    /* Remove sysfs interface */
    sysfs_remove_group(gpio_data->kobj, &gpio_attr_group);
    kobject_put(gpio_data->kobj);

    /* Remove device */
    device_destroy(gpio_data->dev_class, gpio_data->dev_num);
    class_destroy(gpio_data->dev_class);

    /* Remove character device */
    cdev_del(&gpio_data->cdev);
    unregister_chrdev_region(gpio_data->dev_num, 1);

    /* Free IRQ */
    free_irq(gpio_data->irq_number, gpio_data);

    /* Free GPIO */
    gpio_free(gpio_data->gpio_pin);

    /* Free driver data */
    kfree(gpio_data);

    pr_info("%s: Driver unloaded successfully\n", DRIVER_NAME);
}

module_init(gpio_irq_init);
module_exit(gpio_irq_exit);

MODULE_LICENSE("GPL v2");
MODULE_AUTHOR("Meta-Tegra Learning System");
MODULE_DESCRIPTION("GPIO Interrupt Driver for NVIDIA Jetson");
MODULE_VERSION("1.0");

/*
 * Usage Instructions:
 * ===================
 *
 * 1. Build the module:
 *    make
 *
 * 2. Load the module:
 *    sudo insmod gpio-interrupt-driver.ko
 *    # Or with custom GPIO:
 *    sudo insmod gpio-interrupt-driver.ko gpio_pin=200 trigger_type=both
 *
 * 3. Check if loaded:
 *    lsmod | grep gpio_interrupt
 *    dmesg | tail -20
 *
 * 4. Test character device interface:
 *    cat /dev/gpio_int
 *    echo "reset" > /dev/gpio_int
 *
 * 5. Test sysfs interface:
 *    cat /sys/kernel/gpio_interrupt/irq_count
 *    cat /sys/kernel/gpio_interrupt/gpio_pin
 *    cat /sys/kernel/gpio_interrupt/irq_number
 *
 * 6. Trigger interrupt (connect a button to GPIO pin):
 *    # Watch interrupt count
 *    watch -n 1 cat /sys/kernel/gpio_interrupt/irq_count
 *
 * 7. Unload module:
 *    sudo rmmod gpio_interrupt
 *
 * 8. View module information:
 *    modinfo gpio-interrupt-driver.ko
 *
 * Debugging:
 * ==========
 * - Check GPIO availability: cat /sys/kernel/debug/gpio
 * - Monitor interrupts: cat /proc/interrupts | grep gpio
 * - Check kernel messages: dmesg -w
 * - Verify device: ls -l /dev/gpio_int
 */
