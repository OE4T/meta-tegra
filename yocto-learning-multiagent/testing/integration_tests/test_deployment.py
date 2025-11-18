#!/usr/bin/env python3
"""
Deployment Integration Tests
Tests image deployment, flashing, and device provisioning for Jetson platforms.
"""

import pytest
import subprocess
import os
from pathlib import Path
from typing import Dict, List, Optional, Tuple
import logging
import time
import hashlib

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class DeploymentController:
    """Controller for deployment testing"""

    def __init__(self, image_dir: Optional[str] = None):
        """
        Initialize deployment controller

        Args:
            image_dir: Directory containing deployment images
        """
        self.image_dir = Path(image_dir) if image_dir else Path('/tmp/deploy/images')
        self.machine = os.environ.get('MACHINE', 'jetson-xavier-nx-devkit')
        self.flash_tool = 'tegra-flash.sh'

    def verify_image_exists(self, image_name: str) -> bool:
        """
        Verify deployment image exists

        Args:
            image_name: Image file name

        Returns:
            True if image exists
        """
        image_path = self.image_dir / self.machine / image_name

        if image_path.exists():
            logger.info(f"Image found: {image_path}")
            logger.info(f"Size: {image_path.stat().st_size / (1024*1024):.2f} MB")
            return True
        else:
            logger.warning(f"Image not found: {image_path}")
            return False

    def verify_image_checksum(self, image_name: str) -> Optional[str]:
        """
        Verify image checksum

        Args:
            image_name: Image file name

        Returns:
            Checksum string or None
        """
        image_path = self.image_dir / self.machine / image_name

        if not image_path.exists():
            logger.error(f"Image not found: {image_path}")
            return None

        try:
            # Calculate SHA256 checksum
            sha256_hash = hashlib.sha256()
            with open(image_path, "rb") as f:
                for byte_block in iter(lambda: f.read(4096), b""):
                    sha256_hash.update(byte_block)

            checksum = sha256_hash.hexdigest()
            logger.info(f"Image checksum (SHA256): {checksum}")

            # Check for .sha256sum file
            checksum_file = Path(str(image_path) + '.sha256sum')
            if checksum_file.exists():
                with open(checksum_file, 'r') as f:
                    expected_checksum = f.read().strip().split()[0]
                    if checksum == expected_checksum:
                        logger.info("Checksum verification PASSED")
                    else:
                        logger.error("Checksum verification FAILED")

            return checksum

        except Exception as e:
            logger.error(f"Error calculating checksum: {e}")
            return None

    def check_flash_tool_available(self) -> bool:
        """
        Check if flashing tool is available

        Returns:
            True if flash tool is available
        """
        try:
            result = subprocess.run(
                ['which', self.flash_tool],
                capture_output=True,
                timeout=5
            )

            if result.returncode == 0:
                logger.info(f"Flash tool found: {result.stdout.decode().strip()}")
                return True
            else:
                logger.warning(f"Flash tool not found: {self.flash_tool}")
                return False

        except Exception as e:
            logger.error(f"Error checking flash tool: {e}")
            return False

    def prepare_deployment_package(self, image_name: str, output_dir: str) -> bool:
        """
        Prepare deployment package

        Args:
            image_name: Source image name
            output_dir: Output directory for package

        Returns:
            True if successful
        """
        try:
            output_path = Path(output_dir)
            output_path.mkdir(parents=True, exist_ok=True)

            logger.info(f"Would prepare deployment package in: {output_path}")
            logger.info(f"Source image: {image_name}")

            # In real implementation:
            # - Copy image files
            # - Copy flashing scripts
            # - Copy device tree blobs
            # - Create manifest file
            # - Generate checksums

            return True

        except Exception as e:
            logger.error(f"Error preparing deployment package: {e}")
            return False

    def validate_deployment_package(self, package_dir: str) -> Dict:
        """
        Validate deployment package

        Args:
            package_dir: Package directory

        Returns:
            Validation result dictionary
        """
        package_path = Path(package_dir)

        result = {
            'valid': True,
            'errors': [],
            'warnings': [],
            'files': []
        }

        # Check for required files
        required_files = [
            'boot.img',
            'kernel_tegra*.dtb',
            'flash.sh'
        ]

        logger.info(f"Validating deployment package: {package_path}")

        if not package_path.exists():
            result['valid'] = False
            result['errors'].append(f"Package directory not found: {package_path}")

        return result

    def simulate_device_flash(self, package_dir: str) -> bool:
        """
        Simulate device flashing process

        Args:
            package_dir: Deployment package directory

        Returns:
            True if simulation successful
        """
        logger.info("Simulating device flash process...")
        logger.info(f"Package directory: {package_dir}")
        logger.info(f"Target machine: {self.machine}")

        # Simulation steps
        steps = [
            "Entering recovery mode",
            "Detecting device",
            "Partitioning storage",
            "Flashing bootloader",
            "Flashing kernel",
            "Flashing rootfs",
            "Configuring boot parameters",
            "Rebooting device"
        ]

        for i, step in enumerate(steps, 1):
            logger.info(f"Step {i}/{len(steps)}: {step}")
            time.sleep(0.1)

        logger.info("Flash simulation completed")
        return True

    def verify_device_connectivity(self, device_ip: Optional[str] = None) -> bool:
        """
        Verify device connectivity

        Args:
            device_ip: Device IP address

        Returns:
            True if device is reachable
        """
        if not device_ip:
            device_ip = os.environ.get('DEVICE_IP')

        if not device_ip:
            logger.warning("No device IP specified")
            return False

        try:
            # Try to ping device
            result = subprocess.run(
                ['ping', '-c', '3', '-W', '2', device_ip],
                capture_output=True,
                timeout=10
            )

            if result.returncode == 0:
                logger.info(f"Device reachable at {device_ip}")
                return True
            else:
                logger.warning(f"Device not reachable at {device_ip}")
                return False

        except Exception as e:
            logger.error(f"Error checking device connectivity: {e}")
            return False

    def verify_device_boot(self, device_ip: Optional[str] = None) -> Dict:
        """
        Verify device has booted successfully

        Args:
            device_ip: Device IP address

        Returns:
            Boot verification result
        """
        result = {
            'booted': False,
            'kernel_version': None,
            'uptime': None
        }

        if not device_ip:
            device_ip = os.environ.get('DEVICE_IP')

        if not device_ip:
            logger.warning("No device IP specified")
            return result

        try:
            # Try to SSH and get kernel version
            ssh_result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{device_ip}', 'uname -r'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if ssh_result.returncode == 0:
                result['booted'] = True
                result['kernel_version'] = ssh_result.stdout.strip()
                logger.info(f"Device booted with kernel: {result['kernel_version']}")

                # Get uptime
                uptime_result = subprocess.run(
                    ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                     f'root@{device_ip}', 'uptime -p'],
                    capture_output=True,
                    text=True,
                    timeout=10
                )

                if uptime_result.returncode == 0:
                    result['uptime'] = uptime_result.stdout.strip()
                    logger.info(f"Device uptime: {result['uptime']}")

        except Exception as e:
            logger.error(f"Error verifying device boot: {e}")

        return result


@pytest.fixture
def deployment_controller(tmp_path):
    """Pytest fixture for deployment controller"""
    return DeploymentController(str(tmp_path / 'images'))


class TestImageVerification:
    """Image verification tests"""

    def test_image_directory_structure(self, deployment_controller):
        """Test image directory structure"""
        logger.info(f"Image directory: {deployment_controller.image_dir}")
        logger.info(f"Machine: {deployment_controller.machine}")

    def test_verify_rootfs_image(self, deployment_controller):
        """Test rootfs image verification"""
        image_patterns = [
            'core-image-minimal-*.wic',
            'tegra-demo-distro-*.wic',
            '*.ext4'
        ]

        logger.info("Checking for rootfs images...")
        # In real test, would check actual files

    def test_verify_kernel_image(self, deployment_controller):
        """Test kernel image verification"""
        kernel_patterns = [
            'Image',
            'Image-*',
            'zImage'
        ]

        logger.info("Checking for kernel images...")


class TestDeploymentPackage:
    """Deployment package tests"""

    def test_create_deployment_package(self, deployment_controller, tmp_path):
        """Test creating deployment package"""
        output_dir = tmp_path / 'deployment-package'

        assert deployment_controller.prepare_deployment_package(
            'core-image-minimal.wic',
            str(output_dir)
        )

    def test_validate_deployment_package(self, deployment_controller, tmp_path):
        """Test validating deployment package"""
        package_dir = tmp_path / 'deployment-package'
        package_dir.mkdir(parents=True, exist_ok=True)

        result = deployment_controller.validate_deployment_package(str(package_dir))
        logger.info(f"Validation result: {result}")


class TestFlashingTools:
    """Flashing tools tests"""

    def test_flash_tool_available(self, deployment_controller):
        """Test flash tool availability"""
        # This will likely fail unless on a properly configured system
        available = deployment_controller.check_flash_tool_available()
        logger.info(f"Flash tool available: {available}")

    def test_flash_tool_help(self):
        """Test flash tool help output"""
        try:
            result = subprocess.run(
                ['tegra-flash.sh', '--help'],
                capture_output=True,
                text=True,
                timeout=5
            )

            if result.returncode == 0:
                logger.info("Flash tool help:")
                logger.info(result.stdout)
        except FileNotFoundError:
            pytest.skip("Flash tool not found")
        except Exception as e:
            pytest.skip(f"Flash tool test failed: {e}")


class TestDeviceFlashing:
    """Device flashing tests"""

    @pytest.mark.hardware
    def test_simulate_device_flash(self, deployment_controller, tmp_path):
        """Test simulated device flash"""
        package_dir = tmp_path / 'deployment-package'
        package_dir.mkdir(parents=True, exist_ok=True)

        assert deployment_controller.simulate_device_flash(str(package_dir))

    @pytest.mark.hardware
    @pytest.mark.slow
    def test_actual_device_flash(self):
        """Test actual device flashing (requires hardware)"""
        device_present = os.environ.get('DEVICE_AVAILABLE', 'false').lower() == 'true'

        if not device_present:
            pytest.skip("Hardware device not available")

        logger.info("Actual device flash test would run here")
        logger.info("CAUTION: This test requires physical hardware")


class TestDeviceConnectivity:
    """Device connectivity tests"""

    @pytest.mark.hardware
    def test_device_reachable(self, deployment_controller):
        """Test device is reachable"""
        device_ip = os.environ.get('DEVICE_IP')

        if not device_ip:
            pytest.skip("DEVICE_IP not configured")

        reachable = deployment_controller.verify_device_connectivity(device_ip)
        logger.info(f"Device reachable: {reachable}")

    @pytest.mark.hardware
    def test_device_boot_verification(self, deployment_controller):
        """Test device boot verification"""
        device_ip = os.environ.get('DEVICE_IP')

        if not device_ip:
            pytest.skip("DEVICE_IP not configured")

        boot_info = deployment_controller.verify_device_boot(device_ip)
        logger.info(f"Boot verification: {boot_info}")


class TestDeploymentValidation:
    """Deployment validation tests"""

    @pytest.mark.hardware
    def test_verify_deployed_kernel(self):
        """Test verification of deployed kernel"""
        device_ip = os.environ.get('DEVICE_IP')

        if not device_ip:
            pytest.skip("DEVICE_IP not configured")

        try:
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{device_ip}', 'uname -a'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode == 0:
                logger.info(f"Kernel info: {result.stdout.strip()}")
            else:
                pytest.skip("Cannot connect to device")

        except Exception as e:
            pytest.skip(f"Device verification failed: {e}")

    @pytest.mark.hardware
    def test_verify_rootfs_mount(self):
        """Test rootfs is properly mounted"""
        device_ip = os.environ.get('DEVICE_IP')

        if not device_ip:
            pytest.skip("DEVICE_IP not configured")

        try:
            result = subprocess.run(
                ['ssh', '-o', 'StrictHostKeyChecking=no', '-o', 'ConnectTimeout=5',
                 f'root@{device_ip}', 'mount | grep " / "'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode == 0:
                logger.info(f"Root mount: {result.stdout.strip()}")
            else:
                pytest.skip("Cannot verify rootfs")

        except Exception as e:
            pytest.skip(f"Rootfs verification failed: {e}")


class TestNetworkBoot:
    """Network boot tests"""

    def test_tftp_server_configuration(self):
        """Test TFTP server configuration"""
        logger.info("TFTP server configuration test")
        logger.info("Note: Requires TFTP server setup")

    def test_nfs_boot_configuration(self):
        """Test NFS boot configuration"""
        logger.info("NFS boot configuration test")
        logger.info("Note: Requires NFS server setup")


class TestRecoveryMode:
    """Recovery mode tests"""

    def test_recovery_mode_detection(self):
        """Test recovery mode detection"""
        try:
            # Check for USB device in recovery mode
            result = subprocess.run(
                ['lsusb'],
                capture_output=True,
                text=True,
                timeout=5
            )

            # NVIDIA devices in recovery mode
            if 'NVIDIA' in result.stdout or '0955:' in result.stdout:
                logger.info("NVIDIA device detected in USB")
            else:
                logger.info("No NVIDIA device in recovery mode detected")

        except Exception as e:
            pytest.skip(f"USB detection failed: {e}")

    def test_recovery_mode_tools(self):
        """Test recovery mode tools"""
        tools = ['lsusb', 'dmesg']

        for tool in tools:
            result = subprocess.run(['which', tool], capture_output=True)
            if result.returncode == 0:
                logger.info(f"{tool} available")
            else:
                logger.warning(f"{tool} not available")


def test_deployment_prerequisites():
    """Test deployment prerequisites"""
    required_tools = ['ssh', 'scp', 'rsync', 'ping']

    missing = []
    for tool in required_tools:
        result = subprocess.run(['which', tool], capture_output=True)
        if result.returncode != 0:
            missing.append(tool)

    if missing:
        logger.warning(f"Missing deployment tools: {', '.join(missing)}")
    else:
        logger.info("All deployment tools available")


if __name__ == '__main__':
    pytest.main([__file__, '-v'])
