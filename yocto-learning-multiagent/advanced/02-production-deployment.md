# Production Deployment for Yocto & Meta-Tegra Systems

## Overview

This module covers production-grade deployment strategies for Jetson-based embedded systems, including image hardening, security best practices, OTA updates, A/B partition management, and fleet management.

**Target Audience**: System architects and deployment engineers
**Prerequisites**: Strong understanding of Linux security, networking, and embedded systems

---

## 1. Image Hardening

### 1.1 Security-Focused Image Configuration

```python
# conf/local.conf - Production Security Configuration

# Remove development tools and packages
EXTRA_IMAGE_FEATURES:remove = "debug-tweaks tools-debug tools-sdk dev-pkgs"
EXTRA_IMAGE_FEATURES:append = " read-only-rootfs"

# Enable security features
DISTRO_FEATURES:append = " pam systemd usrmerge security"
EXTRA_IMAGE_FEATURES:append = " ima"

# Remove weak algorithms
PACKAGECONFIG:remove:pn-openssh = "des 3des"
PACKAGECONFIG:append:pn-openssh = " pam"

# Use strong crypto
DISTRO_FEATURES:append = " openssl"
PREFERRED_VERSION_openssl = "3.%"

# Minimal package set
IMAGE_FEATURES:remove = "package-management"
PACKAGE_INSTALL:remove = "packagegroup-base-extended"
```

### 1.2 Read-Only Root Filesystem Implementation

```python
# recipes-core/images/production-image.bb

require recipes-core/images/core-image-minimal.bb

SUMMARY = "Hardened production image for Jetson"

# Read-only rootfs
IMAGE_FEATURES:append = " read-only-rootfs"

# Volatile directories
VOLATILE_LOG_DIR = "yes"

# Additional hardening packages
IMAGE_INSTALL:append = " \
    fail2ban \
    aide \
    apparmor \
    apparmor-profiles \
    auditd \
    iptables \
"

# Remove unnecessary services
IMAGE_INSTALL:remove = " \
    avahi-daemon \
    packagegroup-core-boot \
"

# Custom initialization
ROOTFS_POSTPROCESS_COMMAND:append = " setup_production_rootfs; "

setup_production_rootfs() {
    # Create overlay mounts for writable directories
    install -d ${IMAGE_ROOTFS}/etc/systemd/system

    # /var overlay
    cat >> ${IMAGE_ROOTFS}/etc/systemd/system/var.mount << 'EOF'
[Unit]
Description=Overlay mount for /var
Before=local-fs.target

[Mount]
What=overlay
Where=/var
Type=overlay
Options=lowerdir=/var,upperdir=/data/overlay/var/upper,workdir=/data/overlay/var/work

[Install]
WantedBy=local-fs.target
EOF

    # /etc overlay
    cat >> ${IMAGE_ROOTFS}/etc/systemd/system/etc.mount << 'EOF'
[Unit]
Description=Overlay mount for /etc
Before=local-fs.target

[Mount]
What=overlay
Where=/etc
Type=overlay
Options=lowerdir=/etc,upperdir=/data/overlay/etc/upper,workdir=/data/overlay/etc/work

[Install]
WantedBy=local-fs.target
EOF

    # Enable mounts
    ln -sf ../var.mount ${IMAGE_ROOTFS}/etc/systemd/system/local-fs.target.wants/var.mount
    ln -sf ../etc.mount ${IMAGE_ROOTFS}/etc/systemd/system/local-fs.target.wants/etc.mount

    # Set proper permissions
    chmod 644 ${IMAGE_ROOTFS}/etc/systemd/system/*.mount
}
```

### 1.3 Kernel Hardening

```python
# recipes-kernel/linux/linux-tegra_%.bbappend

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://security-hardening.cfg"

# security-hardening.cfg
```

```
# Kernel hardening configuration for production

# Stack protection
CONFIG_STACKPROTECTOR=y
CONFIG_STACKPROTECTOR_STRONG=y
CONFIG_STRICT_KERNEL_RWX=y
CONFIG_STRICT_MODULE_RWX=y

# Address Space Layout Randomization
CONFIG_RANDOMIZE_BASE=y
CONFIG_RANDOMIZE_MEMORY=y

# Control Flow Integrity
CONFIG_CFI_CLANG=y
CONFIG_CFI_PERMISSIVE=n

# Memory protection
CONFIG_PAGE_POISONING=y
CONFIG_PAGE_POISONING_NO_SANITY=y
CONFIG_PAGE_POISONING_ZERO=y
CONFIG_SLAB_FREELIST_RANDOM=y
CONFIG_SLAB_FREELIST_HARDENED=y
CONFIG_SHUFFLE_PAGE_ALLOCATOR=y

# Harden usercopy
CONFIG_HARDENED_USERCOPY=y
CONFIG_FORTIFY_SOURCE=y

# Restrict kernel memory access
CONFIG_STRICT_DEVMEM=y
CONFIG_IO_STRICT_DEVMEM=y
CONFIG_SECURITY=y
CONFIG_SECURITY_YAMA=y

# AppArmor support
CONFIG_SECURITY_APPARMOR=y
CONFIG_SECURITY_APPARMOR_BOOTPARAM_VALUE=1
CONFIG_DEFAULT_SECURITY_APPARMOR=y

# Disable legacy features
CONFIG_LEGACY_PTYS=n
CONFIG_DEVKMEM=n
CONFIG_COMPAT_BRK=n
CONFIG_MODIFY_LDT_SYSCALL=n

# Network security
CONFIG_NETFILTER=y
CONFIG_NETFILTER_ADVANCED=y
CONFIG_IP_NF_IPTABLES=y
CONFIG_IP_NF_FILTER=y
CONFIG_IP_NF_TARGET_REJECT=y

# Audit support
CONFIG_AUDIT=y
CONFIG_AUDITSYSCALL=y

# IMA/EVM
CONFIG_IMA=y
CONFIG_IMA_MEASURE_PCR_IDX=10
CONFIG_IMA_APPRAISE=y
CONFIG_IMA_APPRAISE_BOOTPARAM=y
CONFIG_EVM=y
```

### 1.4 Application Sandboxing with AppArmor

```python
# recipes-security/apparmor/apparmor-profiles_%.bbappend

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += " \
    file://usr.bin.custom-app \
    file://usr.sbin.nvidia-services \
"

do_install:append() {
    install -d ${D}${sysconfdir}/apparmor.d
    install -m 0644 ${WORKDIR}/usr.bin.custom-app ${D}${sysconfdir}/apparmor.d/
    install -m 0644 ${WORKDIR}/usr.sbin.nvidia-services ${D}${sysconfdir}/apparmor.d/
}
```

```
# files/usr.bin.custom-app - AppArmor profile for custom application
#include <tunables/global>

/usr/bin/custom-app {
  #include <abstractions/base>
  #include <abstractions/nvidia>

  # Application binary
  /usr/bin/custom-app mr,

  # Configuration files (read-only)
  /etc/custom-app/** r,

  # Data directory (read-write)
  /var/lib/custom-app/** rw,
  /var/log/custom-app/** w,

  # Temporary files
  /tmp/custom-app.* rw,

  # CUDA/GPU access
  /dev/nvhost-* rw,
  /dev/nvidia* rw,
  /dev/nvmap rw,

  # Network access (if needed)
  network inet stream,
  network inet dgram,

  # Deny dangerous capabilities
  deny capability sys_admin,
  deny capability sys_module,
  deny capability sys_rawio,

  # Allowed capabilities
  capability setuid,
  capability setgid,
  capability dac_override,
}
```

---

## 2. Security Best Practices

### 2.1 Secure Boot Implementation

```python
# conf/local.conf - Secure Boot Configuration

# Enable secure boot
TEGRA_SIGNING_ENV = "file://${TOPDIR}/signing-keys/signing.env"
TEGRA_SIGNING_ARGS = "--key ${TOPDIR}/signing-keys/rsa_priv.pem"

# Sign bootloader and kernel
TEGRA_SIGN_BOOTLOADER = "1"
TEGRA_SIGN_KERNEL = "1"

# Flash encrypted
TEGRA_FLASH_ENCRYPTION_ENABLED = "1"
```

```bash
# Generate signing keys (do this once, securely store keys!)
#!/bin/bash

KEYS_DIR="${TOPDIR}/signing-keys"
mkdir -p "${KEYS_DIR}"
cd "${KEYS_DIR}"

# Generate RSA key pair for signing
openssl genrsa -out rsa_priv.pem 2048
openssl rsa -in rsa_priv.pem -pubout -out rsa_pub.pem

# Generate SBK (Secure Boot Key) and KEK (Key Encryption Key)
# WARNING: Store these securely! Loss means bricked device!
openssl rand -hex 16 > sbk.txt
openssl rand -hex 16 > kek.txt

# Create signing environment
cat > signing.env << 'EOF'
TEGRA_SBK_FILE="sbk.txt"
TEGRA_KEK_FILE="kek.txt"
TEGRA_PKC_FILE="rsa_priv.pem"
EOF

# Set restrictive permissions
chmod 600 *.pem *.txt signing.env
chmod 700 "${KEYS_DIR}"

echo "Keys generated in ${KEYS_DIR}"
echo "BACKUP THESE FILES SECURELY!"
```

### 2.2 Runtime Security Hardening

```python
# recipes-core/systemd/systemd-hardening.bb

SUMMARY = "Systemd security hardening configuration"
LICENSE = "MIT"

SRC_URI = "file://hardened.conf"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/systemd/system.conf.d
    install -m 0644 hardened.conf ${D}${sysconfdir}/systemd/system.conf.d/
}

FILES:${PN} = "${sysconfdir}/systemd/system.conf.d/hardened.conf"
```

```ini
# files/hardened.conf - Systemd hardening
[Manager]
# Limit core dumps
DefaultLimitCORE=0

# Restrict number of processes
DefaultTasksMax=1024

# Timeout settings
DefaultTimeoutStartSec=30s
DefaultTimeoutStopSec=15s

# Security settings
PrivateTmp=yes
NoNewPrivileges=yes
```

```python
# recipes-core/secure-services/secure-service_1.0.bb

SUMMARY = "Template for secure systemd service"
LICENSE = "MIT"

SRC_URI = "file://secure-service.service \
           file://secure-service.sh \
          "

S = "${WORKDIR}"

inherit systemd

SYSTEMD_SERVICE:${PN} = "secure-service.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 secure-service.service ${D}${systemd_system_unitdir}/

    install -d ${D}${sbindir}
    install -m 0755 secure-service.sh ${D}${sbindir}/secure-service
}

FILES:${PN} = "${systemd_system_unitdir}/* ${sbindir}/*"
```

```ini
# files/secure-service.service - Hardened systemd service template
[Unit]
Description=Secure Application Service
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
ExecStart=/usr/sbin/secure-service

# User/Group isolation
User=secure-app
Group=secure-app
DynamicUser=yes

# Filesystem protection
ProtectSystem=strict
ProtectHome=yes
ReadWritePaths=/var/lib/secure-app
PrivateTmp=yes
PrivateDevices=yes
ProtectKernelTunables=yes
ProtectKernelModules=yes
ProtectControlGroups=yes

# Capability restrictions
CapabilityBoundingSet=CAP_NET_BIND_SERVICE
AmbientCapabilities=CAP_NET_BIND_SERVICE
NoNewPrivileges=yes

# Namespace isolation
PrivateNetwork=no
PrivateUsers=yes
ProtectHostname=yes

# System call filtering
SystemCallFilter=@system-service
SystemCallFilter=~@privileged @resources
SystemCallErrorNumber=EPERM

# Resource limits
LimitNOFILE=1024
LimitNPROC=64
MemoryMax=512M
CPUQuota=50%

# Restart policy
Restart=on-failure
RestartSec=10s

[Install]
WantedBy=multi-user.target
```

### 2.3 Network Security

```python
# recipes-connectivity/firewall/firewall-config_1.0.bb

SUMMARY = "Production firewall configuration"
LICENSE = "MIT"

SRC_URI = "file://firewall-rules.sh \
           file://firewall.service \
          "

S = "${WORKDIR}"

RDEPENDS:${PN} = "iptables"

inherit systemd

SYSTEMD_SERVICE:${PN} = "firewall.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 firewall-rules.sh ${D}${sbindir}/firewall-rules

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 firewall.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} = "${sbindir}/* ${systemd_system_unitdir}/*"
```

```bash
# files/firewall-rules.sh - Production iptables rules
#!/bin/bash

# Flush existing rules
iptables -F
iptables -X
iptables -t nat -F
iptables -t nat -X
iptables -t mangle -F
iptables -t mangle -X

# Default policies
iptables -P INPUT DROP
iptables -P FORWARD DROP
iptables -P OUTPUT ACCEPT

# Allow loopback
iptables -A INPUT -i lo -j ACCEPT
iptables -A OUTPUT -o lo -j ACCEPT

# Allow established connections
iptables -A INPUT -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT

# Allow SSH (restrict to management network)
iptables -A INPUT -p tcp --dport 22 -s 10.0.0.0/8 -m conntrack --ctstate NEW,ESTABLISHED -j ACCEPT

# Allow application ports (example: HTTPS)
iptables -A INPUT -p tcp --dport 443 -m conntrack --ctstate NEW,ESTABLISHED -j ACCEPT

# Rate limiting for SSH brute force protection
iptables -A INPUT -p tcp --dport 22 -m conntrack --ctstate NEW -m recent --set
iptables -A INPUT -p tcp --dport 22 -m conntrack --ctstate NEW -m recent --update --seconds 60 --hitcount 4 -j DROP

# Drop invalid packets
iptables -A INPUT -m conntrack --ctstate INVALID -j DROP

# Logging (rate limited)
iptables -A INPUT -m limit --limit 5/min -j LOG --log-prefix "iptables-dropped: " --log-level 7

# Drop everything else
iptables -A INPUT -j DROP

# Save rules
iptables-save > /etc/iptables/rules.v4

echo "Firewall rules applied"
```

---

## 3. OTA Update Strategies

### 3.1 SWUpdate Integration

```python
# recipes-support/swupdate/swupdate_%.bbappend

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://swupdate.cfg \
    file://09-swupdate-args \
"

# Enable required features
PACKAGECONFIG:append = " \
    ssl \
    hash-verify \
    signed-images \
    encrypted-images \
    systemd \
"

# Install configuration
do_install:append() {
    install -d ${D}${sysconfdir}/swupdate
    install -m 0644 ${WORKDIR}/swupdate.cfg ${D}${sysconfdir}/swupdate/

    install -d ${D}${sysconfdir}/swupdate/conf.d
    install -m 0644 ${WORKDIR}/09-swupdate-args ${D}${sysconfdir}/swupdate/conf.d/
}
```

```
# files/swupdate.cfg - SWUpdate configuration
globals :
{
    verbose = true;
    loglevel = 5;
    syslog = true;

    public-key-file = "/etc/swupdate/public.pem";
    aes-key-file = "/etc/swupdate/aes-key";

    postupdate = "/usr/bin/postupdate.sh";
};

download :
{
    retries = 3;
    timeout = 1800;
};

identify : (
    { name = "board"; value = "jetson-xavier"; },
    { name = "hwrev"; value = "1.0"; }
);

suricatta :
{
    tenant = "production";
    id = "${DEVICE_ID}";
    url = "https://update-server.example.com";
    nocheckcert = false;
    cafile = "/etc/ssl/certs/ca-certificates.crt";

    polldelay = 300;
    retry = 5;
    retrywait = 60;
};
```

```python
# recipes-core/images/production-image.bb (continued)

# SWUpdate support
IMAGE_INSTALL:append = " swupdate swupdate-www"

# Create update image
inherit swupdate

SWU_DESCRIPTION = "Production Jetson firmware update"

# Update components
SWU_IMAGES = " \
    core-image-minimal \
    boot.img \
    kernel \
"

SWU_SCRIPTS = "postupdate.sh"

# Signing
SWUPDATE_SIGNING = "RSA"
SWUPDATE_PRIVATE_KEY = "${TOPDIR}/swupdate-keys/swupdate-priv.pem"

# Create .swu update file
python do_swuimage() {
    import os

    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')
    swu_file = os.path.join(deploy_dir, 'production-update.swu')

    # Generate sw-description
    sw_desc = generate_sw_description(d)

    # Create archive with components
    create_swu_archive(sw_desc, swu_file, d)

    # Sign archive
    sign_swu(swu_file, d)
}

addtask swuimage after do_image_complete before do_build
```

### 3.2 Delta Updates for Bandwidth Optimization

```python
# recipes-support/delta-updates/delta-update-generator_1.0.bb

SUMMARY = "Delta update generation tool"
LICENSE = "MIT"

DEPENDS = "bsdiff librsync"

SRC_URI = "file://generate-delta.sh \
           file://apply-delta.sh \
          "

S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 generate-delta.sh ${D}${bindir}/generate-delta
    install -m 0755 apply-delta.sh ${D}${bindir}/apply-delta
}

FILES:${PN} = "${bindir}/*"
RDEPENDS:${PN} = "bsdiff librsync"
```

```bash
# files/generate-delta.sh
#!/bin/bash

OLD_IMAGE=$1
NEW_IMAGE=$2
DELTA_FILE=$3

if [ $# -ne 3 ]; then
    echo "Usage: $0 <old-image> <new-image> <delta-file>"
    exit 1
fi

echo "Generating delta update..."

# Use bsdiff for binary delta
bsdiff "$OLD_IMAGE" "$NEW_IMAGE" "$DELTA_FILE"

ORIGINAL_SIZE=$(stat -c%s "$NEW_IMAGE")
DELTA_SIZE=$(stat -c%s "$DELTA_FILE")
REDUCTION=$(echo "scale=2; (1 - $DELTA_SIZE / $ORIGINAL_SIZE) * 100" | bc)

echo "Original size: $ORIGINAL_SIZE bytes"
echo "Delta size: $DELTA_SIZE bytes"
echo "Reduction: ${REDUCTION}%"
```

### 3.3 Rollback Mechanism

```bash
# recipes-support/swupdate/files/postupdate.sh
#!/bin/bash

# Post-update verification script

echo "Running post-update verification..."

# Test critical services
CRITICAL_SERVICES="
    systemd-journald
    network-manager
    application-service
"

ALL_OK=true
for service in $CRITICAL_SERVICES; do
    if ! systemctl is-active --quiet "$service"; then
        echo "ERROR: Service $service is not running"
        ALL_OK=false
    fi
done

# Test application functionality
if ! /usr/bin/health-check; then
    echo "ERROR: Application health check failed"
    ALL_OK=false
fi

# Verify bootloader version
EXPECTED_BOOTLOADER="35.4.1"
CURRENT_BOOTLOADER=$(cat /proc/device-tree/nvidia,boardids 2>/dev/null | head -1)

if [ "$CURRENT_BOOTLOADER" != "$EXPECTED_BOOTLOADER" ]; then
    echo "WARNING: Bootloader version mismatch"
fi

if [ "$ALL_OK" = true ]; then
    echo "Post-update verification successful"
    # Mark update as successful
    fw_setenv boot_successful 1
    exit 0
else
    echo "Post-update verification FAILED"
    # Trigger rollback
    exit 1
fi
```

---

## 4. A/B Partition Management

### 4.1 Redundant Boot Configuration

```python
# recipes-bsp/u-boot/u-boot-tegra_%.bbappend

# Enable A/B boot support
UBOOT_CONFIG = "redundant"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://redundant-boot.cfg"

# redundant-boot.cfg
```

```
# U-Boot redundant boot configuration
CONFIG_ENV_IS_IN_MMC=y
CONFIG_SYS_REDUNDAND_ENVIRONMENT=y
CONFIG_ENV_SIZE=0x20000
CONFIG_ENV_OFFSET=0x700000
CONFIG_ENV_OFFSET_REDUND=0x720000

# Boot count and fallback
CONFIG_BOOTCOUNT_LIMIT=y
CONFIG_BOOTCOUNT_ENV=y
CONFIG_BOOTCOUNT_BOOTLIMIT=3
```

### 4.2 A/B Partition Layout

```python
# conf/machine/jetson-xavier-ab.conf

require conf/machine/jetson-xavier.conf

# Override partition configuration
PARTITION_LAYOUT_TEMPLATE = "${LAYERDIR}/files/flash-ab.xml"

# A/B specific settings
TEGRA_REDUNDANT_BOOT_ENABLE = "1"
TEGRA_BOOTPART_SIZE_MB = "512"
```

```xml
<!-- files/flash-ab.xml - A/B partition layout -->
<partition_layout>
    <!-- Bootloader A -->
    <partition name="A_bootloader" type="data">
        <allocation_policy> sequential </allocation_policy>
        <size> 8388608 </size>
        <align_boundary> 16384 </align_boundary>
        <filesystem_type> basic </filesystem_type>
        <filename> bootloader.bin </filename>
    </partition>

    <!-- Bootloader B -->
    <partition name="B_bootloader" type="data">
        <allocation_policy> sequential </allocation_policy>
        <size> 8388608 </size>
        <align_boundary> 16384 </align_boundary>
        <filesystem_type> basic </filesystem_type>
        <filename> bootloader.bin </filename>
    </partition>

    <!-- Kernel A -->
    <partition name="A_kernel" type="data">
        <allocation_policy> sequential </allocation_policy>
        <size> 67108864 </size>
        <filesystem_type> basic </filesystem_type>
        <filename> boot.img </filename>
    </partition>

    <!-- Kernel B -->
    <partition name="B_kernel" type="data">
        <allocation_policy> sequential </allocation_policy>
        <size> 67108864 </size>
        <filesystem_type> basic </filesystem_type>
        <filename> boot.img </filename>
    </partition>

    <!-- RootFS A -->
    <partition name="A_APP" type="data">
        <allocation_policy> sequential </allocation_policy>
        <size> 8589934592 </size>
        <filesystem_type> ext4 </filesystem_type>
        <filename> system.img </filename>
        <partition_attribute> 0 </partition_attribute>
    </partition>

    <!-- RootFS B -->
    <partition name="B_APP" type="data">
        <allocation_policy> sequential </allocation_policy>
        <size> 8589934592 </size>
        <filesystem_type> ext4 </filesystem_type>
        <filename> system.img </filename>
        <partition_attribute> 0 </partition_attribute>
    </partition>

    <!-- Shared data partition -->
    <partition name="userdata" type="data">
        <allocation_policy> sequential </allocation_policy>
        <size> APPSIZE </size>
        <filesystem_type> ext4 </filesystem_type>
        <partition_attribute> 0x8000000000000000 </partition_attribute>
    </partition>
</partition_layout>
```

### 4.3 Boot Slot Management

```c
// Boot slot management utility
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define BOOT_CTRL_FILE "/dev/mmcblk0boot0"
#define MAX_RETRIES 3

typedef struct {
    uint8_t active_slot;     // 0 for A, 1 for B
    uint8_t retry_count;
    uint8_t slot_successful[2];
    uint8_t slot_unbootable[2];
} BootControl;

int read_boot_control(BootControl* ctrl) {
    FILE* fp = fopen(BOOT_CTRL_FILE, "rb");
    if (!fp) {
        perror("Failed to open boot control");
        return -1;
    }

    fread(ctrl, sizeof(BootControl), 1, fp);
    fclose(fp);
    return 0;
}

int write_boot_control(const BootControl* ctrl) {
    FILE* fp = fopen(BOOT_CTRL_FILE, "wb");
    if (!fp) {
        perror("Failed to open boot control for writing");
        return -1;
    }

    fwrite(ctrl, sizeof(BootControl), 1, fp);
    fclose(fp);
    sync();
    return 0;
}

int switch_boot_slot(int new_slot) {
    BootControl ctrl;

    if (read_boot_control(&ctrl) < 0) {
        return -1;
    }

    // Mark current slot as successful before switching
    ctrl.slot_successful[ctrl.active_slot] = 1;

    // Switch to new slot
    ctrl.active_slot = new_slot;
    ctrl.retry_count = MAX_RETRIES;
    ctrl.slot_successful[new_slot] = 0;  // Will be verified on boot
    ctrl.slot_unbootable[new_slot] = 0;

    return write_boot_control(&ctrl);
}

int mark_boot_successful() {
    BootControl ctrl;

    if (read_boot_control(&ctrl) < 0) {
        return -1;
    }

    ctrl.slot_successful[ctrl.active_slot] = 1;
    ctrl.retry_count = MAX_RETRIES;

    return write_boot_control(&ctrl);
}

int get_current_slot() {
    BootControl ctrl;

    if (read_boot_control(&ctrl) < 0) {
        return -1;
    }

    return ctrl.active_slot;
}

void print_boot_info() {
    BootControl ctrl;

    if (read_boot_control(&ctrl) < 0) {
        return;
    }

    printf("Boot Control Information:\n");
    printf("  Active Slot: %c\n", ctrl.active_slot ? 'B' : 'A');
    printf("  Retry Count: %d\n", ctrl.retry_count);
    printf("  Slot A: %s, %s\n",
           ctrl.slot_successful[0] ? "Successful" : "Unverified",
           ctrl.slot_unbootable[0] ? "Unbootable" : "Bootable");
    printf("  Slot B: %s, %s\n",
           ctrl.slot_successful[1] ? "Successful" : "Unverified",
           ctrl.slot_unbootable[1] ? "Unbootable" : "Bootable");
}

int main(int argc, char* argv[]) {
    if (argc < 2) {
        printf("Usage: %s <command> [args]\n", argv[0]);
        printf("Commands:\n");
        printf("  info           - Show boot slot information\n");
        printf("  switch <A|B>   - Switch to specified boot slot\n");
        printf("  mark-success   - Mark current slot as successful\n");
        printf("  get-slot       - Print current active slot\n");
        return 1;
    }

    if (strcmp(argv[1], "info") == 0) {
        print_boot_info();
    } else if (strcmp(argv[1], "switch") == 0 && argc == 3) {
        int slot = (argv[2][0] == 'B' || argv[2][0] == 'b') ? 1 : 0;
        if (switch_boot_slot(slot) == 0) {
            printf("Switched to slot %c\n", slot ? 'B' : 'A');
        }
    } else if (strcmp(argv[1], "mark-success") == 0) {
        if (mark_boot_successful() == 0) {
            printf("Marked current boot as successful\n");
        }
    } else if (strcmp(argv[1], "get-slot") == 0) {
        int slot = get_current_slot();
        if (slot >= 0) {
            printf("%c\n", slot ? 'B' : 'A');
        }
    }

    return 0;
}
```

---

## 5. Fleet Management

### 5.1 Device Registration and Provisioning

```python
# recipes-connectivity/device-provisioning/device-provisioning_1.0.bb

SUMMARY = "Device provisioning and fleet management client"
LICENSE = "MIT"

SRC_URI = "file://provision-device.py \
           file://device-provisioning.service \
           file://device-config.json.template \
          "

S = "${WORKDIR}"

RDEPENDS:${PN} = "python3-requests python3-cryptography"

inherit systemd

SYSTEMD_SERVICE:${PN} = "device-provisioning.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 provision-device.py ${D}${sbindir}/provision-device

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 device-provisioning.service ${D}${systemd_system_unitdir}/

    install -d ${D}${sysconfdir}/fleet
    install -m 0644 device-config.json.template ${D}${sysconfdir}/fleet/
}

FILES:${PN} = "${sbindir}/* ${systemd_system_unitdir}/* ${sysconfdir}/fleet/*"
```

```python
# files/provision-device.py
#!/usr/bin/env python3

import os
import sys
import json
import requests
import hashlib
from pathlib import Path
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.hazmat.backends import default_backend

class DeviceProvisioning:
    def __init__(self, config_file='/etc/fleet/device-config.json'):
        self.config_file = config_file
        self.config = self.load_config()
        self.provisioning_server = self.config.get('provisioning_server')
        self.device_id = self.get_device_id()

    def load_config(self):
        """Load provisioning configuration"""
        try:
            with open(self.config_file) as f:
                return json.load(f)
        except FileNotFoundError:
            return {
                'provisioning_server': 'https://fleet.example.com',
                'provision_endpoint': '/api/v1/devices/provision',
                'cert_path': '/etc/fleet/device-cert.pem',
                'key_path': '/etc/fleet/device-key.pem',
            }

    def get_device_id(self):
        """Generate unique device ID based on hardware"""
        # Use Jetson serial number
        try:
            with open('/proc/device-tree/serial-number') as f:
                serial = f.read().strip()
        except:
            serial = 'unknown'

        # Get MAC address
        try:
            with open('/sys/class/net/eth0/address') as f:
                mac = f.read().strip()
        except:
            mac = 'unknown'

        # Create deterministic device ID
        device_string = f"{serial}-{mac}"
        device_id = hashlib.sha256(device_string.encode()).hexdigest()[:16]
        return device_id

    def generate_device_keys(self):
        """Generate device key pair"""
        key_path = Path(self.config['key_path'])
        cert_path = Path(self.config['cert_path'])

        if key_path.exists():
            print(f"Device key already exists at {key_path}")
            return

        # Generate RSA key pair
        private_key = rsa.generate_private_key(
            public_exponent=65537,
            key_size=2048,
            backend=default_backend()
        )

        # Save private key
        key_path.parent.mkdir(parents=True, exist_ok=True)
        with open(key_path, 'wb') as f:
            f.write(private_key.private_bytes(
                encoding=serialization.Encoding.PEM,
                format=serialization.PrivateFormat.PKCS8,
                encryption_algorithm=serialization.NoEncryption()
            ))
        os.chmod(key_path, 0o600)

        # Get public key for CSR
        public_key = private_key.public_key()
        public_pem = public_key.public_bytes(
            encoding=serialization.Encoding.PEM,
            format=serialization.PublicFormat.SubjectPublicKeyInfo
        )

        return public_pem.decode()

    def provision(self):
        """Provision device with fleet management server"""
        print(f"Provisioning device {self.device_id}...")

        # Generate keys if needed
        public_key = self.generate_device_keys()

        # Gather device information
        device_info = {
            'device_id': self.device_id,
            'public_key': public_key,
            'hardware_info': self.get_hardware_info(),
            'software_version': self.get_software_version(),
        }

        # Send provisioning request
        try:
            url = f"{self.provisioning_server}{self.config['provision_endpoint']}"
            response = requests.post(url, json=device_info, timeout=30)
            response.raise_for_status()

            result = response.json()
            print(f"Device provisioned successfully")
            print(f"Device ID: {result['device_id']}")
            print(f"Fleet: {result['fleet_name']}")

            # Save device certificate
            if 'device_cert' in result:
                cert_path = Path(self.config['cert_path'])
                with open(cert_path, 'w') as f:
                    f.write(result['device_cert'])
                os.chmod(cert_path, 0o644)
                print(f"Device certificate saved to {cert_path}")

            # Save fleet configuration
            if 'fleet_config' in result:
                with open('/etc/fleet/fleet-config.json', 'w') as f:
                    json.dump(result['fleet_config'], f, indent=2)

            return True

        except requests.exceptions.RequestException as e:
            print(f"Provisioning failed: {e}")
            return False

    def get_hardware_info(self):
        """Collect hardware information"""
        info = {}

        # Jetson model
        try:
            with open('/proc/device-tree/model') as f:
                info['model'] = f.read().strip()
        except:
            info['model'] = 'unknown'

        # CUDA version
        try:
            import subprocess
            result = subprocess.run(['nvcc', '--version'],
                                  capture_output=True, text=True)
            for line in result.stdout.split('\n'):
                if 'release' in line:
                    info['cuda_version'] = line.split('release')[1].split(',')[0].strip()
        except:
            info['cuda_version'] = 'unknown'

        # Memory
        try:
            with open('/proc/meminfo') as f:
                for line in f:
                    if line.startswith('MemTotal:'):
                        info['memory_kb'] = int(line.split()[1])
        except:
            pass

        return info

    def get_software_version(self):
        """Get software version from OS release"""
        try:
            with open('/etc/os-release') as f:
                for line in f:
                    if line.startswith('VERSION_ID='):
                        return line.split('=')[1].strip().strip('"')
        except:
            return 'unknown'

if __name__ == '__main__':
    provisioner = DeviceProvisioning()

    if '--provision' in sys.argv:
        provisioner.provision()
    elif '--device-id' in sys.argv:
        print(provisioner.device_id)
    else:
        print("Usage: provision-device.py [--provision|--device-id]")
```

### 5.2 Remote Monitoring and Telemetry

```python
# recipes-connectivity/telemetry/telemetry-agent_1.0.bb

SUMMARY = "Device telemetry and monitoring agent"
LICENSE = "MIT"

SRC_URI = "file://telemetry-agent.py \
           file://telemetry.service \
          "

S = "${WORKDIR}"

RDEPENDS:${PN} = "python3-requests python3-psutil"

inherit systemd

SYSTEMD_SERVICE:${PN} = "telemetry.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 telemetry-agent.py ${D}${sbindir}/telemetry-agent

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 telemetry.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} = "${sbindir}/* ${systemd_system_unitdir}/*"
```

```python
# files/telemetry-agent.py
#!/usr/bin/env python3

import os
import time
import json
import psutil
import requests
from datetime import datetime
from pathlib import Path

class TelemetryAgent:
    def __init__(self, config_file='/etc/fleet/fleet-config.json'):
        self.config = self.load_config(config_file)
        self.telemetry_endpoint = self.config.get('telemetry_endpoint')
        self.interval = self.config.get('telemetry_interval', 60)
        self.device_id = self.get_device_id()

    def load_config(self, config_file):
        """Load fleet configuration"""
        try:
            with open(config_file) as f:
                return json.load(f)
        except:
            return {
                'telemetry_endpoint': 'https://telemetry.example.com/api/v1/metrics',
                'telemetry_interval': 60,
            }

    def get_device_id(self):
        """Get device ID"""
        try:
            import subprocess
            result = subprocess.run(['/usr/sbin/provision-device', '--device-id'],
                                  capture_output=True, text=True)
            return result.stdout.strip()
        except:
            return 'unknown'

    def collect_metrics(self):
        """Collect system metrics"""
        metrics = {
            'device_id': self.device_id,
            'timestamp': datetime.utcnow().isoformat(),
            'system': self.collect_system_metrics(),
            'gpu': self.collect_gpu_metrics(),
            'process': self.collect_process_metrics(),
        }
        return metrics

    def collect_system_metrics(self):
        """Collect CPU, memory, disk metrics"""
        return {
            'cpu_percent': psutil.cpu_percent(interval=1),
            'cpu_freq_mhz': psutil.cpu_freq().current if psutil.cpu_freq() else 0,
            'memory_percent': psutil.virtual_memory().percent,
            'memory_available_mb': psutil.virtual_memory().available / 1024 / 1024,
            'disk_percent': psutil.disk_usage('/').percent,
            'disk_free_gb': psutil.disk_usage('/').free / 1024 / 1024 / 1024,
            'boot_time': psutil.boot_time(),
            'uptime_seconds': time.time() - psutil.boot_time(),
        }

    def collect_gpu_metrics(self):
        """Collect GPU-specific metrics"""
        metrics = {}

        try:
            # GPU frequency
            with open('/sys/devices/gpu.0/devfreq/17000000.gv11b/cur_freq') as f:
                metrics['gpu_freq_hz'] = int(f.read())
        except:
            pass

        try:
            # GPU temperature
            with open('/sys/devices/virtual/thermal/thermal_zone1/temp') as f:
                metrics['gpu_temp_c'] = float(f.read()) / 1000
        except:
            pass

        try:
            # Power consumption
            with open('/sys/bus/i2c/drivers/ina3221x/1-0040/iio:device0/in_power0_input') as f:
                metrics['power_mw'] = int(f.read())
        except:
            pass

        return metrics

    def collect_process_metrics(self):
        """Collect application-specific metrics"""
        # Monitor specific processes
        processes = {}
        for proc in psutil.process_iter(['pid', 'name', 'cpu_percent', 'memory_percent']):
            try:
                if proc.info['name'] in ['application', 'inference-engine']:
                    processes[proc.info['name']] = {
                        'pid': proc.info['pid'],
                        'cpu_percent': proc.info['cpu_percent'],
                        'memory_percent': proc.info['memory_percent'],
                    }
            except:
                pass

        return processes

    def send_metrics(self, metrics):
        """Send metrics to telemetry server"""
        try:
            # Load device certificate for mTLS
            cert_path = '/etc/fleet/device-cert.pem'
            key_path = '/etc/fleet/device-key.pem'

            if Path(cert_path).exists() and Path(key_path).exists():
                cert = (cert_path, key_path)
            else:
                cert = None

            response = requests.post(
                self.telemetry_endpoint,
                json=metrics,
                cert=cert,
                timeout=10
            )
            response.raise_for_status()
            return True

        except requests.exceptions.RequestException as e:
            print(f"Failed to send metrics: {e}")
            return False

    def run(self):
        """Main telemetry loop"""
        print(f"Telemetry agent started for device {self.device_id}")
        print(f"Reporting interval: {self.interval} seconds")

        while True:
            try:
                metrics = self.collect_metrics()
                if self.send_metrics(metrics):
                    print(f"Metrics sent at {metrics['timestamp']}")
                else:
                    print("Failed to send metrics, will retry")

            except Exception as e:
                print(f"Error collecting metrics: {e}")

            time.sleep(self.interval)

if __name__ == '__main__':
    agent = TelemetryAgent()
    agent.run()
```

---

## 6. Production Deployment Checklist

### Pre-Deployment

- [ ] Security audit completed
- [ ] Secure boot enabled and tested
- [ ] All signing keys securely stored
- [ ] AppArmor profiles tested
- [ ] Firewall rules validated
- [ ] OTA update mechanism tested
- [ ] A/B partition switching verified
- [ ] Rollback mechanism tested

### Image Validation

- [ ] No development packages included
- [ ] No default passwords
- [ ] All unnecessary services disabled
- [ ] Root filesystem is read-only
- [ ] Audit logging enabled
- [ ] IMA/EVM configured
- [ ] SELinux or AppArmor enforcing

### Deployment

- [ ] Device provisioning successful
- [ ] Device registered with fleet management
- [ ] Telemetry reporting verified
- [ ] Remote update capability tested
- [ ] Monitoring dashboards configured
- [ ] Incident response procedures documented

### Post-Deployment

- [ ] Monitor telemetry for anomalies
- [ ] Review security logs
- [ ] Test emergency rollback procedures
- [ ] Conduct regular security audits
- [ ] Plan update cycles

---

## Real-World Case Study: Industrial IoT Deployment

**Scenario**: Deploy 1000 Jetson Xavier devices for industrial inspection

**Requirements**:
- Zero-touch provisioning
- Secure boot mandatory
- Daily telemetry reports
- Monthly security updates
- 99.9% uptime requirement

**Solution Architecture**:
1. Factory provisioning with unique device certificates
2. Secure boot with hardware-backed keys
3. A/B partitioning with automatic rollback
4. Delta updates to minimize bandwidth
5. Fleet management with real-time monitoring

**Results After 6 Months**:
- 998/1000 devices online (99.8%)
- 0 security incidents
- 47 successful OTA updates
- 3 automatic rollbacks prevented outages
- Average update size reduced 85% with delta updates

**Lessons Learned**:
- Invest heavily in testing rollback mechanisms
- Delta updates critical for large fleets
- Device telemetry invaluable for proactive maintenance
- Redundant boot crucial for reliability
- Certificate management requires automation

---

## Best Practices Summary

1. **Always Enable Secure Boot**: Hardware root of trust is critical
2. **Test Rollback Thoroughly**: Failed updates must not brick devices
3. **Use Read-Only Root**: Prevents unauthorized modifications
4. **Implement A/B Partitioning**: Enables safe updates with fallback
5. **Monitor Everything**: Telemetry helps prevent issues
6. **Automate Provisioning**: Manual provisioning doesn't scale
7. **Plan for Failure**: Design for graceful degradation
8. **Security in Depth**: Multiple layers of defense
9. **Regular Security Updates**: Keep systems patched
10. **Document Everything**: Disaster recovery requires documentation

---

**Next Steps**: Proceed to [Custom BSP Development](03-custom-bsp-development.md) for board bring-up and hardware integration.
