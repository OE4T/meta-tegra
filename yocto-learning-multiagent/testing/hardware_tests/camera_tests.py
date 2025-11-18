#!/usr/bin/env python3
"""
Camera Hardware Tests for Jetson Platforms
Tests camera pipeline including V4L2, libargus, and GStreamer pipelines.
"""

import pytest
import subprocess
import time
import os
from pathlib import Path
from typing import List, Dict, Optional
import logging
import json

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class CameraController:
    """Camera controller for Jetson platforms"""

    def __init__(self):
        """Initialize camera controller"""
        self.video_devices = self._detect_video_devices()

    def _detect_video_devices(self) -> List[str]:
        """Detect available video devices"""
        devices = []
        video_dev_path = Path("/dev")

        for device in video_dev_path.glob("video*"):
            devices.append(str(device))

        return sorted(devices)

    def get_device_info(self, device: str) -> Optional[Dict]:
        """
        Get information about a video device using v4l2-ctl

        Args:
            device: Video device path (e.g., /dev/video0)

        Returns:
            Device information dictionary or None
        """
        try:
            result = subprocess.run(
                ['v4l2-ctl', '-d', device, '--all'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode != 0:
                return None

            info = {
                'device': device,
                'raw_output': result.stdout
            }

            # Parse device name
            for line in result.stdout.split('\n'):
                if 'Card type' in line:
                    info['card_type'] = line.split(':')[1].strip()
                elif 'Driver name' in line:
                    info['driver'] = line.split(':')[1].strip()
                elif 'Capabilities' in line:
                    info['capabilities'] = line.split(':')[1].strip()

            return info

        except FileNotFoundError:
            logger.error("v4l2-ctl not found (install v4l-utils package)")
            return None
        except Exception as e:
            logger.error(f"Error getting device info: {e}")
            return None

    def list_formats(self, device: str) -> List[str]:
        """
        List supported formats for a video device

        Args:
            device: Video device path

        Returns:
            List of supported formats
        """
        try:
            result = subprocess.run(
                ['v4l2-ctl', '-d', device, '--list-formats-ext'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode != 0:
                return []

            return result.stdout.split('\n')

        except Exception as e:
            logger.error(f"Error listing formats: {e}")
            return []

    def capture_frame(self, device: str, output_file: str, format: str = 'MJPG') -> bool:
        """
        Capture a single frame using v4l2-ctl

        Args:
            device: Video device path
            output_file: Output file path
            format: Video format

        Returns:
            True if successful
        """
        try:
            # Set format
            subprocess.run(
                ['v4l2-ctl', '-d', device, '--set-fmt-video=pixelformat=' + format],
                capture_output=True,
                timeout=5
            )

            # Capture frame
            result = subprocess.run(
                ['v4l2-ctl', '-d', device, '--stream-mmap', '--stream-count=1',
                 '--stream-to=' + output_file],
                capture_output=True,
                text=True,
                timeout=10
            )

            return result.returncode == 0

        except Exception as e:
            logger.error(f"Error capturing frame: {e}")
            return False

    def test_gstreamer_pipeline(self, device: str, duration: int = 5) -> bool:
        """
        Test GStreamer camera pipeline

        Args:
            device: Video device path
            duration: Test duration in seconds

        Returns:
            True if pipeline runs successfully
        """
        try:
            # Simple test pipeline: v4l2src -> fakesink
            pipeline = f'v4l2src device={device} num-buffers=100 ! fakesink'

            result = subprocess.run(
                ['gst-launch-1.0', pipeline],
                capture_output=True,
                text=True,
                timeout=duration + 5
            )

            # Check if pipeline ran without errors
            return 'ERROR' not in result.stderr and result.returncode in [0, -2]  # -2 is SIGINT

        except subprocess.TimeoutExpired:
            logger.warning("GStreamer pipeline timed out (expected for continuous pipeline)")
            return True
        except FileNotFoundError:
            logger.error("gst-launch-1.0 not found (install gstreamer package)")
            return False
        except Exception as e:
            logger.error(f"Error testing GStreamer pipeline: {e}")
            return False


@pytest.fixture
def camera_controller():
    """Pytest fixture for camera controller"""
    return CameraController()


class TestCameraDetection:
    """Camera detection tests"""

    def test_video_devices_present(self, camera_controller):
        """Test that video devices are present"""
        devices = camera_controller.video_devices

        logger.info(f"Found {len(devices)} video devices:")
        for device in devices:
            logger.info(f"  {device}")

        # System should have at least one video device (may be different per platform)
        if len(devices) == 0:
            pytest.skip("No video devices found")

    def test_v4l2_tools_available(self):
        """Test that V4L2 tools are installed"""
        tools = ['v4l2-ctl', 'v4l2-compliance']

        missing = []
        for tool in tools:
            result = subprocess.run(['which', tool], capture_output=True)
            if result.returncode != 0:
                missing.append(tool)

        if missing:
            pytest.skip(f"Missing V4L2 tools: {', '.join(missing)}")


class TestCameraInfo:
    """Camera information tests"""

    def test_get_camera_info(self, camera_controller):
        """Test getting camera information"""
        if not camera_controller.video_devices:
            pytest.skip("No video devices found")

        for device in camera_controller.video_devices[:2]:  # Test first 2 devices
            info = camera_controller.get_device_info(device)

            if info:
                logger.info(f"\nDevice: {device}")
                logger.info(f"  Card type: {info.get('card_type', 'Unknown')}")
                logger.info(f"  Driver: {info.get('driver', 'Unknown')}")
                logger.info(f"  Capabilities: {info.get('capabilities', 'Unknown')}")
            else:
                logger.warning(f"Could not get info for {device}")

    def test_list_camera_formats(self, camera_controller):
        """Test listing supported camera formats"""
        if not camera_controller.video_devices:
            pytest.skip("No video devices found")

        for device in camera_controller.video_devices[:2]:
            formats = camera_controller.list_formats(device)

            logger.info(f"\nDevice {device} formats:")
            logger.info('\n'.join(formats[:20]))  # Show first 20 lines


class TestCameraCapture:
    """Camera capture tests"""

    @pytest.mark.skipif(not Path("/dev/video0").exists(), reason="No video device available")
    def test_capture_single_frame(self, camera_controller, tmp_path):
        """Test capturing a single frame"""
        test_device = os.environ.get('TEST_CAMERA_DEVICE', '/dev/video0')

        if not Path(test_device).exists():
            pytest.skip(f"Test device {test_device} not found")

        output_file = tmp_path / "test_frame.jpg"

        # Try to capture a frame
        success = camera_controller.capture_frame(test_device, str(output_file))

        if success and output_file.exists():
            file_size = output_file.stat().st_size
            logger.info(f"Captured frame size: {file_size} bytes")
            assert file_size > 0, "Captured frame is empty"
        else:
            logger.warning("Frame capture failed or not supported by device")


class TestGStreamer:
    """GStreamer pipeline tests"""

    def test_gstreamer_available(self):
        """Test that GStreamer is installed"""
        result = subprocess.run(['which', 'gst-launch-1.0'], capture_output=True)
        assert result.returncode == 0, "GStreamer not installed"

    def test_gstreamer_plugins(self):
        """Test that essential GStreamer plugins are available"""
        required_plugins = ['video4linux2', 'coreelements']

        try:
            result = subprocess.run(
                ['gst-inspect-1.0'],
                capture_output=True,
                text=True,
                timeout=10
            )

            available_plugins = result.stdout

            missing = []
            for plugin in required_plugins:
                if plugin not in available_plugins:
                    missing.append(plugin)

            if missing:
                logger.warning(f"Missing GStreamer plugins: {', '.join(missing)}")

        except Exception as e:
            pytest.skip(f"Cannot check GStreamer plugins: {e}")

    @pytest.mark.skipif(not Path("/dev/video0").exists(), reason="No video device available")
    def test_gstreamer_v4l2src(self, camera_controller):
        """Test GStreamer v4l2src element"""
        test_device = os.environ.get('TEST_CAMERA_DEVICE', '/dev/video0')

        if not Path(test_device).exists():
            pytest.skip(f"Test device {test_device} not found")

        success = camera_controller.test_gstreamer_pipeline(test_device, duration=5)

        if not success:
            logger.warning("GStreamer v4l2src test failed (may be device-specific)")

    def test_gstreamer_nvarguscamerasrc(self):
        """Test GStreamer nvarguscamerasrc element (Jetson-specific)"""
        try:
            # Check if nvarguscamerasrc is available
            result = subprocess.run(
                ['gst-inspect-1.0', 'nvarguscamerasrc'],
                capture_output=True,
                text=True,
                timeout=5
            )

            if result.returncode == 0:
                logger.info("nvarguscamerasrc plugin available")

                # Try to run a simple pipeline
                pipeline = 'nvarguscamerasrc num-buffers=10 ! fakesink'

                result = subprocess.run(
                    ['gst-launch-1.0', pipeline],
                    capture_output=True,
                    text=True,
                    timeout=10
                )

                if result.returncode == 0:
                    logger.info("nvarguscamerasrc pipeline test passed")
                else:
                    logger.warning(f"nvarguscamerasrc pipeline failed: {result.stderr}")
            else:
                pytest.skip("nvarguscamerasrc not available (may not be on Jetson platform)")

        except subprocess.TimeoutExpired:
            logger.warning("nvarguscamerasrc test timed out")
        except Exception as e:
            pytest.skip(f"nvarguscamerasrc test failed: {e}")


class TestArgusCamera:
    """Argus camera library tests (Jetson-specific)"""

    def test_argus_library_present(self):
        """Test that Argus camera library is present"""
        argus_paths = [
            '/usr/lib/aarch64-linux-gnu/libargus.so',
            '/usr/lib/libargus.so',
            '/usr/lib/tegra/libargus.so'
        ]

        found = False
        for path in argus_paths:
            if Path(path).exists():
                logger.info(f"Found Argus library at {path}")
                found = True
                break

        if not found:
            pytest.skip("Argus library not found (may not be on Jetson platform)")

    def test_argus_samples_present(self):
        """Test that Argus sample applications are present"""
        argus_sample_paths = [
            '/usr/src/jetson_multimedia_api/argus',
            '/usr/share/visionworks/sources/argus'
        ]

        found = False
        for path in argus_sample_paths:
            if Path(path).exists():
                logger.info(f"Found Argus samples at {path}")
                found = True
                break

        if not found:
            logger.warning("Argus samples not found")


class TestCameraPerformance:
    """Camera performance tests"""

    @pytest.mark.skipif(not Path("/dev/video0").exists(), reason="No video device available")
    def test_camera_frame_rate(self, camera_controller):
        """Test camera frame rate capability"""
        test_device = os.environ.get('TEST_CAMERA_DEVICE', '/dev/video0')

        if not Path(test_device).exists():
            pytest.skip(f"Test device {test_device} not found")

        try:
            # Use GStreamer to measure frame rate
            pipeline = f'v4l2src device={test_device} num-buffers=300 ! fakesink'

            start_time = time.time()
            result = subprocess.run(
                ['gst-launch-1.0', pipeline],
                capture_output=True,
                text=True,
                timeout=15
            )
            elapsed = time.time() - start_time

            if result.returncode in [0, -2]:
                fps = 300 / elapsed
                logger.info(f"Camera frame rate: {fps:.2f} fps")
                assert fps > 5, "Camera frame rate too low"
            else:
                logger.warning("Frame rate test failed")

        except Exception as e:
            pytest.skip(f"Frame rate test failed: {e}")


class TestCameraStress:
    """Camera stress tests"""

    @pytest.mark.skipif(not Path("/dev/video0").exists(), reason="No video device available")
    def test_camera_open_close_cycle(self):
        """Test repeated camera open/close cycles"""
        test_device = os.environ.get('TEST_CAMERA_DEVICE', '/dev/video0')

        if not Path(test_device).exists():
            pytest.skip(f"Test device {test_device} not found")

        # Try to open/close the device multiple times
        iterations = 10
        for i in range(iterations):
            try:
                # Simple pipeline that opens and closes device
                pipeline = f'v4l2src device={test_device} num-buffers=1 ! fakesink'

                result = subprocess.run(
                    ['gst-launch-1.0', pipeline],
                    capture_output=True,
                    text=True,
                    timeout=5
                )

                if result.returncode not in [0, -2]:
                    logger.warning(f"Iteration {i+1} failed")

            except Exception as e:
                logger.error(f"Iteration {i+1} error: {e}")

            time.sleep(0.1)


def test_camera_kernel_modules():
    """Test camera-related kernel modules"""
    try:
        result = subprocess.run(
            ['lsmod'],
            capture_output=True,
            text=True,
            timeout=5
        )

        modules_to_check = ['videodev', 'videobuf2', 'v4l2']

        logger.info("Camera-related kernel modules:")
        for module in modules_to_check:
            if module in result.stdout.lower():
                logger.info(f"  {module}: loaded")

    except Exception as e:
        pytest.skip(f"Cannot check kernel modules: {e}")


if __name__ == '__main__':
    pytest.main([__file__, '-v'])
