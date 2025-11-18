# Tutorial 02: Creating Custom Recipes
## Building Your Own Applications with BitBake

---

## Learning Objectives

After completing this tutorial, you will be able to:
- Understand BitBake recipe syntax and structure
- Create recipes for custom C/C++ applications
- Write recipes for Python applications
- Use recipe variables and functions effectively
- Debug recipe build failures
- Package and deploy custom software

---

## Prerequisites Checklist

- [ ] Completed Tutorial 01 (Yocto Hello World)
- [ ] Working Yocto build environment
- [ ] Successfully built core-image-minimal
- [ ] Basic understanding of C and Python programming
- [ ] Text editor (vim, nano, or VS Code)
- [ ] Jetson device for testing deployments

---

## Estimated Duration

**Total Time**: 3-4 hours
- Theory and setup: 30 minutes
- C application recipe: 1 hour
- Python application recipe: 1 hour
- Advanced features: 1-1.5 hours

---

## Step-by-Step Instructions

### Step 1: Create a Custom Layer

Start by creating your own meta-layer for custom recipes:

```bash
# Navigate to your Yocto workspace
cd ~/yocto-jetson/poky

# Create a new layer using the layer creation tool
bitbake-layers create-layer ../meta-custom
# Output: Add your new layer with 'bitbake-layers add-layer ../meta-custom'

# Examine the created structure
tree ../meta-custom
```

**Expected structure**:
```
meta-custom/
├── conf/
│   └── layer.conf          # Layer configuration
├── COPYING.MIT             # License file
├── README                  # Layer documentation
└── recipes-example/
    └── example/
        └── example_0.1.bb  # Sample recipe
```

**Explanation**: Creating a separate layer keeps your custom work isolated from upstream layers, making it easier to maintain and share.

### Step 2: Configure Your Custom Layer

Edit the layer configuration:

```bash
cd ~/yocto-jetson/meta-custom

# Update layer.conf
cat > conf/layer.conf << 'EOF'
# Layer configuration for meta-custom

# Layer identification
BBPATH =. "${LAYERDIR}:"

# Recipes and appends
BBFILES += "${LAYERDIR}/recipes-*/*/*.bb \
            ${LAYERDIR}/recipes-*/*/*.bbappend"

BBFILE_COLLECTIONS += "meta-custom"
BBFILE_PATTERN_meta-custom = "^${LAYERDIR}/"
BBFILE_PRIORITY_meta-custom = "10"

# Layer dependency specification
LAYERDEPENDS_meta-custom = "core"
LAYERSERIES_COMPAT_meta-custom = "kirkstone"

# Layer version
LAYERVERSION_meta-custom = "1"
EOF
```

**Explanation**:
- **BBFILE_PRIORITY**: Higher numbers = higher priority (your recipes override others)
- **LAYERDEPENDS**: Lists required layers
- **LAYERSERIES_COMPAT**: Declares compatible Yocto releases

### Step 3: Add Layer to Build Configuration

```bash
# Return to build directory
cd ~/yocto-jetson/builds/jetson-orin-agx

# Add the layer using bitbake-layers
bitbake-layers add-layer ../../meta-custom

# Verify it was added
bitbake-layers show-layers | grep meta-custom
```

### Step 4: Create Your First C Application Recipe

Create a simple "Hello Jetson" application:

```bash
# Create recipe directory structure
mkdir -p ~/yocto-jetson/meta-custom/recipes-apps/hello-jetson/files

# Create the C source code
cat > ~/yocto-jetson/meta-custom/recipes-apps/hello-jetson/files/hello-jetson.c << 'EOF'
/*
 * hello-jetson.c - Simple application for NVIDIA Jetson
 * Demonstrates GPIO detection and system information
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/utsname.h>

#define JETSON_MODEL_FILE "/proc/device-tree/model"
#define GPIO_BASE_PATH "/sys/class/gpio"

void print_banner(void) {
    printf("╔════════════════════════════════════════╗\n");
    printf("║     Hello from NVIDIA Jetson!          ║\n");
    printf("╚════════════════════════════════════════╝\n\n");
}

void print_system_info(void) {
    struct utsname sys_info;
    FILE *fp;
    char model[256] = "Unknown";

    // Get kernel information
    if (uname(&sys_info) == 0) {
        printf("System Information:\n");
        printf("  OS:       %s\n", sys_info.sysname);
        printf("  Kernel:   %s\n", sys_info.release);
        printf("  Arch:     %s\n", sys_info.machine);
        printf("  Hostname: %s\n", sys_info.nodename);
    }

    // Get Jetson model
    fp = fopen(JETSON_MODEL_FILE, "r");
    if (fp != NULL) {
        if (fgets(model, sizeof(model), fp) != NULL) {
            // Remove newline
            model[strcspn(model, "\n\r")] = 0;
            printf("  Model:    %s\n", model);
        }
        fclose(fp);
    }

    printf("\n");
}

void check_gpio_availability(void) {
    printf("GPIO Status:\n");

    if (access(GPIO_BASE_PATH, F_OK) == 0) {
        printf("  ✓ GPIO sysfs interface available\n");

        // Count available GPIO chips
        char cmd[256];
        snprintf(cmd, sizeof(cmd),
                 "ls -d %s/gpiochip* 2>/dev/null | wc -l",
                 GPIO_BASE_PATH);

        FILE *fp = popen(cmd, "r");
        if (fp != NULL) {
            int count = 0;
            if (fscanf(fp, "%d", &count) == 1) {
                printf("  ✓ Found %d GPIO controller(s)\n", count);
            }
            pclose(fp);
        }
    } else {
        printf("  ✗ GPIO sysfs interface not available\n");
    }

    printf("\n");
}

int main(int argc, char *argv[]) {
    print_banner();
    print_system_info();
    check_gpio_availability();

    printf("This application was built with Yocto Project!\n");
    printf("Build Date: %s %s\n", __DATE__, __TIME__);

    return 0;
}
EOF

# Create the Makefile
cat > ~/yocto-jetson/meta-custom/recipes-apps/hello-jetson/files/Makefile << 'EOF'
# Makefile for hello-jetson

CC ?= gcc
CFLAGS ?= -Wall -Wextra -O2
TARGET = hello-jetson
SRCS = hello-jetson.c
OBJS = $(SRCS:.c=.o)

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) -o $@ $^

%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $<

clean:
	rm -f $(TARGET) $(OBJS)

install: $(TARGET)
	install -d $(DESTDIR)$(bindir)
	install -m 0755 $(TARGET) $(DESTDIR)$(bindir)/

.PHONY: all clean install
EOF
```

**Explanation**: The application demonstrates:
- Reading Jetson hardware information
- Checking GPIO availability
- Using standard Linux system calls
- Proper error handling

### Step 5: Write the BitBake Recipe

Create the recipe file:

```bash
cat > ~/yocto-jetson/meta-custom/recipes-apps/hello-jetson/hello-jetson_1.0.bb << 'EOF'
# Recipe for hello-jetson application

SUMMARY = "Hello World application for NVIDIA Jetson"
DESCRIPTION = "A simple application that displays system information \
               and checks GPIO availability on Jetson platforms"
HOMEPAGE = "https://github.com/your-org/hello-jetson"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Source files from the recipe directory
SRC_URI = "file://hello-jetson.c \
           file://Makefile \
          "

# Work directory is where sources are unpacked
S = "${WORKDIR}"

# Compilation happens in the work directory
do_compile() {
    # Call make with appropriate cross-compilation variables
    oe_runmake
}

# Installation to the destination directory
do_install() {
    # Create the bindir in the destination
    install -d ${D}${bindir}

    # Install the binary with executable permissions
    install -m 0755 hello-jetson ${D}${bindir}/
}

# Specify which packages are generated
PACKAGES = "${PN}"

# Specify which files go into the main package
FILES:${PN} = "${bindir}/hello-jetson"

# Make the package Jetson-specific (optional)
COMPATIBLE_MACHINE = "(tegra)"
EOF
```

**Explanation of key variables**:
- **SUMMARY**: Short one-line description
- **SRC_URI**: List of source files or URLs
- **S**: Source directory (where to build from)
- **do_compile()**: Function that compiles the software
- **do_install()**: Function that installs files to destination
- **FILES:${PN}**: Lists files included in the package

### Step 6: Build and Test the Recipe

```bash
# Navigate to build directory
cd ~/yocto-jetson/builds/jetson-orin-agx

# Build the recipe
bitbake hello-jetson

# Check build output
ls -lh tmp/work/aarch64-oe-linux/hello-jetson/1.0-r0/image/usr/bin/

# Add to your image
echo 'IMAGE_INSTALL:append = " hello-jetson"' >> conf/local.conf

# Rebuild the image
bitbake core-image-minimal

# Deploy and test on Jetson
# After flashing/updating, run:
# hello-jetson
```

**Expected output on Jetson**:
```
╔════════════════════════════════════════╗
║     Hello from NVIDIA Jetson!          ║
╚════════════════════════════════════════╝

System Information:
  OS:       Linux
  Kernel:   5.10.120-tegra
  Arch:     aarch64
  Hostname: jetson-orin-agx-devkit
  Model:    NVIDIA Orin AGX Developer Kit

GPIO Status:
  ✓ GPIO sysfs interface available
  ✓ Found 2 GPIO controller(s)

This application was built with Yocto Project!
Build Date: Jan 15 2025 14:23:45
```

### Step 7: Create a Python Application Recipe

Create a Python-based system monitor:

```bash
# Create directory for Python recipe
mkdir -p ~/yocto-jetson/meta-custom/recipes-apps/jetson-monitor/files

# Create the Python application
cat > ~/yocto-jetson/meta-custom/recipes-apps/jetson-monitor/files/jetson-monitor.py << 'EOF'
#!/usr/bin/env python3
"""
jetson-monitor.py - System monitoring tool for NVIDIA Jetson
Displays real-time CPU, GPU, memory, and temperature information
"""

import os
import sys
import time
import argparse
from pathlib import Path


class JetsonMonitor:
    """Monitor system resources on NVIDIA Jetson"""

    THERMAL_ZONES = Path("/sys/class/thermal")
    CPU_FREQ_PATH = Path("/sys/devices/system/cpu")
    MEM_INFO = Path("/proc/meminfo")

    def __init__(self):
        self.running = True

    def read_file(self, filepath):
        """Safely read a file and return contents"""
        try:
            return filepath.read_text().strip()
        except (IOError, FileNotFoundError):
            return None

    def get_cpu_usage(self):
        """Get current CPU usage percentage"""
        # Read /proc/stat for CPU usage
        try:
            with open('/proc/stat', 'r') as f:
                line = f.readline()
                values = [float(x) for x in line.split()[1:]]

                idle = values[3]
                total = sum(values)

                if not hasattr(self, '_prev_idle'):
                    self._prev_idle = idle
                    self._prev_total = total
                    return 0.0

                idle_delta = idle - self._prev_idle
                total_delta = total - self._prev_total

                self._prev_idle = idle
                self._prev_total = total

                usage = 100.0 * (1.0 - idle_delta / total_delta)
                return max(0.0, min(100.0, usage))
        except:
            return 0.0

    def get_cpu_frequencies(self):
        """Get current CPU core frequencies"""
        freqs = []
        cpu_dirs = sorted(self.CPU_FREQ_PATH.glob("cpu[0-9]*"))

        for cpu_dir in cpu_dirs:
            freq_file = cpu_dir / "cpufreq" / "scaling_cur_freq"
            freq_khz = self.read_file(freq_file)
            if freq_khz:
                freqs.append(int(freq_khz) // 1000)  # Convert to MHz

        return freqs

    def get_temperatures(self):
        """Get thermal zone temperatures"""
        temps = {}

        for zone_dir in sorted(self.THERMAL_ZONES.glob("thermal_zone*")):
            zone_type_file = zone_dir / "type"
            temp_file = zone_dir / "temp"

            zone_type = self.read_file(zone_type_file)
            temp_millic = self.read_file(temp_file)

            if zone_type and temp_millic:
                try:
                    temp_c = int(temp_millic) / 1000.0
                    temps[zone_type] = temp_c
                except ValueError:
                    pass

        return temps

    def get_memory_info(self):
        """Get memory usage information"""
        mem_info = {}

        try:
            with open(self.MEM_INFO, 'r') as f:
                for line in f:
                    if ':' in line:
                        key, value = line.split(':', 1)
                        value_kb = int(value.strip().split()[0])
                        mem_info[key] = value_kb
        except:
            return None

        total = mem_info.get('MemTotal', 0)
        available = mem_info.get('MemAvailable', 0)
        used = total - available

        return {
            'total_mb': total // 1024,
            'used_mb': used // 1024,
            'available_mb': available // 1024,
            'percent': (used / total * 100) if total > 0 else 0
        }

    def format_bar(self, percent, width=20):
        """Create a visual progress bar"""
        filled = int(width * percent / 100)
        bar = '█' * filled + '░' * (width - filled)
        return f"[{bar}] {percent:5.1f}%"

    def display_status(self):
        """Display current system status"""
        # Clear screen
        os.system('clear' if os.name == 'posix' else 'cls')

        print("╔═══════════════════════════════════════════════════════╗")
        print("║        NVIDIA Jetson System Monitor                  ║")
        print("╚═══════════════════════════════════════════════════════╝\n")

        # CPU Information
        cpu_usage = self.get_cpu_usage()
        cpu_freqs = self.get_cpu_frequencies()

        print("CPU Information:")
        print(f"  Usage:  {self.format_bar(cpu_usage)}")
        if cpu_freqs:
            freq_str = ", ".join([f"{freq:4d} MHz" for freq in cpu_freqs])
            print(f"  Cores:  {freq_str}")
        print()

        # Memory Information
        mem = self.get_memory_info()
        if mem:
            print("Memory Information:")
            print(f"  Usage:  {self.format_bar(mem['percent'])}")
            print(f"  Used:   {mem['used_mb']:,} MB / {mem['total_mb']:,} MB")
            print(f"  Free:   {mem['available_mb']:,} MB")
            print()

        # Temperature Information
        temps = self.get_temperatures()
        if temps:
            print("Temperature Sensors:")
            for sensor, temp in sorted(temps.items())[:5]:  # Show top 5
                # Color code temperatures
                if temp > 80:
                    indicator = "🔥"
                elif temp > 60:
                    indicator = "🌡️ "
                else:
                    indicator = "❄️ "

                print(f"  {indicator} {sensor:20s}: {temp:5.1f}°C")
            print()

        print("Press Ctrl+C to exit")
        print(f"Last updated: {time.strftime('%H:%M:%S')}")

    def run(self, interval=2):
        """Run the monitor loop"""
        print("Starting Jetson Monitor...")
        print("Gathering initial data...\n")
        time.sleep(1)

        try:
            while self.running:
                self.display_status()
                time.sleep(interval)
        except KeyboardInterrupt:
            print("\n\nMonitoring stopped.")
            sys.exit(0)


def main():
    parser = argparse.ArgumentParser(
        description='Monitor NVIDIA Jetson system resources'
    )
    parser.add_argument(
        '-i', '--interval',
        type=int,
        default=2,
        help='Update interval in seconds (default: 2)'
    )

    args = parser.parse_args()

    monitor = JetsonMonitor()
    monitor.run(interval=args.interval)


if __name__ == '__main__':
    main()
EOF

chmod +x ~/yocto-jetson/meta-custom/recipes-apps/jetson-monitor/files/jetson-monitor.py
```

### Step 8: Create Python Recipe

```bash
cat > ~/yocto-jetson/meta-custom/recipes-apps/jetson-monitor/jetson-monitor_1.0.bb << 'EOF'
# Recipe for jetson-monitor Python application

SUMMARY = "System monitoring tool for NVIDIA Jetson"
DESCRIPTION = "Real-time monitoring of CPU, GPU, memory, and temperature \
               on Jetson platforms using Python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Python recipe inheritance
inherit python3native

# Source files
SRC_URI = "file://jetson-monitor.py"

S = "${WORKDIR}"

# No compilation needed for Python
do_compile[noexec] = "1"

# Install the Python script
do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/jetson-monitor.py ${D}${bindir}/jetson-monitor
}

# Runtime dependencies
RDEPENDS:${PN} = "python3-core"

# Package contents
FILES:${PN} = "${bindir}/jetson-monitor"

# Jetson-specific
COMPATIBLE_MACHINE = "(tegra)"
EOF
```

**Explanation**:
- **inherit python3native**: Adds Python 3 support to the recipe
- **do_compile[noexec]**: Skips compilation (Python is interpreted)
- **RDEPENDS**: Lists runtime dependencies (Python 3 required)

### Step 9: Build Python Recipe

```bash
cd ~/yocto-jetson/builds/jetson-orin-agx

# Build the recipe
bitbake jetson-monitor

# Add to image
echo 'IMAGE_INSTALL:append = " jetson-monitor"' >> conf/local.conf

# Rebuild image
bitbake core-image-minimal
```

### Step 10: Advanced Recipe Features - Using External Sources

Create a recipe that fetches from Git:

```bash
cat > ~/yocto-jetson/meta-custom/recipes-apps/gpio-utils/gpio-utils_git.bb << 'EOF'
# Recipe for gpio-utils from external Git repository

SUMMARY = "GPIO utility tools for Linux"
DESCRIPTION = "Collection of GPIO manipulation tools"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

# Fetch from Git repository
SRC_URI = "git://git.kernel.org/pub/scm/utils/gpio/gpio-utils.git;protocol=https;branch=master"

# Use a specific commit for reproducibility
SRCREV = "${AUTOREV}"

# Git clones to ${WORKDIR}/git
S = "${WORKDIR}/git"

# Build with autotools
inherit autotools pkgconfig

# Additional configuration options
EXTRA_OECONF = "--enable-tools"

# Dependencies
DEPENDS = "linux-libc-headers"

# Runtime dependencies
RDEPENDS:${PN} = "bash"

# Multiple packages can be created
PACKAGES =+ "${PN}-tools ${PN}-examples"

FILES:${PN}-tools = "${bindir}/gpio*"
FILES:${PN}-examples = "${datadir}/gpio-examples/*"
EOF
```

**Explanation**:
- **git://**: Fetches source from Git repository
- **SRCREV**: Specifies which commit to use (AUTOREV = latest)
- **inherit autotools**: Uses autoconf/automake build system
- **PACKAGES**: Creates multiple packages from one recipe

---

## Troubleshooting Common Issues

### Issue 1: Recipe Parse Error

**Symptoms**:
```
ERROR: ParseError at hello-jetson_1.0.bb:15: unparsed line: 'invalid syntax'
```

**Solutions**:
```bash
# Validate recipe syntax
bitbake-layers show-recipes hello-jetson

# Check for common issues:
# - Missing quotes around strings
# - Incorrect variable assignments (use = not :)
# - Python indentation in do_* functions

# Use bitbake's parser directly
bitbake -e hello-jetson | grep "^SUMMARY="
```

### Issue 2: Source File Not Found

**Symptoms**:
```
ERROR: hello-jetson-1.0-r0 do_fetch: Fetcher failure for URL: 'file://hello-jetson.c'
```

**Solutions**:
```bash
# Verify file location
ls -la ~/yocto-jetson/meta-custom/recipes-apps/hello-jetson/files/

# Check SRC_URI paths (relative to recipe)
# Correct:   SRC_URI = "file://hello.c"
# Incorrect: SRC_URI = "file:///absolute/path/hello.c"

# BitBake looks in:
# 1. recipe_dir/files/
# 2. recipe_dir/recipe-name/
# 3. recipe_dir/
```

### Issue 3: Compilation Failure

**Symptoms**:
```
ERROR: hello-jetson-1.0-r0 do_compile: oe_runmake failed
```

**Solutions**:
```bash
# Check the detailed log
less tmp/work/aarch64-oe-linux/hello-jetson/1.0-r0/temp/log.do_compile

# Manually test build
cd tmp/work/aarch64-oe-linux/hello-jetson/1.0-r0
source <build-dir>/tmp/environment-setup-aarch64-oe-linux
make

# Common issues:
# - Wrong compiler flags
# - Missing dependencies
# - Cross-compilation environment not set
```

### Issue 4: Files Not Packaged

**Symptoms**:
```
WARNING: hello-jetson-1.0-r0 do_package: File '/usr/bin/hello-jetson' was installed but not shipped in any package
```

**Solutions**:
```bash
# Add to FILES variable in recipe
FILES:${PN} += "${bindir}/hello-jetson"

# Or debug what was installed
ls -la tmp/work/aarch64-oe-linux/hello-jetson/1.0-r0/image/

# Check package contents
oe-pkgdata-util list-pkg-files hello-jetson
```

### Issue 5: Runtime Dependency Missing

**Symptoms**: Application fails to run on target due to missing libraries

**Solutions**:
```bash
# On target, check missing dependencies
ldd /usr/bin/hello-jetson

# Add runtime dependencies to recipe
RDEPENDS:${PN} = "libgcc libstdc++"

# For Python scripts
RDEPENDS:${PN} = "python3-core python3-modules"
```

---

## Verification Checklist

- [ ] Custom layer created and added to build
- [ ] Layer configuration is valid (check with bitbake-layers show-layers)
- [ ] C application recipe builds without errors
- [ ] Binary is correctly packaged (check with oe-pkgdata-util)
- [ ] Python application recipe builds successfully
- [ ] Both applications added to image via IMAGE_INSTALL
- [ ] Image builds with custom applications included
- [ ] Applications run correctly on Jetson hardware
- [ ] System information displayed correctly
- [ ] No runtime errors or missing dependencies

---

## Recipe Best Practices

### 1. License Compliance
```bash
# Always specify license clearly
LICENSE = "MIT | GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=..."

# Generate checksum
md5sum LICENSE
```

### 2. Version Pinning
```bash
# For reproducible builds, pin versions
SRCREV = "1234567890abcdef"  # Not AUTOREV

# Or use tags
SRCREV = "${AUTOREV}"
PV = "1.0+git${SRCPV}"
```

### 3. Error Handling
```bash
do_install() {
    # Check if binary exists
    if [ ! -f ${B}/hello-jetson ]; then
        bbfatal "Binary not found"
    fi

    install -d ${D}${bindir}
    install -m 0755 ${B}/hello-jetson ${D}${bindir}/
}
```

### 4. Recipe Documentation
```bash
SUMMARY = "One-line description"
DESCRIPTION = "Detailed multi-line description \
               that explains the purpose"
HOMEPAGE = "https://project-url.com"
BUGTRACKER = "https://issues-url.com"
```

---

## Next Steps

### Immediate Practice
1. Modify hello-jetson to add GPU information
2. Create a recipe for your own application
3. Package a third-party open-source tool

### Proceed to Next Tutorial
**Tutorial 03: Building a Meta-Layer** - Create a complete distributable layer

### Advanced Topics to Explore
- Creating shared libraries with recipes
- Using recipe variants with BBCLASSEXTEND
- Creating SDK with populate_sdk
- Debugging with devtool

---

## Recipe Template for Quick Start

```bash
# Generic application recipe template
SUMMARY = "Brief description"
DESCRIPTION = "Longer description"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://source.c \
           file://Makefile \
          "

S = "${WORKDIR}"

do_compile() {
    oe_runmake
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${PN} ${D}${bindir}/
}

RDEPENDS:${PN} = "dependency1 dependency2"
```

---

**Congratulations!** You now know how to create custom BitBake recipes for both compiled and interpreted applications. This is a fundamental skill for building custom Linux distributions with Yocto.

---

*Tutorial created by the Yocto & Meta-Tegra Multi-Agent Learning System*
*Last updated: 2025-01-15*
