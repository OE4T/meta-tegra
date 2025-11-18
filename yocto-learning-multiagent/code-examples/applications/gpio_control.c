/*
 * GPIO Control Application using Sysfs Interface
 *
 * This application demonstrates:
 * - GPIO export/unexport via sysfs
 * - GPIO direction configuration
 * - GPIO value read/write
 * - GPIO edge detection
 * - Poll-based interrupt handling
 * - Error handling and cleanup
 *
 * Compiled for: NVIDIA Jetson platforms
 * Kernel: 5.10+
 *
 * Compile:
 *   gcc -Wall -O2 -o gpio_control gpio_control.c
 *
 * Usage:
 *   ./gpio_control <gpio_number> <command> [value]
 *   Commands: export, unexport, direction, read, write, poll
 *
 * Examples:
 *   ./gpio_control 12 export
 *   ./gpio_control 12 direction out
 *   ./gpio_control 12 write 1
 *   ./gpio_control 12 read
 *   ./gpio_control 12 poll rising
 *   ./gpio_control 12 unexport
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <poll.h>
#include <sys/stat.h>

#define SYSFS_GPIO_PATH "/sys/class/gpio"
#define MAX_BUF 64

/* GPIO direction */
typedef enum {
    GPIO_DIR_IN,
    GPIO_DIR_OUT
} gpio_direction_t;

/* GPIO edge */
typedef enum {
    GPIO_EDGE_NONE,
    GPIO_EDGE_RISING,
    GPIO_EDGE_FALLING,
    GPIO_EDGE_BOTH
} gpio_edge_t;

/**
 * Export a GPIO pin via sysfs
 * Makes the GPIO available in /sys/class/gpio/gpioN/
 */
int gpio_export(unsigned int gpio)
{
    int fd, len;
    char buf[MAX_BUF];

    fd = open(SYSFS_GPIO_PATH "/export", O_WRONLY);
    if (fd < 0) {
        perror("Failed to open GPIO export");
        return -1;
    }

    len = snprintf(buf, sizeof(buf), "%d", gpio);
    if (write(fd, buf, len) < 0) {
        if (errno != EBUSY) {  /* Already exported */
            perror("Failed to export GPIO");
            close(fd);
            return -1;
        }
    }

    close(fd);
    printf("GPIO %d exported successfully\n", gpio);
    return 0;
}

/**
 * Unexport a GPIO pin via sysfs
 * Removes the GPIO from /sys/class/gpio/
 */
int gpio_unexport(unsigned int gpio)
{
    int fd, len;
    char buf[MAX_BUF];

    fd = open(SYSFS_GPIO_PATH "/unexport", O_WRONLY);
    if (fd < 0) {
        perror("Failed to open GPIO unexport");
        return -1;
    }

    len = snprintf(buf, sizeof(buf), "%d", gpio);
    if (write(fd, buf, len) < 0) {
        perror("Failed to unexport GPIO");
        close(fd);
        return -1;
    }

    close(fd);
    printf("GPIO %d unexported successfully\n", gpio);
    return 0;
}

/**
 * Set GPIO direction (input or output)
 */
int gpio_set_direction(unsigned int gpio, gpio_direction_t dir)
{
    int fd;
    char path[MAX_BUF];
    const char *dir_str = (dir == GPIO_DIR_IN) ? "in" : "out";

    snprintf(path, sizeof(path), SYSFS_GPIO_PATH "/gpio%d/direction", gpio);

    fd = open(path, O_WRONLY);
    if (fd < 0) {
        perror("Failed to open GPIO direction");
        return -1;
    }

    if (write(fd, dir_str, strlen(dir_str)) < 0) {
        perror("Failed to set GPIO direction");
        close(fd);
        return -1;
    }

    close(fd);
    printf("GPIO %d direction set to %s\n", gpio, dir_str);
    return 0;
}

/**
 * Read GPIO value (0 or 1)
 */
int gpio_read_value(unsigned int gpio, int *value)
{
    int fd;
    char path[MAX_BUF];
    char buf[3];

    snprintf(path, sizeof(path), SYSFS_GPIO_PATH "/gpio%d/value", gpio);

    fd = open(path, O_RDONLY);
    if (fd < 0) {
        perror("Failed to open GPIO value for reading");
        return -1;
    }

    if (read(fd, buf, sizeof(buf)) < 0) {
        perror("Failed to read GPIO value");
        close(fd);
        return -1;
    }

    *value = (buf[0] == '1') ? 1 : 0;
    close(fd);
    return 0;
}

/**
 * Write GPIO value (0 or 1)
 */
int gpio_write_value(unsigned int gpio, int value)
{
    int fd;
    char path[MAX_BUF];
    const char *val_str = (value) ? "1" : "0";

    snprintf(path, sizeof(path), SYSFS_GPIO_PATH "/gpio%d/value", gpio);

    fd = open(path, O_WRONLY);
    if (fd < 0) {
        perror("Failed to open GPIO value for writing");
        return -1;
    }

    if (write(fd, val_str, 1) < 0) {
        perror("Failed to write GPIO value");
        close(fd);
        return -1;
    }

    close(fd);
    printf("GPIO %d value set to %d\n", gpio, value);
    return 0;
}

/**
 * Set GPIO edge detection
 */
int gpio_set_edge(unsigned int gpio, gpio_edge_t edge)
{
    int fd;
    char path[MAX_BUF];
    const char *edge_str;

    switch (edge) {
        case GPIO_EDGE_NONE:    edge_str = "none"; break;
        case GPIO_EDGE_RISING:  edge_str = "rising"; break;
        case GPIO_EDGE_FALLING: edge_str = "falling"; break;
        case GPIO_EDGE_BOTH:    edge_str = "both"; break;
        default:
            fprintf(stderr, "Invalid edge type\n");
            return -1;
    }

    snprintf(path, sizeof(path), SYSFS_GPIO_PATH "/gpio%d/edge", gpio);

    fd = open(path, O_WRONLY);
    if (fd < 0) {
        perror("Failed to open GPIO edge");
        return -1;
    }

    if (write(fd, edge_str, strlen(edge_str)) < 0) {
        perror("Failed to set GPIO edge");
        close(fd);
        return -1;
    }

    close(fd);
    printf("GPIO %d edge set to %s\n", gpio, edge_str);
    return 0;
}

/**
 * Poll GPIO for edge events
 * Waits for interrupt on GPIO pin
 */
int gpio_poll(unsigned int gpio, gpio_edge_t edge, int timeout_ms)
{
    int fd, ret;
    char path[MAX_BUF];
    char buf[3];
    struct pollfd pfd;

    /* Set edge detection */
    if (gpio_set_edge(gpio, edge) < 0)
        return -1;

    /* Open value file */
    snprintf(path, sizeof(path), SYSFS_GPIO_PATH "/gpio%d/value", gpio);
    fd = open(path, O_RDONLY);
    if (fd < 0) {
        perror("Failed to open GPIO value for polling");
        return -1;
    }

    /* Initial read to clear any pending interrupts */
    if (read(fd, buf, sizeof(buf)) < 0) {
        perror("Failed to read GPIO value");
        close(fd);
        return -1;
    }

    /* Setup poll */
    pfd.fd = fd;
    pfd.events = POLLPRI | POLLERR;

    printf("Waiting for GPIO %d edge event (timeout: %d ms)...\n",
           gpio, timeout_ms);

    /* Poll for events */
    ret = poll(&pfd, 1, timeout_ms);

    if (ret < 0) {
        perror("Poll failed");
        close(fd);
        return -1;
    } else if (ret == 0) {
        printf("Poll timeout\n");
        close(fd);
        return 0;
    }

    /* Event occurred - read value */
    lseek(fd, 0, SEEK_SET);
    if (read(fd, buf, sizeof(buf)) < 0) {
        perror("Failed to read GPIO value after event");
        close(fd);
        return -1;
    }

    printf("GPIO %d edge event detected! Value: %c\n", gpio, buf[0]);

    close(fd);
    return 1;
}

/**
 * Check if GPIO is already exported
 */
int gpio_is_exported(unsigned int gpio)
{
    char path[MAX_BUF];
    struct stat st;

    snprintf(path, sizeof(path), SYSFS_GPIO_PATH "/gpio%d", gpio);
    return (stat(path, &st) == 0);
}

/**
 * Print usage information
 */
void print_usage(const char *prog_name)
{
    printf("Usage: %s <gpio_number> <command> [value]\n\n", prog_name);
    printf("Commands:\n");
    printf("  export              - Export GPIO pin\n");
    printf("  unexport            - Unexport GPIO pin\n");
    printf("  direction <in|out>  - Set GPIO direction\n");
    printf("  read                - Read GPIO value\n");
    printf("  write <0|1>         - Write GPIO value\n");
    printf("  poll <edge> [ms]    - Poll for edge events\n");
    printf("                        edge: none, rising, falling, both\n");
    printf("                        ms: timeout in milliseconds (default: 5000)\n");
    printf("  blink <count> <ms>  - Blink LED count times with ms delay\n");
    printf("\nExamples:\n");
    printf("  %s 12 export\n", prog_name);
    printf("  %s 12 direction out\n", prog_name);
    printf("  %s 12 write 1\n", prog_name);
    printf("  %s 12 read\n", prog_name);
    printf("  %s 12 poll rising 10000\n", prog_name);
    printf("  %s 12 blink 10 500\n", prog_name);
    printf("  %s 12 unexport\n", prog_name);
}

/**
 * Blink LED demonstration
 */
int gpio_blink(unsigned int gpio, int count, int delay_ms)
{
    int i;

    printf("Blinking GPIO %d %d times (delay: %d ms)\n",
           gpio, count, delay_ms);

    for (i = 0; i < count; i++) {
        /* Turn on */
        if (gpio_write_value(gpio, 1) < 0)
            return -1;

        usleep(delay_ms * 1000);

        /* Turn off */
        if (gpio_write_value(gpio, 0) < 0)
            return -1;

        usleep(delay_ms * 1000);

        printf("Blink %d/%d\n", i + 1, count);
    }

    return 0;
}

/**
 * Main function
 */
int main(int argc, char *argv[])
{
    unsigned int gpio;
    int value;
    gpio_edge_t edge;
    int timeout_ms;

    if (argc < 3) {
        print_usage(argv[0]);
        return 1;
    }

    /* Parse GPIO number */
    gpio = atoi(argv[1]);

    /* Handle commands */
    if (strcmp(argv[2], "export") == 0) {
        return (gpio_export(gpio) < 0) ? 1 : 0;

    } else if (strcmp(argv[2], "unexport") == 0) {
        return (gpio_unexport(gpio) < 0) ? 1 : 0;

    } else if (strcmp(argv[2], "direction") == 0) {
        if (argc < 4) {
            fprintf(stderr, "Error: direction requires argument (in/out)\n");
            return 1;
        }
        gpio_direction_t dir = (strcmp(argv[3], "in") == 0) ?
                               GPIO_DIR_IN : GPIO_DIR_OUT;
        return (gpio_set_direction(gpio, dir) < 0) ? 1 : 0;

    } else if (strcmp(argv[2], "read") == 0) {
        if (gpio_read_value(gpio, &value) < 0)
            return 1;
        printf("GPIO %d value: %d\n", gpio, value);
        return 0;

    } else if (strcmp(argv[2], "write") == 0) {
        if (argc < 4) {
            fprintf(stderr, "Error: write requires value argument (0/1)\n");
            return 1;
        }
        value = atoi(argv[3]);
        return (gpio_write_value(gpio, value) < 0) ? 1 : 0;

    } else if (strcmp(argv[2], "poll") == 0) {
        if (argc < 4) {
            fprintf(stderr, "Error: poll requires edge argument\n");
            return 1;
        }

        /* Parse edge */
        if (strcmp(argv[3], "rising") == 0)
            edge = GPIO_EDGE_RISING;
        else if (strcmp(argv[3], "falling") == 0)
            edge = GPIO_EDGE_FALLING;
        else if (strcmp(argv[3], "both") == 0)
            edge = GPIO_EDGE_BOTH;
        else if (strcmp(argv[3], "none") == 0)
            edge = GPIO_EDGE_NONE;
        else {
            fprintf(stderr, "Error: invalid edge type\n");
            return 1;
        }

        /* Parse timeout (optional) */
        timeout_ms = (argc >= 5) ? atoi(argv[4]) : 5000;

        return (gpio_poll(gpio, edge, timeout_ms) < 0) ? 1 : 0;

    } else if (strcmp(argv[2], "blink") == 0) {
        if (argc < 5) {
            fprintf(stderr, "Error: blink requires count and delay\n");
            return 1;
        }
        int count = atoi(argv[3]);
        int delay = atoi(argv[4]);
        return (gpio_blink(gpio, count, delay) < 0) ? 1 : 0;

    } else {
        fprintf(stderr, "Error: unknown command '%s'\n", argv[2]);
        print_usage(argv[0]);
        return 1;
    }

    return 0;
}

/*
 * Complete Usage Example:
 * =======================
 *
 * 1. Compile:
 *    gcc -Wall -O2 -o gpio_control gpio_control.c
 *
 * 2. Control LED on GPIO 12:
 *    sudo ./gpio_control 12 export
 *    sudo ./gpio_control 12 direction out
 *    sudo ./gpio_control 12 write 1      # Turn on
 *    sudo ./gpio_control 12 write 0      # Turn off
 *    sudo ./gpio_control 12 blink 5 500  # Blink 5 times, 500ms
 *    sudo ./gpio_control 12 unexport
 *
 * 3. Read button on GPIO 200:
 *    sudo ./gpio_control 200 export
 *    sudo ./gpio_control 200 direction in
 *    sudo ./gpio_control 200 read
 *    sudo ./gpio_control 200 poll rising 10000  # Wait for press
 *    sudo ./gpio_control 200 unexport
 *
 * 4. Find GPIO numbers:
 *    cat /sys/kernel/debug/gpio
 *    gpioinfo
 *
 * 5. Alternative: libgpiod
 *    For newer systems, use libgpiod instead of sysfs:
 *    gpioget gpiochip0 12
 *    gpioset gpiochip0 12=1
 *    gpiomon --edge=rising gpiochip0 200
 *
 * Note: sysfs GPIO interface is deprecated in favor of character device
 * interface (/dev/gpiochipX) and libgpiod library. However, sysfs is still
 * widely supported and simpler for basic operations.
 */
