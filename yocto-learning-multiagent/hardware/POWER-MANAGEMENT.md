# Power Management Guide

## Overview

Comprehensive power management reference for NVIDIA Jetson platforms, covering power modes, CPU/GPU governors, thermal management, battery integration, and wake source configuration.

## Power Specifications

### Platform Power Profiles

| Platform | Min Power | Typical | Max (MAXN) | TDP | Input Voltage |
|----------|-----------|---------|------------|-----|---------------|
| AGX Orin 64GB | 15W | 30-40W | 60W | 60W | 9-20V |
| AGX Orin 32GB | 15W | 25-35W | 50W | 50W | 9-20V |
| Orin NX 16GB | 10W | 15-20W | 25W | 25W | 9-20V |
| Orin NX 8GB | 10W | 12-15W | 20W | 20W | 9-20V |
| Orin Nano 8GB | 7W | 10-12W | 15W | 15W | 5-20V |
| Orin Nano 4GB | 5W | 7-8W | 10W | 10W | 5-20V |
| AGX Xavier | 10W | 20-25W | 30W | 30W | 9-20V |
| Xavier NX | 10W | 12-15W | 20W | 20W | 9-20V |
| Jetson Nano | 5W | 7-8W | 10W | 10W | 5V (barrel) |

### Power Modes

**Power modes allow trading performance for power consumption.**

## nvpmodel - NVIDIA Power Model Tool

### Available Power Modes

**AGX Orin:**
```bash
# List available modes
nvpmodel -q

# Mode 0: MAXN (60W) - Maximum performance
# Mode 1: 50W
# Mode 2: 30W
# Mode 3: 15W - Minimum power
```

**Orin NX 16GB:**
```
# Mode 0: MAXN (25W)
# Mode 1: 15W
# Mode 2: 10W
```

**Orin Nano:**
```
# Mode 0: MAXN (15W/10W)
# Mode 1: 10W/7W
# Mode 2: 7W/5W
```

### Set Power Mode

```bash
# Set to maximum performance
sudo nvpmodel -m 0

# Set to 15W mode
sudo nvpmodel -m 3

# Query current mode
nvpmodel -q --verbose
```

### Custom Power Mode Configuration

**Edit nvpmodel configuration:**
```bash
# Location: /etc/nvpmodel.conf
sudo vi /etc/nvpmodel.conf
```

**Example Custom Mode:**
```ini
# Custom 20W mode for Orin NX
< POWER_MODEL ID=3 NAME=CUSTOM_20W >
CPU_ONLINE CORE_0 1
CPU_ONLINE CORE_1 1
CPU_ONLINE CORE_2 1
CPU_ONLINE CORE_3 1
CPU_ONLINE CORE_4 0
CPU_ONLINE CORE_5 0
CPU_ONLINE CORE_6 0
CPU_ONLINE CORE_7 0
CPU_A78_0 MIN_FREQ 806400
CPU_A78_0 MAX_FREQ 1420800
GPU MIN_FREQ 306000000
GPU MAX_FREQ 765000000
GPU_POWER_CONTROL_ENABLE GPU_PWR_CNTL_EN on
```

**Apply custom mode:**
```bash
sudo nvpmodel -m 3
```

## CPU Frequency Scaling

### CPU Governors

**Available governors:**
- `schedutil` (default, recommended) - Scheduler-driven frequency selection
- `performance` - Always run at maximum frequency
- `powersave` - Always run at minimum frequency
- `ondemand` - Dynamic scaling based on load
- `conservative` - Gradual frequency changes
- `userspace` - Manual frequency control

### Check CPU Information

```bash
# List all CPUs
lscpu

# Check current frequencies
cat /sys/devices/system/cpu/cpu*/cpufreq/scaling_cur_freq

# Check available frequencies
cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies

# Check current governor
cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor

# Check available governors
cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors
```

### Set CPU Governor

```bash
# Set performance governor (all cores)
for cpu in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do
    echo performance | sudo tee $cpu
done

# Set schedutil governor
for cpu in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do
    echo schedutil | sudo tee $cpu
done

# Set powersave governor
for cpu in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do
    echo powersave | sudo tee $cpu
done
```

### Manual Frequency Control

```bash
# Set userspace governor
echo userspace | sudo tee /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor

# Set specific frequency
echo 1420800 | sudo tee /sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed

# Lock all cores to specific frequency
for cpu in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do
    echo userspace | sudo tee $cpu
done

for cpu in /sys/devices/system/cpu/cpu*/cpufreq/scaling_setspeed; do
    echo 1420800 | sudo tee $cpu
done
```

### CPU Core Control

```bash
# Check online CPUs
cat /sys/devices/system/cpu/online

# Disable CPU core (example: CPU 4)
echo 0 | sudo tee /sys/devices/system/cpu/cpu4/online

# Enable CPU core
echo 1 | sudo tee /sys/devices/system/cpu/cpu4/online

# Disable all non-boot CPUs (keep only CPU0)
for i in {1..7}; do
    echo 0 | sudo tee /sys/devices/system/cpu/cpu$i/online 2>/dev/null
done
```

## GPU Frequency Scaling

### GPU Information

```bash
# Check GPU frequency
cat /sys/devices/gpu.0/devfreq/57000000.gpu/cur_freq

# Available frequencies
cat /sys/devices/gpu.0/devfreq/57000000.gpu/available_frequencies

# Current governor
cat /sys/devices/gpu.0/devfreq/57000000.gpu/governor

# Available governors
cat /sys/devices/gpu.0/devfreq/57000000.gpu/available_governors
```

### Set GPU Governor

```bash
# Performance governor (maximum frequency)
echo performance | sudo tee /sys/devices/gpu.0/devfreq/57000000.gpu/governor

# Powersave governor (minimum frequency)
echo powersave | sudo tee /sys/devices/gpu.0/devfreq/57000000.gpu/governor

# Userspace governor (manual control)
echo userspace | sudo tee /sys/devices/gpu.0/devfreq/57000000.gpu/governor

# Simple on-demand
echo simple_ondemand | sudo tee /sys/devices/gpu.0/devfreq/57000000.gpu/governor
```

### Manual GPU Frequency Control

```bash
# Set userspace governor first
echo userspace | sudo tee /sys/devices/gpu.0/devfreq/57000000.gpu/governor

# Set specific frequency (example: 765MHz)
echo 765000000 | sudo tee /sys/devices/gpu.0/devfreq/57000000.gpu/userspace/set_freq

# Lock to minimum frequency
MIN_FREQ=$(cat /sys/devices/gpu.0/devfreq/57000000.gpu/available_frequencies | awk '{print $1}')
echo $MIN_FREQ | sudo tee /sys/devices/gpu.0/devfreq/57000000.gpu/userspace/set_freq

# Lock to maximum frequency
MAX_FREQ=$(cat /sys/devices/gpu.0/devfreq/57000000.gpu/available_frequencies | awk '{print $NF}')
echo $MAX_FREQ | sudo tee /sys/devices/gpu.0/devfreq/57000000.gpu/userspace/set_freq
```

## Thermal Management

### Thermal Zones

**Check thermal zones:**
```bash
# List thermal zones
ls /sys/class/thermal/thermal_zone*/

# Read temperatures
for tz in /sys/class/thermal/thermal_zone*; do
    echo "$(cat $tz/type): $(cat $tz/temp) mC"
done

# Read with units (Celsius)
paste <(cat /sys/class/thermal/thermal_zone*/type) \
      <(cat /sys/class/thermal/thermal_zone*/temp) | \
      column -s $'\t' -t | \
      sed 's/\([0-9]\+\)000$/\1°C/'
```

**Common thermal zones:**
- CPU-therm: CPU die temperature
- GPU-therm: GPU die temperature
- PMIC-Die: Power management IC temperature
- SOC: System-on-Chip temperature
- thermal-fan-est: Fan speed estimation zone

### Temperature Monitoring Script

```bash
#!/bin/bash
# Continuous thermal monitoring

while true; do
    clear
    echo "=== Jetson Thermal Status ==="
    date

    echo -e "\n=== Temperatures ==="
    for tz in /sys/class/thermal/thermal_zone*; do
        TYPE=$(cat $tz/type)
        TEMP=$(cat $tz/temp)
        TEMP_C=$((TEMP / 1000))
        printf "%-20s %3d°C\n" "$TYPE:" $TEMP_C
    done

    echo -e "\n=== CPU Frequencies ==="
    for i in {0..7}; do
        if [ -e /sys/devices/system/cpu/cpu$i/cpufreq/scaling_cur_freq ]; then
            FREQ=$(cat /sys/devices/system/cpu/cpu$i/cpufreq/scaling_cur_freq)
            FREQ_MHZ=$((FREQ / 1000))
            ONLINE=$(cat /sys/devices/system/cpu/cpu$i/online 2>/dev/null || echo "1")
            printf "CPU%d: %4d MHz (Online: %s)\n" $i $FREQ_MHZ $ONLINE
        fi
    done

    echo -e "\n=== GPU Frequency ==="
    GPU_FREQ=$(cat /sys/devices/gpu.0/devfreq/57000000.gpu/cur_freq)
    GPU_FREQ_MHZ=$((GPU_FREQ / 1000000))
    echo "GPU: $GPU_FREQ_MHZ MHz"

    echo -e "\n=== Power Mode ==="
    nvpmodel -q 2>/dev/null | grep "NV Power Mode"

    sleep 2
done
```

### Thermal Trip Points

**Check trip points:**
```bash
# List trip points
for tz in /sys/class/thermal/thermal_zone*; do
    echo "$(cat $tz/type):"
    for trip in $tz/trip_point_*_temp; do
        [ -e "$trip" ] && echo "  $(basename $trip): $(cat $trip) mC"
    done
done
```

**Thermal throttling:**
- Jetson automatically throttles CPU/GPU when temperature exceeds trip points
- Typical throttling starts at 85-95°C
- Emergency shutdown at 105°C (varies by platform)

### Fan Control

**PWM Fan Control (AGX platforms):**

```bash
# Check fan speed
cat /sys/devices/platform/pwm-fan/hwmon/hwmon*/rpm

# Fan control (if available)
# Location varies by platform
FAN_SYSFS="/sys/devices/platform/pwm-fan/hwmon/hwmon0"

# Check current PWM value (0-255)
cat $FAN_SYSFS/pwm1

# Set fan speed (0=off, 255=max)
echo 128 | sudo tee $FAN_SYSFS/pwm1  # 50% speed
echo 255 | sudo tee $FAN_SYSFS/pwm1  # 100% speed

# Check fan mode
cat $FAN_SYSFS/pwm1_enable
# 0 = full speed
# 1 = manual control
# 2 = automatic control
```

**Custom fan control script:**

```bash
#!/bin/bash
# Temperature-based fan control

FAN_PWM="/sys/devices/platform/pwm-fan/hwmon/hwmon0/pwm1"
TEMP_ZONE="/sys/class/thermal/thermal_zone0/temp"

# Enable manual control
echo 1 > ${FAN_PWM}_enable

while true; do
    TEMP=$(cat $TEMP_ZONE)
    TEMP_C=$((TEMP / 1000))

    if [ $TEMP_C -lt 50 ]; then
        PWM=64   # 25% (quiet)
    elif [ $TEMP_C -lt 65 ]; then
        PWM=128  # 50%
    elif [ $TEMP_C -lt 75 ]; then
        PWM=192  # 75%
    else
        PWM=255  # 100% (max cooling)
    fi

    echo $PWM > $FAN_PWM
    sleep 5
done
```

### Cooling Recommendations

**Passive Cooling:**
- Minimum heatsink requirements (see platform datasheet)
- Thermal interface material (TIM) required
- Proper airflow in enclosure

**Active Cooling:**
- Required for sustained high-power operation
- Fan sizing: 30-50 CFM for AGX platforms
- Consider noise requirements

**Example Configurations:**
- AGX Orin @ 60W: Active cooling mandatory
- Orin NX @ 25W: Large heatsink or small fan
- Orin Nano @ 15W: Large heatsink sufficient

## Power Monitoring

### Real-time Power Monitoring

**tegrastats - NVIDIA Stats Tool:**

```bash
# Basic usage
tegrastats

# Output format:
# RAM X/Y MB (lfb NxZ MB) CPU [X%@MHz, Y%@MHz,...] GPU X%@MHz VIC X%@MHz APE XMHz
# PLL@C MCPU@C PMIC@C GPU@C Tboard@C Tdiode@C WLAN@C PMIC@C VDD_IN X/Y PMIC@C

# Log to file
tegrastats --logfile tegrastats.log

# Interval (milliseconds)
tegrastats --interval 500

# Stop after N records
tegrastats --stop 100
```

**Parse tegrastats output:**

```python
#!/usr/bin/env python3
import re
import time

def parse_tegrastats(line):
    """Parse tegrastats output line"""
    data = {}

    # RAM usage
    ram_match = re.search(r'RAM (\d+)/(\d+)MB', line)
    if ram_match:
        data['ram_used_mb'] = int(ram_match.group(1))
        data['ram_total_mb'] = int(ram_match.group(2))

    # CPU usage and frequency
    cpu_matches = re.findall(r'(\d+)%@(\d+)', line)
    data['cpu_usage'] = [int(m[0]) for m in cpu_matches]
    data['cpu_freq'] = [int(m[1]) for m in cpu_matches]

    # GPU usage and frequency
    gpu_match = re.search(r'GPU\s+(\d+)%@(\d+)', line)
    if gpu_match:
        data['gpu_usage'] = int(gpu_match.group(1))
        data['gpu_freq'] = int(gpu_match.group(2))

    # Temperatures
    temp_matches = re.findall(r'(\w+)@([\d.]+)C', line)
    data['temps'] = {name: float(temp) for name, temp in temp_matches}

    # Power (if available)
    power_match = re.search(r'VDD_IN\s+([\d.]+)/([\d.]+)', line)
    if power_match:
        data['power_curr_mw'] = float(power_match.group(1))
        data['power_avg_mw'] = float(power_match.group(2))

    return data

# Monitor
import subprocess
proc = subprocess.Popen(['tegrastats', '--interval', '1000'],
                       stdout=subprocess.PIPE,
                       universal_newlines=True)

try:
    for line in proc.stdout:
        data = parse_tegrastats(line.strip())
        print(f"Power: {data.get('power_avg_mw', 'N/A')} mW, "
              f"GPU: {data.get('gpu_usage', 'N/A')}%, "
              f"Temp: {data.get('temps', {}).get('GPU', 'N/A')}°C")
except KeyboardInterrupt:
    proc.terminate()
```

### INA3221 Power Monitors

**Read power rails (if available):**

```bash
# INA3221 sensors
ls /sys/bus/i2c/drivers/ina3221x/

# Read voltage and current
for sensor in /sys/bus/i2c/drivers/ina3221x/*/iio:device*; do
    echo "Sensor: $(basename $sensor)"
    for rail in $sensor/in_*; do
        [ -e "$rail" ] && echo "  $(basename $rail): $(cat $rail)"
    done
done
```

**Monitor specific rail:**

```bash
# GPU power rail
cat /sys/bus/i2c/drivers/ina3221x/1-0040/iio:device0/in_voltage0_input
cat /sys/bus/i2c/drivers/ina3221x/1-0040/iio:device0/in_current0_input
cat /sys/bus/i2c/drivers/ina3221x/1-0040/iio:device0/in_power0_input
```

### jtop - Jetson Stats

**Install and use jtop:**

```bash
# Install
sudo pip3 install jetson-stats

# Run
sudo jtop

# Features:
# - Real-time monitoring
# - CPU/GPU/Memory usage
# - Power consumption
# - Thermal status
# - Nice GUI interface
```

## Battery Integration

### Power Supply Configuration

**Device Tree - Battery Charger:**

```dts
battery_charger: battery-charger@6b {
    compatible = "ti,bq2419x";
    reg = <0x6b>;

    ti,charging-term-current-ma = <128>;
    ti,pre-charge-current-limit-ma = <128>;
    ti,fast-charge-current-limit-ma = <4096>;
    ti,charge-voltage-limit-mv = <4208>;
    ti,input-voltage-limit-mv = <5000>;

    battery_charger_gpio: battery-charger-gpio {
        gpio-controller;
        #gpio-cells = <2>;
    };

    vbus_regulator: vbus {
        regulator-name = "vbus_regulator";
        regulator-min-microvolt = <5000000>;
        regulator-max-microvolt = <5000000>;
    };
};

battery: battery {
    compatible = "simple-battery";
    voltage-min-design-microvolt = <3400000>;
    voltage-max-design-microvolt = <4200000>;
    energy-full-design-microwatt-hours = <5800000>;
    charge-full-design-microamp-hours = <1500000>;
};
```

### Battery Monitoring

**Read battery status:**

```bash
# Check power supply devices
ls /sys/class/power_supply/

# Battery info
BATTERY="/sys/class/power_supply/battery"

if [ -e "$BATTERY" ]; then
    echo "Status: $(cat $BATTERY/status)"
    echo "Capacity: $(cat $BATTERY/capacity)%"
    echo "Voltage: $(cat $BATTERY/voltage_now) uV"
    echo "Current: $(cat $BATTERY/current_now) uA"
    echo "Power: $(cat $BATTERY/power_now) uW"
    echo "Temp: $(cat $BATTERY/temp) C"
fi

# AC adapter status
AC="/sys/class/power_supply/ac"
if [ -e "$AC" ]; then
    echo "AC Online: $(cat $AC/online)"
fi
```

**Battery monitoring script:**

```python
#!/usr/bin/env python3
import time

class BatteryMonitor:
    def __init__(self, battery_path="/sys/class/power_supply/battery"):
        self.battery_path = battery_path

    def read_value(self, filename):
        try:
            with open(f"{self.battery_path}/{filename}", 'r') as f:
                return f.read().strip()
        except:
            return None

    def get_status(self):
        return {
            'status': self.read_value('status'),
            'capacity': int(self.read_value('capacity') or 0),
            'voltage_uv': int(self.read_value('voltage_now') or 0),
            'current_ua': int(self.read_value('current_now') or 0),
            'power_uw': int(self.read_value('power_now') or 0),
        }

    def monitor(self):
        while True:
            status = self.get_status()
            print(f"Battery: {status['capacity']}% | "
                  f"Status: {status['status']} | "
                  f"Power: {status['power_uw']/1000000:.2f}W | "
                  f"Voltage: {status['voltage_uv']/1000000:.2f}V")
            time.sleep(5)

if __name__ == '__main__':
    monitor = BatteryMonitor()
    monitor.monitor()
```

### UPS (Uninterruptible Power Supply)

**Detect power loss and initiate shutdown:**

```python
#!/usr/bin/env python3
import time
import subprocess
import os

AC_PATH = "/sys/class/power_supply/ac/online"
BATTERY_CAPACITY = "/sys/class/power_supply/battery/capacity"

def is_ac_online():
    with open(AC_PATH, 'r') as f:
        return f.read().strip() == '1'

def get_battery_capacity():
    with open(BATTERY_CAPACITY, 'r') as f:
        return int(f.read().strip())

def shutdown():
    print("Initiating shutdown due to low battery...")
    subprocess.call(['sudo', 'shutdown', '-h', 'now'])

# Monitor power
ac_offline_time = None
SHUTDOWN_DELAY = 300  # 5 minutes
LOW_BATTERY_THRESHOLD = 10  # 10%

while True:
    ac_online = is_ac_online()
    battery = get_battery_capacity()

    if not ac_online:
        if ac_offline_time is None:
            ac_offline_time = time.time()
            print(f"AC power lost! Battery: {battery}%")

        elapsed = time.time() - ac_offline_time

        if battery <= LOW_BATTERY_THRESHOLD:
            print(f"Critical battery level: {battery}%")
            shutdown()
        elif elapsed > SHUTDOWN_DELAY:
            print(f"AC offline for {SHUTDOWN_DELAY}s")
            shutdown()
    else:
        if ac_offline_time is not None:
            print("AC power restored")
            ac_offline_time = None

    time.sleep(5)
```

## Wake Sources

### RTC Wake

**Configure RTC alarm:**

```bash
# Check RTC device
ls /sys/class/rtc/

# Read current time
cat /sys/class/rtc/rtc0/time

# Set alarm (wake in 60 seconds)
echo 0 > /sys/class/rtc/rtc0/wakealarm
CURRENT=$(cat /sys/class/rtc/rtc0/since_epoch)
WAKE_TIME=$((CURRENT + 60))
echo $WAKE_TIME > /sys/class/rtc/rtc0/wakealarm

# Suspend
systemctl suspend

# System will wake after 60 seconds
```

### GPIO Wake

**Configure GPIO as wake source:**

```bash
# Enable GPIO wake
echo enabled > /sys/devices/platform/gpio-keys/power/wakeup

# Or specific GPIO line
echo enabled > /sys/class/gpio/gpio424/power/wakeup
```

**Device Tree Configuration:**

```dts
gpio-keys {
    compatible = "gpio-keys";

    power {
        label = "Power";
        gpios = <&tegra_main_gpio TEGRA234_MAIN_GPIO(G, 0) GPIO_ACTIVE_LOW>;
        linux,code = <KEY_POWER>;
        gpio-key,wakeup;
    };
};
```

### Network Wake (Wake-on-LAN)

```bash
# Install ethtool
apt-get install ethtool

# Check WoL support
ethtool eth0 | grep -i wake

# Enable WoL
ethtool -s eth0 wol g

# Persistent WoL (add to /etc/network/interfaces)
# post-up /usr/sbin/ethtool -s eth0 wol g
```

## Yocto Integration

### Power Management Recipes

```bitbake
# File: recipes-core/images/jetson-power-image.bb

IMAGE_INSTALL_append = " \
    nvpmodel \
    tegrastats \
    cpufrequtils \
    powertop \
    ethtool \
    pm-utils \
"
```

### Custom nvpmodel Configuration

```bitbake
# File: recipes-bsp/nvpmodel/nvpmodel-config_%.bbappend

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://custom-nvpmodel.conf"

do_install_append() {
    install -m 0644 ${WORKDIR}/custom-nvpmodel.conf \
        ${D}${sysconfdir}/nvpmodel.conf
}
```

### Boot-time Power Mode

```bitbake
# File: recipes-core/initscripts/initscripts_%.bbappend

do_install_append() {
    echo "nvpmodel -m 0" >> ${D}${sysconfdir}/init.d/rcS
}
```

## Testing Procedures

### Power/Performance Test Script

```bash
#!/bin/bash

echo "=== Jetson Power/Performance Test ==="

# Test each power mode
for mode in 0 1 2 3; do
    echo -e "\n=== Testing Power Mode $mode ==="
    sudo nvpmodel -m $mode 2>/dev/null || continue

    sleep 5

    # Check settings
    nvpmodel -q

    # CPU stress test
    echo "Running CPU stress test..."
    stress-ng --cpu $(nproc) --timeout 30s &
    STRESS_PID=$!

    # Monitor for 30 seconds
    for i in {1..30}; do
        tegrastats | head -1
        sleep 1
    done

    wait $STRESS_PID

    sleep 5
done

echo -e "\n=== Test Complete ==="
```

## References

### Official Documentation
- [Jetson Power Guide](https://docs.nvidia.com/jetson/archives/r35.3.1/DeveloperGuide/text/SD/PlatformPowerAndPerformance.html)
- [nvpmodel Documentation](https://docs.nvidia.com/jetson/archives/r35.3.1/DeveloperGuide/text/SD/PlatformPowerAndPerformance/JetsonOrinNxSeriesAndJetsonAgxOrinSeries.html)

### Tools
- [jetson-stats](https://github.com/rbonghi/jetson_stats)
- [stress-ng](https://wiki.ubuntu.com/Kernel/Reference/stress-ng)

---

**Document Version**: 1.0
**Last Updated**: 2025-11
**Maintained By**: Hardware Integration Agent
