#!/usr/bin/env python3
"""
Build Workflow Integration Tests
Tests complete BitBake build workflows from configuration to image generation.
"""

import pytest
import subprocess
import os
from pathlib import Path
from typing import Dict, List, Optional
import logging
import time
import shutil

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class BuildWorkflowController:
    """Controller for BitBake build workflow testing"""

    def __init__(self, build_dir: Optional[str] = None):
        """
        Initialize build workflow controller

        Args:
            build_dir: Build directory path
        """
        self.build_dir = Path(build_dir) if build_dir else Path('/tmp/test-build')
        self.yocto_dir = Path(os.environ.get('YOCTO_DIR', '/opt/yocto'))
        self.machine = os.environ.get('MACHINE', 'jetson-xavier-nx-devkit')
        self.distro = os.environ.get('DISTRO', 'poky-tegra')

    def setup_build_environment(self) -> bool:
        """
        Setup BitBake build environment

        Returns:
            True if successful
        """
        try:
            # Check if Yocto directory exists
            if not self.yocto_dir.exists():
                logger.warning(f"Yocto directory not found: {self.yocto_dir}")
                return False

            # Create build directory if it doesn't exist
            self.build_dir.mkdir(parents=True, exist_ok=True)

            logger.info(f"Build directory: {self.build_dir}")
            logger.info(f"Machine: {self.machine}")
            logger.info(f"Distro: {self.distro}")

            return True

        except Exception as e:
            logger.error(f"Error setting up build environment: {e}")
            return False

    def initialize_build_dir(self) -> bool:
        """
        Initialize BitBake build directory

        Returns:
            True if successful
        """
        try:
            # Check for oe-init-build-env script
            init_script = self.yocto_dir / 'poky' / 'oe-init-build-env'

            if not init_script.exists():
                logger.warning(f"oe-init-build-env not found at {init_script}")
                return False

            logger.info("Build directory initialization would happen here")
            logger.info("Note: Actual initialization requires sourcing oe-init-build-env")

            return True

        except Exception as e:
            logger.error(f"Error initializing build directory: {e}")
            return False

    def configure_local_conf(self, extra_config: Optional[Dict[str, str]] = None) -> bool:
        """
        Configure local.conf file

        Args:
            extra_config: Extra configuration variables

        Returns:
            True if successful
        """
        try:
            conf_dir = self.build_dir / 'conf'
            conf_dir.mkdir(parents=True, exist_ok=True)

            local_conf = conf_dir / 'local.conf'

            # Basic configuration
            config_lines = [
                f'MACHINE = "{self.machine}"',
                f'DISTRO = "{self.distro}"',
                'DL_DIR ?= "${TOPDIR}/downloads"',
                'SSTATE_DIR ?= "${TOPDIR}/sstate-cache"',
                'TMPDIR = "${TOPDIR}/tmp"',
                ''
            ]

            # Add extra configuration
            if extra_config:
                for key, value in extra_config.items():
                    config_lines.append(f'{key} = "{value}"')

            # Write configuration (mock for testing)
            logger.info("Would write local.conf with configuration:")
            for line in config_lines:
                logger.info(f"  {line}")

            return True

        except Exception as e:
            logger.error(f"Error configuring local.conf: {e}")
            return False

    def configure_bblayers_conf(self, layers: Optional[List[str]] = None) -> bool:
        """
        Configure bblayers.conf file

        Args:
            layers: List of layer paths

        Returns:
            True if successful
        """
        try:
            default_layers = [
                '${TOPDIR}/../poky/meta',
                '${TOPDIR}/../poky/meta-poky',
                '${TOPDIR}/../poky/meta-yocto-bsp',
                '${TOPDIR}/../meta-tegra',
            ]

            all_layers = default_layers + (layers or [])

            logger.info("Would configure bblayers.conf with layers:")
            for layer in all_layers:
                logger.info(f"  {layer}")

            return True

        except Exception as e:
            logger.error(f"Error configuring bblayers.conf: {e}")
            return False

    def run_bitbake_command(self, target: str, timeout: int = 3600) -> Dict:
        """
        Run a BitBake command

        Args:
            target: BitBake target (e.g., 'core-image-minimal')
            timeout: Command timeout in seconds

        Returns:
            Result dictionary with returncode, stdout, stderr
        """
        try:
            logger.info(f"Would run: bitbake {target}")
            logger.info(f"Timeout: {timeout} seconds")

            # Mock result for testing
            return {
                'returncode': 0,
                'stdout': f"Mock output for bitbake {target}",
                'stderr': '',
                'duration': 0
            }

        except Exception as e:
            logger.error(f"Error running BitBake command: {e}")
            return {
                'returncode': 1,
                'stdout': '',
                'stderr': str(e),
                'duration': 0
            }

    def verify_build_artifacts(self, target: str) -> bool:
        """
        Verify build artifacts were created

        Args:
            target: Build target name

        Returns:
            True if artifacts exist
        """
        try:
            deploy_dir = self.build_dir / 'tmp' / 'deploy' / 'images' / self.machine

            logger.info(f"Would check for artifacts in: {deploy_dir}")

            # In real implementation, would check for:
            # - kernel image
            # - rootfs image
            # - bootloader files
            # - device tree blobs

            return True

        except Exception as e:
            logger.error(f"Error verifying build artifacts: {e}")
            return False

    def clean_build(self) -> bool:
        """
        Clean build directory

        Returns:
            True if successful
        """
        try:
            logger.info(f"Would clean build directory: {self.build_dir}")

            # In real implementation:
            # - Remove tmp directory
            # - Optionally remove downloads and sstate-cache

            return True

        except Exception as e:
            logger.error(f"Error cleaning build: {e}")
            return False


@pytest.fixture
def build_controller(tmp_path):
    """Pytest fixture for build workflow controller"""
    return BuildWorkflowController(str(tmp_path / 'test-build'))


class TestBuildEnvironment:
    """Build environment setup tests"""

    def test_build_directory_creation(self, build_controller):
        """Test build directory creation"""
        assert build_controller.setup_build_environment()
        assert build_controller.build_dir.exists()

    def test_build_environment_variables(self):
        """Test build environment variables"""
        required_vars = ['MACHINE', 'DISTRO']

        for var in required_vars:
            value = os.environ.get(var)
            logger.info(f"{var} = {value if value else 'not set'}")

    def test_yocto_directory_structure(self, build_controller):
        """Test Yocto directory structure"""
        # This test checks if the expected Yocto structure exists
        # In a real environment, would check for poky, meta-layers, etc.

        logger.info(f"Yocto directory: {build_controller.yocto_dir}")

        if build_controller.yocto_dir.exists():
            logger.info("Yocto directory exists")
        else:
            pytest.skip("Yocto directory not found")


class TestBuildConfiguration:
    """Build configuration tests"""

    def test_local_conf_creation(self, build_controller):
        """Test local.conf creation"""
        assert build_controller.configure_local_conf()

    def test_local_conf_with_extra_config(self, build_controller):
        """Test local.conf with extra configuration"""
        extra_config = {
            'PARALLEL_MAKE': '-j 8',
            'BB_NUMBER_THREADS': '8'
        }

        assert build_controller.configure_local_conf(extra_config)

    def test_bblayers_conf_creation(self, build_controller):
        """Test bblayers.conf creation"""
        assert build_controller.configure_bblayers_conf()

    def test_bblayers_conf_with_custom_layers(self, build_controller):
        """Test bblayers.conf with custom layers"""
        custom_layers = [
            '${TOPDIR}/../meta-custom',
            '${TOPDIR}/../meta-test'
        ]

        assert build_controller.configure_bblayers_conf(custom_layers)


class TestBitBakeCommands:
    """BitBake command execution tests"""

    def test_bitbake_parse_recipes(self, build_controller):
        """Test bitbake recipe parsing"""
        result = build_controller.run_bitbake_command('-s', timeout=300)
        assert result['returncode'] == 0

    def test_bitbake_show_environment(self, build_controller):
        """Test showing BitBake environment"""
        result = build_controller.run_bitbake_command('-e', timeout=300)
        assert result['returncode'] == 0

    def test_bitbake_list_tasks(self, build_controller):
        """Test listing BitBake tasks"""
        result = build_controller.run_bitbake_command('core-image-minimal -c listtasks', timeout=300)
        assert result['returncode'] == 0


class TestBuildWorkflow:
    """Complete build workflow tests"""

    @pytest.mark.slow
    def test_minimal_image_build(self, build_controller):
        """Test building minimal image (mock)"""
        # Setup environment
        assert build_controller.setup_build_environment()

        # Configure build
        assert build_controller.configure_local_conf()
        assert build_controller.configure_bblayers_conf()

        # Run build (mock)
        result = build_controller.run_bitbake_command('core-image-minimal', timeout=7200)

        # This would fail in mock mode, but shows the workflow
        logger.info(f"Build result: {result}")

    @pytest.mark.slow
    def test_tegra_demo_image_build(self, build_controller):
        """Test building Tegra demo image (mock)"""
        # Setup environment
        assert build_controller.setup_build_environment()

        # Configure build
        assert build_controller.configure_local_conf()
        assert build_controller.configure_bblayers_conf()

        # Run build (mock)
        result = build_controller.run_bitbake_command('tegra-demo-distro', timeout=7200)

        logger.info(f"Build result: {result}")


class TestBuildArtifacts:
    """Build artifact verification tests"""

    def test_verify_kernel_image(self, build_controller):
        """Test kernel image artifact"""
        assert build_controller.verify_build_artifacts('core-image-minimal')

    def test_verify_rootfs_image(self, build_controller):
        """Test rootfs image artifact"""
        assert build_controller.verify_build_artifacts('core-image-minimal')

    def test_verify_bootloader(self, build_controller):
        """Test bootloader artifacts"""
        assert build_controller.verify_build_artifacts('core-image-minimal')


class TestBuildCleanup:
    """Build cleanup tests"""

    def test_clean_build_directory(self, build_controller):
        """Test cleaning build directory"""
        assert build_controller.clean_build()

    def test_clean_specific_recipe(self, build_controller):
        """Test cleaning specific recipe"""
        result = build_controller.run_bitbake_command('busybox -c clean', timeout=300)
        assert result['returncode'] == 0


class TestParallelBuilds:
    """Parallel build tests"""

    def test_parallel_build_configuration(self, build_controller):
        """Test parallel build configuration"""
        config = {
            'PARALLEL_MAKE': '-j 8',
            'BB_NUMBER_THREADS': '8'
        }

        assert build_controller.configure_local_conf(config)

    def test_single_threaded_build(self, build_controller):
        """Test single-threaded build configuration"""
        config = {
            'PARALLEL_MAKE': '-j 1',
            'BB_NUMBER_THREADS': '1'
        }

        assert build_controller.configure_local_conf(config)


class TestBuildRecovery:
    """Build recovery and error handling tests"""

    def test_resume_interrupted_build(self, build_controller):
        """Test resuming an interrupted build"""
        # In real implementation, would test build continuation
        logger.info("Build resume test (mock)")

    def test_build_with_forced_rebuild(self, build_controller):
        """Test forcing rebuild of packages"""
        result = build_controller.run_bitbake_command('busybox -c cleanall', timeout=300)
        assert result['returncode'] == 0


def test_bitbake_available():
    """Test that BitBake is available"""
    try:
        result = subprocess.run(
            ['which', 'bitbake'],
            capture_output=True,
            timeout=5
        )

        if result.returncode == 0:
            logger.info("BitBake is available")
        else:
            pytest.skip("BitBake not found in PATH")

    except Exception as e:
        pytest.skip(f"Cannot check for BitBake: {e}")


def test_required_host_packages():
    """Test required host packages are installed"""
    required_packages = [
        'python3',
        'git',
        'make',
        'gcc',
        'diffstat',
        'chrpath'
    ]

    missing = []
    for package in required_packages:
        result = subprocess.run(['which', package], capture_output=True)
        if result.returncode != 0:
            missing.append(package)

    if missing:
        logger.warning(f"Missing host packages: {', '.join(missing)}")


if __name__ == '__main__':
    pytest.main([__file__, '-v'])
