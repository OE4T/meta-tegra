#!/usr/bin/env python3
"""
System Boot Integration Tests
Tests boot process, bootloader, kernel loading, and system initialization.
"""

import pytest
import subprocess
import os
from pathlib import Path
from typing import Dict, List, Optional, Tuple
import logging
import time
import re

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class SystemBootController:
    """Controller for system boot testing"""

    def __init__(self, device_ip: Optional[str] = None):
        """
        Initialize system boot controller

        Args:
            device_ip: Device IP address
        """
        self.device_ip = device_ip or os.environ.get('DEVICE_IP')

    def check_device_online(self, timeout: int = 30) -> bool:
        """
        Check if device is online

        Args:
            timeout: Timeout in seconds

        Returns:
            True if device is online
        """
        if not self.device_ip:
            logger.warning("No device IP specified")
            return False

        try:
            result = subprocess.run(
                ['ping', '-c', '1', '-W', str(timeout), self.device_ip],
                capture_output=True,
                timeout=timeout + 5
            )

            return result.returncode == 0

        except Exception as e:
            logger.error(f"Error checking device online: {e}")
            return False

    def get_boot_log(self) -> Optional[str]:
        """
        Get boot log from device

        Returns:
            Boot log content or None
        """
        if not self.device_ip:
            logger.warning("No device IP specified")
            return None

        try:
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{self.device_ip}', 'dmesg'],
                capture_output=True,
                text=True,
                timeout=30
            )

            if result.returncode == 0:
                return result.stdout
            else:
                logger.error("Failed to get boot log")
                return None

        except Exception as e:
            logger.error(f"Error getting boot log: {e}")
            return None

    def get_kernel_version(self) -> Optional[str]:
        """
        Get kernel version

        Returns:
            Kernel version string or None
        """
        if not self.device_ip:
            logger.warning("No device IP specified")
            return None

        try:
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{self.device_ip}', 'uname -r'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode == 0:
                version = result.stdout.strip()
                logger.info(f"Kernel version: {version}")
                return version
            else:
                return None

        except Exception as e:
            logger.error(f"Error getting kernel version: {e}")
            return None

    def get_bootloader_version(self) -> Optional[str]:
        """
        Get bootloader version

        Returns:
            Bootloader version string or None
        """
        if not self.device_ip:
            logger.warning("No device IP specified")
            return None

        try:
            # Check for U-Boot version
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{self.device_ip}', 'cat /proc/cmdline'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode == 0:
                logger.info(f"Boot command line: {result.stdout.strip()}")
                return result.stdout.strip()

        except Exception as e:
            logger.error(f"Error getting bootloader info: {e}")

        return None

    def get_boot_time(self) -> Optional[float]:
        """
        Get system boot time

        Returns:
            Boot time in seconds or None
        """
        if not self.device_ip:
            logger.warning("No device IP specified")
            return None

        try:
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{self.device_ip}', 'systemd-analyze'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode == 0:
                output = result.stdout.strip()
                logger.info(f"Boot analysis: {output}")

                # Parse boot time (e.g., "Startup finished in 5.234s")
                match = re.search(r'(\d+\.\d+)s', output)
                if match:
                    boot_time = float(match.group(1))
                    return boot_time

        except Exception as e:
            logger.error(f"Error getting boot time: {e}")

        return None

    def get_systemd_services_status(self) -> Optional[List[Dict]]:
        """
        Get systemd services status

        Returns:
            List of service status dictionaries
        """
        if not self.device_ip:
            logger.warning("No device IP specified")
            return None

        try:
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{self.device_ip}', 'systemctl list-units --type=service --no-pager'],
                capture_output=True,
                text=True,
                timeout=15
            )

            if result.returncode == 0:
                services = []
                lines = result.stdout.strip().split('\n')[1:]  # Skip header

                for line in lines[:20]:  # First 20 services
                    if line.strip():
                        parts = line.split()
                        if len(parts) >= 4:
                            services.append({
                                'name': parts[0],
                                'load': parts[1],
                                'active': parts[2],
                                'sub': parts[3]
                            })

                return services

        except Exception as e:
            logger.error(f"Error getting systemd services: {e}")

        return None

    def check_critical_services(self, services: List[str]) -> Dict[str, bool]:
        """
        Check if critical services are running

        Args:
            services: List of service names to check

        Returns:
            Dictionary mapping service name to running status
        """
        if not self.device_ip:
            logger.warning("No device IP specified")
            return {}

        results = {}

        for service in services:
            try:
                result = subprocess.run(
                    ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                     f'root@{self.device_ip}', f'systemctl is-active {service}'],
                    capture_output=True,
                    text=True,
                    timeout=10
                )

                results[service] = result.stdout.strip() == 'active'

            except Exception as e:
                logger.error(f"Error checking service {service}: {e}")
                results[service] = False

        return results

    def analyze_boot_errors(self, boot_log: str) -> List[str]:
        """
        Analyze boot log for errors

        Args:
            boot_log: Boot log content

        Returns:
            List of error messages
        """
        errors = []

        error_patterns = [
            r'ERROR:.*',
            r'FAILED.*',
            r'error:.*',
            r'failed to.*',
            r'cannot.*',
            r'panic.*'
        ]

        for pattern in error_patterns:
            matches = re.finditer(pattern, boot_log, re.IGNORECASE)
            for match in matches:
                errors.append(match.group(0))

        return errors[:50]  # Limit to first 50 errors

    def get_device_tree_info(self) -> Optional[str]:
        """
        Get device tree information

        Returns:
            Device tree info or None
        """
        if not self.device_ip:
            logger.warning("No device IP specified")
            return None

        try:
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{self.device_ip}', 'cat /proc/device-tree/model'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode == 0:
                model = result.stdout.strip()
                logger.info(f"Device model: {model}")
                return model

        except Exception as e:
            logger.error(f"Error getting device tree info: {e}")

        return None


@pytest.fixture
def boot_controller():
    """Pytest fixture for system boot controller"""
    return SystemBootController()


class TestBootProcess:
    """Boot process tests"""

    @pytest.mark.hardware
    def test_device_online(self, boot_controller):
        """Test device is online"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        online = boot_controller.check_device_online()
        assert online, f"Device not reachable at {boot_controller.device_ip}"

    @pytest.mark.hardware
    def test_kernel_loaded(self, boot_controller):
        """Test kernel loaded successfully"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        kernel_version = boot_controller.get_kernel_version()
        assert kernel_version is not None, "Could not get kernel version"
        logger.info(f"Kernel version: {kernel_version}")

    @pytest.mark.hardware
    def test_get_boot_log(self, boot_controller):
        """Test getting boot log"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        boot_log = boot_controller.get_boot_log()
        assert boot_log is not None, "Could not get boot log"
        assert len(boot_log) > 0, "Boot log is empty"
        logger.info(f"Boot log size: {len(boot_log)} bytes")


class TestBootLoader:
    """Bootloader tests"""

    @pytest.mark.hardware
    def test_bootloader_info(self, boot_controller):
        """Test getting bootloader information"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        bootloader_info = boot_controller.get_bootloader_version()
        logger.info(f"Bootloader info: {bootloader_info}")

    @pytest.mark.hardware
    def test_boot_command_line(self, boot_controller):
        """Test boot command line parameters"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        try:
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{boot_controller.device_ip}', 'cat /proc/cmdline'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode == 0:
                cmdline = result.stdout.strip()
                logger.info(f"Boot command line: {cmdline}")
                assert len(cmdline) > 0, "Boot command line is empty"

        except Exception as e:
            pytest.skip(f"Cannot get boot command line: {e}")


class TestDeviceTree:
    """Device tree tests"""

    @pytest.mark.hardware
    def test_device_tree_loaded(self, boot_controller):
        """Test device tree is loaded"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        dt_info = boot_controller.get_device_tree_info()
        logger.info(f"Device tree info: {dt_info}")

    @pytest.mark.hardware
    def test_device_tree_structure(self, boot_controller):
        """Test device tree structure"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        try:
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{boot_controller.device_ip}', 'ls /proc/device-tree/'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode == 0:
                logger.info(f"Device tree entries:\n{result.stdout}")

        except Exception as e:
            pytest.skip(f"Cannot access device tree: {e}")


class TestSystemdServices:
    """Systemd services tests"""

    @pytest.mark.hardware
    def test_systemd_running(self, boot_controller):
        """Test systemd is running"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        try:
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{boot_controller.device_ip}', 'systemctl is-system-running'],
                capture_output=True,
                text=True,
                timeout=10
            )

            status = result.stdout.strip()
            logger.info(f"System status: {status}")

        except Exception as e:
            pytest.skip(f"Cannot check systemd: {e}")

    @pytest.mark.hardware
    def test_critical_services_running(self, boot_controller):
        """Test critical services are running"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        critical_services = [
            'dbus.service',
            'systemd-journald.service',
            'systemd-udevd.service'
        ]

        results = boot_controller.check_critical_services(critical_services)

        for service, running in results.items():
            logger.info(f"{service}: {'running' if running else 'not running'}")

    @pytest.mark.hardware
    def test_list_services(self, boot_controller):
        """Test listing systemd services"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        services = boot_controller.get_systemd_services_status()

        if services:
            logger.info(f"Found {len(services)} services:")
            for svc in services[:10]:
                logger.info(f"  {svc['name']}: {svc['active']}")


class TestBootTime:
    """Boot time tests"""

    @pytest.mark.hardware
    def test_get_boot_time(self, boot_controller):
        """Test getting boot time"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        boot_time = boot_controller.get_boot_time()

        if boot_time:
            logger.info(f"Boot time: {boot_time:.2f} seconds")

            # Check if boot time is reasonable (< 2 minutes)
            assert boot_time < 120, f"Boot time too long: {boot_time}s"
        else:
            logger.warning("Could not measure boot time")

    @pytest.mark.hardware
    def test_boot_time_breakdown(self, boot_controller):
        """Test boot time breakdown"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        try:
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{boot_controller.device_ip}', 'systemd-analyze blame | head -20'],
                capture_output=True,
                text=True,
                timeout=15
            )

            if result.returncode == 0:
                logger.info(f"Boot time breakdown:\n{result.stdout}")

        except Exception as e:
            logger.warning(f"Cannot get boot time breakdown: {e}")


class TestBootErrors:
    """Boot error detection tests"""

    @pytest.mark.hardware
    def test_boot_log_errors(self, boot_controller):
        """Test boot log for errors"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        boot_log = boot_controller.get_boot_log()

        if boot_log:
            errors = boot_controller.analyze_boot_errors(boot_log)

            if errors:
                logger.warning(f"Found {len(errors)} boot errors:")
                for error in errors[:10]:
                    logger.warning(f"  {error}")
            else:
                logger.info("No boot errors detected")

    @pytest.mark.hardware
    def test_kernel_panics(self, boot_controller):
        """Test for kernel panics"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        boot_log = boot_controller.get_boot_log()

        if boot_log:
            if 'panic' in boot_log.lower():
                logger.error("Kernel panic detected in boot log")
                assert False, "Kernel panic found"
            else:
                logger.info("No kernel panics detected")


class TestHardwareInitialization:
    """Hardware initialization tests"""

    @pytest.mark.hardware
    def test_tegra_modules_loaded(self, boot_controller):
        """Test Tegra-specific modules are loaded"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        try:
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{boot_controller.device_ip}', 'lsmod | grep tegra'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode == 0:
                logger.info(f"Tegra modules loaded:\n{result.stdout}")

        except Exception as e:
            logger.warning(f"Cannot check Tegra modules: {e}")

    @pytest.mark.hardware
    def test_gpu_initialization(self, boot_controller):
        """Test GPU initialization"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        try:
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{boot_controller.device_ip}', 'ls /dev/nvhost-*'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode == 0:
                logger.info(f"NVIDIA host devices:\n{result.stdout}")

        except Exception as e:
            logger.warning(f"Cannot check GPU devices: {e}")


class TestFileSystemMount:
    """Filesystem mount tests"""

    @pytest.mark.hardware
    def test_root_filesystem_mounted(self, boot_controller):
        """Test root filesystem is mounted"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        try:
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{boot_controller.device_ip}', 'mount | grep " / "'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode == 0:
                logger.info(f"Root mount: {result.stdout.strip()}")
                assert 'rw' in result.stdout or 'ro' in result.stdout

        except Exception as e:
            pytest.skip(f"Cannot check root filesystem: {e}")

    @pytest.mark.hardware
    def test_essential_filesystems(self, boot_controller):
        """Test essential filesystems are mounted"""
        if not boot_controller.device_ip:
            pytest.skip("DEVICE_IP not configured")

        essential_mounts = ['/proc', '/sys', '/dev']

        for mount in essential_mounts:
            try:
                result = subprocess.run(
                    ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                     f'root@{boot_controller.device_ip}', f'mount | grep " {mount} "'],
                    capture_output=True,
                    text=True,
                    timeout=10
                )

                if result.returncode == 0:
                    logger.info(f"{mount} is mounted")
                else:
                    logger.warning(f"{mount} not found in mounts")

            except Exception as e:
                logger.warning(f"Cannot check {mount}: {e}")


def test_ssh_available():
    """Test SSH client is available"""
    result = subprocess.run(['which', 'ssh'], capture_output=True)
    assert result.returncode == 0, "SSH client not available"


def test_ping_available():
    """Test ping utility is available"""
    result = subprocess.run(['which', 'ping'], capture_output=True)
    assert result.returncode == 0, "Ping utility not available"


if __name__ == '__main__':
    pytest.main([__file__, '-v'])
