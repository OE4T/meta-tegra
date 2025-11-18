# NVIDIA Tegra Platform Guide

## Tegra SoC Architecture Overview

### Platform Generations

```
Tegra X1 (TX1)   → Parker (TX2)   → Xavier (AGX Xavier, NX)   → Orin (AGX Orin, Orin NX)
T210             → T186           → T194                       → T234
Maxwell GPU      → Pascal GPU     → Volta GPU                  → Ampere GPU
ARMv8 (A57)      → ARMv8 (Denver2) → ARMv8.2 (Carmel)          → ARMv8.2 (Carmel)
2015             → 2017           → 2018                       → 2022
```

### SoC Components

All Tegra platforms include:
- **CPU Cluster**: ARM cores with cache hierarchy
- **GPU**: NVIDIA CUDA-capable GPU
- **VIC**: Video Image Compositor
- **NVENC/NVDEC**: Hardware video encoding/decoding
- **ISP**: Image Signal Processor
- **NVJPG**: JPEG encode/decode accelerator
- **Display Engine**: Multi-head display controller
- **Audio Processing Engine (APE)**
- **Power Management IC (PMIC) interface**
- **System-on-Chip Memory Controllers**

## Xavier vs Orin Comparison

### Jetson Xavier Family (T194)

#### AGX Xavier
- **CPU**: 8-core NVIDIA Carmel ARMv8.2 (64-bit)
  - 4MB L2 + 4MB L3 cache
  - Up to 2.26 GHz
- **GPU**: 512-core Volta with Tensor Cores
  - 32 TOPs (INT8)
  - 11 TFLOPS (FP16)
- **Memory**: 32GB LPDDR4x @ 137 GB/s
- **AI Performance**: 32 TOPS
- **Power**: 10W - 30W
- **JetPack**: 4.x, 5.x, 6.x

#### Xavier NX
- **CPU**: 6-core NVIDIA Carmel ARMv8.2
  - Up to 1.9 GHz
- **GPU**: 384-core Volta
  - 21 TOPS (INT8)
  - 6 TFLOPS (FP16)
- **Memory**: 8GB or 16GB LPDDR4x @ 51 GB/s
- **AI Performance**: 21 TOPS
- **Power**: 10W - 20W
- **Form Factor**: SO-DIMM module

### Jetson Orin Family (T234)

#### AGX Orin (64GB)
- **CPU**: 12-core ARM Cortex-A78AE
  - 6MB L2 + 4MB L3 cache
  - Up to 2.2 GHz
- **GPU**: 2048-core Ampere with Tensor Cores
  - 275 TOPS (INT8)
  - 21.5 TFLOPS (FP32)
- **Memory**: 64GB LPDDR5 @ 204 GB/s
- **AI Performance**: 275 TOPS
- **Power**: 15W - 60W
- **Features**: PCIe Gen4, NVMe, 10GbE

#### Orin NX (16GB)
- **CPU**: 8-core ARM Cortex-A78AE
  - Up to 2.0 GHz
- **GPU**: 1024-core Ampere
  - 100 TOPS (INT8)
- **Memory**: 16GB LPDDR5 @ 102 GB/s
- **AI Performance**: 100 TOPS
- **Power**: 10W - 25W
- **Form Factor**: SO-DIMM (Xavier NX compatible)

#### Orin Nano
- **CPU**: 6-core ARM Cortex-A78AE
- **GPU**: 1024-core Ampere
  - 40 TOPS (INT8)
- **Memory**: 4GB or 8GB LPDDR5
- **AI Performance**: 40 TOPS
- **Power**: 5W - 15W
- **Form Factor**: SO-DIMM

### Key Architectural Differences

| Feature | Xavier | Orin |
|---------|--------|------|
| CPU Architecture | Carmel (custom) | Cortex-A78AE (ARM) |
| GPU Architecture | Volta | Ampere |
| Memory Type | LPDDR4x | LPDDR5 |
| PCIe Generation | Gen 4 | Gen 4 (more lanes) |
| Max Display Outputs | 3 | 4 |
| NVENC Engines | 1 | 2 |
| Deep Learning Accelerator | PVA | DLA 2.0 (more capable) |
| Security | SEE, TrustZone | SEE, TrustZone, HSM |

## JetPack Version Matrix

### JetPack Overview
JetPack is NVIDIA's SDK for Jetson platforms, includes:
- Linux4Tegra (L4T) - BSP
- CUDA Toolkit
- cuDNN
- TensorRT
- VPI (Vision Programming Interface)
- Multimedia API
- Development tools

### Version Compatibility

| JetPack | L4T Version | Ubuntu | Kernel | Xavier | Orin |
|---------|-------------|--------|--------|--------|------|
| 4.6.x | 32.7.x | 18.04 | 4.9 | ✓ | ✗ |
| 5.0.x | 35.1.x | 20.04 | 5.10 | ✓ | ✓ |
| 5.1.x | 35.2.x/35.3.x | 20.04 | 5.10 | ✓ | ✓ |
| 6.0 | 36.3 | 22.04 | 5.15 | ✓ | ✓ |

### Component Versions (JetPack 6.0)

```
L4T:           36.3
CUDA:          12.2
cuDNN:         8.9
TensorRT:      8.6
VPI:           3.1
OpenCV:        4.8
Multimedia:    36.x
GStreamer:     1.20
```

### Meta-Tegra Branch Mapping

```bitbake
# Yocto Release → Meta-Tegra Branch → JetPack/L4T
Kirkstone (4.0) → kirkstone-l4t-r35.x → JP 5.x / L4T 35.x
Langdale (4.1)  → master or langdale → JP 5.x / L4T 35.x
Mickledore (4.2)→ master → JP 6.x / L4T 36.x
```

## Driver Architecture

### Boot Flow

```
BootROM → MB1 → MB2 → CPU-BL (TF-A) → UEFI → Extlinux/GRUB → Kernel
                ↓
              Early FW (BPMP, SPE, SCE, RCE)
```

**Key Components:**
- **MB1/MB2**: Early bootloaders (NV proprietary)
- **TF-A**: ARM Trusted Firmware
- **UEFI**: NVIDIA-customized EDK2
- **BPMP**: Boot and Power Management Processor
- **SPE**: Safety Processing Engine (Orin)
- **SCE**: System Control Engine
- **RCE**: Real-time Camera Engine

### Kernel Driver Stack

```
┌─────────────────────────────────────┐
│     User Space Applications         │
├─────────────────────────────────────┤
│  CUDA / cuDNN / TensorRT / V4L2     │
├─────────────────────────────────────┤
│     libargus / nvbuf_utils          │
│     Multimedia API / nvv4l2         │
├─────────────────────────────────────┤
│          Character Devices          │
│  /dev/nvidia* /dev/nvhost* /dev/v4l │
├─────────────────────────────────────┤
│        Kernel Driver Modules        │
│  nvidia.ko nvgpu.ko nvhost_vi.ko    │
│  tegra-camera.ko ...                │
├─────────────────────────────────────┤
│      Device Tree Configuration      │
│  Board DTS + SoC DTSI               │
└─────────────────────────────────────┘
```

### Key Kernel Modules

```bash
# Core GPU driver
nvidia.ko

# GPU kernel driver (open-source path)
nvgpu.ko

# Display
tegra-drm.ko

# Camera
tegra-capture-vi.ko (VI - Video Input)
nvcsi.ko (CSI - Camera Serial Interface)
nvhost-vi.ko

# Video encode/decode
nvhost-nvenc.ko
nvhost-nvdec.ko
nvhost-vic.ko (Video Image Compositor)

# Audio
tegra-snd-*.ko

# Power management
tegra-bpmp.ko
tegra-bpmp-thermal.ko

# Networking
nvethernet.ko (on platforms with integrated ethernet)
```

### Device Tree Organization

```
arch/arm64/boot/dts/nvidia/
├── tegra194.dtsi              # Xavier SoC
├── tegra194-p2888-0001.dtsi   # AGX Xavier module
├── tegra194-p3668-0001.dtsi   # Xavier NX module
├── tegra234.dtsi              # Orin SoC
├── tegra234-p3701-0000.dtsi   # AGX Orin module
└── tegra234-p3767-0000.dtsi   # Orin NX/Nano module
```

**Common DT Bindings:**
- `/chosen` - Boot parameters
- `/cpus` - CPU configuration
- `/memory` - Memory regions
- `/reserved-memory` - Carveouts for firmware/hardware
- `/thermal-zones` - Temperature monitoring
- `/gpio` - GPIO controllers
- `/i2c@*` - I2C buses
- `/spi@*` - SPI buses
- `/pcie@*` - PCIe controllers
- `/display@*` - Display controllers
- `/host1x` - NVIDIA multimedia engine

## Hardware Capabilities

### Multimedia Engine Capabilities

#### Video Encoding (NVENC)

**Xavier:**
- 1x 4K60 or 2x 4K30 H.265
- 1x 4K60 or 2x 4K30 H.264
- Max resolution: 8192x8192

**Orin:**
- 2x engines
- Up to 8x 1080p60 H.265
- AV1 encode support (newer SKUs)

#### Video Decoding (NVDEC)

**Xavier:**
- 2x 4K60 H.265
- 2x 4K60 H.264
- VP9, MPEG-2, VC1

**Orin:**
- Higher throughput
- AV1 decode support
- Multiple concurrent streams

### Camera Capabilities

```
CSI Lanes:
- Xavier: Up to 6x4 or 12x2 CSI-2 lanes
- Orin: Up to 8x4 CSI-2 lanes (more on AGX)

ISP:
- Multi-camera concurrent processing
- HDR pipeline
- Bayer processing
- Noise reduction
```

### Display Capabilities

**Xavier:**
- 3x display heads
- Up to 2x 4K60 or 1x 8K30
- HDMI 2.0, DP 1.4

**Orin:**
- 4x display heads
- Up to 4x 4K60
- HDMI 2.1, DP 1.4a

### Storage & Networking

**Storage:**
- eMMC (module built-in)
- SDMMC (on devkits)
- NVMe (PCIe)
- SATA (on some platforms)

**Networking:**
- Gigabit Ethernet (Xavier)
- 10GbE (AGX Orin)
- PCIe for add-in network cards
- WiFi/BT via M.2 modules

### Power Management

#### Power Modes

```bash
# View available modes
nvpmodel -q

# Xavier NX Modes (example)
# Mode 0: 20W (6-core, full GPU)
# Mode 1: 15W (6-core, GPU limited)
# Mode 2: 10W (2-core, GPU limited)

# Set mode
nvpmodel -m 0

# Jetson clocks (max performance)
jetson_clocks
```

#### Voltage Rails

```
# Major power rails:
VDD_CPU     - CPU core voltage
VDD_GPU     - GPU core voltage
VDD_SOC     - SoC logic voltage
VDD_DDR     - Memory voltage
```

### Thermal Management

```bash
# Temperature zones
/sys/devices/virtual/thermal/thermal_zone*/temp

# Common zones:
# CPU-therm
# GPU-therm
# SOC-therm
# PMIC-die-therm

# Cooling devices
/sys/class/thermal/cooling_device*/
```

## Platform-Specific Configuration

### Boot Configuration

```bash
# Extlinux configuration
/boot/extlinux/extlinux.conf

# Example entry:
LABEL primary
    MENU LABEL primary kernel
    LINUX /boot/Image
    INITRD /boot/initrd
    APPEND ${cbootargs} root=/dev/mmcblk0p1 rw rootwait rootfstype=ext4
```

### Firmware Locations

```
/lib/firmware/nvidia/
├── tegra194/  (Xavier)
│   ├── bpmp.bin
│   ├── camera-rtcpu-rce.img
│   └── xusb.bin
└── tegra234/  (Orin)
    ├── bpmp.bin
    ├── xusb.bin
    └── nvdec.bin
```

### BSP Partition Layout

```
mmcblk0boot0   - Boot Configuration
mmcblk0boot1   - Boot Configuration backup
mmcblk0p1      - APP (rootfs)
mmcblk0p2      - Reserved
...
mmcblk0p10     - kernel (boot image)
mmcblk0p11     - kernel-dtb (device tree)
...
```

## Meta-Tegra Integration

### Machine Configuration

```bitbake
# In meta-tegra/conf/machine/jetson-xavier-nx-devkit.conf
MACHINE = "jetson-xavier-nx-devkit"
SOC_FAMILY = "tegra194"
KERNEL_DEVICETREE = "nvidia/tegra194-p3668-0001-p3509-0000.dtb"
MACHINE_FEATURES = "ext2 ext3 ext4 vfat usbhost usbgadget pci wifi bluetooth"
```

### Key Meta-Tegra Variables

```bitbake
# NVIDIA L4T version
L4T_VERSION = "35.3.1"

# CUDA architecture
CUDA_ARCHITECTURES = "72"  # Xavier (Volta)
CUDA_ARCHITECTURES = "87"  # Orin (Ampere)

# Bootloader
PREFERRED_PROVIDER_virtual/bootloader = "cboot-t19x"  # Xavier
PREFERRED_PROVIDER_virtual/bootloader = "uefi-firmware" # Orin

# Kernel
PREFERRED_PROVIDER_virtual/kernel = "linux-tegra"
PREFERRED_VERSION_linux-tegra = "5.10%"
```

### CUDA Integration

```bitbake
# Enable CUDA
CUDA_ENABLE = "1"

# CUDA packages
inherit cuda

# Link against CUDA
DEPENDS += "cuda-toolkit"
RDEPENDS:${PN} += "cuda-runtime"
```

---

*Last Updated: 2025-11-18*
*Maintained by: Documentation Researcher Agent*
