# Tutorial 06: GPIO Kernel Module Development
## Building Custom GPIO Drivers for Jetson

---

## Learning Objectives

After completing this tutorial, you will be able to:
- Write Linux kernel modules for GPIO control
- Implement interrupt-driven GPIO handlers
- Use modern GPIO consumer interface
- Create character device for userspace access
- Debug kernel modules effectively
- Package kernel modules with Yocto
- Handle module loading and unloading

---

## Prerequisites Checklist

- [ ] Completed Tutorial 01-05 (Yocto through device tree)
- [ ] Understanding of C programming
- [ ] Basic kernel module concepts
- [ ] Jetson device with custom image
- [ ] Kernel headers installed on target
- [ ] Cross-compilation toolchain set up
- [ ] Serial console access for debugging

---

## Estimated Duration

**Total Time**: 5-6 hours
- Kernel module basics: 1 hour
- Simple GPIO module: 1.5 hours
- Interrupt-driven module: 1.5 hours
- Character device interface: 1 hour
- Yocto integration: 1-2 hours

---

## Step-by-Step Instructions

### Step 1: Understand Linux GPIO Subsystem

The Linux kernel provides two GPIO interfaces:

```c
// Legacy interface (deprecated, don't use)
#include <linux/gpio.h>

// Modern consumer interface (use this!)
#include <linux/gpio/consumer.h>
```

**GPIO Consumer Interface Benefits**:
- Handles GPIO ownership and locking
- Integrates with device tree
- Supports GPIO descriptors (safer than numbers)
- Better error handling
- Future-proof

**GPIO workflow**:
```
1. Get GPIO descriptor (from DT or number)
2. Configure direction (input/output)
3. Set/get value
4. Optional: Set up interrupt handler
5. Release GPIO when done
```

### Step 2: Create Simple GPIO Module

Create your first GPIO kernel module:

```bash
# On host development machine
cd ~/yocto-jetson/meta-custom
mkdir -p recipes-kernel/gpio-driver/files

cat > recipes-kernel/gpio-driver/files/simple-gpio.c << 'EOF'
/*
 * simple-gpio.c - Basic GPIO kernel module for Jetson
 * Controls a GPIO pin from kernel space
 */

#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/gpio/consumer.h>
#include <linux/platform_device.h>
#include <linux/of.h>
#include <linux/delay.h>

#define DRIVER_NAME "simple-gpio"

static struct gpio_desc *gpio_led;

static int simple_gpio_probe(struct platform_device *pdev)
{
    int ret;

    dev_info(&pdev->dev, "Probing simple GPIO driver\n");

    /* Get GPIO from device tree */
    gpio_led = devm_gpiod_get(&pdev->dev, "led", GPIOD_OUT_LOW);
    if (IS_ERR(gpio_led)) {
        dev_err(&pdev->dev, "Failed to get LED GPIO: %ld\n",
                PTR_ERR(gpio_led));
        return PTR_ERR(gpio_led);
    }

    /* Set GPIO label */
    gpiod_set_consumer_name(gpio_led, "simple-gpio-led");

    dev_info(&pdev->dev, "GPIO LED acquired successfully\n");

    /* Blink the LED 5 times as a test */
    for (int i = 0; i < 5; i++) {
        gpiod_set_value(gpio_led, 1);  /* LED ON */
        msleep(200);
        gpiod_set_value(gpio_led, 0);  /* LED OFF */
        msleep(200);
    }

    dev_info(&pdev->dev, "Simple GPIO driver probed successfully\n");
    return 0;
}

static int simple_gpio_remove(struct platform_device *pdev)
{
    dev_info(&pdev->dev, "Removing simple GPIO driver\n");

    /* GPIO automatically released by devm_gpiod_get cleanup */

    return 0;
}

/* Device tree match table */
static const struct of_device_id simple_gpio_of_match[] = {
    { .compatible = "custom,simple-gpio" },
    { /* sentinel */ }
};
MODULE_DEVICE_TABLE(of, simple_gpio_of_match);

/* Platform driver structure */
static struct platform_driver simple_gpio_driver = {
    .probe = simple_gpio_probe,
    .remove = simple_gpio_remove,
    .driver = {
        .name = DRIVER_NAME,
        .of_match_table = simple_gpio_of_match,
    },
};

module_platform_driver(simple_gpio_driver);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Your Name");
MODULE_DESCRIPTION("Simple GPIO driver for Jetson");
MODULE_VERSION("1.0");
EOF
```

**Explanation**:
- `devm_gpiod_get()`: Gets GPIO descriptor from device tree, auto-cleanup
- `GPIOD_OUT_LOW`: Configure as output, initial value low
- `gpiod_set_value()`: Set GPIO value (0=low, 1=high)
- `module_platform_driver()`: Registers platform driver with kernel

### Step 3: Create Device Tree Binding

Create DT node for the GPIO module:

```bash
cat > ~/yocto-jetson/meta-custom/recipes-kernel/gpio-driver/files/simple-gpio-overlay.dts << 'EOF'
/dts-v1/;
/plugin/;

#include <dt-bindings/gpio/tegra234-gpio.h>

/ {
    compatible = "nvidia,p3737-0000+p3701-0000", "nvidia,tegra234";

    fragment@0 {
        target-path = "/";
        __overlay__ {
            simple_gpio_device {
                compatible = "custom,simple-gpio";
                status = "okay";

                /* LED GPIO - PQ.05 on 40-pin header pin 31 */
                led-gpios = <&tegra_main_gpio TEGRA234_MAIN_GPIO(Q, 5) GPIO_ACTIVE_HIGH>;
            };
        };
    };
};
EOF
```

### Step 4: Create Makefile for Kernel Module

```bash
cat > ~/yocto-jetson/meta-custom/recipes-kernel/gpio-driver/files/Makefile << 'EOF'
# Makefile for simple-gpio kernel module

obj-m += simple-gpio.o

# Kernel build directory (set by BitBake)
KERNELDIR ?= /lib/modules/$(shell uname -r)/build

# Module build directory
PWD := $(shell pwd)

# Default target
all:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) modules

clean:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) clean

install:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) modules_install

help:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) help

.PHONY: all clean install help
EOF
```

### Step 5: Create BitBake Recipe for Kernel Module

```bash
cat > ~/yocto-jetson/meta-custom/recipes-kernel/gpio-driver/simple-gpio_1.0.bb << 'EOF'
SUMMARY = "Simple GPIO kernel module for Jetson"
DESCRIPTION = "Basic GPIO control driver demonstrating kernel module development"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

inherit module

SRC_URI = "\
    file://simple-gpio.c \
    file://simple-gpio-overlay.dts \
    file://Makefile \
    file://COPYING \
"

S = "${WORKDIR}"

# Kernel module class handles most of the build
# Just need to specify any special flags

EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR}"

# Install device tree overlay as well
do_install:append() {
    install -d ${D}/boot/overlays
    dtc -@ -I dts -O dtb \
        -i ${STAGING_KERNEL_DIR}/include \
        -o ${D}/boot/overlays/simple-gpio.dtbo \
        ${WORKDIR}/simple-gpio-overlay.dts
}

FILES:${PN} += "/boot/overlays/*.dtbo"

# Runtime dependencies
RDEPENDS:${PN} = "kernel-module-gpio-tegra234"

# Only for Jetson platforms
COMPATIBLE_MACHINE = "(tegra)"
EOF

# Create GPL license file
cat > ~/yocto-jetson/meta-custom/recipes-kernel/gpio-driver/files/COPYING << 'EOF'
GPL-2.0-only
See https://spdx.org/licenses/GPL-2.0-only.html
EOF
```

### Step 6: Build and Test Simple GPIO Module

```bash
cd ~/yocto-jetson/builds/jetson-orin-agx

# Build the module
bitbake simple-gpio

# Check output
ls tmp/work/jetson_orin_agx_devkit-poky-linux/simple-gpio/1.0-r0/image/

# Add to image
echo 'IMAGE_INSTALL:append = " simple-gpio"' >> conf/local.conf

# Rebuild image
bitbake core-image-minimal

# Deploy to Jetson
# After flashing or via scp:
scp tmp/work/jetson_orin_agx_devkit-poky-linux/simple-gpio/1.0-r0/image/lib/modules/*/extra/simple-gpio.ko \
    root@jetson-ip:/lib/modules/$(ssh root@jetson-ip uname -r)/extra/
```

**On Jetson device**:
```bash
# Load device tree overlay
cat /boot/overlays/simple-gpio.dtbo > \
    /sys/kernel/config/device-tree/overlays/simple-gpio/dtbo

# Load the module
modprobe simple-gpio

# Check kernel log
dmesg | tail -20

# Expected output:
# simple-gpio: loading out-of-tree module taints kernel.
# simple_gpio_device: Probing simple GPIO driver
# simple_gpio_device: GPIO LED acquired successfully
# simple_gpio_device: Simple GPIO driver probed successfully

# Check module is loaded
lsmod | grep simple_gpio

# Check GPIO status
cat /sys/kernel/debug/gpio | grep simple-gpio

# Unload module
modprobe -r simple-gpio
```

### Step 7: Create Interrupt-Driven GPIO Module

Add interrupt handling for button presses:

```bash
cat > ~/yocto-jetson/meta-custom/recipes-kernel/gpio-driver/files/gpio-button-irq.c << 'EOF'
/*
 * gpio-button-irq.c - Interrupt-driven GPIO button driver
 * Demonstrates GPIO interrupt handling on Jetson
 */

#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/gpio/consumer.h>
#include <linux/interrupt.h>
#include <linux/platform_device.h>
#include <linux/of.h>

#define DRIVER_NAME "gpio-button-irq"

struct gpio_button_data {
    struct gpio_desc *button_gpio;
    struct gpio_desc *led_gpio;
    int irq;
    unsigned long press_count;
    struct device *dev;
};

/* Interrupt handler */
static irqreturn_t button_irq_handler(int irq, void *dev_id)
{
    struct gpio_button_data *data = dev_id;
    int button_state;

    /* Read button state */
    button_state = gpiod_get_value(data->button_gpio);

    if (button_state) {
        /* Button pressed (rising edge) */
        data->press_count++;
        dev_info(data->dev, "Button pressed! Count: %lu\n",
                 data->press_count);

        /* Toggle LED */
        gpiod_set_value(data->led_gpio,
                        !gpiod_get_value(data->led_gpio));
    }

    return IRQ_HANDLED;
}

static int gpio_button_probe(struct platform_device *pdev)
{
    struct gpio_button_data *data;
    int ret;

    dev_info(&pdev->dev, "Probing GPIO button IRQ driver\n");

    /* Allocate driver data */
    data = devm_kzalloc(&pdev->dev, sizeof(*data), GFP_KERNEL);
    if (!data)
        return -ENOMEM;

    data->dev = &pdev->dev;
    platform_set_drvdata(pdev, data);

    /* Get button GPIO (input) */
    data->button_gpio = devm_gpiod_get(&pdev->dev, "button", GPIOD_IN);
    if (IS_ERR(data->button_gpio)) {
        dev_err(&pdev->dev, "Failed to get button GPIO: %ld\n",
                PTR_ERR(data->button_gpio));
        return PTR_ERR(data->button_gpio);
    }

    /* Get LED GPIO (output, initially off) */
    data->led_gpio = devm_gpiod_get(&pdev->dev, "led", GPIOD_OUT_LOW);
    if (IS_ERR(data->led_gpio)) {
        dev_err(&pdev->dev, "Failed to get LED GPIO: %ld\n",
                PTR_ERR(data->led_gpio));
        return PTR_ERR(data->led_gpio);
    }

    /* Get IRQ number from GPIO */
    data->irq = gpiod_to_irq(data->button_gpio);
    if (data->irq < 0) {
        dev_err(&pdev->dev, "Failed to get IRQ: %d\n", data->irq);
        return data->irq;
    }

    /* Request IRQ - trigger on both edges */
    ret = devm_request_irq(&pdev->dev, data->irq,
                           button_irq_handler,
                           IRQF_TRIGGER_RISING | IRQF_TRIGGER_FALLING,
                           DRIVER_NAME, data);
    if (ret) {
        dev_err(&pdev->dev, "Failed to request IRQ %d: %d\n",
                data->irq, ret);
        return ret;
    }

    dev_info(&pdev->dev, "GPIO button IRQ driver initialized\n");
    dev_info(&pdev->dev, "Button on IRQ %d, press to toggle LED\n",
             data->irq);

    return 0;
}

static int gpio_button_remove(struct platform_device *pdev)
{
    struct gpio_button_data *data = platform_get_drvdata(pdev);

    dev_info(&pdev->dev, "Removing GPIO button IRQ driver\n");
    dev_info(&pdev->dev, "Total button presses: %lu\n",
             data->press_count);

    /* Cleanup handled automatically by devm_* functions */

    return 0;
}

static const struct of_device_id gpio_button_of_match[] = {
    { .compatible = "custom,gpio-button-irq" },
    { /* sentinel */ }
};
MODULE_DEVICE_TABLE(of, gpio_button_of_match);

static struct platform_driver gpio_button_driver = {
    .probe = gpio_button_probe,
    .remove = gpio_button_remove,
    .driver = {
        .name = DRIVER_NAME,
        .of_match_table = gpio_button_of_match,
    },
};

module_platform_driver(gpio_button_driver);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Your Name");
MODULE_DESCRIPTION("Interrupt-driven GPIO button driver for Jetson");
MODULE_VERSION("1.0");
EOF
```

**Key concepts**:
- `gpiod_to_irq()`: Converts GPIO descriptor to IRQ number
- `devm_request_irq()`: Registers interrupt handler, auto-cleanup
- `IRQF_TRIGGER_RISING/FALLING`: Interrupt trigger types
- `IRQ_HANDLED`: Return value indicating IRQ was handled

### Step 8: Create Character Device Interface

Allow userspace to control GPIO via /dev/gpioctl:

```bash
cat > ~/yocto-jetson/meta-custom/recipes-kernel/gpio-driver/files/gpio-chardev.c << 'EOF'
/*
 * gpio-chardev.c - GPIO character device driver
 * Provides /dev/gpioctl for userspace GPIO control
 */

#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/fs.h>
#include <linux/cdev.h>
#include <linux/device.h>
#include <linux/gpio/consumer.h>
#include <linux/platform_device.h>
#include <linux/uaccess.h>
#include <linux/of.h>

#define DRIVER_NAME "gpio-chardev"
#define DEVICE_NAME "gpioctl"

/* IOCTL commands */
#define GPIO_IOC_MAGIC 'G'
#define GPIO_IOC_SET _IOW(GPIO_IOC_MAGIC, 1, int)
#define GPIO_IOC_GET _IOR(GPIO_IOC_MAGIC, 2, int)
#define GPIO_IOC_TOGGLE _IO(GPIO_IOC_MAGIC, 3)

struct gpio_chardev_data {
    struct gpio_desc *gpio;
    struct cdev cdev;
    struct class *class;
    dev_t devt;
    struct device *device;
    struct device *parent;
};

static int gpio_chardev_open(struct inode *inode, struct file *filp)
{
    struct gpio_chardev_data *data;

    data = container_of(inode->i_cdev, struct gpio_chardev_data, cdev);
    filp->private_data = data;

    dev_info(data->parent, "Device opened\n");
    return 0;
}

static int gpio_chardev_release(struct inode *inode, struct file *filp)
{
    struct gpio_chardev_data *data = filp->private_data;

    dev_info(data->parent, "Device closed\n");
    return 0;
}

static ssize_t gpio_chardev_read(struct file *filp, char __user *buf,
                                  size_t count, loff_t *f_pos)
{
    struct gpio_chardev_data *data = filp->private_data;
    char value[2];
    int gpio_val;

    if (*f_pos > 0)
        return 0;  /* EOF */

    gpio_val = gpiod_get_value(data->gpio);
    value[0] = gpio_val ? '1' : '0';
    value[1] = '\n';

    if (copy_to_user(buf, value, 2))
        return -EFAULT;

    *f_pos += 2;
    return 2;
}

static ssize_t gpio_chardev_write(struct file *filp, const char __user *buf,
                                   size_t count, loff_t *f_pos)
{
    struct gpio_chardev_data *data = filp->private_data;
    char value;

    if (count < 1)
        return -EINVAL;

    if (copy_from_user(&value, buf, 1))
        return -EFAULT;

    if (value == '0')
        gpiod_set_value(data->gpio, 0);
    else if (value == '1')
        gpiod_set_value(data->gpio, 1);
    else
        return -EINVAL;

    dev_info(data->parent, "GPIO set to %c\n", value);
    return count;
}

static long gpio_chardev_ioctl(struct file *filp, unsigned int cmd,
                                unsigned long arg)
{
    struct gpio_chardev_data *data = filp->private_data;
    int value;

    switch (cmd) {
    case GPIO_IOC_SET:
        if (copy_from_user(&value, (int __user *)arg, sizeof(int)))
            return -EFAULT;
        gpiod_set_value(data->gpio, value ? 1 : 0);
        dev_info(data->parent, "IOCTL: Set GPIO to %d\n", value);
        break;

    case GPIO_IOC_GET:
        value = gpiod_get_value(data->gpio);
        if (copy_to_user((int __user *)arg, &value, sizeof(int)))
            return -EFAULT;
        break;

    case GPIO_IOC_TOGGLE:
        value = gpiod_get_value(data->gpio);
        gpiod_set_value(data->gpio, !value);
        dev_info(data->parent, "IOCTL: Toggled GPIO\n");
        break;

    default:
        return -EINVAL;
    }

    return 0;
}

static const struct file_operations gpio_chardev_fops = {
    .owner = THIS_MODULE,
    .open = gpio_chardev_open,
    .release = gpio_chardev_release,
    .read = gpio_chardev_read,
    .write = gpio_chardev_write,
    .unlocked_ioctl = gpio_chardev_ioctl,
};

static int gpio_chardev_probe(struct platform_device *pdev)
{
    struct gpio_chardev_data *data;
    int ret;

    dev_info(&pdev->dev, "Probing GPIO chardev driver\n");

    data = devm_kzalloc(&pdev->dev, sizeof(*data), GFP_KERNEL);
    if (!data)
        return -ENOMEM;

    data->parent = &pdev->dev;
    platform_set_drvdata(pdev, data);

    /* Get GPIO */
    data->gpio = devm_gpiod_get(&pdev->dev, "control", GPIOD_OUT_LOW);
    if (IS_ERR(data->gpio)) {
        dev_err(&pdev->dev, "Failed to get GPIO: %ld\n",
                PTR_ERR(data->gpio));
        return PTR_ERR(data->gpio);
    }

    /* Allocate character device number */
    ret = alloc_chrdev_region(&data->devt, 0, 1, DEVICE_NAME);
    if (ret < 0) {
        dev_err(&pdev->dev, "Failed to allocate chrdev region\n");
        return ret;
    }

    /* Initialize cdev */
    cdev_init(&data->cdev, &gpio_chardev_fops);
    data->cdev.owner = THIS_MODULE;

    /* Add cdev to system */
    ret = cdev_add(&data->cdev, data->devt, 1);
    if (ret < 0) {
        dev_err(&pdev->dev, "Failed to add cdev\n");
        goto err_cdev;
    }

    /* Create device class */
    data->class = class_create(THIS_MODULE, DEVICE_NAME);
    if (IS_ERR(data->class)) {
        ret = PTR_ERR(data->class);
        dev_err(&pdev->dev, "Failed to create class\n");
        goto err_class;
    }

    /* Create device node */
    data->device = device_create(data->class, NULL, data->devt,
                                  NULL, DEVICE_NAME);
    if (IS_ERR(data->device)) {
        ret = PTR_ERR(data->device);
        dev_err(&pdev->dev, "Failed to create device\n");
        goto err_device;
    }

    dev_info(&pdev->dev, "Character device created: /dev/%s\n",
             DEVICE_NAME);
    dev_info(&pdev->dev, "Major: %d, Minor: %d\n",
             MAJOR(data->devt), MINOR(data->devt));

    return 0;

err_device:
    class_destroy(data->class);
err_class:
    cdev_del(&data->cdev);
err_cdev:
    unregister_chrdev_region(data->devt, 1);
    return ret;
}

static int gpio_chardev_remove(struct platform_device *pdev)
{
    struct gpio_chardev_data *data = platform_get_drvdata(pdev);

    dev_info(&pdev->dev, "Removing GPIO chardev driver\n");

    device_destroy(data->class, data->devt);
    class_destroy(data->class);
    cdev_del(&data->cdev);
    unregister_chrdev_region(data->devt, 1);

    return 0;
}

static const struct of_device_id gpio_chardev_of_match[] = {
    { .compatible = "custom,gpio-chardev" },
    { /* sentinel */ }
};
MODULE_DEVICE_TABLE(of, gpio_chardev_of_match);

static struct platform_driver gpio_chardev_driver = {
    .probe = gpio_chardev_probe,
    .remove = gpio_chardev_remove,
    .driver = {
        .name = DRIVER_NAME,
        .of_match_table = gpio_chardev_of_match,
    },
};

module_platform_driver(gpio_chardev_driver);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Your Name");
MODULE_DESCRIPTION("GPIO character device driver for Jetson");
MODULE_VERSION("1.0");
EOF
```

### Step 9: Create Userspace Test Application

Test the character device from userspace:

```bash
cat > ~/yocto-jetson/meta-custom/recipes-kernel/gpio-driver/files/gpio-test.c << 'EOF'
/*
 * gpio-test.c - Userspace test program for gpio-chardev
 */

#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <string.h>
#include <errno.h>

#define GPIO_IOC_MAGIC 'G'
#define GPIO_IOC_SET _IOW(GPIO_IOC_MAGIC, 1, int)
#define GPIO_IOC_GET _IOR(GPIO_IOC_MAGIC, 2, int)
#define GPIO_IOC_TOGGLE _IO(GPIO_IOC_MAGIC, 3)

int main(int argc, char *argv[])
{
    int fd;
    int value;
    char buf[10];

    if (argc < 2) {
        printf("Usage:\n");
        printf("  %s read          - Read GPIO value\n", argv[0]);
        printf("  %s write <0|1>   - Write GPIO value\n", argv[0]);
        printf("  %s toggle        - Toggle GPIO\n", argv[0]);
        printf("  %s blink <n>     - Blink n times\n", argv[0]);
        return 1;
    }

    fd = open("/dev/gpioctl", O_RDWR);
    if (fd < 0) {
        perror("Failed to open /dev/gpioctl");
        return 1;
    }

    if (strcmp(argv[1], "read") == 0) {
        /* Read using file operations */
        if (read(fd, buf, sizeof(buf)) < 0) {
            perror("Read failed");
            goto err;
        }
        printf("GPIO value: %c\n", buf[0]);

        /* Read using ioctl */
        if (ioctl(fd, GPIO_IOC_GET, &value) < 0) {
            perror("IOCTL GET failed");
            goto err;
        }
        printf("GPIO value (ioctl): %d\n", value);

    } else if (strcmp(argv[1], "write") == 0) {
        if (argc < 3) {
            printf("Please specify value (0 or 1)\n");
            goto err;
        }

        value = atoi(argv[2]);

        /* Write using file operations */
        buf[0] = value ? '1' : '0';
        if (write(fd, buf, 1) < 0) {
            perror("Write failed");
            goto err;
        }
        printf("GPIO set to %d\n", value);

    } else if (strcmp(argv[1], "toggle") == 0) {
        if (ioctl(fd, GPIO_IOC_TOGGLE) < 0) {
            perror("IOCTL TOGGLE failed");
            goto err;
        }
        printf("GPIO toggled\n");

    } else if (strcmp(argv[1], "blink") == 0) {
        int count = argc > 2 ? atoi(argv[2]) : 5;

        printf("Blinking %d times...\n", count);
        for (int i = 0; i < count; i++) {
            value = 1;
            ioctl(fd, GPIO_IOC_SET, &value);
            usleep(200000);  /* 200ms */

            value = 0;
            ioctl(fd, GPIO_IOC_SET, &value);
            usleep(200000);
        }
        printf("Done\n");

    } else {
        printf("Unknown command: %s\n", argv[1]);
        goto err;
    }

    close(fd);
    return 0;

err:
    close(fd);
    return 1;
}
EOF
```

### Step 10: Package Everything with Yocto

Update the recipe to include all components:

```bash
cat > ~/yocto-jetson/meta-custom/recipes-kernel/gpio-driver/gpio-modules_1.0.bb << 'EOF'
SUMMARY = "Collection of GPIO kernel modules and test tools"
DESCRIPTION = "GPIO drivers and utilities for Jetson development"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

inherit module

SRC_URI = "\
    file://simple-gpio.c \
    file://gpio-button-irq.c \
    file://gpio-chardev.c \
    file://gpio-test.c \
    file://Makefile \
    file://COPYING \
"

S = "${WORKDIR}"

# Build all modules
do_compile() {
    # Kernel modules
    oe_runmake KERNELDIR=${STAGING_KERNEL_DIR} \
               'M=${S}' \
               'CC=${KERNEL_CC}' \
               'LD=${KERNEL_LD}'

    # Userspace test tool
    ${CC} ${CFLAGS} ${LDFLAGS} -o gpio-test gpio-test.c
}

do_install:append() {
    # Install userspace tool
    install -d ${D}${bindir}
    install -m 0755 gpio-test ${D}${bindir}/
}

FILES:${PN} += "${bindir}/gpio-test"

RDEPENDS:${PN} = "kernel-module-gpio-tegra234"
COMPATIBLE_MACHINE = "(tegra)"
EOF

# Update Makefile to build all modules
cat > ~/yocto-jetson/meta-custom/recipes-kernel/gpio-driver/files/Makefile << 'EOF'
# Makefile for GPIO kernel modules

obj-m += simple-gpio.o
obj-m += gpio-button-irq.o
obj-m += gpio-chardev.o

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
```

---

## Troubleshooting Common Issues

### Issue 1: Module Fails to Load

**Symptoms**:
```
insmod: ERROR: could not insert module: Invalid module format
```

**Solutions**:
```bash
# Check kernel version mismatch
modinfo simple-gpio.ko | grep vermagic
uname -r

# Ensure module was built against correct kernel
# Rebuild with matching kernel headers

# Check for missing symbols
dmesg | tail -20

# Common fix: rebuild module with correct KERNELDIR
make clean
make KERNELDIR=/lib/modules/$(uname -r)/build
```

### Issue 2: GPIO Request Fails

**Symptoms**:
```
gpio-chardev: Failed to get GPIO: -16
```

**Solutions**:
```bash
# Error -16 is -EBUSY (GPIO already in use)

# Check who's using the GPIO
cat /sys/kernel/debug/gpio | grep -B2 -A2 "gpio-448"

# Free GPIO if needed
echo 448 > /sys/class/gpio/unexport

# Check device tree overlay is loaded
ls /proc/device-tree/ | grep gpio

# Verify DT compatible string matches
cat /proc/device-tree/simple_gpio_device/compatible
```

### Issue 3: Interrupt Not Triggering

**Symptoms**: Button presses don't generate interrupts

**Solutions**:
```bash
# Check IRQ number
cat /proc/interrupts | grep gpio-button

# Verify GPIO is configured as input
cat /sys/kernel/debug/gpio | grep button

# Check interrupt stats
watch -n1 'cat /proc/interrupts | grep gpio-button'

# Enable GPIO interrupt debugging
echo 8 > /proc/sys/kernel/printk  # Enable debug messages
dmesg -w  # Watch for interrupt messages

# Hardware check: verify button is connected correctly
# Use multimeter to test button functionality
```

### Issue 4: Permission Denied on /dev/gpioctl

**Symptoms**: Normal user can't access character device

**Solutions**:
```bash
# Check device permissions
ls -l /dev/gpioctl

# Change permissions (temporary)
sudo chmod 666 /dev/gpioctl

# Permanent fix: create udev rule
cat > /etc/udev/rules.d/99-gpio.rules << 'EOF'
KERNEL=="gpioctl", MODE="0666"
EOF

sudo udevadm control --reload-rules
sudo udevadm trigger
```

### Issue 5: Kernel Oops or Crash

**Symptoms**: System crashes when loading/using module

**Solutions**:
```bash
# Check kernel log for oops
dmesg | tail -100

# Common causes:
# 1. NULL pointer dereference
# 2. Invalid memory access
# 3. Race condition

# Debug with printk
# Add to code:
pr_info("Debug: variable = %d\n", variable);

# Enable kernel debugging
echo 1 > /proc/sys/kernel/panic_on_oops

# Use KASAN if available (kernel address sanitizer)
# Requires kernel built with CONFIG_KASAN=y
```

---

## Verification Checklist

- [ ] Simple GPIO module compiles successfully
- [ ] Module loads without errors
- [ ] GPIO LED blinks during probe
- [ ] Can see module in lsmod output
- [ ] GPIO appears in /sys/kernel/debug/gpio
- [ ] Button IRQ module handles interrupts
- [ ] Interrupt count increases on button press
- [ ] Character device created at /dev/gpioctl
- [ ] Can read/write GPIO via character device
- [ ] IOCTL commands work correctly
- [ ] Userspace test program runs successfully
- [ ] Module unloads cleanly
- [ ] No kernel oops or warnings in dmesg

---

## Best Practices for Kernel Module Development

### 1. Always Use devm_* Functions

```c
/* Good - automatic cleanup */
gpio = devm_gpiod_get(dev, "led", GPIOD_OUT_LOW);
irq = devm_request_irq(dev, irq_num, handler, flags, name, data);

/* Bad - manual cleanup required */
gpio = gpiod_get(dev, "led", GPIOD_OUT_LOW);
// Must call gpiod_put() in remove()
```

### 2. Proper Error Handling

```c
gpio = devm_gpiod_get(dev, "led", GPIOD_OUT_LOW);
if (IS_ERR(gpio)) {
    ret = PTR_ERR(gpio);
    dev_err(dev, "Failed to get GPIO: %d\n", ret);
    return ret;
}
```

### 3. Use dev_* Logging Functions

```c
/* Good - associates messages with device */
dev_info(dev, "Driver loaded\n");
dev_err(dev, "Error: %d\n", ret);

/* Less ideal - no device context */
printk(KERN_INFO "Driver loaded\n");
pr_info("Driver loaded\n");
```

### 4. Module Parameters for Flexibility

```c
static int gpio_number = 448;
module_param(gpio_number, int, 0644);
MODULE_PARM_DESC(gpio_number, "GPIO number to control");

// Usage: insmod mymodule.ko gpio_number=449
```

### 5. Protect Against Concurrent Access

```c
struct my_data {
    struct mutex lock;
    int shared_variable;
};

// Initialize
mutex_init(&data->lock);

// Use
mutex_lock(&data->lock);
data->shared_variable++;
mutex_unlock(&data->lock);
```

---

## Next Steps

### Immediate Practice
1. Modify gpio-chardev to support multiple GPIOs
2. Add sysfs attributes for runtime configuration
3. Implement PWM control via GPIO

### Proceed to Next Tutorial
**Tutorial 07: I2C Sensor Integration** - Communicate with I2C devices

### Advanced Topics
- Kernel timers for GPIO patterns
- DMA with GPIO (high-speed capture)
- GPIO with RT_PREEMPT kernel
- Kernel tracing and debugging

---

**Congratulations!** You can now write, build, and deploy custom GPIO kernel modules for Jetson platforms. This foundational knowledge applies to all Linux kernel driver development.

---

*Tutorial created by the Yocto & Meta-Tegra Multi-Agent Learning System*
*Last updated: 2025-01-15*
