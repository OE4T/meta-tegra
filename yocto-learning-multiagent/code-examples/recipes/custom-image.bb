# Custom Image Recipe for Jetson Platforms
# This recipe demonstrates creating a custom image with specific packages and configurations
#
# Key concepts:
# - Inheriting core-image class
# - Package selection and grouping
# - Image features
# - Post-processing and customization
# - Size constraints and optimization

SUMMARY = "Custom Jetson Image with AI/ML and Development Tools"
DESCRIPTION = "A customized Linux image for NVIDIA Jetson platforms including \
               TensorRT, CUDA, development tools, and custom applications. \
               Suitable for AI/ML development and embedded applications."
LICENSE = "MIT"

# Inherit the core image class which provides basic image building functionality
inherit core-image

# Image features
# These are high-level features that pull in groups of packages
IMAGE_FEATURES += "\
    ssh-server-openssh \
    package-management \
    debug-tweaks \
    tools-debug \
    tools-sdk \
    dev-pkgs \
    splash \
"

# Explanation of IMAGE_FEATURES:
# - ssh-server-openssh: OpenSSH server for remote access
# - package-management: Package manager (opkg/apt) for runtime package installation
# - debug-tweaks: Enable root login, debug tools (useful for development)
# - tools-debug: Debugging tools (gdb, strace, etc.)
# - tools-sdk: Development tools (gcc, make, autotools)
# - dev-pkgs: Development packages (headers, static libs)
# - splash: Boot splash screen support

# Root filesystem type
# Options: ext4, ext3, tar.bz2, tar.gz, cpio, squashfs, etc.
IMAGE_FSTYPES = "ext4 tar.bz2"

# For Jetson, you might also want:
# IMAGE_FSTYPES += "tegraflash"  # For L4T flashing

# Root filesystem size (in KB)
# Set to 0 for automatic size calculation with some overhead
IMAGE_ROOTFS_SIZE ?= "8388608"  # 8GB
IMAGE_ROOTFS_EXTRA_SPACE:append = " + 2097152"  # Add 2GB extra space

# Overhead factor for automatic size calculation
IMAGE_OVERHEAD_FACTOR ?= "1.3"

# Core packages - always included
IMAGE_INSTALL = "\
    packagegroup-core-boot \
    ${CORE_IMAGE_EXTRA_INSTALL} \
"

# NVIDIA Jetson specific packages
IMAGE_INSTALL:append = "\
    cuda-toolkit \
    cudnn \
    tensorrt \
    visionworks \
    opencv \
    l4t-multimedia \
    l4t-camera \
"

# Development tools
IMAGE_INSTALL:append = "\
    python3 \
    python3-pip \
    python3-numpy \
    python3-opencv \
    cmake \
    git \
    vim \
    htop \
    tmux \
"

# Network tools
IMAGE_INSTALL:append = "\
    iproute2 \
    iputils \
    net-tools \
    ethtool \
    wireless-tools \
    wpa-supplicant \
    bluez5 \
"

# Multimedia and camera tools
IMAGE_INSTALL:append = "\
    gstreamer1.0 \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    gstreamer1.0-plugins-tegra \
    v4l-utils \
    ffmpeg \
"

# Custom applications (examples)
IMAGE_INSTALL:append = "\
    simple-app \
    gpio-lib \
    gpio-lib-python \
    kernel-module-template \
"

# System utilities
IMAGE_INSTALL:append = "\
    systemd \
    systemd-analyze \
    udev \
    dbus \
    i2c-tools \
    spi-tools \
    mtd-utils \
"

# Performance and monitoring
IMAGE_INSTALL:append = "\
    perf \
    lttng-tools \
    lttng-ust \
    valgrind \
    stress-ng \
"

# Remove packages (if inherited from base images)
# IMAGE_INSTALL:remove = "package-to-remove"

# Hostname configuration
hostname = "jetson-custom"

# Set root password (only for development!)
# Note: debug-tweaks feature allows empty root password
# For production, remove debug-tweaks and set proper authentication
# EXTRA_USERS_PARAMS = "usermod -P your_password root;"

# Create additional users
EXTRA_USERS_PARAMS += "\
    useradd -p '' nvidia; \
    usermod -a -G video,gpio,i2c,spi,audio nvidia; \
"

# Timezone setting
DEFAULT_TIMEZONE = "America/Los_Angeles"

# Locale configuration
IMAGE_LINGUAS = "en-us"

# Kernel modules to autoload
KERNEL_MODULE_AUTOLOAD += "i2c-dev spidev"

# Custom post-processing function
# This runs after rootfs creation but before image packaging
ROOTFS_POSTPROCESS_COMMAND += "custom_image_postprocess; "

custom_image_postprocess() {
    # Create custom directories
    install -d ${IMAGE_ROOTFS}/opt/nvidia
    install -d ${IMAGE_ROOTFS}/opt/apps
    install -d ${IMAGE_ROOTFS}/data

    # Install custom configuration files
    # echo "Custom configuration" > ${IMAGE_ROOTFS}/etc/custom.conf

    # Set up symbolic links
    # ln -sf /opt/apps ${IMAGE_ROOTFS}/home/nvidia/apps

    # Modify permissions
    chmod 1777 ${IMAGE_ROOTFS}/tmp

    # Create systemd service symlinks (if needed)
    # ln -sf /lib/systemd/system/myservice.service \
    #        ${IMAGE_ROOTFS}/etc/systemd/system/multi-user.target.wants/

    # Clean up unnecessary files to reduce image size
    rm -rf ${IMAGE_ROOTFS}/var/cache/apt/*
    rm -rf ${IMAGE_ROOTFS}/var/lib/apt/lists/*

    # Set CUDA environment variables
    echo 'export PATH=/usr/local/cuda/bin:$PATH' >> ${IMAGE_ROOTFS}/etc/profile
    echo 'export LD_LIBRARY_PATH=/usr/local/cuda/lib64:$LD_LIBRARY_PATH' >> ${IMAGE_ROOTFS}/etc/profile
}

# License manifest generation
COPY_LIC_MANIFEST = "1"
COPY_LIC_DIRS = "1"

# SDK generation (optional)
# This creates an SDK for cross-compilation
# To build SDK: bitbake custom-image -c populate_sdk
# SDK_NAME = "custom-jetson-sdk"

# Image installation scripts (optional)
# For custom installation procedures
# IMAGE_PREPROCESS_COMMAND += "custom_preprocess; "
# IMAGE_POSTPROCESS_COMMAND += "custom_postprocess; "

# Read-only rootfs (for production)
# Uncomment for read-only root filesystem
# IMAGE_FEATURES += "read-only-rootfs"

# Security features (for production)
# Remove debug-tweaks and add security features
# IMAGE_FEATURES:remove = "debug-tweaks"
# EXTRA_IMAGE_FEATURES += "empty-root-password"

# Usage instructions:
# ==================
# 1. Build image:
#    bitbake custom-image
#
# 2. Build SDK:
#    bitbake custom-image -c populate_sdk
#
# 3. Clean build:
#    bitbake custom-image -c cleanall
#
# 4. Check image contents:
#    ls tmp/deploy/images/${MACHINE}/
#
# 5. Check installed packages:
#    oe-pkgdata-util list-pkgs -p tmp/work/${MACHINE}/custom-image/1.0-r0/
#
# 6. Image size analysis:
#    bitbake custom-image -c rootfs -c image_complete
#    du -sh tmp/work/${MACHINE}/custom-image/1.0-r0/rootfs/
#
# 7. Flash to Jetson (example for Jetson Nano):
#    sudo ./flash.sh jetson-nano-devkit mmcblk0p1
#
# 8. Package statistics:
#    buildhistory-collect-srcrevs tmp/deploy/images/${MACHINE}/
#
# Directory structure after build:
# tmp/deploy/images/${MACHINE}/
#   ├── custom-image-${MACHINE}.ext4
#   ├── custom-image-${MACHINE}.tar.bz2
#   ├── custom-image-${MACHINE}.manifest
#   └── custom-image-${MACHINE}.testdata.json
