#!/usr/bin/env python3
"""
AI Performance Tests for Jetson Platforms
Tests inference performance, TensorRT optimization, CUDA functionality, and DLA.
"""

import pytest
import subprocess
import time
import os
from pathlib import Path
from typing import Dict, Optional, List, Tuple
import logging
import json
import platform

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class AIPerformanceController:
    """AI performance testing controller for Jetson platforms"""

    def __init__(self):
        """Initialize AI performance controller"""
        self.platform_info = self._detect_platform()
        self.cuda_available = self._check_cuda()
        self.tensorrt_available = self._check_tensorrt()

    def _detect_platform(self) -> Dict:
        """Detect Jetson platform information"""
        info = {
            'architecture': platform.machine(),
            'system': platform.system(),
            'release': platform.release()
        }

        # Check for Jetson-specific info
        jetson_release = Path('/etc/nv_tegra_release')
        if jetson_release.exists():
            with open(jetson_release, 'r') as f:
                info['jetson_release'] = f.read().strip()

        # Check L4T version
        l4t_release = Path('/etc/nv_tegra_release')
        if l4t_release.exists():
            with open(l4t_release, 'r') as f:
                content = f.read()
                # Parse L4T version
                if 'R' in content:
                    info['l4t_version'] = content.split(',')[0].strip()

        return info

    def _check_cuda(self) -> bool:
        """Check if CUDA is available"""
        try:
            result = subprocess.run(
                ['nvcc', '--version'],
                capture_output=True,
                text=True,
                timeout=5
            )
            return result.returncode == 0
        except FileNotFoundError:
            return False
        except Exception:
            return False

    def _check_tensorrt(self) -> bool:
        """Check if TensorRT is available"""
        tensorrt_paths = [
            '/usr/lib/aarch64-linux-gnu/libnvinfer.so',
            '/usr/lib/libnvinfer.so',
            '/usr/local/lib/libnvinfer.so'
        ]

        for path in tensorrt_paths:
            if Path(path).exists():
                return True

        return False

    def get_cuda_info(self) -> Optional[Dict]:
        """Get CUDA device information"""
        if not self.cuda_available:
            return None

        try:
            result = subprocess.run(
                ['nvidia-smi', '--query-gpu=name,driver_version,memory.total', '--format=csv,noheader'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode == 0:
                parts = result.stdout.strip().split(',')
                return {
                    'name': parts[0].strip() if len(parts) > 0 else 'Unknown',
                    'driver': parts[1].strip() if len(parts) > 1 else 'Unknown',
                    'memory': parts[2].strip() if len(parts) > 2 else 'Unknown'
                }
        except FileNotFoundError:
            logger.warning("nvidia-smi not found")
        except Exception as e:
            logger.error(f"Error getting CUDA info: {e}")

        return None

    def measure_inference_throughput(self, model_path: str, iterations: int = 100) -> Optional[float]:
        """
        Measure inference throughput (mock implementation)

        Args:
            model_path: Path to model file
            iterations: Number of iterations

        Returns:
            Throughput in inferences/second
        """
        # This is a placeholder - real implementation would use actual model inference
        logger.info(f"Measuring inference throughput for {model_path}")
        logger.info("Note: This is a mock implementation")

        return None

    def benchmark_tensorrt(self) -> Optional[Dict]:
        """
        Benchmark TensorRT performance

        Returns:
            Benchmark results dictionary
        """
        if not self.tensorrt_available:
            logger.warning("TensorRT not available")
            return None

        # Check for trtexec tool
        try:
            result = subprocess.run(
                ['which', 'trtexec'],
                capture_output=True,
                timeout=5
            )

            if result.returncode != 0:
                logger.warning("trtexec not found")
                return None

            # Run a simple benchmark (this would need a real model in production)
            logger.info("TensorRT benchmark requires a model file")
            return {'status': 'trtexec available'}

        except Exception as e:
            logger.error(f"TensorRT benchmark error: {e}")
            return None

    def check_dla_available(self) -> bool:
        """
        Check if Deep Learning Accelerator (DLA) is available

        Returns:
            True if DLA is available
        """
        # DLA is available on Xavier and Orin platforms
        dla_device = Path('/dev/nvdla0')
        return dla_device.exists()

    def measure_power_consumption(self, duration: int = 5) -> Optional[Dict]:
        """
        Measure power consumption during inference

        Args:
            duration: Measurement duration in seconds

        Returns:
            Power consumption data
        """
        try:
            # Check for tegrastats (Jetson power monitoring tool)
            result = subprocess.run(
                ['which', 'tegrastats'],
                capture_output=True,
                timeout=5
            )

            if result.returncode != 0:
                logger.warning("tegrastats not available")
                return None

            logger.info(f"Monitoring power for {duration} seconds...")

            # Run tegrastats
            proc = subprocess.Popen(
                ['tegrastats', '--interval', '1000'],
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True
            )

            time.sleep(duration)
            proc.terminate()
            proc.wait(timeout=5)

            return {'status': 'completed', 'duration': duration}

        except Exception as e:
            logger.error(f"Power measurement error: {e}")
            return None


@pytest.fixture
def ai_controller():
    """Pytest fixture for AI performance controller"""
    return AIPerformanceController()


class TestCUDA:
    """CUDA functionality tests"""

    def test_cuda_available(self, ai_controller):
        """Test that CUDA is available"""
        if not ai_controller.cuda_available:
            pytest.skip("CUDA not available")

        logger.info("CUDA is available")

    def test_nvcc_version(self):
        """Test CUDA compiler version"""
        try:
            result = subprocess.run(
                ['nvcc', '--version'],
                capture_output=True,
                text=True,
                timeout=5
            )

            if result.returncode == 0:
                logger.info(f"NVCC output:\n{result.stdout}")
                assert 'release' in result.stdout.lower(), "Invalid nvcc output"
            else:
                pytest.skip("NVCC not available")

        except FileNotFoundError:
            pytest.skip("NVCC not found")

    def test_cuda_device_query(self, ai_controller):
        """Test CUDA device query"""
        if not ai_controller.cuda_available:
            pytest.skip("CUDA not available")

        cuda_info = ai_controller.get_cuda_info()

        if cuda_info:
            logger.info(f"CUDA Device: {cuda_info.get('name', 'Unknown')}")
            logger.info(f"Driver Version: {cuda_info.get('driver', 'Unknown')}")
            logger.info(f"Memory: {cuda_info.get('memory', 'Unknown')}")

    def test_cuda_samples_available(self):
        """Test if CUDA samples are available"""
        cuda_sample_paths = [
            '/usr/local/cuda/samples',
            '/usr/src/cudnn_samples_v8'
        ]

        found = False
        for path in cuda_sample_paths:
            if Path(path).exists():
                logger.info(f"Found CUDA samples at {path}")
                found = True
                break

        if not found:
            logger.warning("CUDA samples not found")


class TestTensorRT:
    """TensorRT functionality tests"""

    def test_tensorrt_available(self, ai_controller):
        """Test that TensorRT is available"""
        if not ai_controller.tensorrt_available:
            pytest.skip("TensorRT not available")

        logger.info("TensorRT is available")

    def test_tensorrt_version(self):
        """Test TensorRT version"""
        try:
            # Try to get version from dpkg
            result = subprocess.run(
                ['dpkg', '-l', 'libnvinfer*'],
                capture_output=True,
                text=True,
                timeout=5
            )

            if result.returncode == 0:
                logger.info(f"TensorRT packages:\n{result.stdout}")

        except Exception as e:
            logger.warning(f"Could not get TensorRT version: {e}")

    def test_trtexec_available(self):
        """Test that trtexec tool is available"""
        try:
            result = subprocess.run(
                ['which', 'trtexec'],
                capture_output=True,
                text=True,
                timeout=5
            )

            if result.returncode == 0:
                trtexec_path = result.stdout.strip()
                logger.info(f"trtexec found at: {trtexec_path}")

                # Get version
                version_result = subprocess.run(
                    ['trtexec', '--help'],
                    capture_output=True,
                    text=True,
                    timeout=5
                )

                if 'TensorRT' in version_result.stdout:
                    logger.info("trtexec is functional")
            else:
                pytest.skip("trtexec not found")

        except FileNotFoundError:
            pytest.skip("trtexec not available")

    def test_tensorrt_python_bindings(self):
        """Test TensorRT Python bindings"""
        try:
            import tensorrt as trt
            logger.info(f"TensorRT Python version: {trt.__version__}")
            assert trt.__version__ is not None
        except ImportError:
            pytest.skip("TensorRT Python bindings not available")


class TestDLA:
    """Deep Learning Accelerator tests"""

    def test_dla_device_present(self, ai_controller):
        """Test that DLA device is present"""
        dla_available = ai_controller.check_dla_available()

        if dla_available:
            logger.info("DLA device found at /dev/nvdla0")

            # Check for additional DLA devices
            for i in range(4):
                dla_path = Path(f'/dev/nvdla{i}')
                if dla_path.exists():
                    logger.info(f"DLA device {i} available")
        else:
            pytest.skip("DLA not available (may not be on Xavier/Orin platform)")

    def test_dla_loadable_support(self):
        """Test DLA loadable support"""
        try:
            # Check for DLA compiler tools
            result = subprocess.run(
                ['which', 'trtexec'],
                capture_output=True,
                timeout=5
            )

            if result.returncode == 0:
                logger.info("TensorRT available for DLA loadables")
            else:
                pytest.skip("TensorRT not available for DLA testing")

        except Exception as e:
            pytest.skip(f"DLA test failed: {e}")


class TestInferencePerformance:
    """Inference performance benchmarking tests"""

    def test_platform_detection(self, ai_controller):
        """Test Jetson platform detection"""
        platform_info = ai_controller.platform_info

        logger.info("Platform Information:")
        for key, value in platform_info.items():
            logger.info(f"  {key}: {value}")

        assert platform_info['architecture'] is not None

    def test_inference_libraries_present(self):
        """Test that inference libraries are present"""
        libraries = [
            'libnvinfer.so',
            'libnvonnxparser.so',
            'libcudnn.so',
            'libcublas.so'
        ]

        lib_paths = [
            '/usr/lib/aarch64-linux-gnu',
            '/usr/lib',
            '/usr/local/lib'
        ]

        found_libs = []
        for lib in libraries:
            for lib_path in lib_paths:
                full_path = Path(lib_path) / lib
                if full_path.exists():
                    found_libs.append(lib)
                    logger.info(f"Found {lib} at {full_path}")
                    break

        logger.info(f"Found {len(found_libs)}/{len(libraries)} required libraries")


class TestCuDNN:
    """cuDNN functionality tests"""

    def test_cudnn_available(self):
        """Test that cuDNN is available"""
        cudnn_paths = [
            '/usr/lib/aarch64-linux-gnu/libcudnn.so',
            '/usr/lib/libcudnn.so',
            '/usr/local/cuda/lib64/libcudnn.so'
        ]

        found = False
        for path in cudnn_paths:
            if Path(path).exists():
                logger.info(f"Found cuDNN at {path}")
                found = True
                break

        if not found:
            pytest.skip("cuDNN not found")

    def test_cudnn_version(self):
        """Test cuDNN version"""
        try:
            result = subprocess.run(
                ['dpkg', '-l', 'libcudnn*'],
                capture_output=True,
                text=True,
                timeout=5
            )

            if result.returncode == 0:
                logger.info(f"cuDNN packages:\n{result.stdout}")

        except Exception as e:
            logger.warning(f"Could not get cuDNN version: {e}")


class TestVPI:
    """Vision Programming Interface (VPI) tests"""

    def test_vpi_available(self):
        """Test that VPI is available"""
        vpi_paths = [
            '/opt/nvidia/vpi2/lib64/libnvvpi.so',
            '/usr/lib/aarch64-linux-gnu/libnvvpi.so'
        ]

        found = False
        for path in vpi_paths:
            if Path(path).exists():
                logger.info(f"Found VPI at {path}")
                found = True
                break

        if not found:
            pytest.skip("VPI not available")

    def test_vpi_samples(self):
        """Test VPI samples availability"""
        vpi_sample_paths = [
            '/opt/nvidia/vpi2/samples',
            '/usr/share/vpi2/samples'
        ]

        found = False
        for path in vpi_sample_paths:
            if Path(path).exists():
                logger.info(f"Found VPI samples at {path}")
                found = True
                break

        if not found:
            logger.warning("VPI samples not found")


class TestPowerMonitoring:
    """Power monitoring tests"""

    def test_tegrastats_available(self):
        """Test that tegrastats is available"""
        try:
            result = subprocess.run(
                ['which', 'tegrastats'],
                capture_output=True,
                timeout=5
            )

            if result.returncode == 0:
                logger.info("tegrastats is available")
            else:
                pytest.skip("tegrastats not available")

        except Exception as e:
            pytest.skip(f"tegrastats check failed: {e}")

    def test_power_measurement(self, ai_controller):
        """Test power measurement capability"""
        power_data = ai_controller.measure_power_consumption(duration=3)

        if power_data:
            logger.info(f"Power measurement completed: {power_data}")
        else:
            logger.warning("Power measurement not available")

    def test_jetson_stats_available(self):
        """Test that jetson-stats is available"""
        try:
            result = subprocess.run(
                ['which', 'jtop'],
                capture_output=True,
                timeout=5
            )

            if result.returncode == 0:
                logger.info("jetson-stats (jtop) is available")
            else:
                logger.warning("jetson-stats not installed (pip3 install jetson-stats)")

        except Exception:
            logger.warning("jetson-stats check failed")


class TestMemoryBandwidth:
    """Memory bandwidth tests"""

    def test_memory_info(self):
        """Test system memory information"""
        try:
            with open('/proc/meminfo', 'r') as f:
                meminfo = f.read()

            logger.info("Memory Information:")
            for line in meminfo.split('\n')[:10]:
                if line:
                    logger.info(f"  {line}")

        except Exception as e:
            pytest.skip(f"Cannot read memory info: {e}")

    def test_gpu_memory_info(self, ai_controller):
        """Test GPU memory information"""
        if not ai_controller.cuda_available:
            pytest.skip("CUDA not available")

        cuda_info = ai_controller.get_cuda_info()

        if cuda_info and 'memory' in cuda_info:
            logger.info(f"GPU Memory: {cuda_info['memory']}")


def test_deepstream_available():
    """Test DeepStream SDK availability"""
    deepstream_paths = [
        '/opt/nvidia/deepstream/deepstream',
        '/opt/nvidia/deepstream/deepstream-6.0',
        '/opt/nvidia/deepstream/deepstream-6.1',
        '/opt/nvidia/deepstream/deepstream-6.2'
    ]

    found = False
    for path in deepstream_paths:
        if Path(path).exists():
            logger.info(f"Found DeepStream at {path}")
            found = True
            break

    if not found:
        logger.warning("DeepStream SDK not found")


def test_jetpack_version():
    """Test JetPack version detection"""
    version_files = [
        '/etc/nv_tegra_release',
        '/etc/nv_boot_control.conf'
    ]

    for version_file in version_files:
        if Path(version_file).exists():
            with open(version_file, 'r') as f:
                content = f.read()
                logger.info(f"{version_file}:\n{content}")


if __name__ == '__main__':
    pytest.main([__file__, '-v'])
