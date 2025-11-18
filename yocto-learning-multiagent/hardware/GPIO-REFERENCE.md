# GPIO Hardware Reference

## Overview

Complete GPIO (General Purpose Input/Output) reference for NVIDIA Jetson platforms, including pinouts, voltage specifications, and configuration examples.

## 40-Pin Expansion Header

All Jetson developer kits feature a 40-pin GPIO header compatible with Raspberry Pi layouts (with voltage differences).

### Standard 40-Pin Header Layout

```
     3.3V (1)  (2)  5V
    SDA1 (3)  (4)  5V
    SCL1 (5)  (6)  GND
GPIO_GEN4 (7)  (8)  TXD0
      GND (9)  (10) RXD0
 GPIO_GEN0 (11) (12) GPIO_GEN1
 GPIO_GEN2 (13) (14) GND
 GPIO_GEN3 (15) (16) GPIO_GEN4
     3.3V (17) (18) GPIO_GEN5
  SPI0_MOSI (19) (20) GND
  SPI0_MISO (21) (22) GPIO_GEN6
  SPI0_SCLK (23) (24) SPI0_CS0
      GND (25) (26) SPI0_CS1
   ID_SDA (27) (28) ID_SCL
  GPIO_GEN7 (29) (30) GND
  GPIO_GEN8 (31) (32) GPIO_GEN9
 GPIO_GEN10 (33) (34) GND
 GPIO_GEN11 (35) (36) GPIO_GEN12
 GPIO_GEN13 (37) (38) GPIO_GEN14
      GND (39) (40) GPIO_GEN15
```

## Platform-Specific GPIO Mappings

### Jetson AGX Orin GPIO Mapping

| Pin | Function | GPIO Chip | GPIO Line | SysFS GPIO | Notes |
|-----|----------|-----------|-----------|------------|-------|
| 3   | I2C1_SDA | - | - | - | I2C Bus 1 |
| 5   | I2C1_SCL | - | - | - | I2C Bus 1 |
| 7   | GPIO09 (AUD_MCLK) | gpiochip0 | 106 | 422 | Audio MCLK |
| 8   | UART1_TX | - | - | - | UART1 |
| 10  | UART1_RX | - | - | - | UART1 |
| 11  | UART1_RTS | - | - | 428 | Can be GPIO |
| 12  | I2S0_SCLK | gpiochip0 | 112 | 428 | Audio I2S |
| 13  | GPIO01 (SPI1_MOSI) | gpiochip0 | 108 | 424 | SPI1 or GPIO |
| 15  | GPIO12 (GPIO_PCC7) | gpiochip0 | 84 | 400 | General GPIO |
| 16  | GPIO07 (SPI1_MISO) | gpiochip0 | 107 | 423 | SPI1 or GPIO |
| 18  | GPIO13 (SPI1_CLK) | gpiochip0 | 109 | 425 | SPI1 or GPIO |
| 19  | SPI0_MOSI | - | - | - | SPI0 |
| 21  | SPI0_MISO | - | - | - | SPI0 |
| 22  | GPIO14 (SPI1_CS0) | gpiochip0 | 110 | 426 | SPI1 CS or GPIO |
| 23  | SPI0_SCLK | - | - | - | SPI0 |
| 24  | SPI0_CS0 | - | - | - | SPI0 |
| 26  | SPI0_CS1 | - | - | 430 | SPI0 CS1 |
| 27  | I2C2_SDA | - | - | - | I2C Bus 2 (reserved) |
| 28  | I2C2_SCL | - | - | - | I2C Bus 2 (reserved) |
| 29  | GPIO16 (CAN0_DIN) | gpiochip0 | 134 | 450 | CAN0 or GPIO |
| 31  | GPIO17 (CAN0_DOUT) | gpiochip0 | 133 | 449 | CAN0 or GPIO |
| 32  | GPIO18 (GPIO_PQ6) | gpiochip0 | 126 | 442 | General GPIO |
| 33  | GPIO19 (GPIO_PR0) | gpiochip0 | 136 | 452 | General GPIO, PWM |
| 35  | I2S0_FS | gpiochip0 | 113 | 429 | Audio I2S |
| 36  | UART1_CTS | - | - | 427 | Can be GPIO |
| 37  | GPIO20 (SPI1_CS1) | gpiochip0 | 111 | 427 | SPI1 CS or GPIO |
| 38  | I2S0_SDIN | gpiochip0 | 115 | 431 | Audio I2S |
| 40  | I2S0_SDOUT | gpiochip0 | 114 | 430 | Audio I2S |

### Jetson Orin NX/Nano GPIO Mapping

| Pin | Function | GPIO Chip | GPIO Line | SysFS GPIO | Notes |
|-----|----------|-----------|-----------|------------|-------|
| 3   | I2C1_SDA | - | - | - | I2C Bus 8 |
| 5   | I2C1_SCL | - | - | - | I2C Bus 8 |
| 7   | GPIO09 (AUD_MCLK) | gpiochip0 | 106 | 422 | Audio MCLK |
| 8   | UART1_TX | - | - | - | UART1 |
| 10  | UART1_RX | - | - | - | UART1 |
| 11  | UART1_RTS | - | - | 344 | Can be GPIO |
| 12  | I2S0_SCLK | gpiochip0 | 112 | 428 | Audio I2S |
| 13  | GPIO01 (SPI1_MOSI) | gpiochip0 | 108 | 424 | SPI1 or GPIO |
| 15  | GPIO12 | gpiochip0 | 84 | 400 | General GPIO |
| 16  | GPIO07 (SPI1_MISO) | gpiochip0 | 107 | 423 | SPI1 or GPIO |
| 18  | GPIO13 (SPI1_CLK) | gpiochip0 | 109 | 425 | SPI1 or GPIO |
| 19  | SPI0_MOSI | - | - | - | SPI0 |
| 21  | SPI0_MISO | - | - | - | SPI0 |
| 22  | GPIO14 (SPI1_CS0) | gpiochip0 | 110 | 426 | SPI1 CS or GPIO |
| 23  | SPI0_SCLK | - | - | - | SPI0 |
| 24  | SPI0_CS0 | - | - | - | SPI0 |
| 26  | SPI0_CS1 | - | - | 347 | SPI0 CS1 |
| 29  | GPIO05 | gpiochip0 | 149 | 465 | General GPIO |
| 31  | GPIO06 | gpiochip0 | 148 | 464 | General GPIO |
| 32  | GPIO18 | gpiochip0 | 126 | 442 | General GPIO, PWM |
| 33  | GPIO19 | gpiochip0 | 136 | 452 | General GPIO, PWM |
| 35  | I2S0_FS | gpiochip0 | 113 | 429 | Audio I2S |
| 36  | UART1_CTS | - | - | 345 | Can be GPIO |
| 37  | GPIO20 (SPI1_CS1) | gpiochip0 | 111 | 427 | SPI1 CS or GPIO |
| 38  | I2S0_SDIN | gpiochip0 | 115 | 431 | Audio I2S |
| 40  | I2S0_SDOUT | gpiochip0 | 114 | 430 | Audio I2S |

### Jetson Xavier NX GPIO Mapping

| Pin | Function | GPIO Chip | GPIO Line | SysFS GPIO | Notes |
|-----|----------|-----------|-----------|------------|-------|
| 7   | GPIO09 (AUD_MCLK) | gpiochip0 | 12 | 422 | Audio MCLK |
| 11  | UART1_RTS | gpiochip0 | 18 | 428 | UART1 or GPIO |
| 12  | I2S0_SCLK | gpiochip0 | 79 | 489 | Audio I2S |
| 13  | GPIO01 (SPI1_MOSI) | gpiochip0 | 16 | 426 | SPI1 or GPIO |
| 15  | GPIO12 | gpiochip0 | 20 | 430 | General GPIO |
| 16  | GPIO07 (SPI1_MISO) | gpiochip0 | 15 | 425 | SPI1 or GPIO |
| 18  | GPIO13 (SPI1_CLK) | gpiochip0 | 17 | 427 | SPI1 or GPIO |
| 19  | SPI0_MOSI | - | - | - | SPI0 |
| 21  | SPI0_MISO | - | - | - | SPI0 |
| 22  | GPIO14 (SPI1_CS0) | gpiochip0 | 19 | 429 | SPI1 CS or GPIO |
| 23  | SPI0_SCLK | - | - | - | SPI0 |
| 24  | SPI0_CS0 | - | - | - | SPI0 |
| 26  | SPI0_CS1 | gpiochip0 | 149 | 417 | SPI0 CS1 |
| 29  | GPIO05 | gpiochip0 | 106 | 516 | General GPIO, CAN0_DIN |
| 31  | GPIO06 | gpiochip0 | 50 | 460 | General GPIO, CAN0_DOUT |
| 32  | GPIO18 | gpiochip0 | 8 | 418 | General GPIO, PWM |
| 33  | GPIO19 | gpiochip0 | 163 | 393 | General GPIO, PWM |
| 35  | I2S0_FS | gpiochip0 | 80 | 490 | Audio I2S |
| 36  | UART1_CTS | gpiochip0 | 69 | 479 | UART1 or GPIO |
| 37  | GPIO20 (SPI1_CS1) | gpiochip0 | 14 | 424 | SPI1 CS or GPIO |
| 38  | I2S0_SDIN | gpiochip0 | 82 | 492 | Audio I2S |
| 40  | I2S0_SDOUT | gpiochip0 | 81 | 491 | Audio I2S |

### Jetson Nano GPIO Mapping

| Pin | Function | GPIO Chip | GPIO Line | SysFS GPIO | Notes |
|-----|----------|-----------|-----------|------------|-------|
| 7   | GPIO09 (AUD_MCLK) | gpiochip0 | 216 | 216 | Audio MCLK |
| 11  | UART2_RTS | gpiochip0 | 50 | 50 | UART2 or GPIO |
| 12  | I2S0_SCLK | gpiochip0 | 79 | 79 | Audio I2S |
| 13  | GPIO01 (SPI2_MOSI) | gpiochip0 | 14 | 14 | SPI2 or GPIO |
| 15  | GPIO12 | gpiochip0 | 194 | 194 | General GPIO |
| 16  | GPIO07 (SPI2_MISO) | gpiochip0 | 232 | 232 | SPI2 or GPIO |
| 18  | GPIO13 (SPI2_CLK) | gpiochip0 | 15 | 15 | SPI2 or GPIO |
| 19  | SPI1_MOSI | gpiochip0 | 16 | 16 | SPI1 |
| 21  | SPI1_MISO | gpiochip0 | 17 | 17 | SPI1 |
| 22  | GPIO14 (SPI2_CS0) | gpiochip0 | 13 | 13 | SPI2 CS or GPIO |
| 23  | SPI1_SCLK | gpiochip0 | 18 | 18 | SPI1 |
| 24  | SPI1_CS0 | gpiochip0 | 19 | 19 | SPI1 |
| 26  | SPI1_CS1 | gpiochip0 | 20 | 20 | SPI1 CS1 |
| 29  | GPIO05 | gpiochip0 | 149 | 149 | General GPIO |
| 31  | GPIO06 | gpiochip0 | 200 | 200 | General GPIO |
| 32  | GPIO18 | gpiochip0 | 168 | 168 | General GPIO, PWM2 |
| 33  | GPIO19 | gpiochip0 | 38 | 38 | General GPIO, PWM1 |
| 35  | I2S0_FS | gpiochip0 | 80 | 80 | Audio I2S |
| 36  | UART2_CTS | gpiochip0 | 51 | 51 | UART2 or GPIO |
| 37  | GPIO20 (SPI2_CS1) | gpiochip0 | 12 | 12 | SPI2 CS or GPIO |
| 38  | I2S0_SDIN | gpiochip0 | 82 | 82 | Audio I2S |
| 40  | I2S0_SDOUT | gpiochip0 | 81 | 81 | Audio I2S |

## Voltage Specifications

### Logic Levels

**CRITICAL: Jetson GPIO is 3.3V ONLY**

| Platform | Logic High | Logic Low | Absolute Maximum | Tolerance |
|----------|-----------|-----------|------------------|-----------|
| AGX Orin | 3.3V | 0V | 3.6V | **NOT 5V tolerant** |
| Orin NX/Nano | 3.3V | 0V | 3.6V | **NOT 5V tolerant** |
| AGX Xavier | 3.3V | 0V | 3.6V | **NOT 5V tolerant** |
| Xavier NX | 3.3V | 0V | 3.6V | **NOT 5V tolerant** |
| Jetson Nano | 3.3V | 0V | 3.6V | **NOT 5V tolerant** |

### Voltage Thresholds

```
VIH (Input High): 2.0V minimum
VIL (Input Low):  0.8V maximum
VOH (Output High): 2.4V minimum (at 2mA)
VOL (Output Low):  0.4V maximum (at 2mA)
```

### Current Specifications

**Per-Pin Limits:**
- Maximum source current: 2-10mA (conservative 2mA recommended)
- Maximum sink current: 2-10mA (conservative 2mA recommended)
- **Never exceed these limits without external buffering**

**Power Rails:**
- 3.3V rail: Up to 1000mA total (varies by platform)
- 5V rail: Up to 3000mA total (varies by platform and power supply)

### Level Shifting

**For 5V Devices:**
```
Required: Bidirectional level shifter
Examples:
- TXS0108E (8-channel)
- TXB0104 (4-channel)
- BSS138-based MOSFET shifters
```

**Circuit Example:**
```
     3.3V           5V
      |             |
      ├─── VCCA    VCCB ───┤
      |             |
Jetson│             │5V Device
 GPIO ├─── A ↔ B ───┤ GPIO
      |   TXS0108E  |
      ├─── GND  GND ───┤
      |             |
     GND           GND
```

## GPIO Access Methods

### Method 1: sysfs Interface (Legacy)

```bash
# Export GPIO (example: GPIO 424 on Orin)
echo 424 > /sys/class/gpio/export

# Set direction
echo out > /sys/class/gpio/gpio424/direction
# or
echo in > /sys/class/gpio/gpio424/direction

# Set value (output)
echo 1 > /sys/class/gpio/gpio424/value
echo 0 > /sys/class/gpio/gpio424/value

# Read value (input)
cat /sys/class/gpio/gpio424/value

# Configure edge detection
echo rising > /sys/class/gpio/gpio424/edge
# Options: none, rising, falling, both

# Unexport when done
echo 424 > /sys/class/gpio/unexport
```

### Method 2: libgpiod (Modern, Recommended)

```bash
# Install tools
apt-get install gpiod libgpiod-dev

# List GPIO chips
gpiodetect

# List GPIO lines
gpioinfo gpiochip0

# Set GPIO high
gpioset gpiochip0 424=1

# Read GPIO
gpioget gpiochip0 424

# Monitor GPIO
gpiomon gpiochip0 424
```

### Method 3: Python with gpiod

```python
#!/usr/bin/env python3
import gpiod
import time

# Open GPIO chip
chip = gpiod.Chip('gpiochip0')

# Get line (GPIO 424)
line = chip.get_line(424)

# Configure as output
line.request(consumer='my-app', type=gpiod.LINE_REQ_DIR_OUT)

# Toggle GPIO
try:
    while True:
        line.set_value(1)
        time.sleep(0.5)
        line.set_value(0)
        time.sleep(0.5)
finally:
    line.release()
```

### Method 4: Python with Jetson.GPIO

```python
#!/usr/bin/env python3
import Jetson.GPIO as GPIO
import time

# Set mode (BOARD uses physical pin numbers)
GPIO.setmode(GPIO.BOARD)
# or GPIO.setmode(GPIO.BCM) for GPIO numbers

# Setup pin 13 as output
GPIO.setup(13, GPIO.OUT)

# Setup pin 11 as input with pull-up
GPIO.setup(11, GPIO.IN, pull_up_down=GPIO.PUD_UP)

# Output
try:
    while True:
        GPIO.output(13, GPIO.HIGH)
        time.sleep(0.5)
        GPIO.output(13, GPIO.LOW)
        time.sleep(0.5)
finally:
    GPIO.cleanup()

# Input
value = GPIO.input(11)

# PWM
pwm = GPIO.PWM(13, 1000)  # 1kHz frequency
pwm.start(50)  # 50% duty cycle
time.sleep(2)
pwm.stop()

GPIO.cleanup()
```

### Method 5: C/C++ with libgpiod

```c
#include <gpiod.h>
#include <stdio.h>
#include <unistd.h>

int main(void) {
    struct gpiod_chip *chip;
    struct gpiod_line *line;
    int ret;

    // Open GPIO chip
    chip = gpiod_chip_open("/dev/gpiochip0");
    if (!chip) {
        perror("gpiod_chip_open");
        return 1;
    }

    // Get GPIO line
    line = gpiod_chip_get_line(chip, 424);
    if (!line) {
        perror("gpiod_chip_get_line");
        gpiod_chip_close(chip);
        return 1;
    }

    // Request line as output
    ret = gpiod_line_request_output(line, "my-app", 0);
    if (ret < 0) {
        perror("gpiod_line_request_output");
        gpiod_chip_close(chip);
        return 1;
    }

    // Toggle GPIO
    for (int i = 0; i < 10; i++) {
        gpiod_line_set_value(line, 1);
        usleep(500000);
        gpiod_line_set_value(line, 0);
        usleep(500000);
    }

    // Cleanup
    gpiod_line_release(line);
    gpiod_chip_close(chip);

    return 0;
}
```

Compile:
```bash
gcc -o gpio_test gpio_test.c -lgpiod
```

## Pinmux Configuration

### Device Tree Pinmux

Pinmux (pin multiplexing) controls which function each pin performs.

**Example: Configure Pin 13 as GPIO instead of SPI**

```dts
// File: tegra234-p3767-0000-p3509-a02.dts (Orin NX example)

/ {
    pinmux@2430000 {
        pinctrl-names = "default";
        pinctrl-0 = <&tegra_main_gpio>;

        tegra_main_gpio: gpio_default {
            // Configure GPIO pin
            spi1_mosi_pg4 {
                nvidia,pins = "spi1_mosi_pg4";
                nvidia,function = "gp";  // General Purpose GPIO
                nvidia,pull = <TEGRA_PIN_PULL_NONE>;
                nvidia,tristate = <TEGRA_PIN_DISABLE>;
                nvidia,enable-input = <TEGRA_PIN_ENABLE>;
            };
        };
    };
};
```

**Common Functions:**
- `gp` - General Purpose GPIO
- `spi1` - SPI1 interface
- `i2c1` - I2C interface
- `uart` - UART interface
- `i2s0` - I2S audio
- `pwm` - PWM output

### Runtime Pinmux (tegra-pinmux-tool)

```bash
# View current pinmux
cat /sys/kernel/debug/pinctrl/2430000.pinmux/pinmux-pins

# Using tegra-pinmux-scripts (if available)
cd /opt/nvidia/tegra-pinmux-scripts
./pinmux-dump.sh
```

### Jetson-IO Tool

NVIDIA provides `jetson-io` for runtime configuration:

```bash
# Launch configuration tool
sudo /opt/nvidia/jetson-io/jetson-io.py

# Configure specific headers
sudo /opt/nvidia/jetson-io/config-by-hardware.py -n "Jetson 40pin Header"
```

## Pull-up/Pull-down Configuration

### Device Tree Configuration

```dts
gpio_pin {
    nvidia,pins = "gpio_pin_name";
    nvidia,function = "gp";
    nvidia,pull = <TEGRA_PIN_PULL_UP>;    // Pull-up
    // or nvidia,pull = <TEGRA_PIN_PULL_DOWN>;  // Pull-down
    // or nvidia,pull = <TEGRA_PIN_PULL_NONE>;  // No pull
    nvidia,tristate = <TEGRA_PIN_DISABLE>;
    nvidia,enable-input = <TEGRA_PIN_ENABLE>;
};
```

### Software Configuration (gpiod)

```c
// Request with pull-up
gpiod_line_request_input_flags(line, "my-app",
    GPIOD_LINE_REQUEST_FLAG_BIAS_PULL_UP);

// Request with pull-down
gpiod_line_request_input_flags(line, "my-app",
    GPIOD_LINE_REQUEST_FLAG_BIAS_PULL_DOWN);
```

## Safety Considerations

### Electrical Safety

1. **Never connect 5V signals directly**
   - Always use level shifters
   - Check voltage with multimeter first

2. **Current limiting**
   - Use series resistors for LEDs (220Ω-1kΩ)
   - Never drive high-current loads directly
   - Use transistors/MOSFETs for loads >10mA

3. **ESD Protection**
   - Use ESD-safe handling procedures
   - Consider TVS diodes for external connections
   - Proper grounding essential

4. **Inductive loads**
   - Always use flyback diodes with relays/motors
   - Optoisolators recommended for isolation

### Example Safe LED Circuit

```
Jetson GPIO (3.3V) ──┬── 330Ω ──┬── LED (Anode)
                     │          │
                     │          └── LED (Cathode) ── GND
                     │
                  (Optional: Level Shifter)
```

**LED Current Calculation:**
```
V_gpio = 3.3V
V_led = 2.0V (typical red LED)
I_desired = 5mA

R = (V_gpio - V_led) / I_desired
R = (3.3 - 2.0) / 0.005
R = 260Ω (use 330Ω standard value)
```

### Example Safe Relay Circuit

```
                          +5V/12V
                            |
                            └── Relay Coil
                            |
Jetson GPIO ── 1kΩ ──┤ Base (NPN Transistor, e.g., 2N2222)
                     │ Collector ── Relay Coil
                     │ Emitter ──┬── GND
                                 │
                            Flyback Diode (1N4007)
                            Cathode ──┬── Relay Coil (+)
                            Anode ────┴── Relay Coil (-)
```

## Testing Procedures

### Basic GPIO Test

```bash
#!/bin/bash
# Test script for GPIO functionality

GPIO=424  # Change to your GPIO number

# Export
echo $GPIO > /sys/class/gpio/export
sleep 0.1

# Configure as output
echo out > /sys/class/gpio/gpio${GPIO}/direction

# Test toggling
for i in {1..5}; do
    echo 1 > /sys/class/gpio/gpio${GPIO}/value
    echo "GPIO HIGH"
    sleep 0.5
    echo 0 > /sys/class/gpio/gpio${GPIO}/value
    echo "GPIO LOW"
    sleep 0.5
done

# Cleanup
echo $GPIO > /sys/class/gpio/unexport
```

### Interrupt Latency Test

```python
#!/usr/bin/env python3
import gpiod
import time

chip = gpiod.Chip('gpiochip0')
line = chip.get_line(424)

line.request(consumer='latency-test',
             type=gpiod.LINE_REQ_EV_BOTH_EDGES)

print("Trigger GPIO and measuring latency...")
while True:
    event = line.event_read()
    if event:
        timestamp = time.time_ns()
        print(f"Event: {event.type}, Time: {timestamp}")
```

### PWM Frequency Test

```python
#!/usr/bin/env python3
import Jetson.GPIO as GPIO
import time

GPIO.setmode(GPIO.BOARD)
pin = 33  # PWM-capable pin

GPIO.setup(pin, GPIO.OUT)
pwm = GPIO.PWM(pin, 1000)  # 1kHz

# Test different duty cycles
for duty in [0, 25, 50, 75, 100]:
    print(f"Testing {duty}% duty cycle")
    pwm.start(duty)
    time.sleep(2)
    pwm.stop()

GPIO.cleanup()
```

## Common Issues and Solutions

### Issue 1: GPIO Already Exported

```bash
# Error: Device or resource busy
# Solution: Unexport first
echo 424 > /sys/class/gpio/unexport
# Then export again
echo 424 > /sys/class/gpio/export
```

### Issue 2: Permission Denied

```bash
# Add user to gpio group
sudo usermod -a -G gpio $USER
# Re-login for changes to take effect

# Or use udev rules
sudo sh -c 'echo "SUBSYSTEM==\"gpio\", GROUP=\"gpio\", MODE=\"0660\"" > /etc/udev/rules.d/99-gpio.rules'
sudo udevadm control --reload-rules
sudo udevadm trigger
```

### Issue 3: Wrong GPIO Number

```bash
# Find correct GPIO number
gpioinfo | grep "your-pin-name"

# Or check device tree
cat /sys/kernel/debug/pinctrl/2430000.pinmux/pinmux-pins | grep "pin-name"
```

## Yocto Integration

### Recipe for GPIO Tools

```bitbake
# File: recipes-support/gpio-tools/gpio-tools_1.0.bb

SUMMARY = "GPIO control tools and libraries"
LICENSE = "MIT"

DEPENDS = "libgpiod"
RDEPENDS_${PN} = "libgpiod python3-libgpiod"

SRC_URI = "file://gpio-test.sh \
           file://gpio-test.py"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/gpio-test.sh ${D}${bindir}/
    install -m 0755 ${WORKDIR}/gpio-test.py ${D}${bindir}/
}
```

### Device Tree Customization

```bitbake
# File: recipes-kernel/linux/linux-tegra_%.bbappend

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://custom-gpio.dts"

do_configure_prepend() {
    # Include custom device tree fragment
    cat ${WORKDIR}/custom-gpio.dts >> ${S}/arch/arm64/boot/dts/nvidia/tegra234-p3767-custom.dts
}
```

## References

### Official Documentation
- [Jetson Linux GPIO Guide](https://docs.nvidia.com/jetson/archives/r35.3.1/DeveloperGuide/text/HR/ControllingPerformance.html)
- [Jetson.GPIO Library](https://github.com/NVIDIA/jetson-gpio)
- [libgpiod Documentation](https://git.kernel.org/pub/scm/libs/libgpiod/libgpiod.git/about/)

### Datasheets
- Jetson Module Data Sheets (see platform-specific documentation)
- [Linux GPIO Subsystem](https://www.kernel.org/doc/html/latest/driver-api/gpio/index.html)

### Tools
- [Jetson-IO Configuration Tool](https://docs.nvidia.com/jetson/l4t/index.html#page/Tegra%20Linux%20Driver%20Package%20Development%20Guide/hw_setup_jetson_io.html)
- [tegra-pinmux-scripts](https://github.com/OE4T/tegra-pinmux-scripts)

---

**Document Version**: 1.0
**Last Updated**: 2025-11
**Maintained By**: Hardware Integration Agent
