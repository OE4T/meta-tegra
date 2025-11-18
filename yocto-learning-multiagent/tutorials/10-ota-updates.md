# Tutorial 10: OTA Update System
## Implementing Over-The-Air Updates for Jetson Devices

---

## Learning Objectives

After completing this tutorial, you will be able to:
- Understand OTA update strategies and architectures
- Implement A/B partition scheme for safe updates
- Create update packages with OSTree or SWUpdate
- Deploy updates over the network
- Implement rollback mechanisms
- Secure update process with signing
- Monitor update status and health
- Scale OTA to fleet management

---

## Prerequisites Checklist

- [ ] Completed Tutorial 01-09 (full Yocto & AI pipeline)
- [ ] Understanding of bootloaders and partitions
- [ ] Network-connected Jetson devices
- [ ] HTTPS/TLS basics
- [ ] Shell scripting knowledge
- [ ] Extra storage space for dual partitions

---

## Estimated Duration

**Total Time**: 8-10 hours
- Update strategy design: 1 hour
- A/B partition setup: 2 hours
- SWUpdate integration: 2-3 hours
- Update server setup: 1-2 hours
- Security and signing: 1-2 hours
- Testing and validation: 1-2 hours

---

## Step-by-Step Instructions

### Step 1: Understand OTA Update Strategies

Choose an OTA update approach:

```
OTA Update Strategies:
┌────────────────────────────────────────────────┐
│  1. A/B Partition Updates                      │
│     - Dual boot partitions                     │
│     - Safe rollback                            │
│     - More storage needed                      │
│     - Fast updates (reboot only)               │
├────────────────────────────────────────────────┤
│  2. Atomic Updates (OSTree)                    │
│     - Git-like filesystem                      │
│     - Efficient delta updates                  │
│     - Transactional                            │
│     - More complex                             │
├────────────────────────────────────────────────┤
│  3. Package-Based Updates                      │
│     - Traditional package managers             │
│     - Granular updates                         │
│     - Less safe (can break system)             │
│     - Minimal overhead                         │
└────────────────────────────────────────────────┘

We'll implement A/B updates with SWUpdate
```

### Step 2: Create A/B Partition Layout

Design dual-partition storage:

```bash
# On host, create WKS file for A/B partitions
cd ~/yocto-jetson/meta-custom

mkdir -p wic
cat > wic/jetson-ab-layout.wks << 'EOF'
# Jetson A/B partition layout
# Total: ~32GB recommended

part /boot/efi --source bootimg-efi --sourceparams="loader=grub-efi" \
     --ondisk mmcblk0 --label efi --align 1024 --size 256M

part /boot --source bootimg-partition --ondisk mmcblk0 --fstype=ext4 \
     --label boot --align 1024 --size 512M --active

# A partition (active)
part / --source rootfs --ondisk mmcblk0 --fstype=ext4 \
     --label rootfs_a --align 1024 --size 8192M

# B partition (standby)
part /mnt/rootfs_b --ondisk mmcblk0 --fstype=ext4 \
     --label rootfs_b --align 1024 --size 8192M --fsoptions="ro"

# Data partition (persistent across updates)
part /data --ondisk mmcblk0 --fstype=ext4 \
     --label data --align 1024 --size 8192M

bootloader --ptable gpt
EOF
```

**Partition Layout**:
```
┌───────────────────────────────────────┐
│ EFI (256MB)                           │  Boot loader
├───────────────────────────────────────┤
│ Boot (512MB)                          │  Kernel, DTB
├───────────────────────────────────────┤
│ RootFS A (8GB)                        │  Active system
├───────────────────────────────────────┤
│ RootFS B (8GB)                        │  Update target
├───────────────────────────────────────┤
│ Data (8GB)                            │  Persistent data
└───────────────────────────────────────┘
  Total: ~25GB minimum
```

### Step 3: Integrate SWUpdate

Add SWUpdate to your Yocto build:

```bash
cd ~/yocto-jetson/meta-custom

# Create SWUpdate configuration
mkdir -p recipes-support/swupdate/files

cat > recipes-support/swupdate/files/swupdate.cfg << 'EOF'
# SWUpdate configuration

globals :
{
    verbose = true;
    loglevel = 5;
    syslog = true;

    /* Update via web interface */
    webserver = {
        port = 8080;
        userid = "admin";
        password_hash = "sha256:admin";
    };

    /* Download settings */
    download = {
        retries = 3;
        timeout = 300;
    };
};

/* Hardware compatibility */
hardware-compatibility: [ "1.0" ];

/* Partition selection based on current active */
bootloader : {
    device = "/dev/mmcblk0";
};

/* Pre/post update scripts */
scripts : {
    pre-update = "/usr/lib/swupdate/preinst.sh";
    post-update = "/usr/lib/swupdate/postinst.sh";
};
EOF

# Create pre-install script
cat > recipes-support/swupdate/files/preinst.sh << 'EOF'
#!/bin/bash
# Pre-update script

echo "Starting update process..."

# Determine current active partition
CURRENT_ROOT=$(findmnt -n -o SOURCE /)

if [[ "$CURRENT_ROOT" == *"rootfs_a"* ]]; then
    export UPDATE_TARGET="rootfs_b"
    export UPDATE_PART="/dev/mmcblk0p3"
else
    export UPDATE_TARGET="rootfs_a"
    export UPDATE_PART="/dev/mmcblk0p2"
fi

echo "Current: $CURRENT_ROOT"
echo "Update target: $UPDATE_TARGET ($UPDATE_PART)"

# Unmount target if mounted
umount /mnt/$UPDATE_TARGET 2>/dev/null || true

exit 0
EOF

# Create post-install script
cat > recipes-support/swupdate/files/postinst.sh << 'EOF'
#!/bin/bash
# Post-update script

echo "Update installed successfully"

# Update bootloader to boot from new partition
# This is bootloader-specific (U-Boot, UEFI, etc.)

# For UEFI/GRUB on Jetson:
if [[ "$UPDATE_TARGET" == "rootfs_b" ]]; then
    grub-editenv /boot/efi/EFI/BOOT/grubenv set boot_partition=rootfs_b
else
    grub-editenv /boot/efi/EFI/BOOT/grubenv set boot_partition=rootfs_a
fi

echo "Bootloader updated to boot from $UPDATE_TARGET"
echo "System will reboot to apply update"

exit 0
EOF

chmod +x recipes-support/swupdate/files/*.sh

# Create SWUpdate bbappend
cat > recipes-support/swupdate/swupdate_%.bbappend << 'EOF'
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "\
    file://swupdate.cfg \
    file://preinst.sh \
    file://postinst.sh \
"

do_install:append() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/swupdate.cfg ${D}${sysconfdir}/swupdate.cfg

    install -d ${D}${libdir}/swupdate
    install -m 0755 ${WORKDIR}/preinst.sh ${D}${libdir}/swupdate/
    install -m 0755 ${WORKDIR}/postinst.sh ${D}${libdir}/swupdate/
}

FILES:${PN} += "${libdir}/swupdate/*"
EOF
```

### Step 4: Create Update Package Format

Define SWUpdate package descriptor:

```bash
cd ~/yocto-jetson/meta-custom

mkdir -p recipes-core/images/files

cat > recipes-core/images/files/sw-description << 'EOF'
software =
{
    version = "1.0.0";

    hardware-compatibility: [ "1.0" ];

    /* Images to update */
    images: (
        {
            filename = "rootfs.ext4.gz";
            type = "raw";
            compressed = "zlib";
            device = "/dev/mmcblk0p3";  /* Will be determined dynamically */
            sha256 = "@rootfs.ext4.gz";
        },
        {
            filename = "Image";
            type = "raw";
            device = "/dev/mmcblk0p1";
            path = "/boot/Image";
            sha256 = "@Image";
        },
        {
            filename = "tegra234.dtb";
            type = "raw";
            device = "/dev/mmcblk0p1";
            path = "/boot/tegra234.dtb";
            sha256 = "@tegra234.dtb";
        }
    );

    /* Update scripts */
    scripts: (
        {
            filename = "update-bootenv.sh";
            type = "shellscript";
            sha256 = "@update-bootenv.sh";
        }
    );
}
EOF

# Create update script
cat > recipes-core/images/files/update-bootenv.sh << 'EOF'
#!/bin/bash
# Update boot environment to switch partitions

# Determine target partition
CURRENT_ROOT=$(findmnt -n -o SOURCE /)

if [[ "$CURRENT_ROOT" == *"rootfs_a"* ]]; then
    NEW_ROOT="rootfs_b"
else
    NEW_ROOT="rootfs_a"
fi

# Update boot configuration (bootloader-specific)
echo "Switching boot partition to $NEW_ROOT"

# For U-Boot:
# fw_setenv boot_partition $NEW_ROOT

# For GRUB:
grub-editenv /boot/efi/EFI/BOOT/grubenv set boot_partition=$NEW_ROOT

# Mark for validation on next boot
touch /data/.update_pending

echo "Boot partition updated. Reboot to apply."
EOF

chmod +x recipes-core/images/files/update-bootenv.sh
```

### Step 5: Create Update Image Recipe

Build update packages automatically:

```bash
cat > ~/yocto-jetson/meta-custom/recipes-core/images/update-image.bb << 'EOF'
SUMMARY = "OTA update package for Jetson"
LICENSE = "MIT"

inherit swupdate

# Base image to update
IMAGE_DEPENDS = "core-image-minimal"

# SWUpdate package contents
SRC_URI = "\
    file://sw-description \
    file://update-bootenv.sh \
"

# Add rootfs and kernel from build
IMAGE_FSTYPES = "ext4.gz"

do_swuimage[depends] += "core-image-minimal:do_image_complete"

# Files to include in update
SWUPDATE_IMAGES = "\
    core-image-minimal-${MACHINE}.ext4.gz \
    Image \
    ${KERNEL_DEVICETREE} \
"

SWUPDATE_IMAGES_FSTYPES[core-image-minimal-${MACHINE}.ext4.gz] = ".ext4.gz"

# Generate SWU package
python do_swuimage() {
    import os
    import hashlib

    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')
    workdir = d.getVar('WORKDIR')

    # Calculate checksums
    files = [
        'rootfs.ext4.gz',
        'Image',
        'tegra234.dtb',
        'update-bootenv.sh'
    ]

    sw_desc = os.path.join(workdir, 'sw-description')
    with open(sw_desc, 'r') as f:
        content = f.read()

    for fname in files:
        fpath = os.path.join(deploy_dir, fname)
        if os.path.exists(fpath):
            sha256 = hashlib.sha256()
            with open(fpath, 'rb') as f:
                sha256.update(f.read())
            checksum = sha256.hexdigest()
            content = content.replace(f'@{fname}', checksum)

    # Write updated sw-description
    with open(sw_desc, 'w') as f:
        f.write(content)

    # Create SWU archive
    os.system(f'cd {deploy_dir} && \
               tar czf update-${MACHINE}-${PV}.swu \
               sw-description {" ".join(files)}')
}

addtask swuimage after do_image_complete before do_build
EOF
```

### Step 6: Set Up Update Server

Create an update server:

```bash
# Create update server application
cat > ~/update-server/server.py << 'EOF'
#!/usr/bin/env python3
"""
Simple OTA update server
Serves SWUpdate packages over HTTPS
"""

from flask import Flask, send_file, jsonify, request
import os
import hashlib
import json

app = Flask(__name__)

UPDATE_DIR = '/var/lib/ota-updates'
DEVICE_DB = '/var/lib/ota-updates/devices.json'

def load_devices():
    """Load device database"""
    if os.path.exists(DEVICE_DB):
        with open(DEVICE_DB, 'r') as f:
            return json.load(f)
    return {}

def save_devices(devices):
    """Save device database"""
    with open(DEVICE_DB, 'w') as f:
        json.dump(devices, f, indent=2)

@app.route('/api/check_update', methods=['POST'])
def check_update():
    """Check if update is available"""

    data = request.json
    device_id = data.get('device_id')
    current_version = data.get('version')
    hardware = data.get('hardware')

    # Find latest compatible update
    updates = []
    for fname in os.listdir(UPDATE_DIR):
        if fname.endswith('.swu') and hardware in fname:
            updates.append(fname)

    if not updates:
        return jsonify({'update_available': False})

    latest = sorted(updates)[-1]
    latest_version = latest.split('-')[2].replace('.swu', '')

    if latest_version > current_version:
        # Calculate checksum
        fpath = os.path.join(UPDATE_DIR, latest)
        sha256 = hashlib.sha256()
        with open(fpath, 'rb') as f:
            sha256.update(f.read())

        return jsonify({
            'update_available': True,
            'version': latest_version,
            'url': f'/api/download/{latest}',
            'size': os.path.getsize(fpath),
            'checksum': sha256.hexdigest()
        })

    return jsonify({'update_available': False})

@app.route('/api/download/<filename>', methods=['GET'])
def download_update(filename):
    """Download update package"""

    fpath = os.path.join(UPDATE_DIR, filename)

    if not os.path.exists(fpath):
        return jsonify({'error': 'Update not found'}), 404

    return send_file(fpath, as_attachment=True)

@app.route('/api/report_status', methods=['POST'])
def report_status():
    """Device reports update status"""

    data = request.json
    device_id = data.get('device_id')
    status = data.get('status')
    version = data.get('version')

    devices = load_devices()
    devices[device_id] = {
        'status': status,
        'version': version,
        'last_seen': datetime.now().isoformat()
    }
    save_devices(devices)

    return jsonify({'status': 'ok'})

@app.route('/api/devices', methods=['GET'])
def list_devices():
    """List all registered devices"""

    devices = load_devices()
    return jsonify(devices)

if __name__ == '__main__':
    os.makedirs(UPDATE_DIR, exist_ok=True)

    # Run with HTTPS (use real certificates in production)
    app.run(host='0.0.0.0', port=8443, ssl_context='adhoc')
EOF

# Create systemd service
cat > ~/update-server/ota-server.service << 'EOF'
[Unit]
Description=OTA Update Server
After=network.target

[Service]
Type=simple
User=ota-server
ExecStart=/usr/bin/python3 /opt/ota-server/server.py
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# Install dependencies
pip3 install flask pyopenssl
```

### Step 7: Create Update Client

Client-side update checker:

```bash
cat > ~/yocto-jetson/meta-custom/recipes-support/ota-client/files/ota-client.py << 'EOF'
#!/usr/bin/env python3
"""
OTA Update Client for Jetson
Checks for and applies updates automatically
"""

import requests
import subprocess
import json
import os
import sys
import hashlib
import time

CONFIG_FILE = '/etc/ota-client.conf'
DEVICE_ID_FILE = '/data/.device_id'
VERSION_FILE = '/etc/version'

def load_config():
    """Load OTA client configuration"""
    with open(CONFIG_FILE, 'r') as f:
        return json.load(f)

def get_device_id():
    """Get or generate device ID"""
    if os.path.exists(DEVICE_ID_FILE):
        with open(DEVICE_ID_FILE, 'r') as f:
            return f.read().strip()

    # Generate from MAC address
    mac = subprocess.check_output(
        ["cat", "/sys/class/net/eth0/address"]
    ).decode().strip()

    device_id = hashlib.sha256(mac.encode()).hexdigest()[:16]

    with open(DEVICE_ID_FILE, 'w') as f:
        f.write(device_id)

    return device_id

def get_current_version():
    """Get current system version"""
    if os.path.exists(VERSION_FILE):
        with open(VERSION_FILE, 'r') as f:
            return f.read().strip()
    return "0.0.0"

def check_for_update(server_url, device_id, version, hardware):
    """Check if update is available"""

    try:
        resp = requests.post(
            f'{server_url}/api/check_update',
            json={
                'device_id': device_id,
                'version': version,
                'hardware': hardware
            },
            timeout=30,
            verify=False  # Use proper certs in production!
        )

        if resp.status_code == 200:
            return resp.json()
        else:
            print(f"Error checking update: {resp.status_code}")
            return None

    except Exception as e:
        print(f"Error: {e}")
        return None

def download_update(url, dest_path):
    """Download update package"""

    print(f"Downloading update from {url}...")

    try:
        resp = requests.get(url, stream=True, verify=False, timeout=300)

        if resp.status_code == 200:
            total_size = int(resp.headers.get('content-length', 0))
            downloaded = 0

            with open(dest_path, 'wb') as f:
                for chunk in resp.iter_content(chunk_size=8192):
                    f.write(chunk)
                    downloaded += len(chunk)

                    if total_size > 0:
                        percent = (downloaded / total_size) * 100
                        print(f"\rProgress: {percent:.1f}%", end='')

            print("\n✓ Download complete")
            return True
        else:
            print(f"Download failed: {resp.status_code}")
            return False

    except Exception as e:
        print(f"Download error: {e}")
        return False

def verify_checksum(file_path, expected_checksum):
    """Verify update package checksum"""

    print("Verifying checksum...")

    sha256 = hashlib.sha256()
    with open(file_path, 'rb') as f:
        for chunk in iter(lambda: f.read(8192), b''):
            sha256.update(chunk)

    actual = sha256.hexdigest()

    if actual == expected_checksum:
        print("✓ Checksum verified")
        return True
    else:
        print(f"✗ Checksum mismatch!")
        print(f"  Expected: {expected_checksum}")
        print(f"  Actual:   {actual}")
        return False

def apply_update(swu_path):
    """Apply SWUpdate package"""

    print("Applying update...")

    try:
        # Use SWUpdate to install
        result = subprocess.run(
            ['swupdate', '-i', swu_path, '-v'],
            capture_output=True,
            text=True,
            timeout=600
        )

        if result.returncode == 0:
            print("✓ Update applied successfully")
            return True
        else:
            print(f"✗ Update failed: {result.stderr}")
            return False

    except Exception as e:
        print(f"Update error: {e}")
        return False

def report_status(server_url, device_id, status, version):
    """Report update status to server"""

    try:
        requests.post(
            f'{server_url}/api/report_status',
            json={
                'device_id': device_id,
                'status': status,
                'version': version
            },
            timeout=30,
            verify=False
        )
    except:
        pass  # Best effort

def main():
    print("=== OTA Update Client ===\n")

    # Load configuration
    config = load_config()
    server_url = config['server_url']
    hardware = config['hardware']
    check_interval = config.get('check_interval', 3600)

    # Get device info
    device_id = get_device_id()
    current_version = get_current_version()

    print(f"Device ID: {device_id}")
    print(f"Current version: {current_version}")
    print(f"Hardware: {hardware}")
    print(f"Server: {server_url}\n")

    while True:
        print("Checking for updates...")

        update_info = check_for_update(
            server_url, device_id, current_version, hardware
        )

        if update_info and update_info.get('update_available'):
            print(f"✓ Update available: {update_info['version']}")
            print(f"  Size: {update_info['size'] / 1024 / 1024:.1f} MB")

            # Download
            swu_path = '/tmp/update.swu'
            if download_update(server_url + update_info['url'], swu_path):

                # Verify
                if verify_checksum(swu_path, update_info['checksum']):

                    # Apply
                    if apply_update(swu_path):
                        report_status(server_url, device_id,
                                      'updated', update_info['version'])

                        print("\n✓ Update complete!")
                        print("System will reboot in 10 seconds...")
                        time.sleep(10)
                        subprocess.run(['reboot'])
                        sys.exit(0)

                    else:
                        report_status(server_url, device_id,
                                      'failed', current_version)

                # Clean up
                os.remove(swu_path)
        else:
            print("No updates available")

        print(f"\nNext check in {check_interval} seconds...")
        time.sleep(check_interval)

if __name__ == "__main__":
    main()
EOF

# Create config file
cat > ~/yocto-jetson/meta-custom/recipes-support/ota-client/files/ota-client.conf << 'EOF'
{
    "server_url": "https://ota-server.example.com:8443",
    "hardware": "jetson-orin-agx",
    "check_interval": 3600,
    "auto_update": true
}
EOF

# Create systemd service
cat > ~/yocto-jetson/meta-custom/recipes-support/ota-client/files/ota-client.service << 'EOF'
[Unit]
Description=OTA Update Client
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
ExecStart=/usr/bin/python3 /usr/sbin/ota-client.py
Restart=always
RestartSec=60

[Install]
WantedBy=multi-user.target
EOF
```

### Step 8: Add Update Signing for Security

Sign update packages:

```bash
# Generate signing keys
openssl genrsa -out private_key.pem 2048
openssl rsa -in private_key.pem -outform PEM -pubout -out public_key.pem

# Create signing script
cat > ~/yocto-jetson/scripts/sign-update.sh << 'EOF'
#!/bin/bash
# Sign SWUpdate package

SWU_FILE=$1
PRIVATE_KEY=$2

if [ -z "$SWU_FILE" ] || [ -z "$PRIVATE_KEY" ]; then
    echo "Usage: $0 <update.swu> <private_key.pem>"
    exit 1
fi

# Extract sw-description
tar xzf "$SWU_FILE" sw-description

# Sign sw-description
openssl dgst -sha256 -sign "$PRIVATE_KEY" \
    -out sw-description.sig sw-description

# Re-package with signature
tar czf "${SWU_FILE%.swu}-signed.swu" \
    sw-description sw-description.sig \
    $(tar tzf "$SWU_FILE" | grep -v sw-description)

# Clean up
rm sw-description sw-description.sig

echo "Signed package: ${SWU_FILE%.swu}-signed.swu"
EOF

chmod +x ~/yocto-jetson/scripts/sign-update.sh
```

### Step 9: Implement Rollback Mechanism

Add automatic rollback on failure:

```bash
cat > ~/yocto-jetson/meta-custom/recipes-support/ota-client/files/update-watchdog.sh << 'EOF'
#!/bin/bash
# Update watchdog - verifies system after update

VALIDATION_FILE="/data/.update_pending"
MAX_BOOT_COUNT=3
BOOT_COUNT_FILE="/data/.boot_count"

if [ -f "$VALIDATION_FILE" ]; then
    # Increment boot counter
    if [ -f "$BOOT_COUNT_FILE" ]; then
        COUNT=$(cat "$BOOT_COUNT_FILE")
    else
        COUNT=0
    fi

    COUNT=$((COUNT + 1))
    echo $COUNT > "$BOOT_COUNT_FILE"

    echo "Update validation: Boot attempt $COUNT/$MAX_BOOT_COUNT"

    # Run system health checks
    if systemctl is-system-running --quiet; then
        echo "✓ System health check passed"

        # Mark update as successful
        rm "$VALIDATION_FILE"
        rm "$BOOT_COUNT_FILE"

        echo "✓ Update validated successfully"
        exit 0
    else
        echo "⚠ System health check failed"

        if [ $COUNT -ge $MAX_BOOT_COUNT ]; then
            echo "✗ Maximum boot attempts reached"
            echo "Rolling back to previous partition..."

            # Switch back to previous partition
            CURRENT_ROOT=$(findmnt -n -o SOURCE /)

            if [[ "$CURRENT_ROOT" == *"rootfs_a"* ]]; then
                grub-editenv /boot/efi/EFI/BOOT/grubenv set boot_partition=rootfs_b
            else
                grub-editenv /boot/efi/EFI/BOOT/grubenv set boot_partition=rootfs_a
            fi

            # Reboot to rollback
            reboot
        fi
    fi
fi
EOF

# Add to systemd
cat > ~/yocto-jetson/meta-custom/recipes-support/ota-client/files/update-watchdog.service << 'EOF'
[Unit]
Description=Update Watchdog
After=multi-user.target

[Service]
Type=oneshot
ExecStart=/usr/sbin/update-watchdog.sh
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
EOF
```

### Step 10: Build and Test Complete OTA System

```bash
cd ~/yocto-jetson/builds/jetson-orin-agx

# Add all OTA components to image
echo 'IMAGE_INSTALL:append = " \
    swupdate \
    ota-client \
    update-watchdog \
"' >> conf/local.conf

# Enable A/B partitions
echo 'WKS_FILE = "jetson-ab-layout.wks"' >> conf/local.conf

# Build image with OTA support
bitbake core-image-minimal

# Build update package
bitbake update-image

# Sign update package
~/yocto-jetson/scripts/sign-update.sh \
    tmp/deploy/images/jetson-orin-agx-devkit/update-*.swu \
    ~/keys/private_key.pem

# Deploy to update server
scp tmp/deploy/images/jetson-orin-agx-devkit/update-*-signed.swu \
    ota-server:/var/lib/ota-updates/

# On Jetson, test update
systemctl start ota-client
journalctl -u ota-client -f
```

---

## Troubleshooting Common Issues

### Issue 1: Update Package Fails to Install

**Symptoms**: SWUpdate reports errors

**Solutions**:
```bash
# Check SWUpdate logs
journalctl -u swupdate -f

# Verify package integrity
swupdate -check -i update.swu

# Test with verbose logging
swupdate -v -i update.swu

# Common issues:
# - Incorrect sw-description syntax
# - Missing files in package
# - Partition size too small
```

### Issue 2: Boot Fails After Update

**Symptoms**: System doesn't boot after update

**Solutions**:
```bash
# Connect serial console to see boot messages

# Force boot from previous partition:
# In U-Boot:
setenv boot_partition rootfs_a
saveenv
boot

# In GRUB:
# Edit boot entry at boot time
# Add: root=/dev/mmcblk0p2  (for partition A)

# Check rollback mechanism:
cat /data/.boot_count
```

### Issue 3: Network Update Download Fails

**Symptoms**: Client can't download update

**Solutions**:
```bash
# Test connectivity
ping ota-server.example.com

# Check DNS resolution
nslookup ota-server.example.com

# Test HTTPS connection
curl -k https://ota-server.example.com:8443/api/check_update

# Check firewall
iptables -L

# Verify certificates (in production)
openssl s_client -connect ota-server.example.com:8443
```

---

## Verification Checklist

- [ ] A/B partitions created correctly
- [ ] SWUpdate installed and configured
- [ ] Update package builds successfully
- [ ] Update server running and accessible
- [ ] Client can check for updates
- [ ] Client can download update package
- [ ] Update applies successfully
- [ ] System boots from new partition
- [ ] Rollback works on failure
- [ ] Update signing and verification functional
- [ ] Watchdog detects failed updates
- [ ] Multiple update cycles work

---

## Next Steps

### Production Deployment
1. Set up redundant update servers
2. Implement CDN for update distribution
3. Add monitoring and analytics
4. Create fleet management dashboard

### Advanced Features
1. Delta updates for bandwidth efficiency
2. Staggered rollout (canary deployments)
3. Update scheduling windows
4. Bandwidth throttling

### Security Hardening
1. Use hardware security module (HSM) for signing
2. Implement secure boot chain
3. Add update authentication
4. Enable encrypted updates

---

## Best Practices

### Update Package Design
- Keep packages small with delta updates
- Version all components consistently
- Include rollback data
- Test thoroughly before release

### Deployment Strategy
- Roll out to test fleet first
- Monitor success rates
- Have rollback plan ready
- Communicate with users

### Monitoring
- Track update success/failure rates
- Monitor device health post-update
- Alert on anomalies
- Log all update activities

---

**Congratulations!** You've implemented a complete OTA update system for Jetson devices with A/B partitions, automatic rollback, signed updates, and remote management capabilities. Your embedded Linux system can now be updated safely and reliably in the field!

---

*Tutorial created by the Yocto & Meta-Tegra Multi-Agent Learning System*
*Last updated: 2025-01-15*

---

## Tutorial Series Complete!

You've now completed all 10 tutorials in the Yocto & Meta-Tegra learning system:

1. ✓ Yocto Hello World
2. ✓ Custom Recipes
3. ✓ Meta-Layer Creation
4. ✓ First Jetson Boot
5. ✓ Device Tree Basics
6. ✓ GPIO Kernel Module
7. ✓ I2C Sensor Integration
8. ✓ Camera Driver
9. ✓ AI Inference Pipeline
10. ✓ OTA Updates

You now have the skills to build complete, production-ready embedded Linux systems on NVIDIA Jetson platforms with custom hardware, AI capabilities, and remote update functionality!
