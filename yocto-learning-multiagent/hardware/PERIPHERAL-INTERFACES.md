# Peripheral Interfaces Guide

## Overview

Comprehensive guide for interfacing with peripheral devices on NVIDIA Jetson platforms, covering I2C, SPI, UART, PWM, and PCIe devices with hardware details, device tree configuration, and software examples.

## I2C (Inter-Integrated Circuit)

### I2C Hardware

**Platform I2C Buses:**

| Platform | I2C Buses | Speed | 40-pin Header |
|----------|-----------|-------|---------------|
| AGX Orin | 8 | 100K/400K/1M | I2C1 (bus 7) |
| Orin NX/Nano | 8 | 100K/400K/1M | I2C1 (bus 7) |
| AGX Xavier | 8 | 100K/400K/1M | I2C1 (bus 7) |
| Xavier NX | 5 | 100K/400K/1M | I2C1 (bus 7) |
| Jetson Nano | 2 | 100K/400K | I2C1 (bus 0/1) |

### I2C Device Tree Configuration

**Basic I2C Device:**

```dts
&gen8_i2c {
    status = "okay";
    clock-frequency = <400000>;  /* 400 kHz */

    /* Example: Temperature sensor LM75 */
    lm75@48 {
        compatible = "national,lm75";
        reg = <0x48>;
        status = "okay";
    };

    /* Example: EEPROM */
    eeprom@50 {
        compatible = "atmel,24c32";
        reg = <0x50>;
        pagesize = <32>;
        status = "okay";
    };

    /* Example: RTC DS3231 */
    rtc@68 {
        compatible = "maxim,ds3231";
        reg = <0x68>;
        interrupt-parent = <&gpio>;
        interrupts = <GPIO_PIN IRQ_TYPE_EDGE_FALLING>;
        status = "okay";
    };
};
```

**I2C with GPIO Reset:**

```dts
&gen8_i2c {
    status = "okay";

    custom_device@20 {
        compatible = "vendor,custom-i2c-device";
        reg = <0x20>;
        reset-gpios = <&tegra_main_gpio TEGRA234_MAIN_GPIO(H, 5) GPIO_ACTIVE_LOW>;
        interrupt-parent = <&tegra_main_gpio>;
        interrupts = <TEGRA234_MAIN_GPIO(H, 6) IRQ_TYPE_EDGE_FALLING>;
        status = "okay";
    };
};
```

### I2C Detection and Access

**List I2C buses:**

```bash
# List I2C adapters
i2cdetect -l

# Output example:
# i2c-7   i2c             Tegra I2C adapter                       I2C adapter
# i2c-8   i2c             Tegra I2C adapter                       I2C adapter
```

**Scan I2C bus:**

```bash
# Scan bus 7 for devices
i2cdetect -y -r 7

# Output shows detected addresses:
#      0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
# 00:          -- -- -- -- -- -- -- -- -- -- -- -- --
# 10: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
# 20: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
# 30: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
# 40: -- -- -- -- -- -- -- -- 48 -- -- -- -- -- -- --
# 50: 50 -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
# 60: -- -- -- -- -- -- -- -- 68 -- -- -- -- -- -- --
# 70: -- -- -- -- -- -- -- --
```

**Read/Write I2C:**

```bash
# Read byte from register
i2cget -y 7 0x48 0x00

# Write byte to register
i2cset -y 7 0x48 0x01 0xFF

# Read block of data
i2cdump -y 7 0x50
```

### I2C Programming

**Python (smbus2):**

```python
#!/usr/bin/env python3
from smbus2 import SMBus

# Open I2C bus
bus = SMBus(7)

# Device address
DEVICE_ADDR = 0x48

try:
    # Read byte from register 0x00
    data = bus.read_byte_data(DEVICE_ADDR, 0x00)
    print(f"Read: 0x{data:02X}")

    # Write byte to register 0x01
    bus.write_byte_data(DEVICE_ADDR, 0x01, 0xFF)

    # Read block of data
    block = bus.read_i2c_block_data(DEVICE_ADDR, 0x00, 16)
    print(f"Block: {[hex(b) for b in block]}")

    # Write block of data
    data_block = [0x01, 0x02, 0x03, 0x04]
    bus.write_i2c_block_data(DEVICE_ADDR, 0x00, data_block)

finally:
    bus.close()
```

**C (libi2c):**

```c
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <linux/i2c-dev.h>

#define I2C_BUS "/dev/i2c-7"
#define DEVICE_ADDR 0x48

int main() {
    int file;
    int adapter_nr = 7;
    char filename[20];

    snprintf(filename, 19, "/dev/i2c-%d", adapter_nr);
    file = open(filename, O_RDWR);

    if (file < 0) {
        perror("Failed to open I2C bus");
        exit(1);
    }

    if (ioctl(file, I2C_SLAVE, DEVICE_ADDR) < 0) {
        perror("Failed to acquire bus access");
        exit(1);
    }

    // Write byte
    unsigned char reg = 0x01;
    unsigned char value = 0xFF;
    unsigned char buf[2] = {reg, value};

    if (write(file, buf, 2) != 2) {
        perror("Failed to write to the i2c bus");
        exit(1);
    }

    // Read byte
    buf[0] = 0x00;  // Register to read
    if (write(file, buf, 1) != 1) {
        perror("Failed to write register address");
        exit(1);
    }

    if (read(file, buf, 1) != 1) {
        perror("Failed to read from the i2c bus");
        exit(1);
    }

    printf("Read value: 0x%02X\n", buf[0]);

    close(file);
    return 0;
}
```

Compile:
```bash
gcc -o i2c_test i2c_test.c
```

### Common I2C Devices

**Temperature Sensors:**
- LM75 (Digital temperature sensor)
- TMP102 (Digital temperature sensor)
- BME280 (Temperature, humidity, pressure)

**EEPROM:**
- AT24C32/64/128/256 (I2C EEPROM)

**RTC:**
- DS3231 (Precision RTC)
- PCF8523 (RTC)

**ADC:**
- ADS1015/1115 (12/16-bit ADC)

**GPIO Expander:**
- PCA9685 (16-channel PWM)
- MCP23017 (16-bit GPIO expander)

## SPI (Serial Peripheral Interface)

### SPI Hardware

**Platform SPI Buses:**

| Platform | SPI Buses | Max Speed | 40-pin Header |
|----------|-----------|-----------|---------------|
| AGX Orin | 3 | 65 MHz | SPI0, SPI1 |
| Orin NX/Nano | 2 | 65 MHz | SPI0, SPI1 |
| AGX Xavier | 3 | 65 MHz | SPI0, SPI1 |
| Xavier NX | 2 | 65 MHz | SPI0, SPI1 |
| Jetson Nano | 2 | 65 MHz | SPI0, SPI1 |

**SPI Modes:**
- Mode 0: CPOL=0, CPHA=0
- Mode 1: CPOL=0, CPHA=1
- Mode 2: CPOL=1, CPHA=0
- Mode 3: CPOL=1, CPHA=1

### SPI Device Tree Configuration

**Basic SPI Device:**

```dts
&spi0 {
    status = "okay";
    spi-max-frequency = <10000000>;  /* 10 MHz */

    spidev@0 {
        compatible = "spidev";
        reg = <0>;
        spi-max-frequency = <10000000>;
        status = "okay";
    };
};

&spi1 {
    status = "okay";

    /* Example: SPI Flash */
    flash@0 {
        compatible = "jedec,spi-nor";
        reg = <0>;
        spi-max-frequency = <25000000>;
        m25p,fast-read;
        status = "okay";

        partitions {
            compatible = "fixed-partitions";
            #address-cells = <1>;
            #size-cells = <1>;

            partition@0 {
                label = "bootloader";
                reg = <0x000000 0x100000>;
                read-only;
            };

            partition@100000 {
                label = "data";
                reg = <0x100000 0xF00000>;
            };
        };
    };

    /* Example: SPI ADC */
    adc@1 {
        compatible = "microchip,mcp3008";
        reg = <1>;
        spi-max-frequency = <1000000>;
        spi-cpol;
        spi-cpha;
        status = "okay";
    };
};
```

### SPI Access

**Check SPI devices:**

```bash
# List SPI devices
ls -l /dev/spidev*

# Typical output:
# /dev/spidev0.0
# /dev/spidev1.0
```

**spidev test program:**

```bash
# Install spi-tools
apt-get install spi-tools

# Test SPI loopback (connect MOSI to MISO)
spi-config -d /dev/spidev0.0 -q
spi-pipe -d /dev/spidev0.0 -s 1000000 <<< "Hello SPI"
```

### SPI Programming

**Python (spidev):**

```python
#!/usr/bin/env python3
import spidev
import time

# Open SPI bus
spi = spidev.SpiDev()
spi.open(0, 0)  # Bus 0, Device 0

# Configure SPI
spi.max_speed_hz = 1000000  # 1 MHz
spi.mode = 0  # SPI mode 0
spi.bits_per_word = 8

try:
    # Transfer data
    tx_data = [0x01, 0x02, 0x03, 0x04]
    rx_data = spi.xfer2(tx_data)
    print(f"TX: {[hex(b) for b in tx_data]}")
    print(f"RX: {[hex(b) for b in rx_data]}")

    # Read MCP3008 ADC (channel 0)
    # MCP3008 protocol: [start bit, SGL/DIFF, D2, D1, D0, x, x, x]
    def read_adc(channel):
        if channel < 0 or channel > 7:
            return -1

        # Start bit (1) + single-ended (1) + channel
        cmd = [0x01, (0x08 + channel) << 4, 0x00]
        reply = spi.xfer2(cmd)

        # Parse 10-bit result
        value = ((reply[1] & 0x03) << 8) + reply[2]
        return value

    # Read all channels
    for ch in range(8):
        value = read_adc(ch)
        voltage = (value / 1023.0) * 3.3
        print(f"Channel {ch}: {value} ({voltage:.2f}V)")

finally:
    spi.close()
```

**C (spidev):**

```c
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <linux/spi/spidev.h>
#include <string.h>

#define SPI_DEVICE "/dev/spidev0.0"

int main() {
    int fd;
    unsigned char tx[4] = {0x01, 0x02, 0x03, 0x04};
    unsigned char rx[4] = {0};
    struct spi_ioc_transfer tr = {
        .tx_buf = (unsigned long)tx,
        .rx_buf = (unsigned long)rx,
        .len = 4,
        .speed_hz = 1000000,
        .bits_per_word = 8,
        .delay_usecs = 0,
    };

    // Open SPI device
    fd = open(SPI_DEVICE, O_RDWR);
    if (fd < 0) {
        perror("Failed to open SPI device");
        return 1;
    }

    // Set SPI mode
    unsigned char mode = SPI_MODE_0;
    if (ioctl(fd, SPI_IOC_WR_MODE, &mode) < 0) {
        perror("Failed to set SPI mode");
        return 1;
    }

    // Set bits per word
    unsigned char bits = 8;
    if (ioctl(fd, SPI_IOC_WR_BITS_PER_WORD, &bits) < 0) {
        perror("Failed to set bits per word");
        return 1;
    }

    // Set max speed
    unsigned int speed = 1000000;
    if (ioctl(fd, SPI_IOC_WR_MAX_SPEED_HZ, &speed) < 0) {
        perror("Failed to set max speed");
        return 1;
    }

    // Transfer data
    if (ioctl(fd, SPI_IOC_MESSAGE(1), &tr) < 0) {
        perror("Failed to transfer data");
        return 1;
    }

    printf("TX: ");
    for (int i = 0; i < 4; i++)
        printf("0x%02X ", tx[i]);
    printf("\n");

    printf("RX: ");
    for (int i = 0; i < 4; i++)
        printf("0x%02X ", rx[i]);
    printf("\n");

    close(fd);
    return 0;
}
```

### Common SPI Devices

**Flash Memory:**
- W25Q32/64/128 (SPI NOR Flash)
- AT25SF128A (SPI Flash)

**ADC:**
- MCP3008 (8-channel 10-bit ADC)
- MCP3204 (4-channel 12-bit ADC)

**DAC:**
- MCP4822 (2-channel 12-bit DAC)

**Display:**
- ILI9341 (TFT LCD controller)
- ST7735 (TFT LCD controller)

**Sensors:**
- MPU6050 (IMU - also supports I2C)
- BME280 (Environmental sensor - also supports I2C)

## UART (Universal Asynchronous Receiver/Transmitter)

### UART Hardware

**Platform UARTs:**

| Platform | UARTs | 40-pin Header |
|----------|-------|---------------|
| AGX Orin | 5 | UART1 (pins 8/10) |
| Orin NX/Nano | 3 | UART1 (pins 8/10) |
| AGX Xavier | 5 | UART1 (pins 8/10) |
| Xavier NX | 3 | UART1 (pins 8/10) |
| Jetson Nano | 2 | UART2 (pins 8/10) |

**Standard Baud Rates:**
- 9600, 19200, 38400, 57600, 115200, 230400, 460800, 921600, 1000000

### UART Device Tree Configuration

```dts
&uarta {
    compatible = "nvidia,tegra234-uart", "nvidia,tegra20-uart";
    status = "okay";
};

&uartb {
    status = "okay";
};

/* UART with hardware flow control */
&uartc {
    status = "okay";
    nvidia,enable-modem-interrupt;
};
```

### UART Access

**List UART devices:**

```bash
# List tty devices
ls -l /dev/ttyTHS*

# Typical UARTs:
# /dev/ttyTHS0 - Debug console (often reserved)
# /dev/ttyTHS1 - UART1 (40-pin header)
# /dev/ttyTHS2 - UART2
```

**Configure UART:**

```bash
# Set baud rate and parameters
stty -F /dev/ttyTHS1 115200 cs8 -cstopb -parenb

# Check settings
stty -F /dev/ttyTHS1 -a

# Disable flow control
stty -F /dev/ttyTHS1 -crtscts

# Enable flow control
stty -F /dev/ttyTHS1 crtscts
```

**Simple serial communication:**

```bash
# Send data
echo "Hello UART" > /dev/ttyTHS1

# Receive data
cat /dev/ttyTHS1

# Or use screen
screen /dev/ttyTHS1 115200

# Or use minicom
minicom -D /dev/ttyTHS1 -b 115200
```

### UART Programming

**Python (pyserial):**

```python
#!/usr/bin/env python3
import serial
import time

# Open serial port
ser = serial.Serial(
    port='/dev/ttyTHS1',
    baudrate=115200,
    bytesize=serial.EIGHTBITS,
    parity=serial.PARITY_NONE,
    stopbits=serial.STOPBITS_ONE,
    timeout=1,
    rtscts=False,  # Hardware flow control
    xonxoff=False  # Software flow control
)

try:
    # Write data
    ser.write(b'Hello UART\n')
    print(f"Sent: {ser.out_waiting} bytes")

    # Read data
    if ser.in_waiting > 0:
        data = ser.read(ser.in_waiting)
        print(f"Received: {data.decode('utf-8', errors='ignore')}")

    # Read line
    line = ser.readline()
    print(f"Line: {line.decode('utf-8', errors='ignore').strip()}")

    # Continuous read
    while True:
        if ser.in_waiting > 0:
            data = ser.read(ser.in_waiting)
            print(data.decode('utf-8', errors='ignore'), end='')

except KeyboardInterrupt:
    pass
finally:
    ser.close()
```

**C (termios):**

```c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <termios.h>

#define UART_DEVICE "/dev/ttyTHS1"

int main() {
    int fd;
    struct termios options;

    // Open UART
    fd = open(UART_DEVICE, O_RDWR | O_NOCTTY | O_NDELAY);
    if (fd < 0) {
        perror("Failed to open UART");
        return 1;
    }

    // Get current options
    tcgetattr(fd, &options);

    // Set baud rate
    cfsetispeed(&options, B115200);
    cfsetospeed(&options, B115200);

    // Set options
    options.c_cflag |= (CLOCAL | CREAD);    // Enable receiver, local mode
    options.c_cflag &= ~PARENB;             // No parity
    options.c_cflag &= ~CSTOPB;             // 1 stop bit
    options.c_cflag &= ~CSIZE;              // Clear data size bits
    options.c_cflag |= CS8;                 // 8 data bits
    options.c_cflag &= ~CRTSCTS;            // No hardware flow control
    options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);  // Raw mode
    options.c_iflag &= ~(IXON | IXOFF | IXANY);  // No software flow control
    options.c_oflag &= ~OPOST;              // Raw output

    // Apply options
    tcsetattr(fd, TCSANOW, &options);

    // Write data
    char tx_buffer[] = "Hello UART\n";
    int n = write(fd, tx_buffer, strlen(tx_buffer));
    printf("Sent %d bytes\n", n);

    // Read data
    char rx_buffer[256];
    n = read(fd, rx_buffer, sizeof(rx_buffer));
    if (n > 0) {
        rx_buffer[n] = '\0';
        printf("Received: %s\n", rx_buffer);
    }

    close(fd);
    return 0;
}
```

### UART Loopback Test

```bash
#!/bin/bash
# UART loopback test (connect TX to RX)

UART="/dev/ttyTHS1"

# Configure UART
stty -F $UART 115200 cs8 -cstopb -parenb raw

# Send and receive
echo "UART Loopback Test" > $UART &
sleep 0.1
timeout 1 cat $UART
```

## PWM (Pulse Width Modulation)

### PWM Hardware

**Platform PWM Channels:**

| Platform | PWM Channels | 40-pin Header |
|----------|--------------|---------------|
| AGX Orin | 8 | Pin 32, 33 |
| Orin NX/Nano | 4 | Pin 32, 33 |
| AGX Xavier | 8 | Pin 32, 33 |
| Xavier NX | 4 | Pin 32, 33 |
| Jetson Nano | 2 | Pin 32, 33 |

### PWM Device Tree Configuration

```dts
&pwm3 {
    status = "okay";
};

&pwm5 {
    status = "okay";
};

/ {
    pwm-fan {
        compatible = "pwm-fan";
        pwms = <&pwm3 0 45334>;
        cooling-levels = <0 64 128 192 255>;
        #cooling-cells = <2>;
    };
};
```

### PWM Access (sysfs)

```bash
# Export PWM channel
echo 0 > /sys/class/pwm/pwmchip0/export

# Set period (nanoseconds) - 1kHz = 1000000ns
echo 1000000 > /sys/class/pwm/pwmchip0/pwm0/period

# Set duty cycle (nanoseconds) - 50% = 500000ns
echo 500000 > /sys/class/pwm/pwmchip0/pwm0/duty_cycle

# Enable PWM
echo 1 > /sys/class/pwm/pwmchip0/pwm0/enable

# Disable PWM
echo 0 > /sys/class/pwm/pwmchip0/pwm0/enable

# Unexport
echo 0 > /sys/class/pwm/pwmchip0/unexport
```

### PWM Programming

**Python:**

```python
#!/usr/bin/env python3
import time

class PWM:
    def __init__(self, chip=0, channel=0):
        self.chip = chip
        self.channel = channel
        self.base_path = f"/sys/class/pwm/pwmchip{chip}"
        self.pwm_path = f"{self.base_path}/pwm{channel}"

        # Export PWM
        with open(f"{self.base_path}/export", 'w') as f:
            f.write(str(channel))

        time.sleep(0.1)

    def set_frequency(self, freq_hz):
        period_ns = int(1e9 / freq_hz)
        with open(f"{self.pwm_path}/period", 'w') as f:
            f.write(str(period_ns))

    def set_duty_cycle(self, duty_percent):
        with open(f"{self.pwm_path}/period", 'r') as f:
            period_ns = int(f.read())

        duty_ns = int(period_ns * duty_percent / 100)
        with open(f"{self.pwm_path}/duty_cycle", 'w') as f:
            f.write(str(duty_ns))

    def enable(self):
        with open(f"{self.pwm_path}/enable", 'w') as f:
            f.write('1')

    def disable(self):
        with open(f"{self.pwm_path}/enable", 'w') as f:
            f.write('0')

    def close(self):
        self.disable()
        with open(f"{self.base_path}/unexport", 'w') as f:
            f.write(str(self.channel))

# Example: LED brightness control
pwm = PWM(chip=0, channel=0)
pwm.set_frequency(1000)  # 1kHz

try:
    # Fade in
    for duty in range(0, 101, 5):
        pwm.set_duty_cycle(duty)
        pwm.enable()
        time.sleep(0.05)

    # Fade out
    for duty in range(100, -1, -5):
        pwm.set_duty_cycle(duty)
        time.sleep(0.05)

finally:
    pwm.close()
```

**Servo Control Example:**

```python
#!/usr/bin/env python3
import time

class Servo:
    def __init__(self, chip=0, channel=0):
        self.pwm = PWM(chip, channel)
        self.pwm.set_frequency(50)  # 50Hz for servo
        self.pwm.enable()

    def set_angle(self, angle):
        # Servo: 1ms (0°) to 2ms (180°) pulse width
        # At 50Hz (20ms period):
        # 0°   = 5% duty cycle (1ms)
        # 90°  = 7.5% duty cycle (1.5ms)
        # 180° = 10% duty cycle (2ms)

        if angle < 0:
            angle = 0
        elif angle > 180:
            angle = 180

        duty = 5 + (angle / 180) * 5
        self.pwm.set_duty_cycle(duty)

    def close(self):
        self.pwm.close()

# Example
servo = Servo(0, 0)

try:
    # Sweep servo
    for angle in range(0, 181, 10):
        servo.set_angle(angle)
        time.sleep(0.1)

    for angle in range(180, -1, -10):
        servo.set_angle(angle)
        time.sleep(0.1)

finally:
    servo.close()
```

## PCIe Devices

### PCIe Hardware

**Platform PCIe:**

| Platform | PCIe Slots | Version | Lanes |
|----------|------------|---------|-------|
| AGX Orin | x8 + x4 | Gen 4 | 12 total |
| Orin NX | x4 (M.2) | Gen 4 | 4 |
| Orin Nano | x4 (M.2) | Gen 3 | 4 |
| AGX Xavier | x8 | Gen 4 | 8 |
| Xavier NX | x4 (M.2) | Gen 4 | 4 |

### PCIe Detection

```bash
# List PCIe devices
lspci

# Verbose output
lspci -v

# Very verbose (full details)
lspci -vvv

# Tree view
lspci -tv

# Specific device details
lspci -s 01:00.0 -vvv

# Check link speed and width
lspci -vv | grep -E "LnkCap|LnkSta"
```

### PCIe Device Tree

```dts
pcie@14160000 {
    compatible = "nvidia,tegra234-pcie";
    device_type = "pci";
    power-domains = <&bpmp TEGRA234_POWER_DOMAIN_PCIEX4BB>;
    reg = <0x00 0x14160000 0x0 0x00020000>,
          <0x00 0x36000000 0x0 0x00040000>,
          <0x00 0x36040000 0x0 0x00040000>,
          <0x00 0x36080000 0x0 0x00040000>;
    reg-names = "appl", "config", "atu_dma", "dbi";

    status = "okay";

    num-lanes = <4>;
    phys = <&p2u_nvhs_0>, <&p2u_nvhs_1>,
           <&p2u_nvhs_2>, <&p2u_nvhs_3>;
    phy-names = "p2u-0", "p2u-1", "p2u-2", "p2u-3";

    nvidia,max-speed = <4>;  /* PCIe Gen 4 */
    nvidia,disable-aspm-states = <0xf>;
    nvidia,enable-power-down;
};
```

### PCIe Benchmarking

**NVMe Benchmark (see STORAGE.md)**

**Network Card Benchmark:**

```bash
# Install iperf3
apt-get install iperf3

# Test network PCIe card
iperf3 -c server_ip -t 60
```

**GPU Benchmark (if applicable):**

```bash
# CUDA bandwidth test
/usr/local/cuda/samples/1_Utilities/bandwidthTest/bandwidthTest

# PCIe bandwidth
/usr/local/cuda/samples/1_Utilities/p2pBandwidthLatencyTest/p2pBandwidthLatencyTest
```

## Testing Procedures

### Peripheral Test Suite

```bash
#!/bin/bash

echo "=== Jetson Peripheral Test ==="

# I2C Test
echo -e "\n=== I2C Test ==="
i2cdetect -l
echo "Scanning I2C bus 7:"
i2cdetect -y -r 7

# SPI Test
echo -e "\n=== SPI Test ==="
ls -l /dev/spidev*

# UART Test
echo -e "\n=== UART Test ==="
ls -l /dev/ttyTHS*
stty -F /dev/ttyTHS1 -a

# PWM Test
echo -e "\n=== PWM Test ==="
ls -l /sys/class/pwm/

# PCIe Test
echo -e "\n=== PCIe Test ==="
lspci -tv

echo -e "\n=== Test Complete ==="
```

## Yocto Integration

### Peripheral Support Recipe

```bitbake
# File: recipes-core/images/jetson-peripheral-image.bb

IMAGE_INSTALL_append = " \
    i2c-tools \
    spi-tools \
    minicom \
    screen \
    python3-smbus2 \
    python3-spidev \
    python3-pyserial \
    pciutils \
    usbutils \
"
```

### Device Tree Recipe

```bitbake
# File: recipes-kernel/linux/linux-tegra_%.bbappend

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://custom-peripherals.dts \
"

do_configure_prepend() {
    # Include custom device tree
    cat ${WORKDIR}/custom-peripherals.dts >> \
        ${S}/arch/arm64/boot/dts/nvidia/tegra234-p3767-custom.dts
}
```

## References

### Official Documentation
- [Jetson Linux Developer Guide - Peripherals](https://docs.nvidia.com/jetson/archives/r35.3.1/DeveloperGuide/)
- [Linux I2C Documentation](https://www.kernel.org/doc/html/latest/i2c/index.html)
- [Linux SPI Documentation](https://www.kernel.org/doc/html/latest/spi/index.html)

### Tools and Libraries
- [i2c-tools](https://i2c.wiki.kernel.org/index.php/I2C_Tools)
- [spi-tools](https://github.com/cpb-/spi-tools)
- [pyserial](https://pyserial.readthedocs.io/)

---

**Document Version**: 1.0
**Last Updated**: 2025-11
**Maintained By**: Hardware Integration Agent
