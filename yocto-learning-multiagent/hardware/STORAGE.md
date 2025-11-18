# Storage Integration Guide

## Overview

Comprehensive storage configuration guide for NVIDIA Jetson platforms, covering NVMe, SD cards, USB storage, network storage (NFS), and RAID configurations.

## Storage Hardware Capabilities

### Platform Storage Summary

| Platform | eMMC | NVMe | SD Card | USB 3.x | SATA |
|----------|------|------|---------|---------|------|
| AGX Orin | 64GB | PCIe Gen4 x4 | Yes* | 5 ports | Via PCIe |
| Orin NX | 16GB | PCIe Gen4 x4 | Yes* | 4 ports | No |
| Orin Nano | 16GB | PCIe Gen3 x4 | Yes* | 4 ports | No |
| AGX Xavier | 32GB | PCIe Gen4 x4 | Yes* | 4 ports | Via PCIe |
| Xavier NX | 16GB | PCIe Gen4 x4 | Yes* | 4 ports | No |
| Jetson Nano | - | - | Primary** | 4 ports | No |

*SD card available on developer kits, not on production modules
**Jetson Nano boots from SD card by default

### Storage Bandwidth

| Interface | Theoretical Max | Practical Performance |
|-----------|----------------|----------------------|
| PCIe Gen4 x4 | 8 GB/s | 6-7 GB/s (NVMe) |
| PCIe Gen3 x4 | 4 GB/s | 3-3.5 GB/s (NVMe) |
| USB 3.1 Gen 2 | 1.25 GB/s | 800-1000 MB/s |
| USB 3.0 | 625 MB/s | 400-500 MB/s |
| eMMC 5.1 | 400 MB/s | 250-300 MB/s |
| SD UHS-I | 104 MB/s | 80-95 MB/s |
| SD UHS-II | 312 MB/s | 250-280 MB/s |

## NVMe Configuration

### Hardware Connection

**M.2 Form Factors:**
- M.2 2280 (most common)
- M.2 2242
- M.2 22110

**Key Type:**
- M Key (NVMe/SATA)
- B+M Key (NVMe/SATA compatible)

### Device Tree Configuration

**Orin NX/Nano NVMe (PCIe):**

```dts
pcie@14160000 {
    compatible = "nvidia,tegra234-pcie";
    reg = <0x00 0x14160000 0x0 0x00020000>,   /* appl registers (128K)      */
          <0x00 0x36000000 0x0 0x00040000>,   /* configuration space (256K) */
          <0x00 0x36040000 0x0 0x00040000>,   /* iATU_DMA reg space (256K)  */
          <0x00 0x36080000 0x0 0x00040000>;   /* DBI reg space (256K)       */
    reg-names = "appl", "config", "atu_dma", "dbi";

    status = "okay";

    nvidia,max-speed = <4>;
    nvidia,disable-aspm-states = <0xf>;
    nvidia,enable-power-down;
    num-lanes = <4>;

    phys = <&p2u_nvhs_0>, <&p2u_nvhs_1>, <&p2u_nvhs_2>, <&p2u_nvhs_3>;
    phy-names = "p2u-0", "p2u-1", "p2u-2", "p2u-3";
};
```

### NVMe Detection and Initialization

```bash
# Check NVMe devices
lspci | grep -i nvme

# List NVMe controllers
nvme list

# Check device
ls -l /dev/nvme*
# Expected: /dev/nvme0, /dev/nvme0n1

# Get NVMe info
nvme id-ctrl /dev/nvme0
nvme id-ns /dev/nvme0n1

# SMART data
nvme smart-log /dev/nvme0
```

### NVMe Partitioning

**GPT Partitioning:**

```bash
# Create partition table
parted /dev/nvme0n1 mklabel gpt

# Create boot partition (EFI)
parted /dev/nvme0n1 mkpart primary fat32 1MiB 512MiB
parted /dev/nvme0n1 set 1 esp on

# Create root partition
parted /dev/nvme0n1 mkpart primary ext4 512MiB 100%

# View partition table
parted /dev/nvme0n1 print

# Alternative: using fdisk
fdisk /dev/nvme0n1
# g (create GPT)
# n (new partition)
# w (write)
```

### NVMe Filesystem Creation

```bash
# Format boot partition (FAT32)
mkfs.vfat -F 32 /dev/nvme0n1p1

# Format root partition (ext4)
mkfs.ext4 -L rootfs /dev/nvme0n1p2

# Or with specific options
mkfs.ext4 -L rootfs -O ^has_journal,^metadata_csum -E lazy_itable_init=0 /dev/nvme0n1p2

# XFS (alternative)
mkfs.xfs -f -L rootfs /dev/nvme0n1p2

# Btrfs (alternative, with compression)
mkfs.btrfs -L rootfs /dev/nvme0n1p2
```

### Boot from NVMe

**Copy rootfs to NVMe:**

```bash
# Mount NVMe
mkdir -p /mnt/nvme
mount /dev/nvme0n1p2 /mnt/nvme

# Copy current root filesystem
rsync -axHAWXS --numeric-ids --info=progress2 / /mnt/nvme/

# Update fstab on NVMe
cat > /mnt/nvme/etc/fstab <<EOF
/dev/nvme0n1p2  /           ext4    defaults        0 1
/dev/nvme0n1p1  /boot/efi   vfat    defaults        0 2
EOF

# Unmount
umount /mnt/nvme
```

**Update extlinux.conf (U-Boot):**

```bash
# Edit /boot/extlinux/extlinux.conf
cat > /boot/extlinux/extlinux.conf <<EOF
TIMEOUT 30
DEFAULT primary

MENU TITLE L4T boot options

LABEL primary
    MENU LABEL primary kernel
    LINUX /boot/Image
    FDT /boot/dtb/kernel_tegra234-p3767-0000-p3509-a02.dtb
    APPEND root=/dev/nvme0n1p2 rw rootwait rootfstype=ext4
EOF
```

### NVMe Performance Optimization

**Queue Depth Tuning:**

```bash
# Check current queue depth
cat /sys/block/nvme0n1/queue/nr_requests

# Increase queue depth
echo 1024 > /sys/block/nvme0n1/queue/nr_requests

# Check scheduler
cat /sys/block/nvme0n1/queue/scheduler

# Set scheduler (none is best for NVMe)
echo none > /sys/block/nvme0n1/queue/scheduler
```

**APST (Autonomous Power State Transition):**

```bash
# Disable APST for lowest latency (at cost of power)
echo 0 > /sys/module/nvme_core/parameters/default_ps_max_latency_us

# Or enable for power saving
echo 25000 > /sys/module/nvme_core/parameters/default_ps_max_latency_us
```

**Benchmark NVMe:**

```bash
# Install fio
apt-get install fio

# Sequential read test
fio --name=seqread --rw=read --bs=1M --size=4G --numjobs=1 \
    --filename=/dev/nvme0n1 --direct=1 --runtime=60

# Sequential write test
fio --name=seqwrite --rw=write --bs=1M --size=4G --numjobs=1 \
    --filename=/dev/nvme0n1 --direct=1 --runtime=60

# Random read (IOPS)
fio --name=randread --rw=randread --bs=4k --size=4G --numjobs=4 \
    --filename=/dev/nvme0n1 --direct=1 --runtime=60 --ioengine=libaio --iodepth=256

# Random write (IOPS)
fio --name=randwrite --rw=randwrite --bs=4k --size=4G --numjobs=4 \
    --filename=/dev/nvme0n1 --direct=1 --runtime=60 --ioengine=libaio --iodepth=256
```

## SD Card Configuration

### SD Card Selection

**Recommended Specs:**
- Class: UHS-I (U3) or UHS-II
- Speed: 90+ MB/s read, 60+ MB/s write
- Capacity: 32GB minimum, 128GB+ recommended
- Brand: SanDisk Extreme, Samsung EVO Plus, Kingston Canvas

**Speed Classes:**
- Class 10: 10 MB/s minimum
- U1: 10 MB/s minimum
- U3: 30 MB/s minimum
- V30: 30 MB/s minimum (video class)

### SD Card Detection

```bash
# Check SD card device
lsblk | grep mmcblk

# Typical devices:
# /dev/mmcblk0 - SD card
# /dev/mmcblk0p1 - First partition

# Get card info
cat /sys/block/mmcblk0/device/cid
cat /sys/block/mmcblk0/device/csd
cat /sys/block/mmcblk0/device/name
```

### SD Card Optimization

**Partition Alignment:**

```bash
# Create aligned partitions (important for SD card performance)
parted /dev/mmcblk0 --align optimal mklabel gpt
parted /dev/mmcblk0 --align optimal mkpart primary ext4 1MiB 100%
```

**Filesystem Tuning:**

```bash
# Format with optimizations
mkfs.ext4 -O ^has_journal,dir_index,extent,^huge_file -m 1 -L rootfs /dev/mmcblk0p1

# Mount options for SD card longevity
mount -o noatime,nodiratime,commit=600 /dev/mmcblk0p1 /mnt
```

**Persistent Mount Options (fstab):**

```bash
cat >> /etc/fstab <<EOF
/dev/mmcblk0p1  /           ext4    noatime,nodiratime,errors=remount-ro  0 1
tmpfs           /tmp        tmpfs   defaults,noatime,mode=1777            0 0
tmpfs           /var/log    tmpfs   defaults,noatime,mode=0755            0 0
EOF
```

**Log to RAM (Reduce SD Writes):**

```bash
# Install log2ram
git clone https://github.com/azlux/log2ram.git
cd log2ram
chmod +x install.sh
./install.sh

# Configure size
echo "SIZE=128M" > /etc/log2ram.conf

systemctl enable log2ram
reboot
```

### SD Card Monitoring

```bash
#!/bin/bash
# SD card health monitoring script

DEVICE="/dev/mmcblk0"

echo "=== SD Card Health Check ==="
echo "Card Name: $(cat /sys/block/mmcblk0/device/name)"
echo "Card Date: $(cat /sys/block/mmcblk0/device/date)"

# Check for bad blocks (use with caution, slow operation)
# badblocks -v $DEVICE

# Filesystem check (unmount first!)
# e2fsck -c -f /dev/mmcblk0p1

# SMART-like data (if supported)
smartctl -a $DEVICE 2>/dev/null || echo "smartctl not available for SD"

# I/O stats
iostat -x $DEVICE 5 2
```

## USB Storage

### USB Detection

```bash
# List USB devices
lsusb

# Check USB storage
lsblk | grep sd

# Typical devices:
# /dev/sda - First USB drive
# /dev/sdb - Second USB drive

# Get USB device info
udevadm info --query=all --name=/dev/sda

# Check USB version
lsusb -t
```

### USB Storage Performance

**USB 3.0 vs 2.0 Detection:**

```bash
# Check USB speed
lsusb -t | grep -A 5 "Driver=usb-storage"

# 5000M = USB 3.0
# 480M = USB 2.0
# 12M = USB 1.1
```

**Optimize USB Performance:**

```bash
# Check if UAS (USB Attached SCSI) is enabled (better performance)
lsusb -t | grep uas

# Disable USB autosuspend for better performance
echo on > /sys/bus/usb/devices/*/power/control

# Or persistent via kernel parameter
# Edit /boot/extlinux/extlinux.conf
# Add: usbcore.autosuspend=-1
```

**Benchmark USB:**

```bash
# Sequential write
dd if=/dev/zero of=/mnt/usb/testfile bs=1M count=1024 conv=fdatasync

# Sequential read
dd if=/mnt/usb/testfile of=/dev/null bs=1M

# Using hdparm
hdparm -tT /dev/sda
```

### USB Auto-Mount

**systemd auto-mount:**

```bash
# Create mount unit
cat > /etc/systemd/system/mnt-usb.mount <<EOF
[Unit]
Description=USB Storage Mount
After=multi-user.target

[Mount]
What=/dev/disk/by-label/USB_STORAGE
Where=/mnt/usb
Type=ext4
Options=defaults,nofail

[Install]
WantedBy=multi-user.target
EOF

# Enable
systemctl enable mnt-usb.mount
```

**udev rules for auto-mount:**

```bash
cat > /etc/udev/rules.d/99-usb-mount.rules <<'EOF'
KERNEL=="sd[a-z][0-9]", SUBSYSTEM=="block", ACTION=="add", \
    ENV{ID_FS_TYPE}=="ext4", \
    RUN+="/bin/mkdir -p /media/%k", \
    RUN+="/bin/mount -o defaults /dev/%k /media/%k"

KERNEL=="sd[a-z][0-9]", SUBSYSTEM=="block", ACTION=="remove", \
    RUN+="/bin/umount /media/%k", \
    RUN+="/bin/rmdir /media/%k"
EOF

# Reload udev
udevadm control --reload-rules
```

## Network Storage (NFS)

### NFS Client Configuration

**Install NFS client:**

```bash
apt-get install nfs-common
```

**Mount NFS Share:**

```bash
# Manual mount
mount -t nfs 192.168.1.100:/export/share /mnt/nfs

# With options
mount -t nfs -o rw,sync,hard,intr 192.168.1.100:/export/share /mnt/nfs
```

**Persistent NFS Mount (fstab):**

```bash
cat >> /etc/fstab <<EOF
192.168.1.100:/export/share  /mnt/nfs  nfs  defaults,_netdev  0 0
EOF

# Alternative with specific options
# 192.168.1.100:/export/share  /mnt/nfs  nfs  rw,sync,hard,intr,timeo=600,retrans=2,_netdev  0 0
```

**systemd Auto-Mount:**

```bash
# Create mount unit
cat > /etc/systemd/system/mnt-nfs.mount <<EOF
[Unit]
Description=NFS Share Mount
After=network-online.target
Wants=network-online.target

[Mount]
What=192.168.1.100:/export/share
Where=/mnt/nfs
Type=nfs
Options=defaults,_netdev

[Install]
WantedBy=multi-user.target
EOF

systemctl enable mnt-nfs.mount
```

### NFS Performance Tuning

**Client Options:**

```bash
# High-performance options
mount -t nfs -o rw,sync,hard,intr,rsize=131072,wsize=131072,tcp,vers=3 \
    192.168.1.100:/export/share /mnt/nfs

# Options explained:
# rsize/wsize=131072  - Large read/write buffer (128KB)
# tcp                 - Use TCP (more reliable than UDP)
# vers=3              - NFSv3 (or vers=4 for NFSv4)
# hard                - Hard mount (keeps retrying on error)
# intr                - Interruptible (can Ctrl+C during hang)
```

**Test NFS Performance:**

```bash
# Write test
dd if=/dev/zero of=/mnt/nfs/testfile bs=1M count=1024 conv=fdatasync

# Read test
dd if=/mnt/nfs/testfile of=/dev/null bs=1M

# Network throughput
iperf3 -c 192.168.1.100 -t 60
```

### NFSv4 with Kerberos (Secure NFS)

```bash
# Install Kerberos client
apt-get install krb5-user nfs-common

# Configure Kerberos
# Edit /etc/krb5.conf

# Mount with Kerberos
mount -t nfs4 -o sec=krb5 192.168.1.100:/export/share /mnt/nfs
```

## RAID Configurations

### Software RAID (mdadm)

**Install mdadm:**

```bash
apt-get install mdadm
```

**RAID 0 (Striping - Performance):**

```bash
# Create RAID 0 with two NVMe drives
mdadm --create /dev/md0 --level=0 --raid-devices=2 /dev/nvme0n1 /dev/nvme1n1

# Check status
cat /proc/mdstat

# Format and mount
mkfs.ext4 /dev/md0
mount /dev/md0 /mnt/raid0
```

**RAID 1 (Mirroring - Redundancy):**

```bash
# Create RAID 1
mdadm --create /dev/md0 --level=1 --raid-devices=2 /dev/nvme0n1 /dev/nvme1n1

# Monitor sync progress
watch cat /proc/mdstat

# Format and mount
mkfs.ext4 /dev/md0
mount /dev/md0 /mnt/raid1
```

**RAID 5 (Striping with Parity):**

```bash
# Requires 3+ drives
mdadm --create /dev/md0 --level=5 --raid-devices=3 \
    /dev/nvme0n1 /dev/nvme1n1 /dev/sda1

# Check
mdadm --detail /dev/md0
```

**RAID 10 (Mirrored Stripes):**

```bash
# Requires 4+ drives
mdadm --create /dev/md0 --level=10 --raid-devices=4 \
    /dev/nvme0n1 /dev/nvme1n1 /dev/sda1 /dev/sdb1
```

**Save RAID Configuration:**

```bash
# Scan and save config
mdadm --detail --scan >> /etc/mdadm/mdadm.conf

# Update initramfs
update-initramfs -u
```

**Monitor RAID:**

```bash
# Check status
mdadm --detail /dev/md0

# Monitor mode
mdadm --monitor --scan --daemonise

# Email alerts
echo "MAILADDR root@localhost" >> /etc/mdadm/mdadm.conf
```

### Btrfs RAID

**Create Btrfs RAID:**

```bash
# RAID 0 (striping)
mkfs.btrfs -d raid0 -m raid0 /dev/nvme0n1 /dev/nvme1n1

# RAID 1 (mirroring)
mkfs.btrfs -d raid1 -m raid1 /dev/nvme0n1 /dev/nvme1n1

# RAID 10
mkfs.btrfs -d raid10 -m raid10 /dev/nvme0n1 /dev/nvme1n1 /dev/sda1 /dev/sdb1

# Mount
mount /dev/nvme0n1 /mnt/btrfs

# Check status
btrfs filesystem show
btrfs device stats /mnt/btrfs
```

**Btrfs Features:**

```bash
# Enable compression
mount -o compress=zstd /dev/nvme0n1 /mnt/btrfs

# Create snapshot
btrfs subvolume snapshot /mnt/btrfs /mnt/btrfs/snapshot_$(date +%Y%m%d)

# Balance filesystem
btrfs balance start /mnt/btrfs

# Scrub for errors
btrfs scrub start /mnt/btrfs
btrfs scrub status /mnt/btrfs
```

### ZFS (Advanced)

**Install ZFS:**

```bash
apt-get install zfsutils-linux
```

**Create ZFS Pool:**

```bash
# Mirror (RAID 1)
zpool create tank mirror /dev/nvme0n1 /dev/nvme1n1

# RAID-Z (RAID 5 equivalent)
zpool create tank raidz /dev/nvme0n1 /dev/nvme1n1 /dev/sda1

# RAID-Z2 (RAID 6 equivalent)
zpool create tank raidz2 /dev/nvme0n1 /dev/nvme1n1 /dev/sda1 /dev/sdb1

# Check status
zpool status
```

**ZFS Features:**

```bash
# Enable compression
zfs set compression=lz4 tank

# Set quota
zfs set quota=100G tank/dataset

# Create snapshot
zfs snapshot tank@snapshot1

# Clone snapshot
zfs clone tank@snapshot1 tank/clone

# Enable deduplication (requires lots of RAM)
zfs set dedup=on tank

# Check pool health
zpool scrub tank
```

## Storage Monitoring

### Disk Health Monitoring

**SMART Monitoring:**

```bash
# Install smartmontools
apt-get install smartmontools

# Enable SMART
smartctl -s on /dev/nvme0n1

# Check health
smartctl -H /dev/nvme0n1

# Full information
smartctl -a /dev/nvme0n1

# Run self-test
smartctl -t short /dev/nvme0n1
smartctl -l selftest /dev/nvme0n1
```

**I/O Statistics:**

```bash
# Install sysstat
apt-get install sysstat

# Monitor I/O
iostat -x 5

# Per-device stats
iostat -x /dev/nvme0n1 5

# Disk usage
df -h

# Inode usage
df -i
```

### Automated Monitoring Script

```bash
#!/bin/bash
# Storage health monitoring

echo "=== Storage Health Report ==="
date

# Disk space
echo -e "\n=== Disk Space ==="
df -h | grep -E 'Filesystem|/dev/(nvme|sd|mmcblk)'

# SMART status
echo -e "\n=== SMART Status ==="
for disk in /dev/nvme0n1 /dev/sda; do
    if [ -e "$disk" ]; then
        echo "Device: $disk"
        smartctl -H $disk 2>/dev/null | grep -i health
    fi
done

# RAID status
if [ -e /proc/mdstat ]; then
    echo -e "\n=== RAID Status ==="
    cat /proc/mdstat
fi

# ZFS status
if command -v zpool &> /dev/null; then
    echo -e "\n=== ZFS Status ==="
    zpool status
fi

# I/O stats
echo -e "\n=== I/O Statistics ==="
iostat -x 1 2 | tail -n +4
```

## Yocto Integration

### Storage Support Recipes

```bitbake
# File: recipes-core/images/jetson-storage-image.bb

IMAGE_INSTALL_append = " \
    e2fsprogs \
    e2fsprogs-resize2fs \
    dosfstools \
    parted \
    gptfdisk \
    nvme-cli \
    smartmontools \
    hdparm \
    mdadm \
    lvm2 \
    nfs-utils \
    fio \
    iozone3 \
"

# Optional: Btrfs support
IMAGE_INSTALL_append = " btrfs-tools "

# Optional: XFS support
IMAGE_INSTALL_append = " xfsprogs "
```

### NVMe Boot Configuration

```bitbake
# File: recipes-bsp/u-boot/u-boot-tegra_%.bbappend

do_install_append() {
    # Update extlinux to boot from NVMe
    sed -i 's|root=/dev/mmcblk0p1|root=/dev/nvme0n1p2|g' \
        ${D}/boot/extlinux/extlinux.conf
}
```

### Custom Filesystem Recipe

```bitbake
# File: recipes-core/images/jetson-minimal-nvme.bb

require recipes-core/images/core-image-minimal.bb

# Boot from NVMe
IMAGE_ROOTFS_SIZE = "8388608"  # 8GB rootfs

# Add NVMe tools
IMAGE_INSTALL_append = " \
    nvme-cli \
    e2fsprogs \
"

# Post-install script to setup NVMe
ROOTFS_POSTPROCESS_COMMAND += "setup_nvme_boot ; "

setup_nvme_boot() {
    # Add NVMe boot configuration
    echo '/dev/nvme0n1p2  /  ext4  defaults  0 1' >> ${IMAGE_ROOTFS}/etc/fstab
}
```

## Testing Procedures

### Storage Performance Test Suite

```bash
#!/bin/bash
# Comprehensive storage test

DEVICE="/dev/nvme0n1"
MOUNT="/mnt/test"

echo "=== Storage Performance Test ==="
echo "Device: $DEVICE"

# Sequential Read
echo -e "\n=== Sequential Read ==="
hdparm -t $DEVICE

# Sequential Write
echo -e "\n=== Sequential Write ==="
dd if=/dev/zero of=${MOUNT}/testfile bs=1M count=1024 conv=fdatasync oflag=direct

# Random Read IOPS
echo -e "\n=== Random Read IOPS ==="
fio --name=randread --ioengine=libaio --iodepth=32 --rw=randread \
    --bs=4k --direct=1 --size=1G --numjobs=4 --runtime=30 \
    --filename=${MOUNT}/testfile

# Random Write IOPS
echo -e "\n=== Random Write IOPS ==="
fio --name=randwrite --ioengine=libaio --iodepth=32 --rw=randwrite \
    --bs=4k --direct=1 --size=1G --numjobs=4 --runtime=30 \
    --filename=${MOUNT}/testfile

# Latency Test
echo -e "\n=== Latency Test ==="
ioping -c 10 ${MOUNT}

# Cleanup
rm -f ${MOUNT}/testfile
```

## References

### Official Documentation
- [NVMe Specification](https://nvmexpress.org/specifications/)
- [Linux Storage Documentation](https://www.kernel.org/doc/html/latest/block/index.html)
- [mdadm Manual](https://raid.wiki.kernel.org/)

### Tools
- [nvme-cli](https://github.com/linux-nvme/nvme-cli)
- [fio](https://github.com/axboe/fio)
- [smartmontools](https://www.smartmontools.org/)

---

**Document Version**: 1.0
**Last Updated**: 2025-11
**Maintained By**: Hardware Integration Agent
