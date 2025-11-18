#!/usr/bin/env python3
"""
GPIO Hardware Tests for Jetson Platforms
Tests GPIO functionality including input/output, interrupts, and edge detection.
"""

import pytest
import subprocess
import time
import os
from pathlib import Path
from typing import Optional, List
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class GPIOController:
    """GPIO controller for Jetson platforms"""

    def __init__(self, gpio_base: str = "/sys/class/gpio"):
        """
        Initialize GPIO controller

        Args:
            gpio_base: Base path for GPIO sysfs interface
        """
        self.gpio_base = Path(gpio_base)
        self.exported_pins = []

    def export(self, pin: int) -> bool:
        """
        Export a GPIO pin

        Args:
            pin: GPIO pin number

        Returns:
            True if successful
        """
        try:
            export_path = self.gpio_base / "export"
            with open(export_path, 'w') as f:
                f.write(str(pin))
            self.exported_pins.append(pin)
            time.sleep(0.1)  # Allow time for export
            return True
        except Exception as e:
            logger.error(f"Failed to export GPIO {pin}: {e}")
            return False

    def unexport(self, pin: int) -> bool:
        """
        Unexport a GPIO pin

        Args:
            pin: GPIO pin number

        Returns:
            True if successful
        """
        try:
            unexport_path = self.gpio_base / "unexport"
            with open(unexport_path, 'w') as f:
                f.write(str(pin))
            if pin in self.exported_pins:
                self.exported_pins.remove(pin)
            return True
        except Exception as e:
            logger.error(f"Failed to unexport GPIO {pin}: {e}")
            return False

    def set_direction(self, pin: int, direction: str) -> bool:
        """
        Set GPIO pin direction

        Args:
            pin: GPIO pin number
            direction: 'in' or 'out'

        Returns:
            True if successful
        """
        try:
            direction_path = self.gpio_base / f"gpio{pin}" / "direction"
            with open(direction_path, 'w') as f:
                f.write(direction)
            return True
        except Exception as e:
            logger.error(f"Failed to set direction for GPIO {pin}: {e}")
            return False

    def write(self, pin: int, value: int) -> bool:
        """
        Write value to GPIO pin

        Args:
            pin: GPIO pin number
            value: 0 or 1

        Returns:
            True if successful
        """
        try:
            value_path = self.gpio_base / f"gpio{pin}" / "value"
            with open(value_path, 'w') as f:
                f.write(str(value))
            return True
        except Exception as e:
            logger.error(f"Failed to write to GPIO {pin}: {e}")
            return False

    def read(self, pin: int) -> Optional[int]:
        """
        Read value from GPIO pin

        Args:
            pin: GPIO pin number

        Returns:
            Pin value (0 or 1), or None on error
        """
        try:
            value_path = self.gpio_base / f"gpio{pin}" / "value"
            with open(value_path, 'r') as f:
                return int(f.read().strip())
        except Exception as e:
            logger.error(f"Failed to read from GPIO {pin}: {e}")
            return None

    def cleanup(self):
        """Cleanup all exported pins"""
        for pin in self.exported_pins[:]:  # Copy list to avoid modification during iteration
            self.unexport(pin)


@pytest.fixture
def gpio_controller():
    """Pytest fixture for GPIO controller"""
    controller = GPIOController()
    yield controller
    controller.cleanup()


class TestGPIOBasics:
    """Basic GPIO functionality tests"""

    @pytest.mark.skipif(not os.path.exists("/sys/class/gpio"), reason="GPIO sysfs not available")
    def test_gpio_export_unexport(self, gpio_controller):
        """Test GPIO export and unexport"""
        # Use a safe GPIO pin (this should be configured for test hardware)
        test_pin = int(os.environ.get('TEST_GPIO_PIN', '0'))

        if test_pin == 0:
            pytest.skip("TEST_GPIO_PIN not configured")

        # Export pin
        assert gpio_controller.export(test_pin), "GPIO export failed"

        # Check if pin directory exists
        gpio_path = gpio_controller.gpio_base / f"gpio{test_pin}"
        assert gpio_path.exists(), f"GPIO{test_pin} directory not created"

        # Unexport pin
        assert gpio_controller.unexport(test_pin), "GPIO unexport failed"

        # Give time for cleanup
        time.sleep(0.1)

    @pytest.mark.skipif(not os.path.exists("/sys/class/gpio"), reason="GPIO sysfs not available")
    def test_gpio_direction(self, gpio_controller):
        """Test GPIO direction setting"""
        test_pin = int(os.environ.get('TEST_GPIO_PIN', '0'))

        if test_pin == 0:
            pytest.skip("TEST_GPIO_PIN not configured")

        assert gpio_controller.export(test_pin)

        # Test output direction
        assert gpio_controller.set_direction(test_pin, 'out')

        # Test input direction
        assert gpio_controller.set_direction(test_pin, 'in')


class TestGPIOOutput:
    """GPIO output functionality tests"""

    @pytest.mark.skipif(not os.path.exists("/sys/class/gpio"), reason="GPIO sysfs not available")
    def test_gpio_output_high_low(self, gpio_controller):
        """Test GPIO output high/low"""
        test_pin = int(os.environ.get('TEST_GPIO_OUTPUT_PIN', '0'))

        if test_pin == 0:
            pytest.skip("TEST_GPIO_OUTPUT_PIN not configured")

        assert gpio_controller.export(test_pin)
        assert gpio_controller.set_direction(test_pin, 'out')

        # Write high
        assert gpio_controller.write(test_pin, 1)
        value = gpio_controller.read(test_pin)
        assert value == 1, "GPIO output high failed"

        # Write low
        assert gpio_controller.write(test_pin, 0)
        value = gpio_controller.read(test_pin)
        assert value == 0, "GPIO output low failed"

    @pytest.mark.skipif(not os.path.exists("/sys/class/gpio"), reason="GPIO sysfs not available")
    def test_gpio_toggle(self, gpio_controller):
        """Test GPIO toggle functionality"""
        test_pin = int(os.environ.get('TEST_GPIO_OUTPUT_PIN', '0'))

        if test_pin == 0:
            pytest.skip("TEST_GPIO_OUTPUT_PIN not configured")

        assert gpio_controller.export(test_pin)
        assert gpio_controller.set_direction(test_pin, 'out')

        # Toggle multiple times
        for i in range(10):
            value = i % 2
            assert gpio_controller.write(test_pin, value)
            read_value = gpio_controller.read(test_pin)
            assert read_value == value, f"Toggle iteration {i} failed"
            time.sleep(0.01)


class TestGPIOInput:
    """GPIO input functionality tests"""

    @pytest.mark.skipif(not os.path.exists("/sys/class/gpio"), reason="GPIO sysfs not available")
    def test_gpio_input_read(self, gpio_controller):
        """Test GPIO input reading"""
        test_pin = int(os.environ.get('TEST_GPIO_INPUT_PIN', '0'))

        if test_pin == 0:
            pytest.skip("TEST_GPIO_INPUT_PIN not configured")

        assert gpio_controller.export(test_pin)
        assert gpio_controller.set_direction(test_pin, 'in')

        # Read value (should not raise exception)
        value = gpio_controller.read(test_pin)
        assert value is not None, "GPIO input read failed"
        assert value in [0, 1], "GPIO value should be 0 or 1"


class TestGPIOPerformance:
    """GPIO performance tests"""

    @pytest.mark.skipif(not os.path.exists("/sys/class/gpio"), reason="GPIO sysfs not available")
    def test_gpio_write_performance(self, gpio_controller):
        """Test GPIO write performance"""
        test_pin = int(os.environ.get('TEST_GPIO_OUTPUT_PIN', '0'))

        if test_pin == 0:
            pytest.skip("TEST_GPIO_OUTPUT_PIN not configured")

        assert gpio_controller.export(test_pin)
        assert gpio_controller.set_direction(test_pin, 'out')

        # Measure write performance
        iterations = 1000
        start_time = time.time()

        for i in range(iterations):
            gpio_controller.write(test_pin, i % 2)

        elapsed = time.time() - start_time
        writes_per_sec = iterations / elapsed

        logger.info(f"GPIO write performance: {writes_per_sec:.2f} writes/sec")

        # Ensure reasonable performance (>100 writes/sec)
        assert writes_per_sec > 100, "GPIO write performance too slow"

    @pytest.mark.skipif(not os.path.exists("/sys/class/gpio"), reason="GPIO sysfs not available")
    def test_gpio_read_performance(self, gpio_controller):
        """Test GPIO read performance"""
        test_pin = int(os.environ.get('TEST_GPIO_INPUT_PIN', '0'))

        if test_pin == 0:
            pytest.skip("TEST_GPIO_INPUT_PIN not configured")

        assert gpio_controller.export(test_pin)
        assert gpio_controller.set_direction(test_pin, 'in')

        # Measure read performance
        iterations = 1000
        start_time = time.time()

        for i in range(iterations):
            gpio_controller.read(test_pin)

        elapsed = time.time() - start_time
        reads_per_sec = iterations / elapsed

        logger.info(f"GPIO read performance: {reads_per_sec:.2f} reads/sec")

        # Ensure reasonable performance (>100 reads/sec)
        assert reads_per_sec > 100, "GPIO read performance too slow"


class TestGPIOStress:
    """GPIO stress tests"""

    @pytest.mark.skipif(not os.path.exists("/sys/class/gpio"), reason="GPIO sysfs not available")
    def test_gpio_rapid_export_unexport(self, gpio_controller):
        """Test rapid GPIO export/unexport cycles"""
        test_pin = int(os.environ.get('TEST_GPIO_PIN', '0'))

        if test_pin == 0:
            pytest.skip("TEST_GPIO_PIN not configured")

        # Rapid export/unexport cycles
        for i in range(100):
            assert gpio_controller.export(test_pin), f"Export failed at iteration {i}"
            time.sleep(0.01)
            assert gpio_controller.unexport(test_pin), f"Unexport failed at iteration {i}"
            time.sleep(0.01)


def test_gpio_sysfs_available():
    """Test that GPIO sysfs interface is available"""
    gpio_path = Path("/sys/class/gpio")
    assert gpio_path.exists(), "GPIO sysfs interface not available"
    assert (gpio_path / "export").exists(), "GPIO export interface not available"
    assert (gpio_path / "unexport").exists(), "GPIO unexport interface not available"


def test_gpio_chip_detection():
    """Test GPIO chip detection"""
    gpiochip_path = Path("/sys/class/gpio")

    if not gpiochip_path.exists():
        pytest.skip("GPIO sysfs not available")

    # Find gpiochip entries
    gpiochips = list(gpiochip_path.glob("gpiochip*"))

    logger.info(f"Found {len(gpiochips)} GPIO chips:")
    for chip in gpiochips:
        label_file = chip / "label"
        if label_file.exists():
            with open(label_file, 'r') as f:
                label = f.read().strip()
                logger.info(f"  {chip.name}: {label}")

    assert len(gpiochips) > 0, "No GPIO chips found"


if __name__ == '__main__':
    pytest.main([__file__, '-v'])
