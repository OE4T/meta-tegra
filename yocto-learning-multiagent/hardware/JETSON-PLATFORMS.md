# Jetson Platform Hardware Reference

## Overview

This guide provides comprehensive hardware specifications and integration details for NVIDIA Jetson platforms supported by meta-tegra.

## Supported Platforms

### Jetson Xavier Series

#### Jetson AGX Xavier
- **Module**: P2888 (compute module)
- **Developer Kit**: P2822 (carrier board)
- **SoC**: NVIDIA Xavier (T194)
- **CPU**: 8-core ARM Carmel @ 2.26 GHz
- **GPU**: 512-core Volta @ 1.377 GHz
- **Memory**: 16GB/32GB LPDDR4x
- **Storage**: 32GB eMMC 5.1
- **MACHINE**: `jetson-agx-xavier-devkit`

**Key Features:**
- Dual NVDLA engines
- 7-way VLIW Vision Processor
- 8K video encode/decode
- PCIe Gen 4
- 100+ TOPS AI performance

#### Jetson Xavier NX
- **Module**: P3668
- **Developer Kit**: P3509 (carrier board)
- **SoC**: NVIDIA Xavier NX (T194)
- **CPU**: 6-core ARM Carmel @ 1.9 GHz
- **GPU**: 384-core Volta @ 1.1 GHz
- **Memory**: 8GB/16GB LPDDR4x
- **Storage**: 16GB eMMC 5.1
- **MACHINE**: `jetson-xavier-nx-devkit`, `jetson-xavier-nx-devkit-emmc`

**Key Features:**
- Compact form factor (70mm x 45mm)
- 260-pin SO-DIMM connector
- 21 TOPS AI performance
- 10W-15W power modes

### Jetson Orin Series

#### Jetson AGX Orin (64GB)
- **Module**: P3701
- **Developer Kit**: P3737
- **SoC**: NVIDIA Orin (T234)
- **CPU**: 12-core ARM Cortex-A78AE @ 2.2 GHz
- **GPU**: 2048-core Ampere @ 1.3 GHz
- **Memory**: 64GB LPDDR5
- **Storage**: 64GB eMMC 5.1
- **MACHINE**: `jetson-agx-orin-devkit`

**Key Features:**
- 275 TOPS AI performance
- Next-gen NVDLA 2.0
- PCIe Gen 4
- 8K60 video processing
- Up to 60W TDP

#### Jetson AGX Orin (32GB)
- **Module**: P3701 (lower SKU)
- **Developer Kit**: P3737
- **SoC**: NVIDIA Orin (T234)
- **CPU**: 8-core ARM Cortex-A78AE @ 2.0 GHz
- **GPU**: 1024-core Ampere @ 1.1 GHz
- **Memory**: 32GB LPDDR5
- **MACHINE**: `jetson-agx-orin-devkit`

**Key Features:**
- 200 TOPS AI performance
- 15W-50W power modes

#### Jetson Orin NX (16GB)
- **Module**: P3767
- **Developer Kit**: P3509
- **SoC**: NVIDIA Orin NX (T234)
- **CPU**: 8-core ARM Cortex-A78AE @ 2.0 GHz
- **GPU**: 1024-core Ampere @ 1.1 GHz
- **Memory**: 16GB LPDDR5
- **MACHINE**: `jetson-orin-nx-devkit`, `jetson-orin-nx-devkit-nvme`

**Key Features:**
- 100 TOPS AI performance
- 260-pin SO-DIMM form factor
- 10W-25W power modes

#### Jetson Orin NX (8GB)
- **Module**: P3767 (lower SKU)
- **Developer Kit**: P3509
- **SoC**: NVIDIA Orin NX (T234)
- **CPU**: 6-core ARM Cortex-A78AE @ 2.0 GHz
- **GPU**: 512-core Ampere @ 1.1 GHz
- **Memory**: 8GB LPDDR5
- **MACHINE**: `jetson-orin-nx-devkit`, `jetson-orin-nx-devkit-nvme`

**Key Features:**
- 70 TOPS AI performance
- 10W-15W power modes

#### Jetson Orin Nano (8GB)
- **Module**: P3768
- **Developer Kit**: P3509
- **SoC**: NVIDIA Orin Nano (T234)
- **CPU**: 6-core ARM Cortex-A78AE @ 1.5 GHz
- **GPU**: 1024-core Ampere @ 625 MHz
- **Memory**: 8GB LPDDR5
- **MACHINE**: `jetson-orin-nano-devkit`, `jetson-orin-nano-devkit-nvme`

**Key Features:**
- 40 TOPS AI performance
- 7W-15W power modes
- Cost-effective entry point

#### Jetson Orin Nano (4GB)
- **Module**: P3768 (lower SKU)
- **Developer Kit**: P3509
- **SoC**: NVIDIA Orin Nano (T234)
- **CPU**: 4-core ARM Cortex-A78AE @ 1.5 GHz
- **GPU**: 512-core Ampere @ 625 MHz
- **Memory**: 4GB LPDDR5
- **MACHINE**: `jetson-orin-nano-devkit`, `jetson-orin-nano-devkit-nvme`

**Key Features:**
- 20 TOPS AI performance
- 5W-10W power modes

### Legacy Platforms

#### Jetson Nano
- **Module**: P3448
- **Developer Kit**: A02/B01
- **SoC**: NVIDIA T210
- **CPU**: 4-core ARM Cortex-A57 @ 1.43 GHz
- **GPU**: 128-core Maxwell
- **Memory**: 4GB LPDDR4
- **MACHINE**: `jetson-nano-devkit`, `jetson-nano-2gb-devkit`

## Platform Comparison Matrix

### Performance Comparison

| Platform | CPU | GPU | AI (TOPS) | Power | Memory | Price Tier |
|----------|-----|-----|-----------|-------|--------|------------|
| AGX Orin 64GB | 12-core A78AE | 2048 Ampere | 275 | 15-60W | 64GB | Premium |
| AGX Orin 32GB | 8-core A78AE | 1024 Ampere | 200 | 15-50W | 32GB | High |
| AGX Xavier | 8-core Carmel | 512 Volta | 100+ | 10-30W | 32GB | High |
| Orin NX 16GB | 8-core A78AE | 1024 Ampere | 100 | 10-25W | 16GB | Mid-High |
| Orin NX 8GB | 6-core A78AE | 512 Ampere | 70 | 10-15W | 8GB | Mid |
| Xavier NX 16GB | 6-core Carmel | 384 Volta | 21 | 10-15W | 16GB | Mid |
| Orin Nano 8GB | 6-core A78AE | 1024 Ampere | 40 | 7-15W | 8GB | Entry |
| Orin Nano 4GB | 4-core A78AE | 512 Ampere | 20 | 5-10W | 4GB | Entry |
| Nano 4GB | 4-core A57 | 128 Maxwell | ~0.5 | 5-10W | 4GB | Budget |

### Hardware Capabilities Matrix

| Feature | AGX Orin | Orin NX | Orin Nano | AGX Xavier | Xavier NX | Nano |
|---------|----------|---------|-----------|------------|-----------|------|
| **PCIe** | Gen4 x8+x4 | Gen4 x4 | Gen3 x4 | Gen4 x8 | Gen4 x4 | Gen2 x4 |
| **CSI Cameras** | 16 | 6 | 4 | 16 | 6 | 2 |
| **Display Outputs** | 3 | 2 | 2 | 3 | 2 | 1 |
| **Ethernet** | 10GbE | 1GbE | 1GbE | 10GbE | 1GbE | 1GbE |
| **USB 3.1** | 5 | 4 | 4 | 4 | 4 | 4 |
| **CAN** | 2 | 2 | - | 2 | 1 | - |
| **NVDLA** | 2x v2.0 | 2x v2.0 | 2x v2.0 | 2x v1.0 | 1x v1.0 | - |
| **Video Enc** | 2x 4K60 | 2x 4K60 | 1x 4K60 | 2x 4K60 | 2x 4K30 | 1x 4K30 |
| **Video Dec** | 8K60 | 8K30 | 8K30 | 8K30 | 8K30 | 4K60 |

### I/O Comparison

| Platform | GPIO | I2C | SPI | UART | PWM | CAN |
|----------|------|-----|-----|------|-----|-----|
| AGX Orin | 40+ | 5 | 3 | 5 | 8 | 2 |
| Orin NX/Nano | 40 | 3 | 2 | 3 | 4 | 0-2 |
| AGX Xavier | 40+ | 5 | 3 | 5 | 8 | 2 |
| Xavier NX | 40 | 3 | 2 | 3 | 4 | 1 |
| Nano | 40 | 2 | 2 | 2 | 2 | 0 |

## Carrier Board Considerations

### Official Developer Kits

#### P2822 (AGX Xavier Carrier)
- Full-size ATX-like form factor
- Comprehensive I/O expansion
- Active cooling required
- Multiple M.2 slots (NVMe, WiFi)
- Industrial temperature range support
- **Use Case**: Development, evaluation, prototyping

#### P3509 (NX/Orin Nano Carrier)
- Compact form factor
- 260-pin SO-DIMM connector
- Single M.2 NVMe slot
- 40-pin GPIO header
- DisplayPort and HDMI outputs
- **Use Case**: Development, compact deployments

#### P3737 (AGX Orin Carrier)
- Enhanced I/O capabilities
- PCIe Gen4 x8 slot
- Multiple CSI/DSI connectors
- 10GbE networking
- M.2 Key-E and Key-M slots
- **Use Case**: High-performance development

### Custom Carrier Board Design

#### Design Resources
```bash
# NVIDIA provides design files for reference carriers
# Available at: https://developer.nvidia.com/jetson-download-center

# Key documents:
- Design Guide
- Schematic Checklist
- PCB Layout Guidelines
- Thermal Design Guide
- Power Tree Design
```

#### Critical Considerations

**Power Supply Design:**
```
AGX Orin: 9-20V input, up to 60W
Orin NX:  9-20V input, up to 25W
Orin Nano: 5-20V input, up to 15W
Xavier NX: 9-20V input, up to 20W
```

**Connector Requirements:**
- Module connector pinout must match exactly
- Signal integrity critical for high-speed interfaces
- Proper impedance matching for CSI/DSI
- Ground plane considerations

**Thermal Management:**
- Minimum heatsink requirements vary by SKU
- Active cooling may be required at higher power modes
- Thermal interface material (TIM) selection critical
- Case temperature monitoring recommended

**Manufacturing:**
- Minimum 6-layer PCB for most designs
- 8-10 layers recommended for AGX modules
- Controlled impedance routing required
- HDI technology may be needed for dense layouts

### Third-Party Carriers

#### Connect Tech Carriers
- Rogue (AGX Xavier/Orin)
- Photon (NX modules)
- Quasar (Nano modules)
- Industrial temperature range
- Extended I/O options

#### Auvidea Carriers
- J20/J120 (compact designs)
- Multiple camera inputs
- Custom I/O configurations

#### Antmicro Carriers
- Open-source designs
- Community support
- Customizable options

## Module Selection Guide

### By Use Case

**AI Inference (Real-time)**
- **Best**: AGX Orin 64GB (275 TOPS)
- **Good**: Orin NX 16GB (100 TOPS)
- **Budget**: Orin Nano 8GB (40 TOPS)

**Computer Vision**
- **Multi-camera**: AGX Orin (16 cameras)
- **4-6 cameras**: Orin NX (6 cameras)
- **Basic**: Orin Nano (4 cameras)

**Robotics**
- **Autonomous Vehicles**: AGX Orin (full sensor suite)
- **Mobile Robots**: Orin NX (balance of power/performance)
- **Educational**: Orin Nano (cost-effective)

**Edge Server**
- **High-density**: AGX Orin (PCIe Gen4, NVMe RAID)
- **Compact**: Orin NX (good performance/watt)

**Industrial Automation**
- **Real-time Control**: AGX Xavier/Orin (CAN, TSN)
- **Machine Vision**: Orin NX (sufficient I/O)
- **HMI**: Orin Nano (good display performance)

**Medical Devices**
- **Imaging**: AGX Orin (high compute, multi-camera)
- **Portable**: Orin NX (low power, sufficient performance)

### By Power Budget

**5-10W**
- Orin Nano 4GB (7W mode)
- Jetson Nano (5W mode)

**10-15W**
- Orin Nano 8GB (15W mode)
- Orin NX 8GB (10W mode)
- Xavier NX (10W mode)

**15-25W**
- Orin NX 16GB (25W mode)
- Xavier NX (15W mode)

**30-60W**
- AGX Orin (MAXN mode)
- AGX Xavier (MAXN mode)

## Yocto Configuration

### Machine Configuration Files

```bash
# Location in meta-tegra
meta-tegra/conf/machine/

# Key machine configs:
jetson-agx-orin-devkit.conf
jetson-orin-nx-devkit.conf
jetson-orin-nano-devkit.conf
jetson-agx-xavier-devkit.conf
jetson-xavier-nx-devkit.conf
jetson-nano-devkit.conf
```

### Example local.conf Settings

```bitbake
# For Jetson AGX Orin
MACHINE = "jetson-agx-orin-devkit"
JETSON_MACHINE = "jetson-agx-orin-devkit"

# For Jetson Orin NX with NVMe
MACHINE = "jetson-orin-nx-devkit-nvme"
JETSON_MACHINE = "jetson-orin-nx-devkit-nvme"

# For Jetson Orin Nano
MACHINE = "jetson-orin-nano-devkit"
JETSON_MACHINE = "jetson-orin-nano-devkit"
```

### Platform-Specific Variables

```bitbake
# AGX Orin specific
TEGRA_BOARDID = "3701"
TEGRA_FAB = "300"
TEGRA_CHIPREV = "0"
TEGRA_CHIP_ID = "0x23"

# Orin NX specific
TEGRA_BOARDID = "3767"
TEGRA_FAB = "000"
TEGRA_CHIPREV = "0"

# Xavier specific
TEGRA_BOARDID = "2888"
TEGRA_FAB = "400"
TEGRA_CHIPREV = "2"
TEGRA_CHIP_ID = "0x19"
```

## Hardware Verification

### Boot-time Checks

```bash
# Verify platform
cat /sys/module/tegra_fuse/parameters/tegra_chip_id

# Check board info
cat /sys/firmware/devicetree/base/model

# Verify memory
free -h

# Check GPU
cat /sys/devices/gpu.0/devfreq/57000000.gpu/available_frequencies

# CPU info
lscpu
cat /sys/devices/system/cpu/cpu*/cpufreq/scaling_available_frequencies
```

### Hardware Testing

```bash
# Install jetson-stats
apt-get install python3-pip
pip3 install jetson-stats

# Monitor system
jtop

# Power monitoring
tegrastats

# GPU stress test
/usr/bin/cuda-samples/1_Utilities/deviceQuery/deviceQuery

# Memory bandwidth test
/usr/bin/cuda-samples/1_Utilities/bandwidthTest/bandwidthTest
```

## References

### Official Documentation
- [Jetson Download Center](https://developer.nvidia.com/jetson-download-center)
- [Jetson AGX Orin Developer Kit User Guide](https://developer.nvidia.com/embedded/jetson-agx-orin-developer-kit-user-guide)
- [Jetson Orin NX/Nano Developer Kit User Guide](https://developer.nvidia.com/embedded/learn/jetson-orin-nano-devkit-user-guide)
- [Jetson AGX Xavier Developer Kit User Guide](https://developer.nvidia.com/embedded/dlc/jetson-agx-xavier-developer-kit-user-guide)
- [Jetson Linux Developer Guide](https://docs.nvidia.com/jetson/archives/r35.3.1/DeveloperGuide/)

### Datasheets
- Jetson AGX Orin Module Data Sheet
- Jetson Orin NX Module Data Sheet
- Jetson Orin Nano Module Data Sheet
- Jetson AGX Xavier Module Data Sheet
- Jetson Xavier NX Module Data Sheet

### Design Guides
- Jetson AGX Orin Series Design Guide
- Jetson Orin NX Series Design Guide
- Jetson Xavier Series Design Guide
- Thermal Design Guide
- Power Estimation Tool

### meta-tegra Resources
- [meta-tegra GitHub](https://github.com/OE4T/meta-tegra)
- [meta-tegra Documentation](https://github.com/OE4T/meta-tegra/wiki)
- [Release Notes](https://github.com/OE4T/meta-tegra/releases)

---

**Document Version**: 1.0
**Last Updated**: 2025-11
**Maintained By**: Hardware Integration Agent
