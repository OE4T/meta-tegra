# Tutorial 01: Your First Yocto Build
## Building a Custom Linux Image for Jetson

---

## Learning Objectives

After completing this tutorial, you will be able to:
- Set up a Yocto build environment from scratch
- Understand the basic Yocto project structure
- Configure a build for Jetson Orin hardware
- Build your first bootable Linux image
- Deploy and boot the image on Jetson hardware

---

## Prerequisites Checklist

- [ ] Ubuntu 20.04 or 22.04 host system (or compatible Linux distribution)
- [ ] At least 100GB of free disk space
- [ ] 8GB RAM minimum (16GB+ recommended)
- [ ] Fast internet connection for downloading source packages
- [ ] Git installed and configured
- [ ] Basic Linux command line knowledge
- [ ] Jetson Orin device (AGX, NX, or Nano)

---

## Estimated Duration

**Total Time**: 4-6 hours
- Initial setup: 30 minutes
- Source download: 1-2 hours (depends on internet speed)
- First build: 2-3 hours (depends on CPU cores)
- Deployment and testing: 30 minutes

---

## Step-by-Step Instructions

### Step 1: Install Required Host Packages

First, ensure your host system has all required dependencies:

```bash
# Update package lists
sudo apt update

# Install essential Yocto build dependencies
sudo apt install -y \
    gawk wget git diffstat unzip texinfo gcc build-essential \
    chrpath socat cpio python3 python3-pip python3-pexpect \
    xz-utils debianutils iputils-ping python3-git python3-jinja2 \
    libegl1-mesa libsdl1.2-dev pylint xterm python3-subunit \
    mesa-common-dev zstd liblz4-tool

# Install additional useful tools
sudo apt install -y \
    vim screen tmux htop tree

# Verify git configuration
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

**Explanation**: These packages provide compilers, build tools, and Python libraries that BitBake (Yocto's build engine) requires. The build will fail without them.

### Step 2: Create Workspace Structure

Organize your Yocto workspace with a clear directory structure:

```bash
# Create workspace directory
mkdir -p ~/yocto-jetson
cd ~/yocto-jetson

# Create a directory for build outputs (separate from sources)
mkdir -p builds/jetson-orin-agx
```

**Explanation**: Keeping sources and builds separate makes it easier to manage multiple build configurations and clean up when needed.

### Step 3: Clone Yocto and Meta-Tegra Layers

Download the required metadata layers:

```bash
# Clone Poky (Yocto reference distribution)
git clone -b kirkstone git://git.yoctoproject.org/poky.git
cd poky

# Clone OpenEmbedded meta-layer
git clone -b kirkstone git://git.openembedded.org/meta-openembedded

# Clone meta-tegra for NVIDIA Jetson support
git clone -b kirkstone https://github.com/OE4T/meta-tegra.git

# Verify all layers are checked out on same branch
git -C . describe --all
git -C meta-openembedded describe --all
git -C meta-tegra describe --all
```

**Explanation**:
- **Poky**: The reference Yocto distribution containing core build tools
- **meta-openembedded**: Additional recipes for common open-source packages
- **meta-tegra**: NVIDIA Jetson-specific BSP (Board Support Package)
- Using the same branch (kirkstone) ensures compatibility

### Step 4: Initialize Build Environment

Set up the BitBake build environment:

```bash
# Source the environment setup script
# This adds BitBake to your PATH and sets up build variables
source oe-init-build-env ../builds/jetson-orin-agx

# You're now in the build directory
pwd  # Should show: ~/yocto-jetson/builds/jetson-orin-agx
```

**Explanation**: The `oe-init-build-env` script:
- Creates the build directory if it doesn't exist
- Generates initial configuration files
- Sets environment variables for BitBake
- Changes your working directory to the build folder

### Step 5: Configure Build Layers

Edit `conf/bblayers.conf` to add required layers:

```bash
# Backup original configuration
cp conf/bblayers.conf conf/bblayers.conf.orig

# Edit the file
cat > conf/bblayers.conf << 'EOF'
# POKY_BBLAYERS_CONF_VERSION is increased each time build/conf/bblayers.conf
# changes incompatibly
POKY_BBLAYERS_CONF_VERSION = "2"

BBPATH = "${TOPDIR}"
BBFILES ?= ""

# Yocto workspace root directory
YOCTO_ROOT = "${TOPDIR}/../../poky"

BBLAYERS ?= " \
  ${YOCTO_ROOT}/meta \
  ${YOCTO_ROOT}/meta-poky \
  ${YOCTO_ROOT}/meta-yocto-bsp \
  ${YOCTO_ROOT}/meta-openembedded/meta-oe \
  ${YOCTO_ROOT}/meta-openembedded/meta-python \
  ${YOCTO_ROOT}/meta-openembedded/meta-networking \
  ${YOCTO_ROOT}/meta-openembedded/meta-multimedia \
  ${YOCTO_ROOT}/meta-tegra \
  "
EOF
```

**Explanation**: This configuration tells BitBake where to find recipes. The order matters - layers listed first have higher priority.

### Step 6: Configure Build Settings

Edit `conf/local.conf` for Jetson-specific settings:

```bash
# Backup original
cp conf/local.conf conf/local.conf.orig

# Add Jetson-specific configurations
cat >> conf/local.conf << 'EOF'

#
# Jetson Orin AGX Configuration
#
MACHINE = "jetson-orin-agx-devkit"

# Parallel build settings - adjust based on your CPU
# Formula: 1.5 x number of CPU cores
BB_NUMBER_THREADS = "8"
PARALLEL_MAKE = "-j 8"

# Download directory (shared across builds to save time/space)
DL_DIR = "${TOPDIR}/../../downloads"

# Shared state cache (speeds up rebuilds)
SSTATE_DIR = "${TOPDIR}/../../sstate-cache"

# Disk space monitoring (prevent out-of-space failures)
BB_DISKMON_DIRS = "\
    STOPTASKS,${TMPDIR},1G,100K \
    STOPTASKS,${DL_DIR},1G,100K \
    STOPTASKS,${SSTATE_DIR},1G,100K \
    STOPTASKS,/tmp,100M,100K \
    HALT,${TMPDIR},100M,1K \
    HALT,${DL_DIR},100M,1K \
    HALT,${SSTATE_DIR},100M,1K \
    HALT,/tmp,10M,1K"

# Enable systemd (modern init system)
DISTRO_FEATURES:append = " systemd"
VIRTUAL-RUNTIME_init_manager = "systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"
VIRTUAL-RUNTIME_initscripts = ""

# Accept NVIDIA EULA for proprietary components
NVIDIA_EULA_ACCEPT = "1"

# Package management (for installing additional packages later)
EXTRA_IMAGE_FEATURES ?= "debug-tweaks package-management ssh-server-openssh"

# Root password for development (CHANGE THIS FOR PRODUCTION!)
EXTRA_USERS_PARAMS = "usermod -P root root;"

EOF
```

**Explanation**:
- **MACHINE**: Specifies the target hardware
- **BB_NUMBER_THREADS**: Controls how many BitBake tasks run in parallel
- **PARALLEL_MAKE**: Controls parallel compilation within each package
- **DL_DIR/SSTATE_DIR**: Shared caches that speed up subsequent builds
- **EXTRA_IMAGE_FEATURES**: Adds debugging tools and SSH access

### Step 7: Start Your First Build

Build a minimal bootable image:

```bash
# Build the core image (this will take 2-3 hours on first run)
bitbake core-image-minimal

# Monitor build progress in another terminal
# In a new terminal, run:
tail -f tmp/log/cooker/*/console-latest.log
```

**Explanation**: `core-image-minimal` creates a small Linux system with just the essentials. This is the fastest image to build for initial testing.

**Expected output during build**:
```
Parsing recipes: 100% |###################################| Time: 0:02:15
Parsing of 2847 .bb files complete (0 cached, 2847 parsed). 4012 targets,
284 skipped, 0 masked, 0 errors.
NOTE: Resolving any missing task queue dependencies

Build Configuration:
BB_VERSION           = "2.0.0"
BUILD_SYS            = "x86_64-linux"
NATIVELSBSTRING      = "ubuntu-22.04"
TARGET_SYS           = "aarch64-oe-linux"
MACHINE              = "jetson-orin-agx-devkit"
DISTRO               = "poky"
DISTRO_VERSION       = "4.0.17"
TUNE_FEATURES        = "aarch64 armv8a crc crypto"
TARGET_FPU           = ""

NOTE: Executing Tasks
Currently  5 running tasks (1234 of 3567)
```

### Step 8: Verify Build Artifacts

After successful build, check the generated files:

```bash
# Navigate to image output directory
cd tmp/deploy/images/jetson-orin-agx-devkit/

# List generated files
ls -lh

# Key files you should see:
# - Image                          : Linux kernel
# - core-image-minimal-*.rootfs.tar.gz : Root filesystem
# - *.dtb                          : Device tree blobs
# - uefi_jetson.bin                : UEFI bootloader
```

**Expected files**:
```
Image -> Image--5.10+git...
core-image-minimal-jetson-orin-agx-devkit.rootfs.tar.gz
tegra234-p3701-0000-p3737-0000.dtb
uefi_jetson.bin
```

### Step 9: Create Flashable Image

Generate a complete flash package:

```bash
# Still in the images directory
cd ~/yocto-jetson/builds/jetson-orin-agx

# Create the tegraflash package
bitbake -c do_image_tegraflash core-image-minimal

# The flashable tarball is created at:
ls -lh tmp/deploy/images/jetson-orin-agx-devkit/*tegraflash.tar.gz
```

### Step 10: Deploy to Jetson Hardware

Flash the image to your Jetson device:

```bash
# Extract the flash package
cd ~/yocto-jetson/flash
tar xzf ../builds/jetson-orin-agx/tmp/deploy/images/jetson-orin-agx-devkit/core-image-minimal-jetson-orin-agx-devkit.tegraflash.tar.gz

# Put Jetson into recovery mode:
# 1. Power off the device
# 2. Connect USB-C cable between Jetson and host
# 3. Hold RECOVERY button
# 4. Press and release POWER button
# 5. Release RECOVERY button after 2 seconds

# Verify device is in recovery mode
lsusb | grep -i nvidia
# Should show: "NVIDIA Corp. APX"

# Flash the device
sudo ./doflash.sh

# Wait for flashing to complete (5-10 minutes)
# You'll see progress updates and finally "Flash complete"
```

### Step 11: First Boot and Verification

Boot your Jetson with the new image:

```bash
# After flashing completes:
# 1. Disconnect USB cable
# 2. Connect serial console (optional but recommended)
# 3. Connect Ethernet cable
# 4. Power on the device

# Connect via serial console (from host)
# Find the device (usually /dev/ttyUSB0 or /dev/ttyACM0)
sudo screen /dev/ttyUSB0 115200

# Or connect via SSH (after device boots)
# Find IP address from your router or use mDNS
ssh root@jetson-orin-agx-devkit.local
# Password: root (as configured in local.conf)

# Once logged in, verify the system
uname -a
# Should show: Linux jetson-orin-agx-devkit 5.10.xxx aarch64 GNU/Linux

cat /etc/os-release
# Should show your Yocto-built image details

# Check available disk space
df -h

# Verify system is using systemd
ps aux | grep systemd

# Check kernel modules
lsmod | head
```

---

## Troubleshooting Common Issues

### Issue 1: Build Fails with "No space left on device"

**Symptoms**:
```
ERROR: No space left on device or exceeds fs.inotify.max_user_watches
```

**Solutions**:
```bash
# Check disk space
df -h /

# If disk space is low, clean old build artifacts
cd ~/yocto-jetson/builds/jetson-orin-agx
bitbake -c clean core-image-minimal
rm -rf tmp/

# Increase inotify watches limit
echo "fs.inotify.max_user_watches=524288" | sudo tee -a /etc/sysctl.conf
sudo sysctl -p
```

### Issue 2: Build Hangs at "Pseudo is not running"

**Symptoms**: Build appears frozen during do_rootfs task

**Solutions**:
```bash
# Kill stale pseudo processes
killall pseudo

# Clear pseudo database
rm -rf tmp/work/jetson-orin-agx-devkit/core-image-minimal/*/pseudo/

# Restart build
bitbake core-image-minimal
```

### Issue 3: Git Clone Failures

**Symptoms**:
```
ERROR: Fetcher failure: Unable to fetch URL from any source
```

**Solutions**:
```bash
# Check internet connectivity
ping -c 3 git.yoctoproject.org

# Clear download cache for problematic package
rm -rf downloads/git2/*

# Retry with verbose logging
bitbake -v -k core-image-minimal
```

### Issue 4: Device Not Detected in Recovery Mode

**Symptoms**: `lsusb` doesn't show NVIDIA device

**Solutions**:
```bash
# Check USB cable connection (use the USB-C port next to power)

# Install NVIDIA USB drivers
sudo apt install nvidia-l4t-tools

# Try different USB port on host (USB 3.0 preferred)

# Verify recovery mode entry:
# - LED should be steady (not blinking)
# - Check dmesg output:
dmesg | tail -20
# Should show NVIDIA APX device
```

### Issue 5: Flash Hangs at "Waiting for device"

**Symptoms**: Flash script waits indefinitely for device

**Solutions**:
```bash
# Check device is in recovery mode
lsusb | grep -i nvidia

# Run flash with verbose output
sudo ./doflash.sh -v

# If still fails, use manual flash mode:
sudo ./flash.sh jetson-orin-agx-devkit mmcblk0p1
```

### Issue 6: Image Too Large for Flash

**Symptoms**:
```
Error: Image size exceeds partition size
```

**Solutions**:
```bash
# Check image size
ls -lh tmp/deploy/images/jetson-orin-agx-devkit/*.img

# Reduce image size in local.conf
echo 'IMAGE_ROOTFS_EXTRA_SPACE = "0"' >> conf/local.conf

# Use a smaller base image
bitbake core-image-base  # Instead of full image

# Rebuild
bitbake -c clean core-image-minimal
bitbake core-image-minimal
```

---

## Verification Checklist

- [ ] Host system has all required packages installed
- [ ] All metadata layers cloned successfully
- [ ] Build configuration files created and edited
- [ ] `bitbake core-image-minimal` completes without errors
- [ ] Flash package generated in deploy/images directory
- [ ] Jetson device enters recovery mode (visible in lsusb)
- [ ] Flash process completes successfully
- [ ] Device boots and shows login prompt
- [ ] Can log in as root with configured password
- [ ] System information shows correct kernel and architecture
- [ ] Network connectivity works (can ping external hosts)

---

## Key Concepts Learned

### BitBake Basics
- **Recipe**: A `.bb` file that describes how to build a software package
- **Layer**: A collection of related recipes and configuration
- **Image**: A complete bootable system built from many recipes
- **Task**: An action BitBake performs (e.g., do_compile, do_install)

### Yocto Directory Structure
```
poky/                           # Yocto root
├── meta/                       # Core recipes
├── meta-poky/                  # Poky distribution config
├── meta-tegra/                 # Jetson-specific recipes
└── build/
    ├── conf/                   # Build configuration
    │   ├── local.conf         # Machine and build settings
    │   └── bblayers.conf      # Layer configuration
    ├── downloads/              # Source tarballs (cached)
    ├── sstate-cache/           # Compiled artifacts (cached)
    └── tmp/
        ├── deploy/images/      # Final build outputs
        └── work/               # Build workspace
```

### Meta-Tegra Architecture
Meta-tegra provides:
- BSP recipes for bootloader, kernel, and firmware
- Machine configurations for all Jetson platforms
- NVIDIA proprietary driver integration
- Flash tool integration for deployment

---

## Next Steps

### Immediate Next Steps
1. **Explore the built image**:
   - SSH into your Jetson
   - Explore the filesystem structure
   - Check running processes with `ps aux`

2. **Build a larger image**:
   ```bash
   bitbake core-image-base  # Includes more utilities
   ```

3. **Add packages to your image**:
   Edit `conf/local.conf`:
   ```bash
   IMAGE_INSTALL:append = " htop vim python3"
   ```

### Proceed to Next Tutorial
**Tutorial 02: Creating Custom Recipes** - Learn to add your own applications to the image

### Advanced Exploration
- Experiment with different MACHINE configurations (Orin NX, Orin Nano)
- Try different image recipes (core-image-full-cmdline, core-image-weston)
- Enable GPU and multimedia support

### Resources for Continued Learning
- Yocto Project Documentation: https://docs.yoctoproject.org/
- Meta-Tegra Wiki: https://github.com/OE4T/meta-tegra/wiki
- NVIDIA Jetson Linux Developer Guide
- Yocto Project Dev Manual: https://docs.yoctoproject.org/dev-manual/

---

## Build Time Optimization Tips

For subsequent builds:

```bash
# Enable build history (helps debug)
echo 'INHERIT += "buildhistory"' >> conf/local.conf
echo 'BUILDHISTORY_COMMIT = "1"' >> conf/local.conf

# Use rm_work to save disk space (removes temp files after each package)
echo 'INHERIT += "rm_work"' >> conf/local.conf

# Enable parallel downloads
echo 'BB_FETCH_PREMIRRORONLY = "0"' >> conf/local.conf
echo 'BB_NO_NETWORK = "0"' >> conf/local.conf
```

---

**Congratulations!** You've successfully built and deployed your first Yocto-based Linux image for NVIDIA Jetson. You now have a foundation for creating custom embedded Linux systems.

---

*Tutorial created by the Yocto & Meta-Tegra Multi-Agent Learning System*
*Last updated: 2025-01-15*
