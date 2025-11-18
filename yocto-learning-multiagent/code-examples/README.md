# Code Examples for Yocto & Meta-Tegra Learning

This directory contains comprehensive code examples for developing, building, and deploying applications on NVIDIA Jetson platforms using Yocto Project and the meta-tegra layer.

## Directory Structure

```
code-examples/
├── recipes/              # BitBake recipe examples
├── kernel-modules/       # Linux kernel driver examples
├── device-trees/         # Device tree configuration examples
├── applications/         # Userspace application examples
├── scripts/             # Build automation and utility scripts
└── README.md            # This file
```

## Quick Start

### 1. BitBake Recipes (`recipes/`)

Learn how to create and package applications, libraries, kernel modules, and custom images.

**Key examples:**
- `simple-app_1.0.bb` - Basic application recipe
- `gpio-lib_1.0.bb` - Library with Python bindings
- `kernel-module-template_1.0.bb` - Kernel module packaging
- `custom-image.bb` - Custom image recipe
- `machine-config-example.conf` - Machine configuration

**Usage:**
```bash
# Copy recipe to your layer
cp recipes/simple-app_1.0.bb meta-yourlayer/recipes-apps/simple-app/

# Build recipe
bitbake simple-app
```

### 2. Kernel Modules (`kernel-modules/`)

Production-ready Linux kernel drivers for common peripherals.

**Examples:**
- GPIO interrupt driver
- I2C sensor driver
- SPI device driver
- Platform driver template

**Usage:**
```bash
# Compile module
cd kernel-modules
make

# Load module
sudo insmod gpio-interrupt-driver.ko gpio_pin=12

# Test module
cat /dev/gpio_int
```

### 3. Device Trees (`device-trees/`)

Device tree configurations for hardware enablement.

**Examples:**
- GPIO configuration and overlays
- I2C sensor nodes
- SPI device configuration
- Custom pinmux settings

**Usage:**
```bash
# Compile device tree
dtc -@ -I dts -O dtb -o gpio-overlay.dtbo gpio-overlay.dts

# Apply overlay (runtime)
sudo mkdir -p /sys/kernel/config/device-tree/overlays/gpio
sudo cat gpio-overlay.dtbo > /sys/kernel/config/device-tree/overlays/gpio/dtbo
```

### 4. Userspace Applications (`applications/`)

Complete applications demonstrating hardware interfacing and AI/ML.

**Examples:**
- GPIO control via sysfs
- I2C device communication
- V4L2 camera capture
- TensorRT inference demo

**Usage:**
```bash
# Compile C/C++ applications
gcc -o gpio_control gpio_control.c
g++ -o camera_capture camera_capture.cpp

# Run Python application
python3 inference_demo.py --model resnet50.engine --input image.jpg
```

### 5. Build Scripts (`scripts/`)

Automation scripts for development workflow.

**Examples:**
- `setup-build-environment.sh` - Initialize Yocto build
- `flash-jetson.sh` - Flash device with built images
- `validate-recipes.sh` - Recipe validation and testing
- `performance-test.sh` - Performance benchmarking

**Usage:**
```bash
# Setup build environment
./scripts/setup-build-environment.sh -m jetson-xavier-nx-devkit

# Validate recipes
./scripts/validate-recipes.sh simple-app

# Flash device
sudo ./scripts/flash-jetson.sh -m jetson-xavier-nx-devkit
```

## Learning Path

### Beginner

1. **Start with recipes**: Understand BitBake recipe structure
   - Build `simple-app_1.0.bb`
   - Study variable usage and task definitions
   - Learn about package management

2. **Explore device trees**: Hardware configuration basics
   - Compile and apply GPIO overlay
   - Understand pin configuration
   - Test with LED/button examples

3. **Try userspace apps**: Basic hardware interfacing
   - Use `gpio_control.c` to control GPIO pins
   - Read I2C sensors with `i2c_reader.c`

### Intermediate

1. **Create custom recipes**: Package your applications
   - Adapt recipe templates for your code
   - Handle dependencies properly
   - Configure installation paths

2. **Develop kernel modules**: Driver development
   - Study platform driver template
   - Implement custom device driver
   - Integrate with device tree

3. **Build custom images**: System integration
   - Modify `custom-image.bb` for your needs
   - Add custom packages
   - Configure system services

### Advanced

1. **Multi-layer development**: Complex BSP customization
   - Create custom layer structure
   - Override existing recipes
   - Implement machine-specific features

2. **AI/ML integration**: Deep learning deployment
   - Optimize TensorRT models
   - Benchmark inference performance
   - Create deployment recipes

3. **Production deployment**: Release engineering
   - Implement security features
   - Optimize image size
   - Set up OTA updates

## Prerequisites

### Development Host

- Ubuntu 20.04 or 22.04 (64-bit)
- Minimum 50GB free disk space
- 4GB RAM (8GB+ recommended)
- Git, Python 3.6+, GCC toolchain

```bash
sudo apt-get update
sudo apt-get install gawk wget git diffstat unzip texinfo \
    gcc build-essential chrpath socat cpio python3 python3-pip \
    python3-pexpect xz-utils debianutils iputils-ping python3-git \
    python3-jinja2 libegl1-mesa libsdl1.2-dev pylint xterm \
    python3-subunit mesa-common-dev zstd liblz4-tool
```

### Target Device

- NVIDIA Jetson (TX2, Xavier, Orin, Nano)
- L4T version compatible with meta-tegra branch
- Serial console access (recommended)
- Network connection

## Common Workflows

### Building a Complete System

```bash
# 1. Setup environment
./scripts/setup-build-environment.sh -m jetson-xavier-nx-devkit -b kirkstone

# 2. Source build environment
source sources/poky/oe-init-build-env build

# 3. Add custom recipes
# Copy your recipes to appropriate layer

# 4. Build image
bitbake custom-image

# 5. Flash to device
sudo ../scripts/flash-jetson.sh -m jetson-xavier-nx-devkit
```

### Developing a New Driver

```bash
# 1. Study template
cd kernel-modules
cat platform-driver-template.c

# 2. Create device tree node
# Edit device-trees/custom-pinmux.dtsi

# 3. Write driver
# Implement your driver based on template

# 4. Create recipe
# Adapt kernel-module-template_1.0.bb

# 5. Build and test
bitbake kernel-module-yourdriver
```

### Deploying AI Model

```bash
# 1. Convert model to ONNX
python3 convert_to_onnx.py --model model.pth --output model.onnx

# 2. Optimize with TensorRT
./applications/inference_demo.py --model model.onnx --build --precision fp16

# 3. Test inference
./applications/inference_demo.py --model model.engine --input test.jpg

# 4. Package in recipe
# Create recipe based on simple-app_1.0.bb
```

## Testing and Validation

### Recipe Validation

```bash
# Validate single recipe
./scripts/validate-recipes.sh simple-app

# Validate entire layer
./scripts/validate-recipes.sh -l meta-custom/recipes-apps

# Check dependencies
bitbake simple-app -g
```

### Runtime Testing

```bash
# On target device

# Test GPIO
./gpio_control 12 export
./gpio_control 12 direction out
./gpio_control 12 write 1

# Test I2C
./i2c_reader 1 scan
./i2c_reader 1 bme280 0x76

# Test camera
./camera_capture /dev/video0 1920 1080 YUYV 1

# Run benchmarks
./scripts/performance-test.sh --all
```

## Troubleshooting

### Build Issues

**Problem:** Recipe parse errors
```bash
# Solution: Validate syntax
./scripts/validate-recipes.sh problematic-recipe

# Check environment
bitbake -e recipe-name | less
```

**Problem:** Missing dependencies
```bash
# Solution: Check dependency tree
bitbake recipe-name -g
cat pn-depends.dot | grep recipe-name
```

### Runtime Issues

**Problem:** Kernel module won't load
```bash
# Check kernel messages
dmesg | tail -20

# Verify module info
modinfo module_name.ko

# Check dependencies
lsmod | grep dependency
```

**Problem:** Device tree not applied
```bash
# Check device tree
ls /proc/device-tree/
cat /proc/device-tree/compatible

# Verify overlay
cat /sys/kernel/config/device-tree/overlays/*/status
```

## Resources

### Documentation

- [Yocto Project Documentation](https://docs.yoctoproject.org)
- [Meta-Tegra Layer](https://github.com/OE4T/meta-tegra)
- [NVIDIA Jetson Developer Resources](https://developer.nvidia.com/embedded)
- [Linux Kernel Documentation](https://www.kernel.org/doc/html/latest/)

### Community

- [Yocto Project Mailing Lists](https://www.yoctoproject.org/community/mailing-lists/)
- [NVIDIA Developer Forums](https://forums.developer.nvidia.com/c/agx-autonomous-machines/jetson-embedded-systems/70)
- [Meta-Tegra GitHub Issues](https://github.com/OE4T/meta-tegra/issues)

### Tools

- [BitBake Cheat Sheet](https://elinux.org/Bitbake_Cheat_Sheet)
- [Device Tree Compiler](https://git.kernel.org/pub/scm/utils/dtc/dtc.git)
- [TensorRT](https://developer.nvidia.com/tensorrt)

## Contributing

Found an issue or have an improvement?

1. Test your changes thoroughly
2. Follow existing code style
3. Document new examples
4. Submit with clear description

## License

These examples are provided for educational purposes. Individual components may have different licenses - check file headers for details.

## Support

For questions and issues:
- Review example documentation
- Check troubleshooting section
- Search existing issues
- Consult official documentation

---

**Note:** These examples are educational references. Always review and test code before production use. Hardware configurations may vary - adjust examples for your specific setup.

**Last Updated:** November 2025
