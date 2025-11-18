# Hardware Integration Guides

## Overview

Comprehensive hardware-specific integration documentation for NVIDIA Jetson platforms in Yocto & meta-tegra projects.

## Available Guides

### 1. [Jetson Platform Reference](JETSON-PLATFORMS.md)
Complete overview of all supported Jetson platforms including:
- Xavier AGX, NX, Nano specifications
- Orin AGX, NX, Nano specifications
- Platform comparison matrix and selection guide
- Hardware capabilities breakdown
- Carrier board design considerations
- Yocto machine configuration

**Size**: 12KB | **Platforms**: All Jetson devices

### 2. [GPIO Reference](GPIO-REFERENCE.md)
Detailed GPIO configuration and usage guide:
- Complete GPIO mapping for each platform
- 40-pin header pinouts and diagrams
- Voltage levels and electrical specifications
- Pinmux configuration and device tree examples
- Safety considerations and level shifting
- Programming examples (Python, C, sysfs, gpiod)

**Size**: 19KB | **Critical**: 3.3V only, NOT 5V tolerant

### 3. [Camera Integration](CAMERA-INTEGRATION.md)
CSI camera and USB camera integration guide:
- CSI-2 camera support and hardware specs
- V4L2 configuration and API usage
- GStreamer hardware-accelerated pipelines
- ISP tuning and Argus camera API
- Multi-camera configurations
- Camera testing procedures

**Size**: 25KB | **Max Cameras**: 16 (AGX), 6 (NX), 4 (Nano)

### 4. [Network Configuration](NETWORKING.md)
Network interface integration:
- Ethernet (1GbE/10GbE) configuration
- WiFi/Bluetooth setup and drivers
- CAN bus integration (SocketCAN)
- TSN (Time-Sensitive Networking) configuration
- Network boot (PXE) setup

**Size**: 20KB | **Features**: 10GbE on AGX platforms, CAN-FD support

### 5. [Storage Configuration](STORAGE.md)
Storage device integration and optimization:
- NVMe SSD configuration and boot
- SD card optimization and longevity
- USB storage (USB 3.0/3.1)
- Network storage (NFS client/server)
- Software RAID configurations (mdadm, Btrfs, ZFS)

**Size**: 18KB | **Max Speed**: PCIe Gen4 x4 (8 GB/s)

### 6. [Power Management](POWER-MANAGEMENT.md)
Power optimization and thermal control:
- Power modes and nvpmodel configuration
- CPU/GPU frequency governors
- Thermal management and fan control
- Battery integration
- Wake sources (RTC, GPIO, WoL)
- Power monitoring tools

**Size**: 20KB | **Power Range**: 5W-60W depending on platform

### 7. [Peripheral Interfaces](PERIPHERAL-INTERFACES.md)
Low-level peripheral integration:
- I2C device configuration and programming
- SPI device configuration and programming
- UART serial communication
- PWM (Pulse Width Modulation) control
- PCIe device integration

**Size**: 22KB | **Interfaces**: I2C, SPI, UART, PWM, PCIe

## Quick Reference

### Platform Capabilities at a Glance

| Feature | AGX Orin | Orin NX | Orin Nano | AGX Xavier | Xavier NX | Nano |
|---------|----------|---------|-----------|------------|-----------|------|
| AI TOPS | 275 | 100 | 40 | 100+ | 21 | 0.5 |
| Power | 15-60W | 10-25W | 5-15W | 10-30W | 10-20W | 5-10W |
| CSI Cameras | 16 | 6 | 4 | 16 | 6 | 2 |
| Ethernet | 10GbE | 1GbE | 1GbE | 10GbE | 1GbE | 1GbE |
| PCIe | Gen4 x8+x4 | Gen4 x4 | Gen3 x4 | Gen4 x8 | Gen4 x4 | Gen2 x4 |
| CAN | 2x | 2x | - | 2x | 1x | - |
| GPIO | 40+ | 40 | 40 | 40+ | 40 | 40 |

### Common Use Cases by Platform

**AGX Orin (60W)**
- Autonomous vehicles
- Robotics with multiple sensors
- AI inference servers
- Industrial automation

**Orin NX (25W)**
- Mobile robotics
- Drones
- Multi-camera systems
- Edge AI gateways

**Orin Nano (15W)**
- Entry-level AI/ML
- Smart cameras
- IoT edge devices
- Educational projects

## Hardware Safety Guidelines

### Critical Safety Information

1. **Voltage Levels**
   - GPIO: 3.3V ONLY (NOT 5V tolerant!)
   - Use level shifters for 5V devices
   - Absolute maximum: 3.6V

2. **Current Limits**
   - Per GPIO: 2-10mA maximum
   - Use external drivers for loads >10mA
   - Never directly drive relays, motors, or high-power LEDs

3. **ESD Protection**
   - Use ESD-safe handling
   - Consider TVS diodes for external connections
   - Proper grounding essential

4. **Thermal Management**
   - Monitor temperatures continuously
   - Provide adequate cooling (heatsink/fan)
   - Throttling occurs at 85-95°C

## Device Tree Resources

All guides include device tree examples for:
- Pinmux configuration
- Peripheral enablement
- Custom hardware integration
- Platform-specific settings

Device tree files are located in:
```
meta-tegra/recipes-kernel/linux/linux-tegra-*/arch/arm64/boot/dts/nvidia/
```

## Testing Tools

Essential tools for hardware integration:
```bash
# Install comprehensive test suite
apt-get install \
    i2c-tools \
    spi-tools \
    v4l-utils \
    can-utils \
    nvme-cli \
    ethtool \
    pciutils \
    usbutils \
    gpiod \
    python3-smbus2 \
    python3-spidev \
    python3-pyserial
```

## Yocto Integration

Each guide includes:
- Yocto recipe examples
- Image configuration snippets
- Kernel configuration requirements
- Device tree integration

Example minimal image with hardware support:
```bitbake
require recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL_append = " \
    i2c-tools \
    spi-tools \
    v4l-utils \
    can-utils \
    nvme-cli \
    gstreamer1.0-plugins-tegra \
    nvidia-argus \
"
```

## Support and Documentation

### Official NVIDIA Resources
- [Jetson Developer Zone](https://developer.nvidia.com/embedded/jetson)
- [Jetson Linux Developer Guide](https://docs.nvidia.com/jetson/archives/r35.3.1/DeveloperGuide/)
- [Jetson Download Center](https://developer.nvidia.com/jetson-download-center)

### meta-tegra Resources
- [GitHub Repository](https://github.com/OE4T/meta-tegra)
- [Documentation Wiki](https://github.com/OE4T/meta-tegra/wiki)
- [Release Notes](https://github.com/OE4T/meta-tegra/releases)

### Community
- [NVIDIA Developer Forums](https://forums.developer.nvidia.com/c/agx-autonomous-machines/jetson-embedded-systems/)
- [meta-tegra Discussions](https://github.com/OE4T/meta-tegra/discussions)

## Contributing

These guides are maintained by the Hardware Integration Agent as part of the Yocto & Meta-Tegra learning system.

For updates or corrections:
1. Review the specific guide
2. Test on actual hardware
3. Document findings
4. Update guide with tested configurations

## Document History

**Version**: 1.0
**Created**: 2025-11-18
**Last Updated**: 2025-11-18
**Maintained By**: Hardware Integration Agent

---

## Quick Start

1. **Identify your platform**: See [JETSON-PLATFORMS.md](JETSON-PLATFORMS.md)
2. **Configure GPIO**: See [GPIO-REFERENCE.md](GPIO-REFERENCE.md)
3. **Set power mode**: See [POWER-MANAGEMENT.md](POWER-MANAGEMENT.md)
4. **Add peripherals**: See specific interface guides
5. **Test and validate**: Use testing procedures in each guide

**Total Documentation Size**: ~135KB of comprehensive hardware integration knowledge
