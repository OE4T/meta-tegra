# Linux Kernel Development for Tegra Platforms

## Kernel Configuration in Yocto

### Kernel Recipe Structure

```bitbake
# meta-tegra/recipes-kernel/linux/linux-tegra_5.10.bb

LINUX_VERSION ?= "5.10.104"
PV = "${LINUX_VERSION}+git${SRCPV}"

KERNEL_BRANCH = "oe4t-patches-l4t-r35.3.1"

SRC_URI = "git://github.com/OE4T/linux-tegra-5.10.git;branch=${KERNEL_BRANCH} \
           file://defconfig \
          "

SRCREV = "abc123..."

S = "${WORKDIR}/git"

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

COMPATIBLE_MACHINE = "tegra"
```

### Kernel Configuration Methods

#### 1. Using defconfig

```bitbake
# In recipe
SRC_URI += "file://defconfig"

# Generate defconfig from running kernel
# On target:
zcat /proc/config.gz > running.config

# Or from build:
bitbake -c menuconfig virtual/kernel
# Then save and copy
cp tmp/work/.../linux-tegra/.config meta-layer/recipes-kernel/linux/files/defconfig
```

#### 2. Using Kernel Fragments

```bitbake
# In recipe
SRC_URI += "file://enable-features.cfg \
            file://disable-features.cfg \
           "

# enable-features.cfg
CONFIG_FEATURE=y
CONFIG_MODULE=m
# CONFIG_UNWANTED is not set

# In bbappend
KERNEL_FEATURES:append = " features/debug.scc"
```

#### 3. Using menuconfig

```bash
# Interactive configuration
bitbake -c menuconfig virtual/kernel

# Save changes
bitbake -c savedefconfig virtual/kernel

# Deploy changes
bitbake -c deploy virtual/kernel
```

### Common Kernel Configuration Options

```kconfig
# Tegra-specific
CONFIG_ARCH_TEGRA=y
CONFIG_TEGRA_HOST1X=y
CONFIG_TEGRA_NVMAP=y
CONFIG_TEGRA_XUDC=y

# GPU
CONFIG_TEGRA_GRHOST=y
CONFIG_TEGRA_GRHOST_NVDEC=y
CONFIG_TEGRA_GRHOST_VIC=y

# Display
CONFIG_DRM_TEGRA=y
CONFIG_TEGRA_DC=y

# Camera
CONFIG_VIDEO_TEGRA_VI=y
CONFIG_VIDEO_TEGRA_VI_TPG=y

# Networking
CONFIG_NVETHERNET=y

# Power management
CONFIG_TEGRA_BPMP=y
CONFIG_TEGRA_BPMP_THERMAL=y

# USB
CONFIG_USB_TEGRA_XUDC=y
CONFIG_PHY_TEGRA_XUSB=y

# PCI Express
CONFIG_PCIE_TEGRA194=y  # Xavier
CONFIG_PCIE_TEGRA234=y  # Orin

# RTC
CONFIG_RTC_DRV_TEGRA=y
```

## Device Tree Development

### Device Tree Structure

```dts
// tegra234-p3767-0000.dtsi (Orin NX module)
#include "tegra234.dtsi"

/ {
    model = "NVIDIA Jetson Orin NX";
    compatible = "nvidia,p3767-0000", "nvidia,tegra234";

    memory@80000000 {
        device_type = "memory";
        reg = <0x0 0x80000000 0x0 0x80000000>;  // 2GB
    };

    reserved-memory {
        #address-cells = <2>;
        #size-cells = <2>;
        ranges;

        linux,cma {
            compatible = "shared-dma-pool";
            reusable;
            size = <0x0 0x10000000>;  // 256MB
            linux,cma-default;
        };
    };
};

// GPIO example
&gpio {
    status = "okay";

    camera-control-output {
        gpio-hog;
        output-high;
        gpios = <CAM1_PWDN 0>;
        label = "cam1-pwdn";
    };
};

// I2C device
&i2c1 {
    status = "okay";
    clock-frequency = <400000>;

    camera_sensor@1a {
        compatible = "vendor,sensor";
        reg = <0x1a>;

        clocks = <&cam_clk>;
        clock-names = "mclk";

        reset-gpios = <&gpio CAM_RESET GPIO_ACTIVE_LOW>;

        port {
            sensor_out: endpoint {
                remote-endpoint = <&csi_in>;
                clock-lanes = <0>;
                data-lanes = <1 2>;
            };
        };
    };
};

// CSI configuration
&nvcsi {
    num-channels = <1>;

    channel@0 {
        reg = <0>;
        ports {
            port@0 {
                reg = <0>;
                csi_in: endpoint {
                    remote-endpoint = <&sensor_out>;
                };
            };
            port@1 {
                reg = <1>;
                csi_out: endpoint {
                    remote-endpoint = <&vi_in>;
                };
            };
        };
    };
};
```

### Device Tree Bindings Documentation

```yaml
# Documentation/devicetree/bindings/vendor,device.yaml
%YAML 1.2
---
$id: http://devicetree.org/schemas/vendor,device.yaml#
$schema: http://devicetree.org/meta-schemas/core.yaml#

title: Vendor Device Binding

maintainers:
  - Developer Name <email@example.com>

description: |
  Description of the device and its capabilities.

properties:
  compatible:
    enum:
      - vendor,device-v1
      - vendor,device-v2

  reg:
    maxItems: 1

  clocks:
    maxItems: 1

  clock-names:
    items:
      - const: mclk

  reset-gpios:
    maxItems: 1
    description: GPIO for hardware reset

  power-domains:
    maxItems: 1

required:
  - compatible
  - reg
  - clocks

additionalProperties: false

examples:
  - |
    i2c {
        #address-cells = <1>;
        #size-cells = <0>;

        device@10 {
            compatible = "vendor,device-v1";
            reg = <0x10>;
            clocks = <&clock_controller 0>;
            clock-names = "mclk";
        };
    };
```

### Device Tree Overlays

```dts
// Custom overlay
/dts-v1/;
/plugin/;

/ {
    overlay-name = "Custom Hardware";
    compatible = "nvidia,p3767-0000";

    fragment@0 {
        target-path = "/";
        __overlay__ {
            custom-device {
                compatible = "vendor,custom";
                status = "okay";
            };
        };
    };

    fragment@1 {
        target = <&i2c1>;
        __overlay__ {
            #address-cells = <1>;
            #size-cells = <0>;

            custom@20 {
                compatible = "vendor,sensor";
                reg = <0x20>;
            };
        };
    };
};
```

### Compiling Device Trees in Yocto

```bitbake
# In machine configuration or recipe
KERNEL_DEVICETREE = "nvidia/tegra234-p3767-0000.dtb \
                     nvidia/tegra234-p3767-custom-overlay.dtbo \
                    "

# In kernel bbappend
SRC_URI += "file://custom-board.dts"

do_compile:append() {
    oe_runmake dtbs
}

do_deploy:append() {
    install -m 0644 ${B}/arch/arm64/boot/dts/custom-board.dtb \
        ${DEPLOYDIR}/custom-board.dtb
}
```

## Driver Development Patterns

### Platform Driver Template

```c
// drivers/custom/custom-driver.c
#include <linux/module.h>
#include <linux/platform_device.h>
#include <linux/of.h>
#include <linux/clk.h>
#include <linux/gpio/consumer.h>

struct custom_device {
    struct device *dev;
    void __iomem *regs;
    struct clk *clk;
    struct gpio_desc *reset_gpio;
};

static int custom_probe(struct platform_device *pdev)
{
    struct custom_device *cdev;
    struct resource *res;
    int ret;

    dev_info(&pdev->dev, "Probing custom device\n");

    cdev = devm_kzalloc(&pdev->dev, sizeof(*cdev), GFP_KERNEL);
    if (!cdev)
        return -ENOMEM;

    cdev->dev = &pdev->dev;

    // Map registers
    res = platform_get_resource(pdev, IORESOURCE_MEM, 0);
    cdev->regs = devm_ioremap_resource(&pdev->dev, res);
    if (IS_ERR(cdev->regs))
        return PTR_ERR(cdev->regs);

    // Get clock
    cdev->clk = devm_clk_get(&pdev->dev, "mclk");
    if (IS_ERR(cdev->clk)) {
        dev_err(&pdev->dev, "Failed to get clock\n");
        return PTR_ERR(cdev->clk);
    }

    // Get GPIO
    cdev->reset_gpio = devm_gpiod_get(&pdev->dev, "reset", GPIOD_OUT_HIGH);
    if (IS_ERR(cdev->reset_gpio)) {
        dev_err(&pdev->dev, "Failed to get reset GPIO\n");
        return PTR_ERR(cdev->reset_gpio);
    }

    // Enable clock
    ret = clk_prepare_enable(cdev->clk);
    if (ret) {
        dev_err(&pdev->dev, "Failed to enable clock\n");
        return ret;
    }

    platform_set_drvdata(pdev, cdev);

    dev_info(&pdev->dev, "Probe successful\n");
    return 0;
}

static int custom_remove(struct platform_device *pdev)
{
    struct custom_device *cdev = platform_get_drvdata(pdev);

    clk_disable_unprepare(cdev->clk);

    dev_info(&pdev->dev, "Device removed\n");
    return 0;
}

static const struct of_device_id custom_of_match[] = {
    { .compatible = "vendor,custom-device" },
    { /* sentinel */ }
};
MODULE_DEVICE_TABLE(of, custom_of_match);

static struct platform_driver custom_driver = {
    .probe = custom_probe,
    .remove = custom_remove,
    .driver = {
        .name = "custom-driver",
        .of_match_table = custom_of_match,
    },
};

module_platform_driver(custom_driver);

MODULE_DESCRIPTION("Custom Device Driver");
MODULE_AUTHOR("Developer Name");
MODULE_LICENSE("GPL v2");
```

### V4L2 Camera Driver Pattern

```c
#include <media/v4l2-device.h>
#include <media/v4l2-subdev.h>
#include <media/v4l2-ctrls.h>

struct sensor_dev {
    struct v4l2_subdev sd;
    struct v4l2_ctrl_handler ctrl_handler;
    struct media_pad pad;

    struct i2c_client *client;
    struct gpio_desc *reset_gpio;
    struct clk *mclk;

    struct v4l2_mbus_framefmt format;
    bool streaming;
};

static int sensor_s_stream(struct v4l2_subdev *sd, int enable)
{
    struct sensor_dev *sensor = container_of(sd, struct sensor_dev, sd);

    if (enable) {
        // Start streaming
        dev_info(sensor->sd.dev, "Starting stream\n");
        sensor->streaming = true;
    } else {
        // Stop streaming
        dev_info(sensor->sd.dev, "Stopping stream\n");
        sensor->streaming = false;
    }

    return 0;
}

static const struct v4l2_subdev_video_ops sensor_video_ops = {
    .s_stream = sensor_s_stream,
};

static const struct v4l2_subdev_ops sensor_ops = {
    .video = &sensor_video_ops,
};

static int sensor_probe(struct i2c_client *client,
                        const struct i2c_device_id *id)
{
    struct sensor_dev *sensor;
    int ret;

    sensor = devm_kzalloc(&client->dev, sizeof(*sensor), GFP_KERNEL);
    if (!sensor)
        return -ENOMEM;

    sensor->client = client;

    v4l2_i2c_subdev_init(&sensor->sd, client, &sensor_ops);

    sensor->pad.flags = MEDIA_PAD_FL_SOURCE;
    ret = media_entity_pads_init(&sensor->sd.entity, 1, &sensor->pad);
    if (ret)
        return ret;

    return v4l2_async_register_subdev(&sensor->sd);
}
```

### Character Device Driver

```c
#include <linux/cdev.h>
#include <linux/fs.h>
#include <linux/uaccess.h>

struct custom_cdev {
    struct cdev cdev;
    dev_t devnum;
    struct class *class;
    struct device *device;
    void *data;
};

static int custom_open(struct inode *inode, struct file *file)
{
    struct custom_cdev *cdev = container_of(inode->i_cdev,
                                            struct custom_cdev, cdev);
    file->private_data = cdev;
    return 0;
}

static ssize_t custom_read(struct file *file, char __user *buf,
                          size_t count, loff_t *ppos)
{
    struct custom_cdev *cdev = file->private_data;

    // Implementation
    return 0;
}

static long custom_ioctl(struct file *file, unsigned int cmd,
                        unsigned long arg)
{
    struct custom_cdev *cdev = file->private_data;

    switch (cmd) {
    case CUSTOM_IOC_CMD:
        // Handle command
        break;
    default:
        return -ENOTTY;
    }

    return 0;
}

static const struct file_operations custom_fops = {
    .owner = THIS_MODULE,
    .open = custom_open,
    .read = custom_read,
    .unlocked_ioctl = custom_ioctl,
};
```

## Debugging Techniques

### Kernel Debug Output

```c
// Print levels
pr_emerg("Emergency: system is unusable\n");
pr_alert("Alert: action must be taken immediately\n");
pr_crit("Critical: critical conditions\n");
pr_err("Error: error conditions\n");
pr_warn("Warning: warning conditions\n");
pr_notice("Notice: normal but significant condition\n");
pr_info("Info: informational\n");
pr_debug("Debug: debug-level messages\n");

// Device-specific
dev_err(&pdev->dev, "Error occurred\n");
dev_warn(&pdev->dev, "Warning\n");
dev_info(&pdev->dev, "Information\n");
dev_dbg(&pdev->dev, "Debug info\n");

// Dynamic debug
// Enable at runtime:
// echo 'file driver.c +p' > /sys/kernel/debug/dynamic_debug/control
```

### Kernel Configuration for Debugging

```kconfig
# Enable debug info
CONFIG_DEBUG_INFO=y
CONFIG_DEBUG_KERNEL=y

# Debug filesystem
CONFIG_DEBUG_FS=y

# KGDB (kernel debugger)
CONFIG_KGDB=y
CONFIG_KGDB_SERIAL_CONSOLE=y

# Memory debugging
CONFIG_DEBUG_SLAB=y
CONFIG_DEBUG_KMEMLEAK=y

# Lock debugging
CONFIG_DEBUG_SPINLOCK=y
CONFIG_DEBUG_MUTEXES=y
CONFIG_LOCKDEP=y

# Stack traces
CONFIG_STACKTRACE=y
CONFIG_KALLSYMS=y
CONFIG_KALLSYMS_ALL=y
```

### Using ftrace

```bash
# Enable function tracing
cd /sys/kernel/debug/tracing
echo function > current_tracer
echo 1 > tracing_on

# Filter specific functions
echo custom_probe > set_ftrace_filter

# View trace
cat trace

# Function graph tracer
echo function_graph > current_tracer

# Event tracing
echo 1 > events/i2c/enable
```

### Kernel Crash Analysis

```bash
# Enable kdump in kernel config
CONFIG_CRASH_DUMP=y
CONFIG_KEXEC=y

# Analyze crash dump with crash utility
crash vmlinux dump.img

# Common crash commands
bt          # Backtrace
ps          # Process list
log         # Kernel log
dis -l      # Disassemble with source
```

### Using printk Timestamps

```bash
# Enable timestamps
echo 1 > /sys/module/printk/parameters/time

# View with timestamps
dmesg -T
```

## Performance Tuning

### CPU Governor Settings

```bash
# Available governors
cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors

# Set performance mode
echo performance > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor

# Set on all CPUs
for cpu in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do
    echo performance > $cpu
done

# View frequencies
cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq
cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq
```

### GPU Performance

```bash
# Lock GPU frequency
echo 1 > /sys/devices/gpu.0/railgate_enable
cat /sys/devices/gpu.0/devfreq/17000000.gpu/available_frequencies
echo <freq> > /sys/devices/gpu.0/devfreq/17000000.gpu/min_freq
echo <freq> > /sys/devices/gpu.0/devfreq/17000000.gpu/max_freq
```

### Memory Tuning

```bash
# View memory info
cat /proc/meminfo

# CMA (Contiguous Memory Allocator)
# Set in kernel cmdline or device tree
cma=256M

# Swap configuration
echo 0 > /proc/sys/vm/swappiness  # Minimize swap usage
```

### IRQ Affinity

```bash
# View IRQ assignments
cat /proc/interrupts

# Set IRQ affinity (pin to specific CPU)
echo 2 > /proc/irq/<irq_num>/smp_affinity  # CPU 1
echo 4 > /proc/irq/<irq_num>/smp_affinity  # CPU 2
```

### Real-time Configuration

```kconfig
# RT kernel options
CONFIG_PREEMPT_RT=y
CONFIG_HIGH_RES_TIMERS=y
CONFIG_NO_HZ_FULL=y
```

```bash
# Set process priority
chrt -f 99 /path/to/process  # SCHED_FIFO, priority 99
chrt -r 50 /path/to/process  # SCHED_RR, priority 50

# Isolate CPUs for real-time (kernel cmdline)
isolcpus=2,3 nohz_full=2,3 rcu_nocbs=2,3
```

### Profiling Tools

```bash
# perf (performance analysis)
perf record -a -g -- sleep 10
perf report

# Profile specific function
perf record -e cycles -c 10000 --call-graph dwarf -- ./program

# System-wide profiling
perf top

# Trace system calls
strace -c ./program
strace -p <pid>
```

## Kernel Module Development in Yocto

### Module Recipe

```bitbake
# recipes-kernel/custom-module/custom-module_1.0.bb
SUMMARY = "Custom Kernel Module"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=..."

inherit module

SRC_URI = "file://Makefile \
           file://custom-module.c \
          "

S = "${WORKDIR}"

# Specify kernel to build against
KERNEL_MODULE_AUTOLOAD += "custom-module"

# Module parameters
KERNEL_MODULE_PROBECONF += "custom-module"
module_conf_custom-module = "options custom-module param=value"
```

### Makefile for Module

```makefile
# Makefile
obj-m := custom-module.o

SRC := $(shell pwd)

all:
	$(MAKE) -C $(KERNEL_SRC) M=$(SRC) modules

modules_install:
	$(MAKE) -C $(KERNEL_SRC) M=$(SRC) modules_install

clean:
	rm -f *.o *~ core .depend .*.cmd *.ko *.mod.c
	rm -f Module.markers Module.symvers modules.order
	rm -rf .tmp_versions Modules.symvers
```

---

*Last Updated: 2025-11-18*
*Maintained by: Documentation Researcher Agent*
