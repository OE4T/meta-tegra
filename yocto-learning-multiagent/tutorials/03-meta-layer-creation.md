# Tutorial 03: Building a Meta-Layer
## Creating a Professional, Distributable Yocto Layer

---

## Learning Objectives

After completing this tutorial, you will be able to:
- Design a complete, well-structured meta-layer
- Implement layer dependencies and compatibility
- Create machine configurations for custom hardware
- Write distro configurations with custom policies
- Package and distribute a meta-layer
- Maintain layer versioning and documentation

---

## Prerequisites Checklist

- [ ] Completed Tutorial 01 (Yocto Hello World)
- [ ] Completed Tutorial 02 (Custom Recipes)
- [ ] Understanding of Yocto layer structure
- [ ] Git installed and configured
- [ ] Text editor for configuration files
- [ ] Basic understanding of hardware BSPs

---

## Estimated Duration

**Total Time**: 4-5 hours
- Planning and design: 30 minutes
- Layer structure creation: 1 hour
- Machine configuration: 1.5 hours
- Distro configuration: 1 hour
- Testing and documentation: 1-2 hours

---

## Step-by-Step Instructions

### Step 1: Plan Your Meta-Layer Architecture

Before creating the layer, plan its purpose and scope:

```bash
# Create planning document
cat > ~/yocto-jetson/meta-jetson-robotics-design.md << 'EOF'
# Meta-Jetson-Robotics Layer Design

## Purpose
Create a comprehensive BSP layer for robotic applications on NVIDIA Jetson
platforms, including custom hardware configurations, ROS2 integration,
and specialized drivers.

## Scope
- Custom carrier board machine definitions
- Robot-specific distro configuration
- Sensor drivers (IMU, LIDAR, cameras)
- ROS2 integration recipes
- AI/ML inference tools
- Development tools and utilities

## Dependencies
- meta-tegra (NVIDIA BSP)
- meta-openembedded
- meta-python
- meta-ros2 (optional)

## Target Users
- Robotics developers
- Autonomous system integrators
- AI/edge computing applications
EOF
```

### Step 2: Create Professional Layer Structure

Generate a complete layer with proper organization:

```bash
cd ~/yocto-jetson

# Create the layer using bitbake-layers
bitbake-layers create-layer meta-jetson-robotics
cd meta-jetson-robotics

# Create comprehensive directory structure
mkdir -p conf/machine
mkdir -p conf/distro
mkdir -p recipes-kernel/linux
mkdir -p recipes-bsp/bootloader
mkdir -p recipes-bsp/device-tree
mkdir -p recipes-core/images
mkdir -p recipes-devtools
mkdir -p recipes-drivers/sensors
mkdir -p recipes-drivers/cameras
mkdir -p recipes-ros2
mkdir -p recipes-ai/inference
mkdir -p classes
mkdir -p wic
mkdir -p scripts
mkdir -p docs

# Create directory tree visualization
tree -L 2
```

**Expected structure**:
```
meta-jetson-robotics/
├── conf/
│   ├── layer.conf              # Layer configuration
│   ├── machine/                # Machine definitions
│   └── distro/                 # Distribution configs
├── recipes-kernel/
│   └── linux/                  # Kernel customizations
├── recipes-bsp/
│   ├── bootloader/             # Bootloader configs
│   └── device-tree/            # Device tree overlays
├── recipes-core/
│   └── images/                 # Custom image definitions
├── recipes-drivers/
│   ├── sensors/                # Sensor drivers
│   └── cameras/                # Camera drivers
├── recipes-devtools/           # Development tools
├── recipes-ros2/               # ROS2 integration
├── recipes-ai/
│   └── inference/              # AI inference engines
├── classes/                    # Custom bbclasses
├── wic/                        # Disk layout configs
├── scripts/                    # Helper scripts
├── docs/                       # Documentation
├── COPYING.MIT                 # License
└── README.md                   # Layer readme
```

### Step 3: Configure Layer Metadata

Create a comprehensive layer.conf:

```bash
cat > conf/layer.conf << 'EOF'
# Layer configuration for meta-jetson-robotics
# Copyright (C) 2025 Your Organization

# We have a conf and classes directory, add to BBPATH
BBPATH =. "${LAYERDIR}:"

# We have recipes-* directories, add to BBFILES
BBFILES += "${LAYERDIR}/recipes-*/*/*.bb \
            ${LAYERDIR}/recipes-*/*/*.bbappend"

BBFILE_COLLECTIONS += "jetson-robotics"
BBFILE_PATTERN_jetson-robotics = "^${LAYERDIR}/"
BBFILE_PRIORITY_jetson-robotics = "20"

# Layer version
LAYERVERSION_jetson-robotics = "1"

# Layer dependencies - specifies other layers required
LAYERDEPENDS_jetson-robotics = "\
    core \
    openembedded-layer \
    meta-python \
    tegra \
"

# Compatible with these Yocto releases
LAYERSERIES_COMPAT_jetson-robotics = "kirkstone langdale mickledore"

# Additional configuration
# Path to custom classes
BBFILES_DYNAMIC += " \
    ros-layer:${LAYERDIR}/dynamic-layers/meta-ros/*/*.bb \
    ros-layer:${LAYERDIR}/dynamic-layers/meta-ros/*/*.bbappend \
"

# Custom license directory
LICENSE_PATH += "${LAYERDIR}/licenses"

# Variables for layer-specific settings
JETSON_ROBOTICS_VERSION = "1.0.0"
JETSON_ROBOTICS_LAYER_DIR = "${LAYERDIR}"
EOF
```

**Explanation**:
- **BBFILE_PRIORITY**: Set higher than base layers to override recipes
- **LAYERDEPENDS**: Enforces required layers at parse time
- **LAYERSERIES_COMPAT**: Declares compatibility with Yocto releases
- **BBFILES_DYNAMIC**: Conditionally includes recipes based on other layers

### Step 4: Create Custom Machine Configuration

Define a custom carrier board machine:

```bash
cat > conf/machine/jetson-orin-robotics.conf << 'EOF'
# Machine configuration for Jetson Orin on custom robotics carrier board
# Based on jetson-orin-agx-devkit with custom hardware modifications

#@TYPE: Machine
#@NAME: NVIDIA Jetson Orin Robotics Platform
#@DESCRIPTION: Custom Jetson Orin AGX configuration for robotics applications
#              with additional sensor interfaces and power management

# Include base Jetson Orin configuration
require conf/machine/jetson-orin-agx-devkit.conf

# Override machine features
MACHINE_FEATURES += "\
    wifi \
    bluetooth \
    can \
    usbgadget \
    rtc \
"

# Remove features not present on carrier board
MACHINE_FEATURES:remove = "nvme"

# Kernel configuration
KERNEL_DEVICETREE:append = " \
    nvidia/tegra234-p3701-0000-p3737-0000-robotics.dtb \
"

# Custom kernel config fragments
SRC_URI:append:jetson-orin-robotics = " \
    file://robotics-sensors.cfg \
    file://can-bus.cfg \
    file://realtime.cfg \
"

# Serial console (different UART on custom carrier)
SERIAL_CONSOLES = "115200;ttyTCU0"

# U-Boot environment
UBOOT_EXTLINUX = "1"

# Additional firmware
MACHINE_EXTRA_RDEPENDS += "\
    linux-firmware-wifi \
    linux-firmware-bluetooth \
    can-utils \
    i2c-tools \
"

# Preferred providers for this machine
PREFERRED_PROVIDER_virtual/kernel = "linux-tegra"
PREFERRED_VERSION_linux-tegra = "5.10%"

# GPU configuration
CUDA_VERSION = "11.4"
CUDA_ARCHITECTURES = "87"  # Orin architecture

# Multimedia support
TEGRA_MULTIMEDIA_SUPPORT = "1"
TEGRA_CAMERA_SUPPORT = "1"

# Storage configuration
IMAGE_BOOT_FILES = "\
    Image \
    ${KERNEL_DEVICETREE} \
"

# WIC image configuration (disk layout)
WKS_FILE = "jetson-orin-robotics.wks"

# Custom power limits (15W mode for passive cooling)
NVPOWER_BOARD_SPEC = "robotics-carrier-board"
NVPOWER_MODE = "15W"

# Additional boot parameters
KERNEL_ARGS:append = " \
    isolcpus=0-3 \
    nohz_full=0-3 \
    rcu_nocbs=0-3 \
    maxcpus=8 \
"
EOF
```

**Explanation**:
- **require**: Inherits from base machine config
- **MACHINE_FEATURES**: Declares hardware capabilities
- **KERNEL_DEVICETREE**: Specifies device tree for custom board
- **MACHINE_EXTRA_RDEPENDS**: Adds required packages
- **KERNEL_ARGS**: Custom kernel boot parameters for real-time

### Step 5: Create Device Tree Overlay

Add custom hardware support via device tree:

```bash
mkdir -p recipes-bsp/device-tree/files

cat > recipes-bsp/device-tree/files/tegra234-p3701-0000-p3737-0000-robotics.dts << 'EOF'
// Device tree overlay for Jetson Orin Robotics Carrier Board
/dts-v1/;
/plugin/;

#include <dt-bindings/gpio/tegra234-gpio.h>
#include <dt-bindings/interrupt-controller/arm-gic.h>

/ {
    overlay-name = "Jetson Orin Robotics Carrier Board";
    compatible = "nvidia,p3737-0000+p3701-0000", "nvidia,tegra234";

    fragment@0 {
        target-path = "/";
        __overlay__ {
            // Custom IMU sensor on I2C bus 1
            i2c@3160000 {
                status = "okay";

                imu@68 {
                    compatible = "invensense,mpu9250";
                    reg = <0x68>;
                    interrupt-parent = <&gpio>;
                    interrupts = <TEGRA234_MAIN_GPIO(H, 2) IRQ_TYPE_EDGE_RISING>;
                    vdd-supply = <&vdd_3v3_sys>;
                    vddio-supply = <&vdd_1v8_sys>;
                };
            };

            // LIDAR on SPI bus
            spi@3210000 {
                status = "okay";

                lidar@0 {
                    compatible = "robotics,lidar-sensor";
                    reg = <0>;
                    spi-max-frequency = <10000000>;
                    interrupt-parent = <&gpio>;
                    interrupts = <TEGRA234_MAIN_GPIO(H, 3) IRQ_TYPE_EDGE_FALLING>;
                };
            };

            // CAN bus interface
            can@c310000 {
                status = "okay";
                can-mode = "fd";
                bitrate = <1000000>;
                data-bitrate = <5000000>;
            };

            // Additional GPIO LEDs for status indication
            gpio-leds {
                compatible = "gpio-leds";

                led-status {
                    label = "status:green";
                    gpios = <&gpio TEGRA234_MAIN_GPIO(Q, 5) GPIO_ACTIVE_HIGH>;
                    default-state = "on";
                    linux,default-trigger = "heartbeat";
                };

                led-error {
                    label = "error:red";
                    gpios = <&gpio TEGRA234_MAIN_GPIO(Q, 6) GPIO_ACTIVE_HIGH>;
                    default-state = "off";
                };
            };

            // Power management zones
            thermal-zones {
                robotics-zone {
                    polling-delay-passive = <1000>;
                    polling-delay = <5000>;
                    thermal-sensors = <&tegra234_tz 0>;

                    trips {
                        critical {
                            temperature = <95000>;
                            hysteresis = <2000>;
                            type = "critical";
                        };
                    };
                };
            };
        };
    };
};
EOF

# Create the device tree recipe
cat > recipes-bsp/device-tree/device-tree-robotics_1.0.bb << 'EOF'
SUMMARY = "Device tree overlay for Jetson Orin Robotics Carrier"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit devicetree

SRC_URI = "file://tegra234-p3701-0000-p3737-0000-robotics.dts"

S = "${WORKDIR}"

COMPATIBLE_MACHINE = "jetson-orin-robotics"

# Kernel will use this DTB
KERNEL_DEVICETREE:append = " nvidia/tegra234-p3701-0000-p3737-0000-robotics.dtb"
EOF
```

### Step 6: Create Kernel Configuration Fragments

Add kernel features needed for robotics:

```bash
mkdir -p recipes-kernel/linux/linux-tegra

# CAN bus support
cat > recipes-kernel/linux/linux-tegra/can-bus.cfg << 'EOF'
# CAN bus support for robotics applications
CONFIG_CAN=y
CONFIG_CAN_RAW=y
CONFIG_CAN_BCM=y
CONFIG_CAN_GW=y
CONFIG_CAN_SLCAN=y
CONFIG_CAN_DEV=y
CONFIG_CAN_CALC_BITTIMING=y

# NVIDIA Tegra CAN controller
CONFIG_CAN_MTTCAN=y

# Virtual CAN for testing
CONFIG_CAN_VCAN=y
CONFIG_CAN_VXCAN=y

# CAN over Ethernet
CONFIG_CAN_J1939=y
EOF

# Sensor support
cat > recipes-kernel/linux/linux-tegra/robotics-sensors.cfg << 'EOF'
# Sensor drivers for robotics platform

# I2C/SPI sensor support
CONFIG_I2C=y
CONFIG_I2C_CHARDEV=y
CONFIG_SPI=y
CONFIG_SPI_SPIDEV=y

# IMU sensors
CONFIG_IIO=y
CONFIG_IIO_BUFFER=y
CONFIG_IIO_KFIFO_BUF=y
CONFIG_IIO_TRIGGERED_BUFFER=y
CONFIG_INV_MPU6050_I2C=y
CONFIG_INV_MPU6050_SPI=y

# Environmental sensors
CONFIG_SENSORS_BME280=y
CONFIG_SENSORS_BME280_I2C=y

# LIDAR support
CONFIG_LIDAR=y

# GPS/GNSS
CONFIG_GNSS=y
CONFIG_GNSS_SERIAL=y
CONFIG_GNSS_UBX_SERIAL=y
EOF

# Real-time configuration
cat > recipes-kernel/linux/linux-tegra/realtime.cfg << 'EOF'
# Real-time kernel configuration for robotics

# Preemption model
CONFIG_PREEMPT_RT=y
CONFIG_PREEMPT=y
CONFIG_PREEMPT_COUNT=y

# High resolution timers
CONFIG_HIGH_RES_TIMERS=y
CONFIG_NO_HZ_FULL=y

# CPU isolation
CONFIG_NO_HZ=y
CONFIG_NO_HZ_IDLE=y
CONFIG_RCU_NOCB_CPU=y

# Real-time scheduling
CONFIG_RT_GROUP_SCHED=y

# Reduce latency
CONFIG_HZ_1000=y
CONFIG_HZ=1000
EOF

# Create kernel append recipe
cat > recipes-kernel/linux/linux-tegra_%.bbappend << 'EOF'
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:jetson-orin-robotics = " \
    file://can-bus.cfg \
    file://robotics-sensors.cfg \
    file://realtime.cfg \
"

# Build real-time kernel variant
do_configure:append:jetson-orin-robotics() {
    # Merge config fragments
    ${S}/scripts/kconfig/merge_config.sh \
        -m ${B}/.config \
        ${WORKDIR}/can-bus.cfg \
        ${WORKDIR}/robotics-sensors.cfg \
        ${WORKDIR}/realtime.cfg
}
EOF
```

### Step 7: Create Custom Distribution Configuration

Define distribution policies and defaults:

```bash
cat > conf/distro/jetson-robotics.conf << 'EOF'
# Jetson Robotics Distribution Configuration
# Optimized for robotic applications on NVIDIA Jetson

require conf/distro/poky.conf

DISTRO = "jetson-robotics"
DISTRO_NAME = "Jetson Robotics Linux"
DISTRO_VERSION = "1.0.0"
DISTRO_CODENAME = "falcon"

# Organization info
MAINTAINER = "Robotics Team <robotics@example.com>"
TARGET_VENDOR = "-robotics"

# SDK and toolchain
SDK_VENDOR = "-roboticssdk"
SDK_VERSION = "${DISTRO_VERSION}"
SDK_NAME = "${DISTRO}-${TCLIBC}-${SDK_ARCH}-${IMAGE_BASENAME}-${TUNE_PKGARCH}"

# System initialization
INIT_MANAGER = "systemd"
DISTRO_FEATURES:append = " systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"
VIRTUAL-RUNTIME_init_manager = "systemd"
VIRTUAL-RUNTIME_initscripts = ""

# Required features for robotics
DISTRO_FEATURES:append = " \
    opengl \
    wayland \
    vulkan \
    bluetooth \
    wifi \
    pam \
    usrmerge \
    ipv4 \
    ipv6 \
"

# Remove unwanted features
DISTRO_FEATURES:remove = " \
    x11 \
    3g \
    nfc \
"

# Security features
DISTRO_FEATURES:append = " \
    seccomp \
    integrity \
    smack \
"

# Preferred providers
PREFERRED_PROVIDER_virtual/kernel = "linux-tegra"
PREFERRED_PROVIDER_virtual/bootloader = "cboot-tegra"
PREFERRED_PROVIDER_u-boot = "u-boot-tegra"

# Preferred versions
PREFERRED_VERSION_python3 = "3.10%"
PREFERRED_VERSION_opencv = "4.6%"

# C library
TCLIBC = "glibc"
TCLIBCAPPEND = ""

# Package management
PACKAGE_CLASSES = "package_deb"
EXTRA_IMAGE_FEATURES = "debug-tweaks package-management"

# Compiler optimizations for ARM
TARGET_CFLAGS:append = " -O3 -ffast-math"
TARGET_CXXFLAGS:append = " -O3 -ffast-math"

# CUDA and AI frameworks
CUDA_VERSION = "11.4"
TENSORRT_VERSION = "8.5"
CUDNN_VERSION = "8.6"

# ROS2 version
ROS_DISTRO = "humble"

# Network configuration
CONNECTIVITY_CHECK_URIS = "https://www.example.com/"

# Default hostname
hostname:pn-base-files = "jetson-robot"

# Timezone
DEFAULT_TIMEZONE = "UTC"

# Default locale
DEFAULT_LOCALE = "en_US.UTF-8"
IMAGE_LINGUAS = "en-us"

# Root filesystem size
IMAGE_ROOTFS_SIZE ?= "8192"
IMAGE_ROOTFS_EXTRA_SPACE = "2097152"

# Development tools in SDK
TOOLCHAIN_HOST_TASK:append = " \
    nativesdk-cmake \
    nativesdk-python3 \
    nativesdk-python3-pip \
"

# Custom distro features
JETSON_ROBOTICS_FEATURES = "ros2 ai-inference sensor-drivers"
EOF
```

**Explanation**:
- **DISTRO_FEATURES**: Declares system capabilities
- **PREFERRED_PROVIDER**: Selects which recipe to use for virtual packages
- **PACKAGE_CLASSES**: Determines package format (deb, rpm, or ipk)
- **TARGET_CFLAGS**: Compiler optimization flags

### Step 8: Create Custom Image Recipe

Define a robotics-optimized image:

```bash
cat > recipes-core/images/jetson-robotics-image.bb << 'EOF'
SUMMARY = "Jetson Robotics Platform Image"
DESCRIPTION = "Complete image for robotic applications with ROS2, \
               AI inference, and sensor integration"
LICENSE = "MIT"

# Inherit from core image class
inherit core-image

# Base packages
IMAGE_INSTALL = "\
    packagegroup-core-boot \
    packagegroup-core-full-cmdline \
    ${CORE_IMAGE_EXTRA_INSTALL} \
"

# System utilities
IMAGE_INSTALL += "\
    systemd \
    systemd-analyze \
    dbus \
    util-linux \
    e2fsprogs \
    dosfstools \
    htop \
    iotop \
    strace \
    lsof \
    procps \
"

# Development tools
IMAGE_INSTALL += "\
    git \
    cmake \
    gcc \
    g++ \
    python3 \
    python3-pip \
    python3-numpy \
    python3-dev \
    gdb \
    valgrind \
"

# Networking
IMAGE_INSTALL += "\
    openssh \
    iproute2 \
    iptables \
    nfs-utils \
    ethtool \
    bridge-utils \
    wireless-tools \
    wpa-supplicant \
    hostapd \
    mosquitto \
    mosquitto-clients \
"

# Hardware interfaces
IMAGE_INSTALL += "\
    i2c-tools \
    spi-tools \
    can-utils \
    usbutils \
    pciutils \
"

# NVIDIA Jetson packages
IMAGE_INSTALL += "\
    cuda-libraries \
    cudnn \
    tensorrt \
    nvidia-docker \
    jetson-gpio \
    jetson-stats \
"

# Computer vision
IMAGE_INSTALL += "\
    opencv \
    opencv-python \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    v4l-utils \
"

# ROS2 (if meta-ros2 layer is present)
IMAGE_INSTALL += "\
    ${@bb.utils.contains('BBFILE_COLLECTIONS', 'ros-layer', 'ros-base ros-core', '', d)} \
"

# Custom robotics packages
IMAGE_INSTALL += "\
    sensor-drivers \
    robot-controller \
    ai-inference-server \
"

# Image features
IMAGE_FEATURES += "\
    splash \
    ssh-server-openssh \
    package-management \
    debug-tweaks \
    tools-sdk \
    tools-debug \
    tools-profile \
"

# Root filesystem type
IMAGE_FSTYPES = "tar.gz ext4 wic.gz"

# Extra space (2GB)
IMAGE_ROOTFS_EXTRA_SPACE = "2097152"

# Post-install commands
ROOTFS_POSTPROCESS_COMMAND += "robotics_postinstall;"

robotics_postinstall() {
    # Create robot user
    echo "robot:x:1000:1000:Robot User:/home/robot:/bin/bash" >> ${IMAGE_ROOTFS}/etc/passwd
    echo "robot:x:1000:" >> ${IMAGE_ROOTFS}/etc/group

    # Create home directory
    install -d -m 0755 ${IMAGE_ROOTFS}/home/robot
    chown 1000:1000 ${IMAGE_ROOTFS}/home/robot

    # Add robot user to necessary groups
    sed -i 's/^\(dialout:.*\)/\1robot/' ${IMAGE_ROOTFS}/etc/group
    sed -i 's/^\(gpio:.*\)/\1robot/' ${IMAGE_ROOTFS}/etc/group
    sed -i 's/^\(i2c:.*\)/\1robot/' ${IMAGE_ROOTFS}/etc/group

    # Create workspace directory
    install -d -m 0755 ${IMAGE_ROOTFS}/home/robot/workspace
    chown 1000:1000 ${IMAGE_ROOTFS}/home/robot/workspace

    # Install startup script
    install -d ${IMAGE_ROOTFS}/opt/robotics
    echo "#!/bin/bash" > ${IMAGE_ROOTFS}/opt/robotics/startup.sh
    echo "# Robot initialization script" >> ${IMAGE_ROOTFS}/opt/robotics/startup.sh
    chmod +x ${IMAGE_ROOTFS}/opt/robotics/startup.sh
}

# SDK includes cross-compiler and tools
inherit populate_sdk
EOF
```

### Step 9: Create Helper Scripts

Add useful scripts for developers:

```bash
mkdir -p scripts

cat > scripts/setup-build.sh << 'EOF'
#!/bin/bash
# Setup script for Jetson Robotics build environment

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LAYER_DIR="$(dirname "$SCRIPT_DIR")"

echo "=== Jetson Robotics Build Setup ==="
echo ""

# Check prerequisites
command -v bitbake-layers >/dev/null 2>&1 || {
    echo "Error: bitbake not found. Please source oe-init-build-env first."
    exit 1
}

# Add the layer if not already added
if ! bitbake-layers show-layers | grep -q "meta-jetson-robotics"; then
    echo "Adding meta-jetson-robotics layer..."
    bitbake-layers add-layer "$LAYER_DIR"
fi

# Set machine if not set
if ! grep -q "^MACHINE.*jetson-orin-robotics" conf/local.conf; then
    echo "Setting MACHINE to jetson-orin-robotics..."
    echo 'MACHINE = "jetson-orin-robotics"' >> conf/local.conf
fi

# Set distro if not set
if ! grep -q "^DISTRO.*jetson-robotics" conf/local.conf; then
    echo "Setting DISTRO to jetson-robotics..."
    echo 'DISTRO = "jetson-robotics"' >> conf/local.conf
fi

echo ""
echo "Setup complete! You can now build with:"
echo "  bitbake jetson-robotics-image"
echo ""
EOF

chmod +x scripts/setup-build.sh
```

### Step 10: Document the Layer

Create comprehensive documentation:

```bash
cat > README.md << 'EOF'
# Meta-Jetson-Robotics

A comprehensive Yocto/OpenEmbedded BSP layer for robotic applications on NVIDIA Jetson platforms.

## Features

- Custom machine configuration for robotics carrier boards
- Real-time kernel configuration
- CAN bus, I2C, SPI sensor support
- ROS2 integration
- AI inference optimizations (CUDA, TensorRT, cuDNN)
- Pre-configured development environment

## Supported Hardware

- NVIDIA Jetson Orin AGX (custom robotics carrier)
- NVIDIA Jetson Orin NX (custom robotics carrier)
- Other Tegra234-based platforms (with modifications)

## Dependencies

This layer depends on:

- meta-tegra (https://github.com/OE4T/meta-tegra)
- meta-openembedded (https://github.com/openembedded/meta-openembedded)
- meta-python (part of meta-openembedded)
- meta-ros2 (optional, for ROS support)

## Quick Start

### 1. Clone Required Layers

```bash
git clone -b kirkstone git://git.yoctoproject.org/poky
cd poky
git clone -b kirkstone git://git.openembedded.org/meta-openembedded
git clone -b kirkstone https://github.com/OE4T/meta-tegra
git clone https://github.com/your-org/meta-jetson-robotics
```

### 2. Initialize Build Environment

```bash
source oe-init-build-env build-robotics
```

### 3. Configure Build

Run the setup script:

```bash
../meta-jetson-robotics/scripts/setup-build.sh
```

Or manually edit `conf/local.conf`:

```
MACHINE = "jetson-orin-robotics"
DISTRO = "jetson-robotics"
```

And `conf/bblayers.conf` to include all required layers.

### 4. Build Image

```bash
bitbake jetson-robotics-image
```

## Images

### jetson-robotics-image

Complete robotics platform image including:
- ROS2 Humble
- TensorRT and CUDA
- Sensor drivers (IMU, LIDAR, cameras)
- Development tools
- Network utilities

## Customization

### Adding Custom Packages

Edit `conf/local.conf`:

```bash
IMAGE_INSTALL:append = " your-package"
```

### Kernel Configuration

Kernel config fragments are in `recipes-kernel/linux/linux-tegra/`.

### Device Tree Modifications

Device tree sources are in `recipes-bsp/device-tree/files/`.

## Testing

See `docs/testing.md` for hardware testing procedures.

## Contributing

Contributions are welcome! Please submit pull requests or issues on GitHub.

## License

MIT License - see COPYING.MIT for details.

## Maintainers

- Robot Team <robotics@example.com>

## Version History

- 1.0.0 (2025-01-15): Initial release
  - Jetson Orin support
  - ROS2 Humble integration
  - Custom carrier board support
EOF

# Create license file
cat > COPYING.MIT << 'EOF'
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
EOF
```

### Step 11: Validate and Test the Layer

```bash
# Return to build directory
cd ~/yocto-jetson/builds/jetson-orin-agx

# Add the layer
bitbake-layers add-layer ../../meta-jetson-robotics

# Show layers to verify
bitbake-layers show-layers

# Validate layer configuration
bitbake-layers show-appends
bitbake-layers show-recipes "linux-tegra"

# Parse test (check for errors)
bitbake -p

# Build the custom image (dry run first)
bitbake -n jetson-robotics-image

# Full build
bitbake jetson-robotics-image
```

---

## Troubleshooting Common Issues

### Issue 1: Layer Dependency Not Found

**Symptoms**:
```
ERROR: Layer 'jetson-robotics' depends on layer 'tegra', but layer is not enabled
```

**Solutions**:
```bash
# Check available layers
bitbake-layers show-layers

# Add missing dependency
bitbake-layers add-layer ../poky/meta-tegra

# Verify dependencies in layer.conf
cat ../meta-jetson-robotics/conf/layer.conf | grep LAYERDEPENDS
```

### Issue 2: Machine Configuration Not Found

**Symptoms**:
```
ERROR: OE-core's config sanity checker detected a potential misconfiguration.
MACHINE=jetson-orin-robotics is invalid
```

**Solutions**:
```bash
# List available machines
ls ../meta-jetson-robotics/conf/machine/

# Verify machine config syntax
bitbake-getvar -r jetson-orin-robotics MACHINE

# Check for typos in conf/local.conf
grep MACHINE conf/local.conf
```

### Issue 3: Recipe Conflicts

**Symptoms**:
```
ERROR: Multiple recipes provide linux-tegra
```

**Solutions**:
```bash
# Check which layers provide the recipe
bitbake-layers show-recipes linux-tegra

# Set preferred provider in distro conf
echo 'PREFERRED_PROVIDER_virtual/kernel = "linux-tegra"' >> \
    ../meta-jetson-robotics/conf/distro/jetson-robotics.conf

# Or set priority higher
# Edit layer.conf: BBFILE_PRIORITY_jetson-robotics = "20"
```

### Issue 4: Device Tree Compilation Failure

**Symptoms**:
```
ERROR: device-tree-robotics-1.0-r0 do_compile: Device tree compilation failed
```

**Solutions**:
```bash
# Check device tree syntax
dtc -I dts -O dtb recipes-bsp/device-tree/files/*.dts

# Common issues:
# - Missing includes
# - Incorrect node references
# - Syntax errors in properties

# Add verbose compilation
echo 'DTC_FLAGS += "-v"' >> recipes-bsp/device-tree/device-tree-robotics_1.0.bb
```

### Issue 5: Image Build Out of Disk Space

**Symptoms**:
```
ERROR: No space left on device
```

**Solutions**:
```bash
# Check disk usage
df -h tmp/

# Clean unnecessary packages
bitbake -c cleansstate unwanted-package

# Enable rm_work to save space
echo 'INHERIT += "rm_work"' >> conf/local.conf

# Reduce image size
sed -i 's/IMAGE_ROOTFS_EXTRA_SPACE.*/IMAGE_ROOTFS_EXTRA_SPACE = "524288"/' \
    ../meta-jetson-robotics/recipes-core/images/jetson-robotics-image.bb
```

---

## Verification Checklist

- [ ] Layer directory structure created correctly
- [ ] layer.conf has all required variables set
- [ ] Machine configuration inherits from base properly
- [ ] Device tree overlay compiles without errors
- [ ] Kernel config fragments are syntactically correct
- [ ] Distro configuration parses successfully
- [ ] Custom image recipe includes required packages
- [ ] Helper scripts execute without errors
- [ ] README.md documents usage clearly
- [ ] Layer parses without errors (bitbake -p)
- [ ] Custom image builds successfully
- [ ] Resulting image boots on hardware
- [ ] All custom features function correctly

---

## Layer Distribution Best Practices

### Version Control

```bash
cd ~/yocto-jetson/meta-jetson-robotics

# Initialize git repository
git init
git add .
git commit -m "Initial commit of meta-jetson-robotics layer"

# Create version tag
git tag -a v1.0.0 -m "Release version 1.0.0"

# Push to remote repository
git remote add origin https://github.com/your-org/meta-jetson-robotics.git
git push -u origin master
git push --tags
```

### Semantic Versioning

Follow semantic versioning (MAJOR.MINOR.PATCH):
- MAJOR: Incompatible API changes
- MINOR: Backwards-compatible functionality
- PATCH: Backwards-compatible bug fixes

### Testing Matrix

Document tested configurations:

| Yocto Release | Meta-Tegra | Status |
|---------------|------------|--------|
| kirkstone     | kirkstone  | ✓ Tested |
| langdale      | langdale   | ✓ Tested |
| mickledore    | mickledore   | ⚠ Untested |

---

## Next Steps

### Immediate Next Steps
1. Test layer on actual hardware
2. Add CI/CD for automated testing
3. Create additional machine variants

### Proceed to Next Tutorial
**Tutorial 04: First Jetson Boot** - Deploy and validate your custom image

### Advanced Topics
- Creating dynamic layers with BBFILES_DYNAMIC
- Layer compatibility testing
- Publishing layers to OpenEmbedded layer index

---

**Congratulations!** You've created a professional, distributable Yocto layer with custom machine configurations, distro policies, and image recipes. This layer can be shared and maintained as a standalone project.

---

*Tutorial created by the Yocto & Meta-Tegra Multi-Agent Learning System*
*Last updated: 2025-01-15*
