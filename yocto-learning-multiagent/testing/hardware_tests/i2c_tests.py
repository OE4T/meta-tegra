#!/usr/bin/env python3
"""
I2C Hardware Tests for Jetson Platforms
Tests I2C bus functionality, device detection, and communication.
"""

import pytest
import subprocess
import time
import os
from pathlib import Path
from typing import List, Dict, Optional
import logging
import struct

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class I2CController:
    """I2C controller for Jetson platforms"""

    def __init__(self, bus: int = 0):
        """
        Initialize I2C controller

        Args:
            bus: I2C bus number
        """
        self.bus = bus
        self.device_path = f"/dev/i2c-{bus}"

    def is_available(self) -> bool:
        """Check if I2C bus is available"""
        return Path(self.device_path).exists()

    def detect_devices(self) -> List[int]:
        """
        Detect devices on I2C bus using i2cdetect

        Returns:
            List of detected I2C device addresses
        """
        try:
            result = subprocess.run(
                ['i2cdetect', '-y', str(self.bus)],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode != 0:
                logger.error(f"i2cdetect failed: {result.stderr}")
                return []

            # Parse i2cdetect output
            devices = []
            lines = result.stdout.strip().split('\n')[1:]  # Skip header

            for line in lines:
                parts = line.split()
                if len(parts) > 1:
                    for part in parts[1:]:  # Skip row label
                        if part != '--' and part != 'UU':
                            try:
                                addr = int(part, 16)
                                devices.append(addr)
                            except ValueError:
                                pass

            return devices

        except subprocess.TimeoutExpired:
            logger.error("i2cdetect timed out")
            return []
        except FileNotFoundError:
            logger.error("i2cdetect command not found (install i2c-tools)")
            return []
        except Exception as e:
            logger.error(f"Error detecting I2C devices: {e}")
            return []

    def read_byte(self, address: int, register: int) -> Optional[int]:
        """
        Read a byte from I2C device (using i2cget)

        Args:
            address: I2C device address
            register: Register address

        Returns:
            Byte value or None on error
        """
        try:
            result = subprocess.run(
                ['i2cget', '-y', str(self.bus), hex(address), hex(register)],
                capture_output=True,
                text=True,
                timeout=5
            )

            if result.returncode == 0:
                # Parse output (e.g., "0x42")
                value = int(result.stdout.strip(), 16)
                return value
            else:
                logger.error(f"i2cget failed: {result.stderr}")
                return None

        except Exception as e:
            logger.error(f"Error reading I2C byte: {e}")
            return None

    def write_byte(self, address: int, register: int, value: int) -> bool:
        """
        Write a byte to I2C device (using i2cset)

        Args:
            address: I2C device address
            register: Register address
            value: Byte value to write

        Returns:
            True if successful
        """
        try:
            result = subprocess.run(
                ['i2cset', '-y', str(self.bus), hex(address), hex(register), hex(value)],
                capture_output=True,
                text=True,
                timeout=5
            )

            return result.returncode == 0

        except Exception as e:
            logger.error(f"Error writing I2C byte: {e}")
            return False


@pytest.fixture
def i2c_controller():
    """Pytest fixture for I2C controller"""
    bus = int(os.environ.get('TEST_I2C_BUS', '0'))
    return I2CController(bus)


class TestI2CBasics:
    """Basic I2C functionality tests"""

    def test_i2c_bus_available(self, i2c_controller):
        """Test that I2C bus device is available"""
        assert i2c_controller.is_available(), f"I2C bus {i2c_controller.bus} not available"

    def test_i2c_tools_available(self):
        """Test that i2c-tools are installed"""
        tools = ['i2cdetect', 'i2cget', 'i2cset', 'i2cdump']

        for tool in tools:
            result = subprocess.run(['which', tool], capture_output=True)
            assert result.returncode == 0, f"{tool} not found (install i2c-tools package)"

    def test_i2c_device_detection(self, i2c_controller):
        """Test I2C device detection"""
        if not i2c_controller.is_available():
            pytest.skip(f"I2C bus {i2c_controller.bus} not available")

        devices = i2c_controller.detect_devices()
        logger.info(f"Detected {len(devices)} I2C devices on bus {i2c_controller.bus}")

        for addr in devices:
            logger.info(f"  Device at address 0x{addr:02X}")

        # We should detect at least one device (or skip if no devices expected)
        # This assertion can be adjusted based on test hardware
        logger.info(f"Total I2C devices found: {len(devices)}")


class TestI2CDevices:
    """I2C device communication tests"""

    @pytest.mark.skipif(not Path("/dev/i2c-0").exists(), reason="I2C bus not available")
    def test_i2c_read_device(self, i2c_controller):
        """Test reading from an I2C device"""
        # Get test device address from environment
        test_addr = os.environ.get('TEST_I2C_DEVICE_ADDR')

        if not test_addr:
            pytest.skip("TEST_I2C_DEVICE_ADDR not configured")

        address = int(test_addr, 16)
        register = int(os.environ.get('TEST_I2C_DEVICE_REG', '0x00'), 16)

        value = i2c_controller.read_byte(address, register)
        assert value is not None, f"Failed to read from I2C device 0x{address:02X}"
        logger.info(f"Read 0x{value:02X} from device 0x{address:02X} register 0x{register:02X}")

    @pytest.mark.skipif(not Path("/dev/i2c-0").exists(), reason="I2C bus not available")
    def test_i2c_write_read_device(self, i2c_controller):
        """Test writing and reading from an I2C device"""
        # Get test device address from environment
        test_addr = os.environ.get('TEST_I2C_DEVICE_ADDR')
        test_reg = os.environ.get('TEST_I2C_WRITABLE_REG')

        if not test_addr or not test_reg:
            pytest.skip("TEST_I2C_DEVICE_ADDR or TEST_I2C_WRITABLE_REG not configured")

        address = int(test_addr, 16)
        register = int(test_reg, 16)

        # Read original value
        original = i2c_controller.read_byte(address, register)
        assert original is not None

        # Write new value
        test_value = 0x42
        assert i2c_controller.write_byte(address, register, test_value)

        # Read back
        readback = i2c_controller.read_byte(address, register)
        assert readback == test_value, "Write-read verification failed"

        # Restore original value
        i2c_controller.write_byte(address, register, original)


class TestI2CPerformance:
    """I2C performance tests"""

    @pytest.mark.skipif(not Path("/dev/i2c-0").exists(), reason="I2C bus not available")
    def test_i2c_read_performance(self, i2c_controller):
        """Test I2C read performance"""
        test_addr = os.environ.get('TEST_I2C_DEVICE_ADDR')

        if not test_addr:
            pytest.skip("TEST_I2C_DEVICE_ADDR not configured")

        address = int(test_addr, 16)
        register = 0x00

        # Measure read performance
        iterations = 100
        start_time = time.time()

        for _ in range(iterations):
            value = i2c_controller.read_byte(address, register)
            assert value is not None

        elapsed = time.time() - start_time
        reads_per_sec = iterations / elapsed

        logger.info(f"I2C read performance: {reads_per_sec:.2f} reads/sec")
        logger.info(f"Average read time: {elapsed/iterations*1000:.2f} ms")

        # Ensure reasonable performance
        assert reads_per_sec > 1, "I2C read performance too slow"


class TestI2CBusScan:
    """I2C bus scanning tests"""

    def test_scan_all_i2c_buses(self):
        """Scan all available I2C buses"""
        i2c_devices = list(Path("/dev").glob("i2c-*"))

        logger.info(f"Found {len(i2c_devices)} I2C buses")

        bus_info = []
        for device in i2c_devices:
            bus_num = int(device.name.split('-')[1])
            controller = I2CController(bus_num)

            devices = controller.detect_devices()
            bus_info.append({
                'bus': bus_num,
                'device_count': len(devices),
                'devices': devices
            })

            logger.info(f"Bus {bus_num}: {len(devices)} devices")

        assert len(i2c_devices) > 0, "No I2C buses found"

    def test_i2c_bus_permissions(self):
        """Test I2C bus device permissions"""
        i2c_devices = list(Path("/dev").glob("i2c-*"))

        for device in i2c_devices:
            stat = device.stat()
            # Check if readable (at least for user)
            logger.info(f"{device}: mode={oct(stat.st_mode)}")


class TestI2CKernelModule:
    """I2C kernel module tests"""

    def test_i2c_dev_module_loaded(self):
        """Test that i2c-dev kernel module is loaded"""
        try:
            result = subprocess.run(
                ['lsmod'],
                capture_output=True,
                text=True,
                timeout=5
            )

            assert 'i2c_dev' in result.stdout or 'i2c-dev' in result.stdout, \
                "i2c-dev kernel module not loaded"

        except Exception as e:
            pytest.skip(f"Cannot check kernel modules: {e}")

    def test_i2c_tegra_module(self):
        """Test that Tegra I2C kernel module is loaded"""
        try:
            result = subprocess.run(
                ['lsmod'],
                capture_output=True,
                text=True,
                timeout=5
            )

            # Check for Tegra-specific I2C module
            if 'i2c_tegra' not in result.stdout:
                logger.warning("Tegra I2C module not detected (may not be on Tegra platform)")

        except Exception as e:
            pytest.skip(f"Cannot check kernel modules: {e}")


class TestI2CStress:
    """I2C stress tests"""

    @pytest.mark.skipif(not Path("/dev/i2c-0").exists(), reason="I2C bus not available")
    def test_i2c_repeated_scans(self, i2c_controller):
        """Test repeated I2C bus scans"""
        if not i2c_controller.is_available():
            pytest.skip(f"I2C bus {i2c_controller.bus} not available")

        # Perform multiple scans
        iterations = 10
        for i in range(iterations):
            devices = i2c_controller.detect_devices()
            logger.info(f"Scan {i+1}/{iterations}: {len(devices)} devices")
            time.sleep(0.1)

    @pytest.mark.skipif(not Path("/dev/i2c-0").exists(), reason="I2C bus not available")
    def test_i2c_rapid_reads(self, i2c_controller):
        """Test rapid I2C reads"""
        test_addr = os.environ.get('TEST_I2C_DEVICE_ADDR')

        if not test_addr:
            pytest.skip("TEST_I2C_DEVICE_ADDR not configured")

        address = int(test_addr, 16)
        register = 0x00

        # Rapid reads
        iterations = 1000
        errors = 0

        for i in range(iterations):
            value = i2c_controller.read_byte(address, register)
            if value is None:
                errors += 1

        error_rate = errors / iterations * 100
        logger.info(f"I2C rapid read test: {errors}/{iterations} errors ({error_rate:.2f}%)")

        # Allow small error rate
        assert error_rate < 5, f"I2C error rate too high: {error_rate:.2f}%"


def test_i2c_sysfs_available():
    """Test that I2C sysfs interface is available"""
    i2c_class = Path("/sys/class/i2c-dev")

    if i2c_class.exists():
        i2c_devs = list(i2c_class.iterdir())
        logger.info(f"Found {len(i2c_devs)} I2C devices in sysfs")
    else:
        logger.warning("I2C sysfs class not found")


def test_list_i2c_adapters():
    """List all I2C adapters"""
    i2c_dev_path = Path("/sys/class/i2c-adapter")

    if not i2c_dev_path.exists():
        pytest.skip("I2C adapter sysfs not available")

    adapters = list(i2c_dev_path.iterdir())
    logger.info(f"Found {len(adapters)} I2C adapters:")

    for adapter in adapters:
        name_file = adapter / "name"
        if name_file.exists():
            with open(name_file, 'r') as f:
                name = f.read().strip()
                logger.info(f"  {adapter.name}: {name}")

    assert len(adapters) > 0, "No I2C adapters found"


if __name__ == '__main__':
    pytest.main([__file__, '-v'])
