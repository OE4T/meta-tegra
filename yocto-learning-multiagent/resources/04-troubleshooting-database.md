# Troubleshooting Database for Yocto & Meta-Tegra

## Build Errors

### ERROR: Package X depends on Y but it isn't being built

**Symptom:**
```
ERROR: core-image-minimal-1.0-r0 do_rootfs: Could not invoke dnf.
ERROR: Logfile of failure stored in: .../temp/log.do_rootfs.12345
```

**Common Causes:**
1. Missing recipe dependency
2. Recipe not included in image
3. Package manager database corruption

**Solution:**

```bitbake
# Option 1: Add to image
IMAGE_INSTALL:append = " missing-package"

# Option 2: Add as dependency
RDEPENDS:${PN} += "missing-package"

# Option 3: Clean and rebuild
bitbake -c cleanall problematic-recipe
bitbake core-image-minimal
```

**Prevention:**
- Use `DEPENDS` for build-time dependencies
- Use `RDEPENDS:${PN}` for runtime dependencies
- Check package inclusion with: `bitbake -g image-name && cat pn-buildlist`

---

### ERROR: Nothing PROVIDES X (but Y DEPENDS on it)

**Symptom:**
```
ERROR: Nothing PROVIDES 'virtual/kernel' (but /path/to/recipe DEPENDS on it)
ERROR: Required build target 'core-image-minimal' has no buildable providers.
```

**Common Causes:**
1. Missing layer
2. Incorrect PREFERRED_PROVIDER
3. Typo in dependency name

**Solution:**

```bitbake
# Check available providers
bitbake-layers show-recipes virtual/kernel

# Set provider in local.conf or machine config
PREFERRED_PROVIDER_virtual/kernel = "linux-tegra"

# Ensure layer is added
bitbake-layers add-layer /path/to/meta-tegra

# Verify layer compatibility
bitbake-layers show-layers
```

---

### ERROR: QA Issue: Architecture did not match

**Symptom:**
```
ERROR: QA Issue: Architecture did not match (314 to 183) on /path/to/file [arch]
```

**Common Causes:**
1. Wrong PACKAGE_ARCH setting
2. Mixing architectures
3. Incorrect machine configuration

**Solution:**

```bitbake
# Set correct architecture
PACKAGE_ARCH = "${MACHINE_ARCH}"

# Or for machine-specific packages
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"

# Clean and rebuild
bitbake -c cleansstate recipe-name
```

---

### ERROR: Task X failed with exit code 1

**Symptom:**
```
ERROR: Task (/path/to/recipe.bb:do_compile) failed with exit code 1
```

**Debugging Steps:**

```bash
# 1. Check the log file
cat tmp/work/.../temp/log.do_compile

# 2. Run task with logging
bitbake -c compile recipe-name -v

# 3. Get shell in build environment
bitbake -c devshell recipe-name
# Then manually run compile commands

# 4. Enable debug output
SYSTEMD_OPTIONS = "-v"  # For systemd recipes
DEBUG_BUILD = "1"
```

---

### ERROR: Fetcher failure for URL

**Symptom:**
```
ERROR: Fetcher failure for URL: 'https://example.com/source.tar.gz'
ERROR: The checksum(s) do not match the expected values
```

**Solutions:**

```bitbake
# 1. Update checksum
# Build will show correct checksum, update in recipe:
SRC_URI[sha256sum] = "new-checksum-here"

# 2. Bypass checksum (not recommended for production)
BB_STRICT_CHECKSUM = "0"

# 3. Use mirrors
PREMIRRORS:prepend = "\
    git://.*/.* https://mirror.example.com/sources/ \
    https://.*/.* https://mirror.example.com/sources/ \
"

# 4. Check network connectivity and firewall
```

---

### ERROR: Multiple .bb files are due to be built

**Symptom:**
```
ERROR: Multiple .bb files are due to be built which each provide recipe-name
```

**Solution:**

```bash
# Find duplicate recipes
bitbake-layers show-overlayed

# Increase priority of desired layer
# In meta-custom/conf/layer.conf:
BBFILE_PRIORITY_meta-custom = "10"  # Higher than conflicting layer

# Or use BBMASKto ignore specific recipes
BBMASK += "/path/to/unwanted/recipe.bb"
```

---

## Device Tree Issues

### Device Not Detected / Driver Not Loading

**Symptom:**
```bash
# Device not appearing
ls /dev/device-name  # Not found

# Driver exists but not bound
ls /sys/bus/platform/drivers/driver-name/
# No device symlinks
```

**Debugging Steps:**

```bash
# 1. Check device tree compilation
dtc -I dtb -O dts /boot/devicetree.dtb -o /tmp/decompiled.dts
grep -A 10 "device-name" /tmp/decompiled.dts

# 2. Verify device tree is loaded
ls /proc/device-tree/
ls /sys/firmware/devicetree/base/

# 3. Check if node has status = "okay"
cat /sys/firmware/devicetree/base/path/to/node/status

# 4. Verify compatible string
cat /sys/firmware/devicetree/base/path/to/node/compatible

# 5. Check driver registration
cat /sys/bus/platform/drivers/*/module
```

**Common Fixes:**

```dts
// Ensure status is okay
&device_node {
    status = "okay";  // Not "disabled" or "fail"
};

// Check compatible string matches driver
compatible = "vendor,device-v1";  // Must match driver's of_device_id

// Verify all required properties
reg = <0x12345000 0x1000>;
clocks = <&clock_node>;
interrupts = <GIC_SPI 123 IRQ_TYPE_LEVEL_HIGH>;
```

---

### GPIO Not Working

**Symptom:**
- GPIO cannot be exported
- Wrong pin state
- Permission denied

**Debugging:**

```bash
# Check GPIO controller
ls /sys/class/gpio/

# Check device tree GPIO definition
cat /sys/kernel/debug/gpio

# Verify GPIO number calculation
# Tegra: GPIO(port, offset) = (port * 8) + offset
# Example: GPIO_PH3 = (7 * 8) + 3 = 59

# Check if already claimed
cat /sys/kernel/debug/gpio | grep "gpio-XXX"
```

**Solution:**

```dts
// Use GPIO descriptor (preferred)
&device {
    reset-gpios = <&gpio TEGRA234_MAIN_GPIO(H, 3) GPIO_ACTIVE_LOW>;
};

// Or use GPIO hog for early init
&gpio {
    camera-control {
        gpio-hog;
        gpios = <TEGRA234_MAIN_GPIO(H, 3) 0>;
        output-high;
        label = "camera-enable";
    };
};
```

---

### I2C Device Not Responding

**Symptom:**
```bash
i2cdetect -y 0
# Address shows UU or --
```

**Debugging:**

```bash
# Check I2C bus is enabled
ls /dev/i2c-*

# Scan bus for devices
i2cdetect -y -r 0  # Use -r for read probe

# Check device tree configuration
cat /sys/firmware/devicetree/base/i2c@.../device@.../reg

# View I2C transfers
echo 1 > /sys/kernel/debug/dynamic_debug/control
echo "file i2c-core-base.c +p" > /sys/kernel/debug/dynamic_debug/control
dmesg -w

# Check clock frequency
cat /sys/kernel/debug/clk/clk_summary | grep i2c
```

**Solution:**

```dts
&i2c1 {
    status = "okay";
    clock-frequency = <100000>;  // Try lower speed first

    device@1a {
        compatible = "vendor,device";
        reg = <0x1a>;

        // Ensure all required properties
        clocks = <&cam_mclk>;
        vdd-supply = <&vdd_1v8>;
    };
};
```

---

## Boot Problems

### System Hangs at U-Boot/UEFI

**Symptom:**
- No kernel boot messages
- Stuck at bootloader prompt

**Debugging:**

```bash
# Check boot configuration
cat /boot/extlinux/extlinux.conf

# Verify kernel exists
ls -l /boot/Image

# Check boot arguments
# In extlinux.conf:
APPEND ${cbootargs} console=ttyTCU0,115200 root=/dev/mmcblk0p1 rootwait rw
```

**Common Issues:**

```bash
# 1. Wrong root device
# Fix: Update root= parameter
root=/dev/mmcblk0p1  # For eMMC
root=/dev/sda1        # For USB/SATA

# 2. Missing initramfs (if required)
INITRD /boot/initrd

# 3. Kernel panic - verify kernel config
CONFIG_EXT4_FS=y  # Root filesystem support
CONFIG_DEVTMPFS=y
CONFIG_DEVTMPFS_MOUNT=y
```

---

### Kernel Panic on Boot

**Symptom:**
```
Kernel panic - not syncing: VFS: Unable to mount root fs on unknown-block(0,0)
```

**Solutions:**

```bash
# 1. Wrong root filesystem type
# Add to kernel config:
CONFIG_EXT4_FS=y

# 2. Root device not detected
# Ensure driver is built-in (not module):
CONFIG_MMC=y
CONFIG_MMC_SDHCI=y
CONFIG_MMC_SDHCI_TEGRA=y

# 3. Missing initramfs
# Build and deploy initramfs:
IMAGE_FSTYPES += "cpio.gz"

# 4. Device tree mismatch
# Ensure correct DTB is loaded
# Check extlinux.conf or UEFI configuration
```

---

### Slow Boot Time

**Symptom:**
- Boot takes >60 seconds
- Long delays at specific services

**Debugging:**

```bash
# Analyze boot time
systemd-analyze
systemd-analyze blame
systemd-analyze critical-chain

# Check for failed services
systemctl --failed

# Disable slow services
systemctl disable slow-service.service
systemctl mask unwanted-service.service
```

**Optimization:**

```bitbake
# Disable unnecessary services in image recipe
SYSTEMD_AUTO_ENABLE:pn-service-name = "disable"

# Remove unnecessary packages
PACKAGE_EXCLUDE += "slow-package"

# Optimize kernel
CONFIG_CC_OPTIMIZE_FOR_PERFORMANCE=y  # Not FOR_SIZE
```

---

## Driver Loading Issues

### Module Not Loading Automatically

**Symptom:**
```bash
lsmod | grep module_name
# No output

ls /lib/modules/$(uname -r)/kernel/drivers/
# Module exists
```

**Solutions:**

```bitbake
# 1. Autoload in recipe
KERNEL_MODULE_AUTOLOAD += "module_name"

# 2. Add to /etc/modules-load.d/
echo "module_name" > /etc/modules-load.d/module_name.conf

# 3. Check for missing dependencies
modinfo module_name | grep depends
modprobe -v module_name
```

---

### Module Loading Fails with "Unknown Symbol"

**Symptom:**
```bash
modprobe custom_module
# modprobe: ERROR: could not insert 'custom_module': Unknown symbol in module
```

**Debugging:**

```bash
# Check missing symbols
dmesg | tail
# Look for "Unknown symbol" errors

# Verify kernel version match
modinfo custom_module | grep vermagic
uname -r

# Check symbol dependencies
modprobe --show-depends custom_module
```

**Solution:**

```bitbake
# Rebuild module against correct kernel
bitbake -c cleansstate custom-module
bitbake -c compile custom-module

# Ensure kernel headers match running kernel
KERNEL_VERSION = "${@d.getVar('PREFERRED_VERSION_linux-tegra')}"
```

---

### Driver Claims Device but Doesn't Work

**Symptom:**
- Device shows in sysfs
- No functionality (e.g., camera doesn't stream)

**Debugging:**

```bash
# Check driver binding
ls -l /sys/bus/platform/drivers/driver-name/
# Should show symlinks to devices

# Check for probe errors
dmesg | grep -i "driver-name\|error\|fail"

# Verify device resources
cat /sys/devices/platform/device-name/uevent
cat /proc/iomem | grep device
cat /proc/interrupts | grep device

# Check regulator/clock status
cat /sys/kernel/debug/regulator/regulator-name/enable_count
cat /sys/kernel/debug/clk/clk-name/clk_enable_count
```

---

## Performance Problems

### Poor Video Encode/Decode Performance

**Symptom:**
- Low FPS during encode/decode
- CPU usage high instead of hardware acceleration

**Verification:**

```bash
# Check hardware encoder is used
gst-launch-1.0 videotestsrc ! nvvidconv ! "video/x-raw(memory:NVMM)" ! \
    nvv4l2h264enc ! h264parse ! qtmux ! filesink location=test.mp4

# Monitor hardware utilization
tegrastats

# Check for NVMM allocations
cat /proc/nvmap/iovmm/tegra-carveout/allocations
```

**Solutions:**

```bash
# Ensure proper GStreamer plugins
opkg install gstreamer1.0-plugins-nvvideo4linux2

# Use correct buffer type
nvvidconv ! "video/x-raw(memory:NVMM)"

# Check nvbuf_utils
ldconfig -p | grep libnvbuf

# Verify kernel drivers loaded
lsmod | grep nvhost
```

---

### High CPU Usage / Thermal Throttling

**Symptom:**
- CPU at 100%
- Thermal warnings in dmesg
- Performance degradation

**Debugging:**

```bash
# Check temperatures
cat /sys/devices/virtual/thermal/thermal_zone*/temp

# Check CPU frequency
cat /sys/devices/system/cpu/cpu*/cpufreq/scaling_cur_freq

# Check for throttling
dmesg | grep -i "thermal\|throttl"

# Monitor with tegrastats
tegrastats
```

**Solutions:**

```bash
# 1. Improve cooling
# Check heatsink is properly seated

# 2. Adjust power mode
nvpmodel -m 0  # Max performance
jetson_clocks   # Lock clocks to max

# 3. Optimize application
# Profile with perf
perf record -a -g -- ./application
perf report

# 4. Check for runaway processes
top
```

---

### GPU Not Utilized

**Symptom:**
- GPU idle while expecting workload
- Poor graphics/compute performance

**Verification:**

```bash
# Check GPU stats
cat /sys/devices/gpu.0/load

# Verify CUDA works
/usr/local/cuda/samples/1_Utilities/deviceQuery/deviceQuery

# Check GPU frequency
cat /sys/devices/gpu.0/devfreq/17000000.gpu/cur_freq
```

**Solutions:**

```bash
# Ensure CUDA libraries are installed
ldconfig -p | grep cuda

# Check for GPU railgating (power save)
echo 0 > /sys/devices/gpu.0/railgate_enable

# Set high-performance GPU governor
echo performance > /sys/devices/gpu.0/devfreq/17000000.gpu/governor

# Verify application uses GPU
nvidia-smi  # If available
tegrastats
```

---

## Network Issues

### Ethernet Not Working

**Symptom:**
- No eth0 interface
- Interface exists but no link

**Debugging:**

```bash
# Check interface exists
ip link show

# Check driver loaded
lsmod | grep nvethernet
dmesg | grep -i ethernet

# Check device tree
cat /proc/device-tree/ethernet@*/status

# Check PHY detection
cat /sys/class/net/eth0/phydev/phy_id
```

**Solutions:**

```dts
// Enable ethernet in device tree
&ethernet {
    status = "okay";
    phy-handle = <&phy>;
    phy-mode = "rgmii-id";

    mdio {
        #address-cells = <1>;
        #size-cells = <0>;

        phy: phy@0 {
            reg = <0>;
        };
    };
};
```

```bitbake
# Ensure driver is built
KERNEL_FEATURES:append = " features/ethernet.scc"
```

---

### WiFi Module Not Detected

**Symptom:**
- No wlan0 interface
- M.2 WiFi card not recognized

**Debugging:**

```bash
# Check PCIe detection
lspci
lsusb  # For USB WiFi

# Check kernel driver
lsmod | grep -E "brcm|iwl|ath"

# Check firmware loading
dmesg | grep -i firmware

# Verify module is inserted
ls /sys/class/net/
```

**Solutions:**

```bitbake
# Add WiFi firmware
IMAGE_INSTALL:append = " linux-firmware-bcm43xx"
# Or for Intel:
IMAGE_INSTALL:append = " linux-firmware-iwlwifi"

# Enable driver in kernel
CONFIG_BRCMFMAC=m
CONFIG_BRCMFMAC_PCIE=y
```

---

## Camera & Display Issues

### Camera Not Streaming

**Symptom:**
```bash
v4l2-ctl --list-devices
# Shows device

gst-launch-1.0 nvarguscamerasrc ! fakesink
# Error or timeout
```

**Debugging:**

```bash
# Check V4L2 device
v4l2-ctl -d /dev/video0 --all

# Check for errors
dmesg | grep -i "vi\|csi\|camera"

# Verify camera I2C communication
i2cdetect -y <bus>

# Check camera power/clocks
cat /sys/kernel/debug/clk/clk_summary | grep cam
cat /sys/kernel/debug/regulator/*/enable
```

**Solutions:**

```dts
// Ensure complete camera graph
&nvcsi {
    num-channels = <1>;
    channel@0 {
        // Properly configure ports
    };
};

&vi {
    num-channels = <1>;
    ports {
        // Connect to CSI
    };
};
```

---

### Display Not Working / No Output

**Symptom:**
- No HDMI output
- Black screen

**Debugging:**

```bash
# Check display subsystem
ls /sys/class/drm/

# Check connector status
cat /sys/class/drm/card*/card*/status

# Check modes
cat /sys/class/drm/card*/card*/modes

# Test framebuffer
cat /dev/urandom > /dev/fb0
```

**Solutions:**

```bash
# Force HDMI detection
echo 1 > /sys/class/drm/card0-HDMI-A-1/status

# Check X server logs
cat /var/log/Xorg.0.log

# Use correct driver
# For Tegra, ensure tegra-drm or nvidia-drm is loaded
```

---

*Last Updated: 2025-11-18*
*Maintained by: Documentation Researcher Agent*
