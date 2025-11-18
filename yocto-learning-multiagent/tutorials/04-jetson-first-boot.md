# Tutorial 04: First Jetson Deployment
## Flashing, Booting, and Validating Your Custom Image

---

## Learning Objectives

After completing this tutorial, you will be able to:
- Prepare Jetson hardware for flashing
- Use NVIDIA flash tools with Yocto images
- Configure serial console access
- Perform first boot validation
- Debug boot failures
- Set up remote access (SSH, network)
- Verify hardware functionality

---

## Prerequisites Checklist

- [ ] Completed Tutorial 01-03 (Yocto basics through layer creation)
- [ ] Successfully built a Yocto image for Jetson
- [ ] NVIDIA Jetson device (Orin AGX, Orin NX, or Orin Nano)
- [ ] USB-C cable for flashing
- [ ] Host computer running Linux (Ubuntu 20.04/22.04 recommended)
- [ ] Serial console cable (USB-to-UART, optional but recommended)
- [ ] Power supply for Jetson (19V for Orin AGX)
- [ ] Basic understanding of Linux boot process

---

## Estimated Duration

**Total Time**: 2-3 hours
- Hardware setup: 30 minutes
- Flash preparation: 30 minutes
- Flashing process: 20-30 minutes
- First boot and validation: 30-60 minutes
- Network configuration: 15-30 minutes

---

## Step-by-Step Instructions

### Step 1: Verify Build Artifacts

Before flashing, ensure your build completed successfully:

```bash
cd ~/yocto-jetson/builds/jetson-orin-agx

# Check that the build completed
ls -lh tmp/deploy/images/jetson-orin-agx-devkit/

# Key files to verify:
# 1. Kernel image
ls -lh tmp/deploy/images/jetson-orin-agx-devkit/Image*

# 2. Root filesystem
ls -lh tmp/deploy/images/jetson-orin-agx-devkit/*.tar.gz

# 3. Device tree blobs
ls -lh tmp/deploy/images/jetson-orin-agx-devkit/*.dtb

# 4. Bootloader
ls -lh tmp/deploy/images/jetson-orin-agx-devkit/*uefi*.bin

# 5. Tegraflash package (most important for flashing)
ls -lh tmp/deploy/images/jetson-orin-agx-devkit/*tegraflash*.tar.gz
```

**Expected output**:
```
-rw-r--r-- 1 user user  37M Jan 15 10:30 Image
-rw-r--r-- 1 user user 512M Jan 15 10:45 core-image-minimal-jetson-orin-agx-devkit.tar.gz
-rw-r--r-- 1 user user 245K Jan 15 10:30 tegra234-p3701-0000-p3737-0000.dtb
-rw-r--r-- 1 user user 8.5M Jan 15 10:28 uefi_jetson.bin
-rw-r--r-- 1 user user 1.2G Jan 15 10:50 core-image-minimal-jetson-orin-agx-devkit.tegraflash.tar.gz
```

### Step 2: Extract Tegraflash Package

The tegraflash package contains everything needed to flash the Jetson:

```bash
# Create a flash workspace
mkdir -p ~/jetson-flash
cd ~/jetson-flash

# Extract the tegraflash archive
tar xzf ~/yocto-jetson/builds/jetson-orin-agx/tmp/deploy/images/jetson-orin-agx-devkit/*tegraflash.tar.gz

# Verify contents
ls -lh

# You should see:
# - doflash.sh           : Main flash script
# - initrd               : Initial ramdisk for flashing
# - kernel/Image         : Linux kernel
# - bootloader/*         : UEFI and other bootloaders
# - *.dtb                : Device tree blobs
# - *.conf               : Flash configuration files
# - *.img                : Partition images
```

**Explanation**: The tegraflash package is a self-contained flash environment that includes all necessary binaries, scripts, and configuration files.

### Step 3: Set Up Serial Console (Optional but Recommended)

Serial console provides critical boot debugging information:

```bash
# Install serial terminal software
sudo apt install screen picocom minicom

# Connect USB-to-UART adapter to Jetson's 40-pin header:
# Pin 6  (GND)  -> Adapter GND
# Pin 8  (TXD)  -> Adapter RX
# Pin 10 (RXD)  -> Adapter TX

# Find the serial device
ls /dev/ttyUSB*
# Or for built-in adapters:
ls /dev/ttyACM*

# Test connection (typical device is /dev/ttyUSB0)
# Note: Don't do this yet if device isn't connected

# Add your user to dialout group for serial access
sudo usermod -aG dialout $USER

# Log out and back in for group change to take effect
```

**Pin Diagram for Jetson Orin AGX 40-pin Header**:
```
        3.3V (1) (2)  5V
   I2C2_SDA (3) (4)  5V
   I2C2_SCL (5) (6)  GND  <- Connect to Adapter GND
GPIO29_PQ.5 (7) (8)  TXD <- Connect to Adapter RX
        GND (9) (10) RXD <- Connect to Adapter TX
       ...
```

### Step 4: Put Jetson into Recovery Mode

Recovery mode allows flashing via USB:

```bash
# For Jetson Orin AGX:
# 1. Power off the device (unplug power)
# 2. Connect USB-C cable from Jetson (USB-C port near power) to host PC
# 3. Locate the RECOVERY button (usually labeled, near GPIO header)
# 4. Locate the POWER button (front of carrier board)
# 5. Hold down RECOVERY button
# 6. While holding RECOVERY, press and release POWER button
# 7. Continue holding RECOVERY for 2-3 seconds
# 8. Release RECOVERY button

# Verify device is in recovery mode
lsusb | grep -i nvidia

# Expected output:
# Bus 001 Device 015: ID 0955:7023 NVIDIA Corp. APX
```

**Troubleshooting recovery mode**:
```bash
# If device not detected:

# 1. Check USB cable (must support data, not just charging)
# 2. Try different USB port on host (USB 3.0 preferred)
# 3. Check dmesg output
dmesg | tail -30

# You should see:
# [12345.678901] usb 1-2: new high-speed USB device number 15 using xhci_hcd
# [12345.789012] usb 1-2: New USB device found, idVendor=0955, idProduct=7023

# 4. Ensure recovery button sequence is correct
# 5. Some boards may need to short recovery pins instead of button
```

### Step 5: Flash the Jetson Device

Now flash your custom Yocto image:

```bash
# Ensure you're in the flash directory
cd ~/jetson-flash

# Run the flash script with sudo
sudo ./doflash.sh

# Alternative: specify device explicitly
# sudo ./flash.sh jetson-orin-agx-devkit mmcblk0p1
```

**Flash process output**:
```
*****************************************
*                                       *
*  Step 1: Uploading bootloader        *
*                                       *
*****************************************
[   0.1234 ] tegrarcm_v2 --chip 0x23 0 --rcm rcm_2_encrypt.rcm
[   0.1245 ] Bootloader Version: 35.5.0 | Build Time: 14:22:18
[   0.2456 ] Uploading BCT...
[   1.3456 ] BCT upload complete
[   1.3457 ] Uploading bootloader...
[   3.4567 ] Bootloader upload complete

*****************************************
*                                       *
*  Step 2: Flashing partitions         *
*                                       *
*****************************************
[   3.5678 ] Writing partition: A_kernel
[   5.6789 ] Writing partition: A_kernel-dtb
[   6.7890 ] Writing partition: A_rootfs (this may take a while...)
[  45.1234 ] Writing partition: A_rootfs complete
[  45.2345 ] Writing partition: esp
[  46.3456 ] All partitions written successfully

*****************************************
*                                       *
*  Step 3: Finalizing                  *
*                                       *
*****************************************
[  46.4567 ] Flashing completed successfully
[  46.4568 ] Device will reboot automatically
```

**Explanation**:
- Flash process typically takes 10-20 minutes
- Rootfs partition takes the longest (largest partition)
- Progress is shown for each partition
- Device auto-reboots after successful flash

### Step 6: Connect Serial Console

Before the device finishes booting, connect to serial console:

```bash
# In a new terminal, connect to serial console
# Replace /dev/ttyUSB0 with your actual device
sudo screen /dev/ttyUSB0 115200

# Or using picocom:
sudo picocom -b 115200 /dev/ttyUSB0

# Or using minicom:
sudo minicom -D /dev/ttyUSB0 -b 115200

# To exit screen: Ctrl+A, then k, then y
# To exit picocom: Ctrl+A, then Ctrl+X
# To exit minicom: Ctrl+A, then q
```

### Step 7: Monitor First Boot

Watch the boot process via serial console:

```
[    0.000000] Booting Linux on physical CPU 0x0000000000 [0x410fd421]
[    0.000000] Linux version 5.10.120-tegra (oe-user@oe-host) (gcc)
[    0.000000] Machine model: NVIDIA Jetson AGX Orin Developer Kit
[    0.000000] earlycon: uart8250 at MMIO32 0x03100000 (options '115200n8')
[    0.000000] printk: bootconsole [uart8250] enabled

... (many boot messages) ...

[    3.456789] tegra-xusb 3610000.usb: Firmware version: 60.09 release
[    4.123456] nvgpu: 17000000.gpu gv11b_init_hal:150 [INFO]  chip=0x23a
[    5.234567] CUDA Driver Version: 11.4
[    6.345678] Ethernet link is UP - 1000/Full

... (systemd initialization) ...

[  OK  ] Started Serial Getty on ttyTCU0
[  OK  ] Started Getty on tty1
[  OK  ] Reached target Login Prompts

Jetson Orin AGX Developer Kit - Yocto 4.0.17 (kirkstone)
jetson-orin-agx-devkit login:
```

**Key boot milestones to watch for**:
- Bootloader initialization
- Kernel decompression and start
- Device tree loading
- GPU initialization (nvgpu)
- Network interface initialization
- Systemd target reached
- Login prompt

### Step 8: First Login and System Validation

Log in and validate the system:

```bash
# At login prompt (via serial or SSH)
login: root
password: root  # Or whatever you configured in local.conf

# Verify kernel version
uname -a
# Output: Linux jetson-orin-agx-devkit 5.10.120-tegra #1 SMP PREEMPT ...

# Check system info
cat /etc/os-release

# Output:
# ID=poky
# NAME="Poky (Yocto Project Reference Distro)"
# VERSION="4.0.17 (kirkstone)"
# VERSION_ID=4.0.17
# PRETTY_NAME="Poky (Yocto Project Reference Distro) 4.0.17 (kirkstone)"

# Verify Jetson model
cat /proc/device-tree/model
# Output: NVIDIA Jetson AGX Orin Developer Kit

# Check CPU info
cat /proc/cpuinfo | grep -E "processor|model name|cpu cores"

# Check memory
free -h

# Verify storage
df -h

# Check loaded kernel modules
lsmod | head -20

# Verify systemd status
systemctl status

# Check for any failed services
systemctl --failed
```

### Step 9: Validate Hardware Components

Test key hardware subsystems:

```bash
# === GPIO Validation ===
# List GPIO controllers
ls -la /sys/class/gpio/

# Expected: gpiochip0, gpiochip1, export, unexport

# Check GPIO debug info (if debugfs is mounted)
cat /sys/kernel/debug/gpio

# === I2C Validation ===
# List I2C buses
ls /dev/i2c-*

# Scan I2C bus (replace X with bus number)
i2cdetect -y -r 1

# === Network Validation ===
# Check network interfaces
ip addr show

# Test connectivity
ping -c 3 8.8.8.8

# Check routing
ip route show

# === USB Validation ===
# List USB devices
lsusb

# === PCI Validation ===
# List PCI devices (including NVMe, PCIe)
lspci

# === GPU Validation ===
# Check CUDA availability
ls /dev/nvidia*

# Expected: /dev/nvidia0, /dev/nvidiactl, /dev/nvidia-modeset

# === Thermal Zones ===
# Check temperatures
cat /sys/class/thermal/thermal_zone*/temp

# Or with friendly names:
for zone in /sys/class/thermal/thermal_zone*; do
    type=$(cat $zone/type 2>/dev/null)
    temp=$(cat $zone/temp 2>/dev/null)
    if [ -n "$temp" ]; then
        echo "$type: $((temp/1000))°C"
    fi
done

# === Power Status ===
# Check power supplies
ls /sys/class/power_supply/

# Check battery/power info
cat /sys/class/power_supply/*/status 2>/dev/null
```

### Step 10: Configure Network Access

Set up network for remote access:

```bash
# === Ethernet Configuration ===
# Check interface name
ip link show

# Typical output: eth0, enp0s1, etc.

# Configure static IP (if needed)
cat > /etc/systemd/network/20-wired.network << 'EOF'
[Match]
Name=eth0

[Network]
Address=192.168.1.100/24
Gateway=192.168.1.1
DNS=8.8.8.8
DNS=8.8.4.4
EOF

# Restart networkd
systemctl restart systemd-networkd

# Or use DHCP (default in most images)
cat > /etc/systemd/network/20-wired.network << 'EOF'
[Match]
Name=eth0

[Network]
DHCP=yes
EOF

systemctl restart systemd-networkd

# Wait a few seconds for DHCP
sleep 5

# Check IP address
ip addr show eth0

# === SSH Configuration ===
# Verify SSH server is running
systemctl status sshd

# If not running, start it
systemctl start sshd
systemctl enable sshd

# Test SSH from host computer:
# ssh root@<jetson-ip-address>

# === WiFi Configuration (if available) ===
# List wireless interfaces
iw dev

# Scan for networks
iw dev wlan0 scan | grep SSID

# Connect to WiFi using wpa_supplicant
wpa_passphrase "YourSSID" "YourPassword" >> /etc/wpa_supplicant.conf

# Start wpa_supplicant
wpa_supplicant -B -i wlan0 -c /etc/wpa_supplicant.conf

# Get IP via DHCP
dhclient wlan0
```

### Step 11: Performance Benchmarking

Validate system performance:

```bash
# === CPU Performance ===
# Install stress test (if included in image)
# Otherwise, use this simple test:
cat > /tmp/cpu-test.sh << 'EOF'
#!/bin/bash
echo "CPU Performance Test"
time dd if=/dev/zero bs=1M count=1024 | md5sum
EOF

chmod +x /tmp/cpu-test.sh
/tmp/cpu-test.sh

# === Memory Performance ===
cat > /tmp/mem-test.sh << 'EOF'
#!/bin/bash
echo "Memory Performance Test"
# Allocate and write 1GB
time dd if=/dev/zero of=/tmp/test.img bs=1M count=1024
# Read back
time dd if=/tmp/test.img of=/dev/null bs=1M
rm /tmp/test.img
EOF

chmod +x /tmp/mem-test.sh
/tmp/mem-test.sh

# === Storage Performance ===
# Write test
dd if=/dev/zero of=/tmp/testfile bs=1M count=1024 conv=fdatasync

# Read test
echo 3 > /proc/sys/vm/drop_caches  # Clear cache
dd if=/tmp/testfile of=/dev/null bs=1M

rm /tmp/testfile

# === GPU Check ===
# If CUDA samples are installed:
# /usr/local/cuda/samples/bin/aarch64/linux/release/deviceQuery

# Or check CUDA device
cat > /tmp/cuda-check.py << 'EOF'
#!/usr/bin/env python3
import os
print("Checking CUDA devices...")
devices = [d for d in os.listdir('/dev') if d.startswith('nvidia')]
if devices:
    print(f"Found CUDA devices: {devices}")
else:
    print("No CUDA devices found")
EOF

python3 /tmp/cuda-check.py
```

---

## Troubleshooting Common Issues

### Issue 1: Device Not Entering Recovery Mode

**Symptoms**: `lsusb` doesn't show NVIDIA APX device

**Solutions**:
```bash
# 1. Check USB connection
# - Use USB-C port closest to power connector
# - Try different USB cable (must support data)
# - Try different USB port on host (USB 3.0 preferred)

# 2. Verify button sequence
# Some boards have different recovery procedures:

# Method 1: Recovery + Power buttons
# - Hold RECOVERY
# - Press and release POWER
# - Hold RECOVERY for 2-3 more seconds
# - Release RECOVERY

# Method 2: Force Recovery pins (if buttons don't work)
# - Locate FC REC pin on GPIO header (pin 9 or 10)
# - Short FC REC to GND (pin 6)
# - Power on device
# - Remove short after 2 seconds

# 3. Check kernel messages
dmesg -w
# Then connect device - you should see USB messages

# 4. Install NVIDIA tools
sudo apt install nvidia-l4t-tools
```

### Issue 2: Flash Fails with "USB Device Not Found"

**Symptoms**:
```
Error: No APX device found. Make sure device is in recovery mode.
```

**Solutions**:
```bash
# 1. Re-enter recovery mode
# Power cycle and try recovery mode again

# 2. Check USB permissions
sudo chmod 666 /dev/bus/usb/*/$(lsusb | grep NVIDIA | cut -d' ' -f4 | tr -d ':')

# 3. Run flash with verbose mode
sudo ./doflash.sh -v

# 4. Check for conflicting processes
ps aux | grep tegrarcm
# Kill any existing processes

# 5. Try manual flash command
sudo ./flash.sh jetson-orin-agx-devkit mmcblk0p1
```

### Issue 3: Device Boots But No Serial Output

**Symptoms**: No text appears on serial console

**Solutions**:
```bash
# 1. Verify serial connection
# Check wiring: TX -> RX, RX -> TX, GND -> GND

# 2. Check baud rate
# Must be 115200 for Jetson

# 3. Verify correct UART pins
# Jetson Orin AGX uses pins 8 and 10 on 40-pin header

# 4. Check device permissions
ls -l /dev/ttyUSB0
# Should show: crw-rw---- 1 root dialout

sudo usermod -aG dialout $USER
# Log out and back in

# 5. Test with different terminal program
# Try screen, minicom, picocom

# 6. Check kernel boot parameters
# Serial console might be disabled in kernel cmdline
```

### Issue 4: Boot Hangs at UEFI or Kernel

**Symptoms**: Boot stops at bootloader or early kernel

**Solutions**:
```bash
# Boot hangs at UEFI:
# 1. Check bootloader configuration in flash package
less ~/jetson-flash/*.conf

# 2. Verify UEFI binary is correct
ls -lh ~/jetson-flash/bootloader/uefi_jetson.bin

# 3. Re-flash with verbose logging
sudo ./doflash.sh -v

# Boot hangs at kernel:
# 1. Check kernel command line
# Look for "console=" parameter in serial output

# 2. Verify device tree is correct
# Check dtb file matches hardware

# 3. Try minimal kernel cmdline
# Edit flash configuration to reduce boot parameters

# 4. Check for missing firmware
# Some devices need firmware blobs loaded early
```

### Issue 5: No Network Connectivity

**Symptoms**: Can't ping gateway or external hosts

**Solutions**:
```bash
# 1. Check physical connection
ip link show

# Interface should show "state UP"

# 2. Verify driver is loaded
lsmod | grep -i eth
dmesg | grep -i ethernet

# 3. Check DHCP client
systemctl status systemd-networkd

# 4. Manually configure IP
ip addr add 192.168.1.100/24 dev eth0
ip link set eth0 up
ip route add default via 192.168.1.1

# 5. Check network configuration files
ls /etc/systemd/network/
cat /etc/systemd/network/*.network

# 6. Test with static IP
cat > /etc/systemd/network/10-static.network << 'EOF'
[Match]
Name=eth0

[Network]
Address=192.168.1.100/24
Gateway=192.168.1.1
DNS=8.8.8.8
EOF

systemctl restart systemd-networkd
```

### Issue 6: GPU Not Detected

**Symptoms**: `/dev/nvidia*` devices don't exist

**Solutions**:
```bash
# 1. Check if GPU driver modules are loaded
lsmod | grep nvidia

# 2. Load nvidia driver manually
modprobe nvidia

# 3. Check kernel logs for GPU errors
dmesg | grep -i nvidia
dmesg | grep -i gpu

# 4. Verify CUDA installation
ls -la /usr/local/cuda

# 5. Check device tree for GPU node
ls /proc/device-tree/gpu*

# 6. Rebuild with GPU support enabled
# In local.conf:
# MACHINE_FEATURES += "cuda"
```

---

## Verification Checklist

- [ ] Tegraflash package extracted successfully
- [ ] Device enters recovery mode (shows in lsusb as APX)
- [ ] Flash process completes without errors
- [ ] Device reboots after flash
- [ ] Serial console shows boot messages
- [ ] Login prompt appears
- [ ] Can log in as root
- [ ] Kernel version is correct
- [ ] Device tree model matches hardware
- [ ] GPIO sysfs interface available
- [ ] I2C buses detected
- [ ] Network interface present
- [ ] Can get IP address (DHCP or static)
- [ ] Can ping external hosts
- [ ] SSH server running
- [ ] GPU devices present (/dev/nvidia*)
- [ ] No failed systemd services
- [ ] Temperature sensors readable
- [ ] Storage mounted correctly

---

## Post-Deployment Configuration

### Set Up Development Environment

```bash
# Create development user
useradd -m -s /bin/bash developer
passwd developer

# Add to necessary groups
usermod -aG sudo,gpio,i2c,spi,dialout developer

# Install additional development tools
# (if not already in image)
opkg update
opkg install vim git cmake python3-pip

# Set up SSH keys for passwordless login
# On host:
ssh-copy-id root@jetson-ip-address
```

### Configure Automatic Services

```bash
# Enable NTP for time synchronization
systemctl enable systemd-timesyncd
systemctl start systemd-timesyncd

# Check time status
timedatectl status

# Set timezone if needed
timedatectl set-timezone America/Los_Angeles
timedatectl list-timezones  # List available timezones
```

### Install Additional Packages

```bash
# Update package database
opkg update

# Install common utilities
opkg install htop iotop lsof strace

# Install development libraries
opkg install python3-numpy python3-opencv

# List installed packages
opkg list-installed

# Search for packages
opkg list | grep -i python
```

---

## Next Steps

### Immediate Next Steps
1. Familiarize yourself with the booted system
2. Test GPIO, I2C, SPI interfaces
3. Run your custom applications
4. Set up remote development workflow

### Proceed to Next Tutorial
**Tutorial 05: Device Tree Basics** - Learn to customize hardware configuration

### Hardware Testing
1. Connect sensors and peripherals
2. Test camera interfaces
3. Validate GPU compute capability
4. Measure power consumption

---

## Quick Reference Commands

```bash
# Check system status
systemctl status
journalctl -xe

# Monitor system resources
htop
iotop
iostat

# Hardware information
lshw -short
lscpu
lsblk

# Temperature monitoring
watch -n 1 'cat /sys/class/thermal/thermal_zone*/temp'

# Network troubleshooting
ip addr
ip route
ss -tuln

# Kernel messages
dmesg | tail -50
journalctl -k

# Service management
systemctl list-units --failed
systemctl restart service-name
```

---

**Congratulations!** You've successfully flashed and deployed your custom Yocto-built Linux image to NVIDIA Jetson hardware. Your device is now running a custom, optimized system ready for development and deployment.

---

*Tutorial created by the Yocto & Meta-Tegra Multi-Agent Learning System*
*Last updated: 2025-01-15*
