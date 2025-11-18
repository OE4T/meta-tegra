/*
 * I2C Device Reader - Userspace I2C Communication
 *
 * This application demonstrates:
 * - Opening I2C device from userspace
 * - I2C read/write operations
 * - SMBus protocol functions
 * - Device scanning and detection
 * - Error handling
 *
 * Example devices:
 * - BME280: Temperature, Humidity, Pressure sensor
 * - MPU6050: 6-axis IMU
 * - ADS1015: 12-bit ADC
 *
 * Compiled for: NVIDIA Jetson platforms
 * Kernel: 5.10+
 *
 * Compile:
 *   gcc -Wall -O2 -o i2c_reader i2c_reader.c
 *
 * Usage:
 *   ./i2c_reader <i2c_bus> <command> [args]
 *
 * Dependencies:
 *   - i2c-tools package (for i2c-dev.h)
 *   - Kernel CONFIG_I2C_CHARDEV=y
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <linux/i2c.h>
#include <linux/i2c-dev.h>

#define MAX_BUF 256

/**
 * Open I2C device
 */
int i2c_open(int bus)
{
    char dev_path[20];
    int fd;

    snprintf(dev_path, sizeof(dev_path), "/dev/i2c-%d", bus);

    fd = open(dev_path, O_RDWR);
    if (fd < 0) {
        perror("Failed to open I2C device");
        return -1;
    }

    return fd;
}

/**
 * Set I2C slave address
 */
int i2c_set_address(int fd, uint8_t addr)
{
    if (ioctl(fd, I2C_SLAVE, addr) < 0) {
        perror("Failed to set I2C slave address");
        return -1;
    }
    return 0;
}

/**
 * Write single byte to I2C device
 */
int i2c_write_byte(int fd, uint8_t data)
{
    if (write(fd, &data, 1) != 1) {
        perror("Failed to write byte to I2C device");
        return -1;
    }
    return 0;
}

/**
 * Read bytes from I2C device
 */
int i2c_read_bytes(int fd, uint8_t *buf, int len)
{
    if (read(fd, buf, len) != len) {
        perror("Failed to read from I2C device");
        return -1;
    }
    return 0;
}

/**
 * Write byte to register
 */
int i2c_write_register(int fd, uint8_t reg, uint8_t value)
{
    uint8_t buf[2];
    buf[0] = reg;
    buf[1] = value;

    if (write(fd, buf, 2) != 2) {
        perror("Failed to write to I2C register");
        return -1;
    }
    return 0;
}

/**
 * Read byte from register
 */
int i2c_read_register(int fd, uint8_t reg, uint8_t *value)
{
    if (write(fd, &reg, 1) != 1) {
        perror("Failed to write register address");
        return -1;
    }

    if (read(fd, value, 1) != 1) {
        perror("Failed to read from register");
        return -1;
    }

    return 0;
}

/**
 * Read multiple bytes from register
 */
int i2c_read_block(int fd, uint8_t reg, uint8_t *buf, int len)
{
    struct i2c_msg msgs[2];
    struct i2c_rdwr_ioctl_data msgset;

    /* Write register address */
    msgs[0].addr = 0;  /* Will be set by ioctl */
    msgs[0].flags = 0;
    msgs[0].len = 1;
    msgs[0].buf = &reg;

    /* Read data */
    msgs[1].addr = 0;
    msgs[1].flags = I2C_M_RD;
    msgs[1].len = len;
    msgs[1].buf = buf;

    msgset.msgs = msgs;
    msgset.nmsgs = 2;

    if (ioctl(fd, I2C_RDWR, &msgset) < 0) {
        perror("Failed to read block from I2C device");
        return -1;
    }

    return 0;
}

/**
 * Scan I2C bus for devices
 */
void i2c_scan_bus(int fd)
{
    uint8_t addr;
    int result;

    printf("Scanning I2C bus...\n");
    printf("     0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f\n");

    for (addr = 0; addr < 128; addr++) {
        if (addr % 16 == 0)
            printf("%02x: ", addr);

        /* Try to set address and read */
        if (ioctl(fd, I2C_SLAVE, addr) < 0) {
            printf("   ");
        } else {
            result = i2c_smbus_read_byte(fd);
            if (result >= 0)
                printf("%02x ", addr);
            else
                printf("-- ");
        }

        if (addr % 16 == 15)
            printf("\n");
    }

    printf("\n");
}

/**
 * Read BME280 sensor
 */
void read_bme280(int fd, uint8_t addr)
{
    uint8_t chip_id, status;
    uint8_t data[8];
    int32_t temp_raw, press_raw, hum_raw;
    float temperature, pressure, humidity;

    if (i2c_set_address(fd, addr) < 0)
        return;

    /* Read chip ID */
    if (i2c_read_register(fd, 0xD0, &chip_id) < 0)
        return;

    printf("BME280 Chip ID: 0x%02X (expected 0x60)\n", chip_id);

    if (chip_id != 0x60) {
        fprintf(stderr, "Error: Invalid chip ID\n");
        return;
    }

    /* Read status */
    if (i2c_read_register(fd, 0xF3, &status) < 0)
        return;

    printf("Status: 0x%02X\n", status);

    /* Configure sensor for forced mode */
    i2c_write_register(fd, 0xF2, 0x01);  /* Humidity oversampling x1 */
    i2c_write_register(fd, 0xF4, 0x25);  /* Temp/Press oversampling x1, forced mode */

    usleep(100000);  /* Wait for measurement */

    /* Read raw data */
    if (i2c_read_block(fd, 0xF7, data, 8) < 0)
        return;

    /* Parse raw values */
    press_raw = (data[0] << 12) | (data[1] << 4) | (data[2] >> 4);
    temp_raw = (data[3] << 12) | (data[4] << 4) | (data[5] >> 4);
    hum_raw = (data[6] << 8) | data[7];

    printf("Raw values: Temp=%d, Press=%d, Hum=%d\n",
           temp_raw, press_raw, hum_raw);

    /* Simplified conversion (actual BME280 needs calibration data) */
    temperature = (float)temp_raw / 5120.0;
    pressure = (float)press_raw / 256.0;
    humidity = (float)hum_raw / 1024.0;

    printf("Approximate values:\n");
    printf("  Temperature: %.2f °C\n", temperature);
    printf("  Pressure: %.2f hPa\n", pressure);
    printf("  Humidity: %.2f %%\n", humidity);

    printf("\nNote: For accurate readings, implement proper calibration\n");
}

/**
 * Read MPU6050 sensor
 */
void read_mpu6050(int fd, uint8_t addr)
{
    uint8_t who_am_i;
    uint8_t data[14];
    int16_t ax, ay, az, temp, gx, gy, gz;

    if (i2c_set_address(fd, addr) < 0)
        return;

    /* Read WHO_AM_I register */
    if (i2c_read_register(fd, 0x75, &who_am_i) < 0)
        return;

    printf("MPU6050 WHO_AM_I: 0x%02X (expected 0x68)\n", who_am_i);

    if (who_am_i != 0x68) {
        fprintf(stderr, "Error: Invalid WHO_AM_I\n");
        return;
    }

    /* Wake up sensor */
    i2c_write_register(fd, 0x6B, 0x00);
    usleep(100000);

    /* Read all sensor data */
    if (i2c_read_block(fd, 0x3B, data, 14) < 0)
        return;

    /* Parse sensor data */
    ax = (data[0] << 8) | data[1];
    ay = (data[2] << 8) | data[3];
    az = (data[4] << 8) | data[5];
    temp = (data[6] << 8) | data[7];
    gx = (data[8] << 8) | data[9];
    gy = (data[10] << 8) | data[11];
    gz = (data[12] << 8) | data[13];

    printf("\nMPU6050 Sensor Data:\n");
    printf("  Accelerometer: X=%6d Y=%6d Z=%6d\n", ax, ay, az);
    printf("  Gyroscope:     X=%6d Y=%6d Z=%6d\n", gx, gy, gz);
    printf("  Temperature:   %d (%.2f °C)\n", temp, temp/340.0 + 36.53);
}

/**
 * Read ADS1015 ADC
 */
void read_ads1015(int fd, uint8_t addr, int channel)
{
    uint16_t config, value;
    int16_t adc_value;
    float voltage;

    if (i2c_set_address(fd, addr) < 0)
        return;

    /* Configure ADC:
     * - Single-shot mode
     * - Channel 0
     * - ±4.096V range
     * - 1600 samples/sec
     */
    config = 0xC000 |           /* Start single conversion */
             (channel << 12) |  /* Channel select */
             0x0200 |           /* ±4.096V range */
             0x0080 |           /* Single-shot mode */
             0x0060;            /* 1600 SPS */

    /* Write config register */
    uint8_t buf[3];
    buf[0] = 0x01;  /* Config register */
    buf[1] = (config >> 8) & 0xFF;
    buf[2] = config & 0xFF;

    if (write(fd, buf, 3) != 3) {
        perror("Failed to write ADS1015 config");
        return;
    }

    usleep(10000);  /* Wait for conversion */

    /* Read conversion result */
    uint8_t reg = 0x00;  /* Conversion register */
    if (write(fd, &reg, 1) != 1) {
        perror("Failed to write register address");
        return;
    }

    if (read(fd, buf, 2) != 2) {
        perror("Failed to read conversion result");
        return;
    }

    value = (buf[0] << 8) | buf[1];
    adc_value = (int16_t)(value >> 4);  /* 12-bit value, right-aligned */
    voltage = (float)adc_value * 4.096 / 2048.0;

    printf("ADS1015 Channel %d:\n", channel);
    printf("  Raw value: %d (0x%04X)\n", adc_value, value);
    printf("  Voltage: %.3f V\n", voltage);
}

/**
 * Print usage information
 */
void print_usage(const char *prog_name)
{
    printf("Usage: %s <i2c_bus> <command> [args]\n\n", prog_name);
    printf("Commands:\n");
    printf("  scan                      - Scan I2C bus for devices\n");
    printf("  read <addr> <reg>         - Read byte from register\n");
    printf("  write <addr> <reg> <val>  - Write byte to register\n");
    printf("  dump <addr> <reg> <len>   - Dump registers\n");
    printf("  bme280 <addr>             - Read BME280 sensor\n");
    printf("  mpu6050 <addr>            - Read MPU6050 sensor\n");
    printf("  ads1015 <addr> <ch>       - Read ADS1015 ADC channel\n");
    printf("\nExamples:\n");
    printf("  %s 1 scan\n", prog_name);
    printf("  %s 1 read 0x76 0xD0\n", prog_name);
    printf("  %s 1 write 0x76 0xF4 0x27\n", prog_name);
    printf("  %s 1 dump 0x76 0xF0 16\n", prog_name);
    printf("  %s 1 bme280 0x76\n", prog_name);
    printf("  %s 1 mpu6050 0x68\n", prog_name);
    printf("  %s 1 ads1015 0x48 0\n", prog_name);
    printf("\nNote: Run with sudo if permission denied\n");
}

/**
 * Main function
 */
int main(int argc, char *argv[])
{
    int fd, bus;
    uint8_t addr, reg, value;
    int len, channel;

    if (argc < 3) {
        print_usage(argv[0]);
        return 1;
    }

    /* Parse I2C bus number */
    bus = atoi(argv[1]);

    /* Open I2C device */
    fd = i2c_open(bus);
    if (fd < 0)
        return 1;

    /* Handle commands */
    if (strcmp(argv[2], "scan") == 0) {
        i2c_scan_bus(fd);

    } else if (strcmp(argv[2], "read") == 0) {
        if (argc < 5) {
            fprintf(stderr, "Error: read requires address and register\n");
            close(fd);
            return 1;
        }
        addr = strtol(argv[3], NULL, 0);
        reg = strtol(argv[4], NULL, 0);

        if (i2c_set_address(fd, addr) < 0 ||
            i2c_read_register(fd, reg, &value) < 0) {
            close(fd);
            return 1;
        }

        printf("Read from 0x%02X reg 0x%02X: 0x%02X (%d)\n",
               addr, reg, value, value);

    } else if (strcmp(argv[2], "write") == 0) {
        if (argc < 6) {
            fprintf(stderr, "Error: write requires address, register, and value\n");
            close(fd);
            return 1;
        }
        addr = strtol(argv[3], NULL, 0);
        reg = strtol(argv[4], NULL, 0);
        value = strtol(argv[5], NULL, 0);

        if (i2c_set_address(fd, addr) < 0 ||
            i2c_write_register(fd, reg, value) < 0) {
            close(fd);
            return 1;
        }

        printf("Wrote 0x%02X to 0x%02X reg 0x%02X\n", value, addr, reg);

    } else if (strcmp(argv[2], "dump") == 0) {
        if (argc < 6) {
            fprintf(stderr, "Error: dump requires address, start register, and length\n");
            close(fd);
            return 1;
        }
        addr = strtol(argv[3], NULL, 0);
        reg = strtol(argv[4], NULL, 0);
        len = atoi(argv[5]);

        if (i2c_set_address(fd, addr) < 0) {
            close(fd);
            return 1;
        }

        printf("Dumping %d bytes from 0x%02X starting at reg 0x%02X:\n",
               len, addr, reg);

        for (int i = 0; i < len; i++) {
            if (i2c_read_register(fd, reg + i, &value) < 0) {
                close(fd);
                return 1;
            }
            if (i % 16 == 0)
                printf("0x%02X: ", reg + i);
            printf("%02X ", value);
            if (i % 16 == 15)
                printf("\n");
        }
        printf("\n");

    } else if (strcmp(argv[2], "bme280") == 0) {
        if (argc < 4) {
            fprintf(stderr, "Error: bme280 requires address\n");
            close(fd);
            return 1;
        }
        addr = strtol(argv[3], NULL, 0);
        read_bme280(fd, addr);

    } else if (strcmp(argv[2], "mpu6050") == 0) {
        if (argc < 4) {
            fprintf(stderr, "Error: mpu6050 requires address\n");
            close(fd);
            return 1;
        }
        addr = strtol(argv[3], NULL, 0);
        read_mpu6050(fd, addr);

    } else if (strcmp(argv[2], "ads1015") == 0) {
        if (argc < 5) {
            fprintf(stderr, "Error: ads1015 requires address and channel\n");
            close(fd);
            return 1;
        }
        addr = strtol(argv[3], NULL, 0);
        channel = atoi(argv[4]);
        read_ads1015(fd, addr, channel);

    } else {
        fprintf(stderr, "Error: unknown command '%s'\n", argv[2]);
        print_usage(argv[0]);
        close(fd);
        return 1;
    }

    close(fd);
    return 0;
}

/*
 * Additional Notes:
 * =================
 *
 * 1. Install i2c-tools:
 *    sudo apt-get install i2c-tools libi2c-dev
 *
 * 2. Find I2C buses:
 *    ls /dev/i2c-*
 *    i2cdetect -l
 *
 * 3. Scan bus:
 *    sudo i2cdetect -y -r 1
 *
 * 4. Enable I2C:
 *    # Check kernel config
 *    grep I2C /boot/config-$(uname -r)
 *
 * 5. Permissions:
 *    sudo usermod -a -G i2c $USER
 *    # Or create udev rule:
 *    echo 'KERNEL=="i2c-[0-9]*", GROUP="i2c"' | sudo tee /etc/udev/rules.d/99-i2c.rules
 *
 * 6. Python alternative:
 *    sudo apt-get install python3-smbus
 *    import smbus
 *    bus = smbus.SMBus(1)
 *    data = bus.read_byte_data(0x76, 0xD0)
 */
