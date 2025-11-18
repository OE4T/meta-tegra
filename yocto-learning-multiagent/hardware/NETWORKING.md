# Network Integration Guide

## Overview

Comprehensive networking reference for NVIDIA Jetson platforms, covering Ethernet, WiFi, Bluetooth, CAN bus, TSN (Time-Sensitive Networking), and network boot configurations.

## Ethernet Configuration

### Hardware Capabilities

| Platform | Ethernet Interfaces | Speeds | Features |
|----------|-------------------|--------|----------|
| AGX Orin | 1x 10GbE (MGBE), 1x 1GbE | 10G/1G/100M | TSN, PTP, RGMII |
| Orin NX | 1x 1GbE | 1G/100M/10M | RGMII |
| Orin Nano | 1x 1GbE | 1G/100M/10M | RGMII |
| AGX Xavier | 1x 10GbE (MGBE), 1x 1GbE | 10G/1G/100M | TSN, PTP |
| Xavier NX | 1x 1GbE | 1G/100M/10M | RGMII |
| Jetson Nano | 1x 1GbE | 1G/100M/10M | RGMII |

### PHY Configuration

**Common PHY Chips:**
- Realtek RTL8211F (1GbE)
- Marvell 88E1512 (1GbE)
- Aquantia AQR113C (10GbE)

**Device Tree Configuration:**

```dts
ethernet@2490000 {
    compatible = "nvidia,nveqos";
    reg = <0x0 0x02490000 0x0 0x10000>,    /* EQOS MAC */
          <0x0 0x02310000 0x0 0x1000>;     /* MGBE MAC (10GbE) */
    interrupts = <GIC_SPI 194 IRQ_TYPE_LEVEL_HIGH>;
    clocks = <&bpmp_clks TEGRA234_CLK_EQOS_AXI>,
             <&bpmp_clks TEGRA234_CLK_EQOS_RX>,
             <&bpmp_clks TEGRA234_CLK_EQOS_TX>;
    clock-names = "master_bus", "slave_bus", "rx", "tx", "ptp_ref";
    resets = <&bpmp_resets TEGRA234_RESET_EQOS>;
    reset-names = "eqos";

    phy-mode = "rgmii-id";
    phy-handle = <&phy0>;

    nvidia,max-platform-mtu = <16383>;
    nvidia,pause_frames = <0>;
    nvidia,phy-reset-gpio = <&tegra_main_gpio TEGRA234_MAIN_GPIO(G, 5) GPIO_ACTIVE_LOW>;
    nvidia,phy-reset-duration = <10>;
    nvidia,phy-reset-post-delay = <224>;

    mdio {
        compatible = "nvidia,eqos-mdio";
        #address-cells = <1>;
        #size-cells = <0>;

        phy0: ethernet-phy@0 {
            reg = <0>;
            interrupt-parent = <&tegra_main_gpio>;
            interrupts = <TEGRA234_MAIN_GPIO(G, 4) IRQ_TYPE_LEVEL_LOW>;

            /* PHY-specific settings */
            realtek,clkout-disable;
            realtek,aldps-disable;
        };
    };
};
```

### Interface Configuration

**Static IP Configuration:**

```bash
# Using NetworkManager
nmcli con mod eth0 ipv4.addresses 192.168.1.100/24
nmcli con mod eth0 ipv4.gateway 192.168.1.1
nmcli con mod eth0 ipv4.dns "8.8.8.8 8.8.4.4"
nmcli con mod eth0 ipv4.method manual
nmcli con up eth0

# Using ifconfig (legacy)
ifconfig eth0 192.168.1.100 netmask 255.255.255.0 up
route add default gw 192.168.1.1
echo "nameserver 8.8.8.8" > /etc/resolv.conf

# Using systemd-networkd
cat > /etc/systemd/network/10-eth0.network <<EOF
[Match]
Name=eth0

[Network]
Address=192.168.1.100/24
Gateway=192.168.1.1
DNS=8.8.8.8
DNS=8.8.4.4
EOF

systemctl restart systemd-networkd
```

**DHCP Configuration:**

```bash
# NetworkManager
nmcli con mod eth0 ipv4.method auto
nmcli con up eth0

# systemd-networkd
cat > /etc/systemd/network/10-eth0.network <<EOF
[Match]
Name=eth0

[Network]
DHCP=yes
EOF

systemctl restart systemd-networkd
```

### VLAN Configuration

```bash
# Create VLAN interface
ip link add link eth0 name eth0.100 type vlan id 100
ip addr add 192.168.100.10/24 dev eth0.100
ip link set eth0.100 up

# Persistent VLAN (systemd-networkd)
cat > /etc/systemd/network/10-eth0.100.netdev <<EOF
[NetDev]
Name=eth0.100
Kind=vlan

[VLAN]
Id=100
EOF

cat > /etc/systemd/network/10-eth0.100.network <<EOF
[Match]
Name=eth0.100

[Network]
Address=192.168.100.10/24
EOF

systemctl restart systemd-networkd
```

### Link Aggregation (Bonding)

```bash
# Load bonding module
modprobe bonding mode=802.3ad

# Create bond interface
ip link add bond0 type bond mode 802.3ad
ip link set eth0 down
ip link set eth1 down
ip link set eth0 master bond0
ip link set eth1 master bond0
ip addr add 192.168.1.100/24 dev bond0
ip link set bond0 up

# Verify
cat /proc/net/bonding/bond0
```

### Performance Tuning

**Increase Ring Buffer Size:**

```bash
# Check current settings
ethtool -g eth0

# Set ring buffer (TX/RX)
ethtool -G eth0 rx 4096 tx 4096
```

**Enable Jumbo Frames:**

```bash
# Set MTU to 9000
ip link set eth0 mtu 9000

# Verify
ip link show eth0
```

**Offload Features:**

```bash
# Check current offload settings
ethtool -k eth0

# Enable TCP segmentation offload
ethtool -K eth0 tso on

# Enable generic receive offload
ethtool -K eth0 gro on

# Enable scatter-gather
ethtool -K eth0 sg on
```

**Interrupt Coalescing:**

```bash
# Reduce interrupt rate for high throughput
ethtool -C eth0 rx-usecs 50 tx-usecs 50

# Or increase interrupts for low latency
ethtool -C eth0 rx-usecs 1 tx-usecs 1
```

### 10GbE Configuration (AGX Orin/Xavier)

**Device Tree:**

```dts
mgbe0@6810000 {
    compatible = "nvidia,nveqos";
    reg = <0x0 0x06810000 0x0 0x10000>;
    interrupts = <GIC_SPI 384 IRQ_TYPE_LEVEL_HIGH>;

    phy-mode = "usxgmii";
    phy-handle = <&mgbe0_phy>;

    nvidia,max-platform-mtu = <9000>;

    mdio {
        compatible = "nvidia,eqos-mdio";
        #address-cells = <1>;
        #size-cells = <0>;

        mgbe0_phy: ethernet-phy@0 {
            reg = <0x0>;
            /* Aquantia AQR113C 10GbE PHY */
        };
    };
};
```

**Performance Test:**

```bash
# Install iperf3
apt-get install iperf3

# Server
iperf3 -s

# Client
iperf3 -c 192.168.1.100 -t 60 -P 4

# Expected: 9.4+ Gbps on 10GbE
```

## WiFi Configuration

### Supported WiFi Modules

**M.2 Key-E WiFi Modules:**
- Intel AX200/AX210 (WiFi 6)
- Qualcomm QCA6174 (WiFi 5)
- Realtek RTL8822CE (WiFi 5)

**PCIe Mini Cards:**
- Intel 8265 (WiFi 5)
- Atheros AR9271 (USB)

### Driver Installation

**Intel WiFi (iwlwifi):**

```bash
# Check if driver loaded
lsmod | grep iwlwifi

# Load module
modprobe iwlwifi

# Check device
lspci | grep -i wireless
ip link show

# Expected: wlan0 interface
```

**Device Tree (M.2 Key-E):**

```dts
pcie@14160000 {
    compatible = "nvidia,tegra234-pcie";
    /* ... */

    pcie@0 {
        reg = <0x0 0x0 0x0 0x0 0x0>;
        #address-cells = <3>;
        #size-cells = <2>;
        device_type = "pci";
        ranges;

        /* WiFi module on PCIe */
    };
};
```

### WiFi Network Configuration

**WPA2 Personal:**

```bash
# Using NetworkManager
nmcli dev wifi list
nmcli dev wifi connect "SSID" password "password"

# Using wpa_supplicant
cat > /etc/wpa_supplicant/wpa_supplicant.conf <<EOF
network={
    ssid="YourSSID"
    psk="YourPassword"
    key_mgmt=WPA-PSK
}
EOF

wpa_supplicant -B -i wlan0 -c /etc/wpa_supplicant/wpa_supplicant.conf
dhclient wlan0
```

**WPA2 Enterprise (802.1X):**

```bash
cat > /etc/wpa_supplicant/wpa_supplicant-enterprise.conf <<EOF
network={
    ssid="EnterpriseSSID"
    key_mgmt=WPA-EAP
    eap=PEAP
    identity="username"
    password="password"
    phase2="auth=MSCHAPV2"
}
EOF

wpa_supplicant -B -i wlan0 -c /etc/wpa_supplicant/wpa_supplicant-enterprise.conf
dhclient wlan0
```

**WiFi Access Point Mode:**

```bash
# Install hostapd
apt-get install hostapd dnsmasq

# Configure hostapd
cat > /etc/hostapd/hostapd.conf <<EOF
interface=wlan0
driver=nl80211
ssid=JetsonAP
hw_mode=g
channel=6
wmm_enabled=0
macaddr_acl=0
auth_algs=1
ignore_broadcast_ssid=0
wpa=2
wpa_passphrase=YourPassword
wpa_key_mgmt=WPA-PSK
wpa_pairwise=TKIP
rsn_pairwise=CCMP
EOF

# Configure dnsmasq
cat > /etc/dnsmasq.conf <<EOF
interface=wlan0
dhcp-range=192.168.50.10,192.168.50.50,255.255.255.0,24h
EOF

# Configure interface
ifconfig wlan0 192.168.50.1 netmask 255.255.255.0 up

# Start services
systemctl start hostapd
systemctl start dnsmasq
```

### WiFi Performance Optimization

```bash
# Disable power save
iw dev wlan0 set power_save off

# Set regulatory domain
iw reg set US

# Check link quality
iw dev wlan0 link
iwconfig wlan0

# Scan for networks
iw dev wlan0 scan | grep -E "SSID|signal"
```

## Bluetooth Configuration

### Bluetooth Stack

**BlueZ Installation:**

```bash
apt-get install bluez bluez-tools

# Start Bluetooth service
systemctl start bluetooth
systemctl enable bluetooth

# Check status
hciconfig
# Expected: hci0 interface
```

### Bluetooth Device Pairing

**Command Line (bluetoothctl):**

```bash
bluetoothctl

# In bluetoothctl prompt:
power on
agent on
default-agent
scan on
# Wait for device to appear
pair AA:BB:CC:DD:EE:FF
trust AA:BB:CC:DD:EE:FF
connect AA:BB:CC:DD:EE:FF
```

**Python (pybluez):**

```python
#!/usr/bin/env python3
import bluetooth

# Discover nearby devices
nearby_devices = bluetooth.discover_devices(lookup_names=True)
print("Found {} devices".format(len(nearby_devices)))

for addr, name in nearby_devices:
    print("  {} - {}".format(addr, name))

# Connect to device
sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
sock.connect(("AA:BB:CC:DD:EE:FF", 1))  # Port 1
sock.send("Hello")
sock.close()
```

### BLE (Bluetooth Low Energy)

**GATT Server Example:**

```python
#!/usr/bin/env python3
import dbus
import dbus.mainloop.glib
from gi.repository import GLib

# Simplified BLE GATT server
# Full implementation requires bluez D-Bus API

class BLEServer:
    def __init__(self):
        dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)
        self.bus = dbus.SystemBus()

    def register_service(self):
        # Register GATT service
        # Implementation details omitted for brevity
        pass

    def start(self):
        mainloop = GLib.MainLoop()
        mainloop.run()

if __name__ == '__main__':
    server = BLEServer()
    server.start()
```

## CAN Bus Integration

### Hardware Support

| Platform | CAN Controllers | Driver |
|----------|----------------|--------|
| AGX Orin | 2x CAN-FD | mttcan |
| Orin NX | 2x CAN-FD | mttcan |
| AGX Xavier | 2x CAN-FD | mttcan |
| Xavier NX | 1x CAN-FD | mttcan |
| Orin Nano | - | External required |
| Jetson Nano | - | External required |

### Device Tree Configuration

```dts
mttcan@c310000 {
    compatible = "nvidia,tegra234-mttcan";
    reg = <0x0 0xc310000 0x0 0x1000>,
          <0x0 0xc311000 0x0 0x100>;
    interrupts = <GIC_SPI 40 IRQ_TYPE_LEVEL_HIGH>;
    clocks = <&bpmp_clks TEGRA234_CLK_CAN1>,
             <&bpmp_clks TEGRA234_CLK_CAN1_HOST>;
    clock-names = "can", "can_host";
    resets = <&bpmp_resets TEGRA234_RESET_CAN1>;
    reset-names = "can";
    status = "okay";

    nvidia,can-core-clock = <80000000>;
    nvidia,can-clock-source = "pll_aon";

    pinctrl-names = "default";
    pinctrl-0 = <&can1_default>;
};
```

**Pinmux Configuration:**

```dts
can1_default: can1_default {
    can1_tx_pa1 {
        nvidia,pins = "can1_dout_pa1";
        nvidia,function = "can1";
        nvidia,pull = <TEGRA_PIN_PULL_NONE>;
        nvidia,tristate = <TEGRA_PIN_DISABLE>;
        nvidia,enable-input = <TEGRA_PIN_DISABLE>;
    };

    can1_rx_pa0 {
        nvidia,pins = "can1_din_pa0";
        nvidia,function = "can1";
        nvidia,pull = <TEGRA_PIN_PULL_UP>;
        nvidia,tristate = <TEGRA_PIN_DISABLE>;
        nvidia,enable-input = <TEGRA_PIN_ENABLE>;
    };
};
```

### CAN Transceiver

**Required:** External CAN transceiver (e.g., TI TCAN332, MCP2551)

**Schematic:**

```
Jetson CAN_TX ──────┐
                    │
                ┌───▼───┐
                │  CAN  │
                │ Trans-│   CANH ──── CAN Bus H
                │ ceiver│
Jetson CAN_RX ──┤  IC   │   CANL ──── CAN Bus L
                │       │
                └───┬───┘
                    │
                   GND

120Ω termination resistor between CANH and CANL at each end of bus
```

### SocketCAN Configuration

**Bring up CAN interface:**

```bash
# Load CAN module
modprobe can
modprobe can_raw

# Configure bitrate and bring up
ip link set can0 type can bitrate 500000
ip link set can0 up

# Verify
ip -details link show can0
```

**CAN-FD Configuration:**

```bash
# CAN-FD with data bitrate 2 Mbps
ip link set can0 type can bitrate 500000 dbitrate 2000000 fd on
ip link set can0 up
```

### CAN Testing

**can-utils Tools:**

```bash
# Install can-utils
apt-get install can-utils

# Send CAN frame
cansend can0 123#DEADBEEF

# Receive CAN frames
candump can0

# Generate traffic
cangen can0 -v

# Loopback test
ip link set can0 type can loopback on
cansend can0 123#AABBCCDD
candump can0
```

**Python CAN:**

```python
#!/usr/bin/env python3
import can

# Create bus
bus = can.interface.Bus(channel='can0', bustype='socketcan')

# Send message
msg = can.Message(arbitration_id=0x123,
                  data=[0xDE, 0xAD, 0xBE, 0xEF],
                  is_extended_id=False)
bus.send(msg)
print(f"Sent: {msg}")

# Receive message
msg = bus.recv(timeout=1.0)
if msg:
    print(f"Received: {msg}")

bus.shutdown()
```

**C SocketCAN:**

```c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <net/if.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <linux/can.h>
#include <linux/can/raw.h>

int main() {
    int s;
    struct sockaddr_can addr;
    struct ifreq ifr;
    struct can_frame frame;

    // Create socket
    s = socket(PF_CAN, SOCK_RAW, CAN_RAW);

    // Specify CAN interface
    strcpy(ifr.ifr_name, "can0");
    ioctl(s, SIOCGIFINDEX, &ifr);

    // Bind socket
    addr.can_family = AF_CAN;
    addr.can_ifindex = ifr.ifr_ifindex;
    bind(s, (struct sockaddr *)&addr, sizeof(addr));

    // Send frame
    frame.can_id = 0x123;
    frame.can_dlc = 4;
    frame.data[0] = 0xDE;
    frame.data[1] = 0xAD;
    frame.data[2] = 0xBE;
    frame.data[3] = 0xEF;

    if (write(s, &frame, sizeof(struct can_frame)) != sizeof(struct can_frame)) {
        perror("write");
        return 1;
    }

    printf("CAN frame sent\n");

    // Receive frame
    if (read(s, &frame, sizeof(struct can_frame)) < 0) {
        perror("read");
        return 1;
    }

    printf("Received CAN ID: 0x%X, DLC: %d\n", frame.can_id, frame.can_dlc);

    close(s);
    return 0;
}
```

Compile:
```bash
gcc -o can_test can_test.c
```

## TSN (Time-Sensitive Networking)

### TSN Support

**Available on:**
- AGX Orin (10GbE MGBE interface)
- AGX Xavier (10GbE MGBE interface)

**Features:**
- IEEE 802.1Qbv (Time-Aware Shaping)
- IEEE 802.1Qav (Credit-Based Shaping)
- IEEE 802.1AS (gPTP - Generalized Precision Time Protocol)
- Hardware timestamps

### PTP Configuration

**Install linuxptp:**

```bash
apt-get install linuxptp

# Start PTP daemon (grandmaster)
ptp4l -i eth0 -m -s

# Or as slave
ptp4l -i eth0 -m

# Check synchronization
pmc -u -b 0 'GET TIME_STATUS_NP'
```

**PTP Configuration File:**

```ini
# /etc/linuxptp/ptp4l.conf
[global]
tx_timestamp_timeout 10
logMinPdelayReqInterval 0
logSyncInterval 0
twoStepFlag 1
summary_interval 0
clock_servo linreg

[eth0]
network_transport L2
delay_mechanism E2E
```

### TSN Qbv (Time-Aware Shaping)

**Configure Gate Control List:**

```bash
# Example: Configure time-aware traffic shaping
tc qdisc add dev eth0 parent root handle 100 taprio \
    num_tc 4 \
    map 0 1 2 3 0 0 0 0 0 0 0 0 0 0 0 0 \
    queues 1@0 1@1 1@2 1@3 \
    base-time 0 \
    sched-entry S 01 300000 \
    sched-entry S 02 300000 \
    sched-entry S 04 300000 \
    sched-entry S 08 300000 \
    clockid CLOCK_TAI
```

### TSN Testing

```python
#!/usr/bin/env python3
import socket
import struct
import time

# Create raw socket for TSN
sock = socket.socket(socket.AF_PACKET, socket.SOCK_RAW, socket.htons(0x0800))
sock.bind(("eth0", 0))

# Set socket priority (for traffic class)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_PRIORITY, 3)

# Send packet
packet = b'\x00' * 64  # Dummy packet
sock.send(packet)

# Measure latency
start = time.time_ns()
sock.send(packet)
end = time.time_ns()
print(f"TX Latency: {(end - start) / 1000:.2f} us")

sock.close()
```

## Network Boot (PXE)

### TFTP Server Setup

```bash
# Install TFTP server
apt-get install tftpd-hpa

# Configure TFTP
cat > /etc/default/tftpd-hpa <<EOF
TFTP_USERNAME="tftp"
TFTP_DIRECTORY="/var/lib/tftpboot"
TFTP_ADDRESS="0.0.0.0:69"
TFTP_OPTIONS="--secure"
EOF

# Create directory
mkdir -p /var/lib/tftpboot
chmod 777 /var/lib/tftpboot

# Restart service
systemctl restart tftpd-hpa
```

### DHCP Server for PXE

```bash
# Install DHCP server
apt-get install isc-dhcp-server

# Configure DHCP
cat > /etc/dhcp/dhcpd.conf <<EOF
subnet 192.168.1.0 netmask 255.255.255.0 {
    range 192.168.1.100 192.168.1.200;
    option routers 192.168.1.1;
    option domain-name-servers 8.8.8.8;

    # PXE boot options
    next-server 192.168.1.10;  # TFTP server IP
    filename "pxelinux.0";
}
EOF

# Restart DHCP
systemctl restart isc-dhcp-server
```

### Jetson PXE Boot

**Place boot files in TFTP directory:**

```bash
# Copy kernel and device tree
cp Image /var/lib/tftpboot/
cp tegra234-p3767-0000-p3509-a02.dtb /var/lib/tftpboot/

# Create boot script
cat > /var/lib/tftpboot/boot.scr <<EOF
setenv bootargs root=/dev/nfs nfsroot=192.168.1.10:/nfs/jetson,nfsvers=3 ip=dhcp
load tftp ${kernel_addr_r} Image
load tftp ${fdt_addr_r} tegra234-p3767-0000-p3509-a02.dtb
booti ${kernel_addr_r} - ${fdt_addr_r}
EOF

# Convert to u-boot format
mkimage -A arm64 -T script -C none -d boot.scr boot.scr.uimg
```

**NFS Root Filesystem:**

```bash
# Install NFS server
apt-get install nfs-kernel-server

# Export filesystem
echo "/nfs/jetson *(rw,sync,no_subtree_check,no_root_squash)" >> /etc/exports

# Extract rootfs
mkdir -p /nfs/jetson
tar -xf jetson-rootfs.tar.bz2 -C /nfs/jetson/

# Restart NFS
exportfs -a
systemctl restart nfs-kernel-server
```

## Yocto Integration

### Networking Packages

```bitbake
# File: recipes-core/images/jetson-networking-image.bb

IMAGE_INSTALL_append = " \
    iproute2 \
    ethtool \
    iperf3 \
    tcpdump \
    wireless-tools \
    wpa-supplicant \
    hostapd \
    bluez5 \
    can-utils \
    linuxptp \
    nfs-utils \
"
```

### WiFi Driver Recipe

```bitbake
# File: recipes-kernel/linux/linux-tegra_%.bbappend

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://wifi-config.cfg"

# Enable WiFi drivers
do_configure_append() {
    # Intel WiFi
    echo 'CONFIG_IWLWIFI=m' >> ${B}/.config
    echo 'CONFIG_IWLMVM=m' >> ${B}/.config

    # Bluetooth
    echo 'CONFIG_BT=m' >> ${B}/.config
    echo 'CONFIG_BT_HCIUART=m' >> ${B}/.config
}
```

### CAN Configuration Recipe

```bitbake
# File: recipes-connectivity/can/can-setup_1.0.bb

SUMMARY = "CAN bus configuration"
LICENSE = "MIT"

SRC_URI = "file://can-setup.sh"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/can-setup.sh ${D}${sbindir}/

    install -d ${D}${systemd_system_unitdir}
    cat > ${D}${systemd_system_unitdir}/can-setup.service <<EOF
[Unit]
Description=CAN Bus Setup
After=network.target

[Service]
Type=oneshot
ExecStart=${sbindir}/can-setup.sh
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
EOF
}

inherit systemd
SYSTEMD_SERVICE_${PN} = "can-setup.service"
```

## Testing and Verification

### Network Performance Test Script

```bash
#!/bin/bash

echo "=== Network Interface Test ==="

# Test all interfaces
for iface in /sys/class/net/*; do
    iface=$(basename $iface)
    [[ "$iface" == "lo" ]] && continue

    echo -e "\nInterface: $iface"

    # Link status
    ethtool $iface 2>/dev/null | grep -E "Link detected|Speed|Duplex"

    # IP address
    ip addr show $iface | grep "inet "

    # Statistics
    ip -s link show $iface | grep -A 2 "RX:\|TX:"
done

# CAN interfaces
echo -e "\n=== CAN Interfaces ==="
ip link show type can

# WiFi
echo -e "\n=== WiFi Status ==="
iwconfig 2>/dev/null | grep -v "no wireless"

# Bluetooth
echo -e "\n=== Bluetooth ==="
hciconfig 2>/dev/null
```

## References

### Official Documentation
- [Jetson Linux Networking Guide](https://docs.nvidia.com/jetson/archives/r35.3.1/DeveloperGuide/text/SD/Networking.html)
- [Linux SocketCAN](https://www.kernel.org/doc/html/latest/networking/can.html)
- [TSN Documentation](https://docs.nvidia.com/jetson/archives/r35.3.1/DeveloperGuide/text/SD/TimeSync/TimeSync.html)

### Tools
- [can-utils](https://github.com/linux-can/can-utils)
- [linuxptp](http://linuxptp.sourceforge.net/)
- [NetworkManager](https://networkmanager.dev/)

---

**Document Version**: 1.0
**Last Updated**: 2025-11
**Maintained By**: Hardware Integration Agent
