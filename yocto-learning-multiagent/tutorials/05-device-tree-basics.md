# Tutorial 05: Device Tree Fundamentals
## Understanding and Customizing Hardware Configuration on Jetson

---

## Learning Objectives

After completing this tutorial, you will be able to:
- Understand device tree structure and syntax
- Read and interpret device tree source files
- Create custom device tree overlays
- Modify pinmux configuration for Jetson
- Debug device tree compilation and loading issues
- Enable/disable hardware peripherals via device tree
- Create runtime-loadable device tree overlays

---

## Prerequisites Checklist

- [ ] Completed Tutorial 01-04 (Yocto basics through first boot)
- [ ] Running Jetson device with custom image
- [ ] Serial console access to Jetson
- [ ] Understanding of hardware peripherals (I2C, SPI, GPIO)
- [ ] Text editor with syntax highlighting
- [ ] Device tree compiler (dtc) installed
- [ ] Basic understanding of hardware addressing

---

## Estimated Duration

**Total Time**: 4-5 hours
- Theory and concepts: 1 hour
- Reading existing device trees: 1 hour
- Creating simple overlay: 1 hour
- Pinmux configuration: 1 hour
- Testing and debugging: 1-2 hours

---

## Step-by-Step Instructions

### Step 1: Understanding Device Tree Basics

Device tree is a data structure that describes hardware to the kernel:

```bash
# On your Jetson device, examine the runtime device tree
cd /proc/device-tree

# List top-level nodes
ls -la

# Key nodes you'll see:
# - model           : Hardware model string
# - compatible      : Compatible device strings
# - #address-cells  : Address size
# - #size-cells     : Size field size
# - aliases/        : Device aliases
# - chosen/         : Bootloader-kernel communication
# - memory@*/       : Memory regions
# - cpus/           : CPU configuration
# - i2c@*/          : I2C controllers
# - spi@*/          : SPI controllers
# - gpio@*/         : GPIO controllers
# - serial@*/       : UART controllers

# View the model string
cat model
# Output: NVIDIA Jetson AGX Orin Developer Kit

# View compatible strings
cat compatible | tr '\0' '\n'
# Output:
# nvidia,p3737-0000+p3701-0000
# nvidia,tegra234
# nvidia,tegra23x
```

**Device Tree Hierarchy**:
```
/ (root)
├── model = "NVIDIA Jetson AGX Orin Developer Kit"
├── compatible = "nvidia,p3737-0000+p3701-0000", "nvidia,tegra234"
├── #address-cells = <2>
├── #size-cells = <2>
├── aliases { ... }
├── chosen { ... }
├── memory@80000000 { ... }
├── cpus {
│   ├── cpu@0 { ... }
│   ├── cpu@1 { ... }
│   └── ...
│}
├── i2c@3160000 {
│   ├── #address-cells = <1>
│   ├── #size-cells = <0>
│   ├── compatible = "nvidia,tegra234-i2c"
│   └── sensor@68 { ... }
│}
└── gpio@2200000 { ... }
```

### Step 2: Extract and Decompile Device Tree

Learn to work with device tree files:

```bash
# On host development machine
cd ~/yocto-jetson/builds/jetson-orin-agx

# Find compiled device tree blobs
ls tmp/deploy/images/jetson-orin-agx-devkit/*.dtb

# Decompile a DTB to readable DTS format
dtc -I dtb -O dts \
    tmp/deploy/images/jetson-orin-agx-devkit/tegra234-p3701-0000-p3737-0000.dtb \
    -o ~/jetson-orin-base.dts

# View the decompiled source
less ~/jetson-orin-base.dts

# Or extract from running system (on Jetson)
# Create a DTS from /proc/device-tree
ssh root@jetson-ip "dtc -I fs -O dts /proc/device-tree" > jetson-running.dts
```

**Example DTS structure**:
```dts
/dts-v1/;

/ {
    #address-cells = <0x02>;
    #size-cells = <0x02>;
    model = "NVIDIA Jetson AGX Orin Developer Kit";
    compatible = "nvidia,p3737-0000+p3701-0000\0nvidia,tegra234\0nvidia,tegra23x";

    chosen {
        bootargs = "console=ttyTCU0,115200";
        stdout-path = "serial0:115200n8";
    };

    cpus {
        #address-cells = <0x01>;
        #size-cells = <0x00>;

        cpu@0 {
            device_type = "cpu";
            compatible = "arm,cortex-a78";
            reg = <0x00>;
            enable-method = "psci";
        };
    };

    memory@80000000 {
        device_type = "memory";
        reg = <0x00 0x80000000 0x02 0x00000000>;
    };
};
```

### Step 3: Find Device Tree Sources in Meta-Tegra

Locate the original DTS files in the Yocto workspace:

```bash
cd ~/yocto-jetson/poky/meta-tegra

# Find device tree source files
find . -name "*.dts" -o -name "*.dtsi"

# Key files for Jetson Orin:
# ./recipes-bsp/tegra-binaries/tegra-firmware-dtbs/tegra234-p3701-0000-p3737-0000.dts
# ./recipes-kernel/linux/linux-tegra-*/arch/arm64/boot/dts/nvidia/tegra234.dtsi
# ./recipes-kernel/linux/linux-tegra-*/arch/arm64/boot/dts/nvidia/tegra234-soc/

# Look at the structure
tree recipes-bsp/tegra-binaries/tegra-firmware-dtbs/

# The actual kernel DTS files are in kernel source (after build)
cd ~/yocto-jetson/builds/jetson-orin-agx
ls tmp/work-shared/jetson-orin-agx-devkit/kernel-source/arch/arm64/boot/dts/nvidia/
```

### Step 4: Create Your First Device Tree Overlay

Create a simple overlay to add a GPIO LED:

```bash
# Create directory for device tree overlays
cd ~/yocto-jetson/meta-custom
mkdir -p recipes-kernel/dtb-overlays/files

# Create a simple LED overlay
cat > recipes-kernel/dtb-overlays/files/gpio-led-overlay.dts << 'EOF'
/dts-v1/;
/plugin/;

// Include necessary definitions
#include <dt-bindings/gpio/tegra234-gpio.h>

/ {
    // Overlay identification
    compatible = "nvidia,p3737-0000+p3701-0000", "nvidia,tegra234";

    // Fragment 0: Add GPIO LED to root
    fragment@0 {
        target-path = "/";
        __overlay__ {
            gpio-leds {
                compatible = "gpio-leds";
                status = "okay";

                // User LED on GPIO PQ.05 (pin 31 on 40-pin header)
                led-user1 {
                    label = "user-led-1";
                    gpios = <&tegra_main_gpio TEGRA234_MAIN_GPIO(Q, 5) GPIO_ACTIVE_HIGH>;
                    default-state = "off";
                    linux,default-trigger = "heartbeat";
                };

                // User LED on GPIO PQ.06
                led-user2 {
                    label = "user-led-2";
                    gpios = <&tegra_main_gpio TEGRA234_MAIN_GPIO(Q, 6) GPIO_ACTIVE_HIGH>;
                    default-state = "off";
                    linux,default-trigger = "mmc0";
                };
            };
        };
    };
};
EOF
```

**Explanation of overlay syntax**:
- `/dts-v1/;` - DTS version declaration
- `/plugin/;` - Marks this as an overlay (not standalone)
- `fragment@N` - Numbered overlay fragments
- `target-path` - Path to node being modified
- `__overlay__` - Overlay content to merge
- `compatible` - Must match base device tree

### Step 5: Create Recipe for Device Tree Overlay

Create a BitBake recipe to build and deploy the overlay:

```bash
cat > ~/yocto-jetson/meta-custom/recipes-kernel/dtb-overlays/gpio-led-overlay_1.0.bb << 'EOF'
SUMMARY = "GPIO LED device tree overlay"
DESCRIPTION = "Adds GPIO LEDs to Jetson Orin"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit devicetree

# Source files
SRC_URI = "file://gpio-led-overlay.dts"

S = "${WORKDIR}"

# Dependencies - need DT bindings from kernel
DEPENDS = "virtual/kernel"

# Compatible machine
COMPATIBLE_MACHINE = "(tegra234)"

# DT compiler flags
DTC_FLAGS += "-@"  # Generate symbols for overlay resolution

# Output directory
DEPLOYDIR = "${DEPLOY_DIR_IMAGE}"

do_compile() {
    # Compile the overlay
    dtc -@ -I dts -O dtb \
        -i ${STAGING_KERNEL_DIR}/include \
        -o ${B}/gpio-led-overlay.dtbo \
        ${WORKDIR}/gpio-led-overlay.dts
}

do_install() {
    install -d ${D}/boot/overlays
    install -m 0644 ${B}/gpio-led-overlay.dtbo ${D}/boot/overlays/
}

do_deploy() {
    install -d ${DEPLOYDIR}/overlays
    install -m 0644 ${B}/gpio-led-overlay.dtbo ${DEPLOYDIR}/overlays/
}

addtask deploy after do_install before do_build

FILES:${PN} = "/boot/overlays/*.dtbo"
EOF
```

### Step 6: Build and Deploy Device Tree Overlay

```bash
cd ~/yocto-jetson/builds/jetson-orin-agx

# Build the overlay
bitbake gpio-led-overlay

# Check output
ls tmp/deploy/images/jetson-orin-agx-devkit/overlays/

# Add to image
echo 'IMAGE_INSTALL:append = " gpio-led-overlay"' >> conf/local.conf

# Rebuild image
bitbake core-image-minimal

# Flash to device or manually copy overlay
# Manual copy:
scp tmp/deploy/images/jetson-orin-agx-devkit/overlays/gpio-led-overlay.dtbo \
    root@jetson-ip:/boot/overlays/
```

### Step 7: Load Overlay at Runtime

On the Jetson device, load the overlay:

```bash
# Method 1: Using device tree overlay loader (if available)
mkdir -p /sys/kernel/config/device-tree/overlays/gpio-led
cat /boot/overlays/gpio-led-overlay.dtbo > \
    /sys/kernel/config/device-tree/overlays/gpio-led/dtbo

# Apply the overlay
echo 1 > /sys/kernel/config/device-tree/overlays/gpio-led/status

# Verify it loaded
cat /sys/kernel/config/device-tree/overlays/gpio-led/status

# Method 2: Boot-time loading via bootloader
# Edit /boot/extlinux/extlinux.conf
vi /boot/extlinux/extlinux.conf

# Add to APPEND line:
# fdtoverlays=/boot/overlays/gpio-led-overlay.dtbo

# Reboot
reboot

# After reboot, verify LED is present
ls /sys/class/leds/
# Should see: user-led-1  user-led-2

# Control the LED
echo 1 > /sys/class/leds/user-led-1/brightness  # Turn on
echo 0 > /sys/class/leds/user-led-1/brightness  # Turn off

# Change trigger
cat /sys/class/leds/user-led-1/trigger
# Available: [none] heartbeat timer oneshot

echo heartbeat > /sys/class/leds/user-led-1/trigger
```

### Step 8: Advanced - I2C Device Addition

Create an overlay to add an I2C sensor:

```bash
cat > ~/yocto-jetson/meta-custom/recipes-kernel/dtb-overlays/files/i2c-sensor-overlay.dts << 'EOF'
/dts-v1/;
/plugin/;

#include <dt-bindings/gpio/tegra234-gpio.h>
#include <dt-bindings/interrupt-controller/irq.h>

/ {
    compatible = "nvidia,p3737-0000+p3701-0000", "nvidia,tegra234";

    // Fragment 0: Configure I2C bus
    fragment@0 {
        target = <&gen8_i2c>;  // I2C bus 1 (alias: gen8_i2c)
        __overlay__ {
            status = "okay";
            clock-frequency = <400000>;  // 400 kHz

            // BME280 environmental sensor at address 0x76
            bme280@76 {
                compatible = "bosch,bme280";
                reg = <0x76>;
                status = "okay";
            };

            // MPU9250 IMU at address 0x68
            mpu9250@68 {
                compatible = "invensense,mpu9250";
                reg = <0x68>;
                interrupt-parent = <&tegra_main_gpio>;
                interrupts = <TEGRA234_MAIN_GPIO(H, 2) IRQ_TYPE_EDGE_RISING>;
                vdd-supply = <&vdd_3v3_sys>;
                vddio-supply = <&vdd_1v8_sys>;
                status = "okay";

                mount-matrix = "1", "0", "0",
                               "0", "1", "0",
                               "0", "0", "1";
            };
        };
    };
};
EOF
```

**Explanation**:
- `target = <&gen8_i2c>` - References existing I2C node by label
- `reg = <0x76>` - I2C device address
- `interrupt-parent` - GPIO controller for interrupts
- `interrupts` - GPIO pin and trigger type
- `vdd-supply` - Power supply regulator reference
- `mount-matrix` - Sensor orientation matrix

### Step 9: Pinmux Configuration

Understanding and modifying pin functions:

```bash
# On Jetson, check current pinmux
cat /sys/kernel/debug/pinctrl/2430000.pinmux/pinmux-pins | head -50

# Example output:
# pin 0 (dap6_sclk_pa0): 2430000.pinmux (GPIO UNCLAIMED) function i2s6 group dap6_sclk_pa0
# pin 1 (dap6_dout_pa1): 2430000.pinmux (GPIO UNCLAIMED) function i2s6 group dap6_dout_pa1

# Create pinmux overlay for custom configuration
cat > ~/yocto-jetson/meta-custom/recipes-kernel/dtb-overlays/files/pinmux-custom.dts << 'EOF'
/dts-v1/;
/plugin/;

/ {
    compatible = "nvidia,p3737-0000+p3701-0000", "nvidia,tegra234";

    fragment@0 {
        target = <&pinmux>;
        __overlay__ {
            pinctrl-names = "default";
            pinctrl-0 = <&pinmux_default>;

            pinmux_default: common {
                // Configure UART_A for serial console
                uart3_tx_py0 {
                    nvidia,pins = "uart3_tx_py0";
                    nvidia,function = "uarta";
                    nvidia,pull = <TEGRA_PIN_PULL_NONE>;
                    nvidia,tristate = <TEGRA_PIN_DISABLE>;
                    nvidia,enable-input = <TEGRA_PIN_DISABLE>;
                };

                uart3_rx_py1 {
                    nvidia,pins = "uart3_rx_py1";
                    nvidia,function = "uarta";
                    nvidia,pull = <TEGRA_PIN_PULL_UP>;
                    nvidia,tristate = <TEGRA_PIN_DISABLE>;
                    nvidia,enable-input = <TEGRA_PIN_ENABLE>;
                };

                // Configure I2C pins
                gen8_i2c_scl_pl0 {
                    nvidia,pins = "gen8_i2c_scl_pl0";
                    nvidia,function = "i2c8";
                    nvidia,pull = <TEGRA_PIN_PULL_NONE>;
                    nvidia,tristate = <TEGRA_PIN_DISABLE>;
                    nvidia,enable-input = <TEGRA_PIN_ENABLE>;
                    nvidia,io-high-voltage = <TEGRA_PIN_DISABLE>;
                    nvidia,lpdr = <TEGRA_PIN_DISABLE>;
                };

                gen8_i2c_sda_pl1 {
                    nvidia,pins = "gen8_i2c_sda_pl1";
                    nvidia,function = "i2c8";
                    nvidia,pull = <TEGRA_PIN_PULL_NONE>;
                    nvidia,tristate = <TEGRA_PIN_DISABLE>;
                    nvidia,enable-input = <TEGRA_PIN_ENABLE>;
                    nvidia,io-high-voltage = <TEGRA_PIN_DISABLE>;
                    nvidia,lpdr = <TEGRA_PIN_DISABLE>;
                };

                // Configure GPIO pins
                soc_gpio54_pq6 {
                    nvidia,pins = "soc_gpio54_pq6";
                    nvidia,function = "rsvd0";  // GPIO function
                    nvidia,pull = <TEGRA_PIN_PULL_DOWN>;
                    nvidia,tristate = <TEGRA_PIN_DISABLE>;
                    nvidia,enable-input = <TEGRA_PIN_DISABLE>;
                };
            };
        };
    };
};
EOF
```

**Pinmux properties**:
- `nvidia,function` - Pin function (gpio, i2c, spi, uart, etc.)
- `nvidia,pull` - Pull-up/down resistor
- `nvidia,tristate` - Enable/disable tri-state
- `nvidia,enable-input` - Enable input buffer
- `nvidia,io-high-voltage` - 3.3V vs 1.8V I/O

### Step 10: Debug Device Tree Issues

Tools and techniques for troubleshooting:

```bash
# On Jetson device:

# 1. Check device tree compilation errors in kernel log
dmesg | grep -i "device tree"
dmesg | grep -i "dtb"
dmesg | grep -i "overlay"

# 2. Verify specific device initialization
dmesg | grep -i i2c
dmesg | grep -i "i2c-1"

# 3. Check if device is probed
ls /sys/bus/i2c/devices/
# Should see: 1-0068  1-0076  (for devices at 0x68 and 0x76 on bus 1)

# 4. Read device properties at runtime
cat /proc/device-tree/i2c@3160000/bme280@76/compatible

# 5. Check pinmux status
cat /sys/kernel/debug/pinctrl/2430000.pinmux/pinmux-pins | grep -i "pl0\|pl1"

# 6. Validate GPIO configuration
cat /sys/kernel/debug/gpio

# 7. Use dtc to validate DTS syntax on host
cd ~/yocto-jetson/meta-custom/recipes-kernel/dtb-overlays/files
dtc -I dts -O dtb -o /tmp/test.dtbo gpio-led-overlay.dts
# Check for errors/warnings

# 8. Compare device trees before/after overlay
# Before:
dtc -I fs -O dts /proc/device-tree > before.dts

# Apply overlay, then:
dtc -I fs -O dts /proc/device-tree > after.dts

# Compare:
diff -u before.dts after.dts

# 9. Check for node conflicts
find /proc/device-tree -name "*@76*"  # Find I2C device at 0x76
```

---

## Troubleshooting Common Issues

### Issue 1: Overlay Fails to Compile

**Symptoms**:
```
Error: gpio-led-overlay.dts:15: syntax error
```

**Solutions**:
```bash
# 1. Check DTS syntax
# Common errors:
# - Missing semicolons
# - Unmatched braces
# - Incorrect node names (must match: node@address or node-name)

# 2. Verify includes are available
ls tmp/work-shared/jetson-orin-agx-devkit/kernel-source/include/dt-bindings/

# 3. Use verbose compilation
dtc -@ -I dts -O dtb -v \
    -i tmp/work-shared/jetson-orin-agx-devkit/kernel-source/include \
    gpio-led-overlay.dts

# 4. Check for undefined references
# Make sure gpio controller references exist:
grep -r "tegra_main_gpio" \
    tmp/work-shared/jetson-orin-agx-devkit/kernel-source/arch/arm64/boot/dts/
```

### Issue 2: Overlay Loads But Device Not Working

**Symptoms**: Overlay applies successfully but hardware doesn't respond

**Solutions**:
```bash
# 1. Verify device node was created
ls /proc/device-tree/gpio-leds/

# 2. Check driver binding
cat /sys/bus/platform/drivers/leds-gpio/bind

# 3. Look for probe errors
dmesg | grep -i "leds-gpio"

# 4. Verify GPIO is not already in use
cat /sys/kernel/debug/gpio | grep "PQ.05"

# 5. Check if GPIO is exported to userspace
ls /sys/class/gpio/gpio*

# 6. Manually test GPIO
echo 448 > /sys/class/gpio/export  # PQ.05 = GPIO 448
echo out > /sys/class/gpio/gpio448/direction
echo 1 > /sys/class/gpio/gpio448/value
```

### Issue 3: I2C Device Not Detected

**Symptoms**: I2C device doesn't appear in /sys/bus/i2c/devices/

**Solutions**:
```bash
# 1. Verify I2C bus is enabled
ls /dev/i2c-*

# 2. Scan I2C bus for devices
i2cdetect -y -r 1  # Scan bus 1

# Expected output showing device at 0x76:
#      0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
# 00:          -- -- -- -- -- -- -- -- -- -- -- -- --
# ...
# 70: -- -- -- -- -- -- 76 --

# 3. Check if driver is loaded
lsmod | grep bme280

# 4. Load driver manually if needed
modprobe bme280-i2c

# 5. Check I2C bus frequency
cat /sys/bus/i2c/devices/i2c-1/of_node/clock-frequency
# Should match overlay setting (400000 = 400kHz)

# 6. Verify pinmux for I2C pins
cat /sys/kernel/debug/pinctrl/2430000.pinmux/pinmux-pins | grep -i "i2c.*pl"
```

### Issue 4: Wrong GPIO Number Calculation

**Symptoms**: GPIO doesn't respond, wrong pin toggling

**Solutions**:
```bash
# Jetson Orin GPIO calculation:
# Format: P<PORT>.<PIN>
# Port bases (for Orin):
#   PA=316, PB=324, PC=332, ..., PQ=443, PR=451, ...

# Example: PQ.05
# Base: 443
# Pin: 5
# Linux GPIO: 443 + 5 = 448

# Verify mapping:
cat /sys/kernel/debug/gpio | grep "gpio-448"

# Create helper script:
cat > /tmp/gpio-calc.sh << 'EOF'
#!/bin/bash
# Tegra GPIO calculator
declare -A ports=(
    [PA]=316 [PB]=324 [PC]=332 [PD]=340 [PE]=348 [PF]=356
    [PG]=364 [PH]=372 [PI]=380 [PJ]=388 [PK]=396 [PL]=404
    [PM]=412 [PN]=420 [PO]=428 [PP]=436 [PQ]=443 [PR]=451
)

port=${1%%.*}
pin=${1##*.}
gpio=$((${ports[$port]} + 10#$pin))
echo "$1 = GPIO $gpio"
EOF

chmod +x /tmp/gpio-calc.sh
/tmp/gpio-calc.sh PQ.05
# Output: PQ.05 = GPIO 448
```

### Issue 5: Pinmux Conflict

**Symptoms**: Pin doesn't work in configured mode

**Solutions**:
```bash
# 1. Check pin function
cat /sys/kernel/debug/pinctrl/2430000.pinmux/pinmux-pins | grep "pq6"

# 2. Look for conflicts
dmesg | grep -i "pinctrl\|pinmux"

# 3. Verify pin is not claimed by another driver
cat /sys/kernel/debug/gpio | grep -B2 -A2 "gpio-454"

# 4. Use NVIDIA pinmux tool (if available)
/opt/nvidia/jetson-io/jetson-io.py
# This shows graphical pinmux configuration

# 5. Check device tree for duplicate pin config
grep -r "soc_gpio54_pq6" /proc/device-tree/
```

---

## Verification Checklist

- [ ] Can decompile DTB to readable DTS
- [ ] Understand device tree node structure
- [ ] Created simple GPIO LED overlay
- [ ] Overlay compiles without errors
- [ ] Overlay loads at runtime
- [ ] LED appears in /sys/class/leds/
- [ ] Can control LED via sysfs
- [ ] Created I2C device overlay
- [ ] I2C device detected by kernel
- [ ] Driver binds to I2C device
- [ ] Can read I2C device data
- [ ] Understand pinmux configuration
- [ ] Can debug overlay loading issues

---

## Device Tree Best Practices

### 1. Use Labels and Phandles

```dts
// Define a label
i2c@3160000 {
    label = "gen8_i2c";
    ...
};

// Reference by label in overlay
fragment@0 {
    target = <&gen8_i2c>;  // Much better than target-path
    ...
};
```

### 2. Include Proper Bindings

```dts
// Always include necessary headers
#include <dt-bindings/gpio/tegra234-gpio.h>
#include <dt-bindings/interrupt-controller/irq.h>
#include <dt-bindings/clock/tegra234-clock.h>
#include <dt-bindings/reset/tegra234-reset.h>
```

### 3. Document Your Overlays

```dts
/dts-v1/;
/plugin/;

/*
 * Overlay: gpio-led-overlay.dts
 * Purpose: Add GPIO LEDs for status indication
 * Hardware: Jetson Orin AGX on custom carrier
 * Pins used: PQ.05 (GPIO 448), PQ.06 (GPIO 449)
 * Author: Your Name
 * Date: 2025-01-15
 */
```

### 4. Use Status Properties

```dts
// Disable by default, enable selectively
node {
    status = "disabled";  // or "okay"
};
```

### 5. Validate Before Deployment

```bash
# Always validate syntax
dtc -I dts -O dtb -o /tmp/test.dtbo overlay.dts

# Check for warnings
# Fix all warnings before deployment
```

---

## Next Steps

### Immediate Practice
1. Create overlay for SPI device
2. Add PWM output for motor control
3. Configure CAN bus interface

### Proceed to Next Tutorial
**Tutorial 06: GPIO Kernel Module** - Write a custom GPIO driver

### Advanced Device Tree Topics
- Runtime overlay loading and unloading
- Device tree binding documentation
- Custom device tree nodes for proprietary hardware
- Device tree unit testing

---

## Useful Device Tree References

### Node Properties Reference

| Property | Type | Description |
|----------|------|-------------|
| compatible | string-list | Device compatibility strings |
| reg | cell-array | Device address |
| status | string | "okay", "disabled", "fail", "fail-sss" |
| interrupts | cell-array | Interrupt specifier |
| clocks | phandle-array | Clock references |
| gpios | phandle-array | GPIO references |
| *-supply | phandle | Power supply reference |

### Common GPIO Flags

```c
GPIO_ACTIVE_HIGH    // Logic high = active
GPIO_ACTIVE_LOW     // Logic low = active
GPIO_OPEN_DRAIN     // Open-drain output
GPIO_OPEN_SOURCE    // Open-source output
GPIO_PULL_UP        // Enable pull-up
GPIO_PULL_DOWN      // Enable pull-down
```

### Interrupt Types

```c
IRQ_TYPE_EDGE_RISING    // Trigger on rising edge
IRQ_TYPE_EDGE_FALLING   // Trigger on falling edge
IRQ_TYPE_EDGE_BOTH      // Trigger on both edges
IRQ_TYPE_LEVEL_HIGH     // Trigger when high
IRQ_TYPE_LEVEL_LOW      // Trigger when low
```

---

**Congratulations!** You now understand device tree fundamentals and can create custom hardware configurations for your Jetson platform. This is essential knowledge for integrating custom hardware and sensors.

---

*Tutorial created by the Yocto & Meta-Tegra Multi-Agent Learning System*
*Last updated: 2025-01-15*
