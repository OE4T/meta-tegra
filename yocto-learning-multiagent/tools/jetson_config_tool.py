#!/usr/bin/env python3
"""
Jetson Configuration Tool - Platform detection and configuration helper for NVIDIA Jetson.

This tool provides automated platform detection, JetPack version checking,
hardware capability queries, and configuration file generation for Jetson platforms.

Features:
- Automatic Jetson platform detection
- JetPack/L4T version detection and validation
- Hardware capability queries (GPU, CUDA cores, memory, etc.)
- Configuration file generation for Yocto builds
- Board support package (BSP) compatibility checking
- Device tree overlay recommendations
- Performance mode configuration

Author: Meta-Tegra Learning System
License: MIT
"""

import argparse
import json
import os
import platform
import re
import subprocess
import sys
from dataclasses import dataclass, field
from enum import Enum
from pathlib import Path
from typing import Dict, List, Optional, Tuple


class JetsonPlatform(Enum):
    """Supported Jetson platforms."""
    NANO = "jetson-nano"
    NANO_2GB = "jetson-nano-2gb"
    TX1 = "jetson-tx1"
    TX2 = "jetson-tx2"
    TX2_NX = "jetson-tx2-nx"
    XAVIER_NX = "jetson-xavier-nx"
    XAVIER_AGX = "jetson-agx-xavier"
    ORIN_NANO = "jetson-orin-nano"
    ORIN_NX = "jetson-orin-nx"
    ORIN_AGX = "jetson-agx-orin"
    UNKNOWN = "unknown"


@dataclass
class PlatformInfo:
    """Information about detected Jetson platform."""
    platform: JetsonPlatform
    module: str
    soc: str
    tegra_chip: str
    l4t_version: str
    jetpack_version: str
    cuda_version: str
    cuda_arch: str
    gpu_name: str
    cuda_cores: int
    memory_gb: float
    emmc_size_gb: int
    carrier_board: str
    device_mode: str  # e.g., "desktop", "headless", "server"


@dataclass
class HardwareCapabilities:
    """Hardware capabilities of the platform."""
    # GPU
    has_gpu: bool = False
    gpu_arch: str = ""
    cuda_cores: int = 0
    tensor_cores: int = 0
    max_gpu_freq_mhz: int = 0

    # CPU
    cpu_cores: int = 0
    cpu_arch: str = ""
    max_cpu_freq_mhz: int = 0

    # Memory
    total_memory_mb: int = 0
    memory_type: str = ""

    # Storage
    emmc_available: bool = False
    sd_card_available: bool = False
    nvme_available: bool = False

    # Interfaces
    has_wifi: bool = False
    has_bluetooth: bool = False
    has_ethernet: bool = False
    usb3_ports: int = 0
    usb2_ports: int = 0

    # Display
    has_hdmi: bool = False
    has_displayport: bool = False
    max_displays: int = 0

    # AI/ML Features
    has_dla: bool = False  # Deep Learning Accelerator
    dla_count: int = 0
    has_pva: bool = False  # Programmable Vision Accelerator
    has_nvenc: bool = False  # Video encoder
    has_nvdec: bool = False  # Video decoder


class JetsonDetector:
    """Detect Jetson platform and hardware information."""

    # Platform detection patterns
    PLATFORM_PATTERNS = {
        JetsonPlatform.NANO: [
            "jetson-nano",
            "p3450",
        ],
        JetsonPlatform.NANO_2GB: [
            "jetson-nano-2gb",
            "p3541",
        ],
        JetsonPlatform.TX1: [
            "jetson-tx1",
            "p2371",
        ],
        JetsonPlatform.TX2: [
            "jetson-tx2",
            "p2771",
            "p3489",
        ],
        JetsonPlatform.TX2_NX: [
            "jetson-tx2-nx",
        ],
        JetsonPlatform.XAVIER_NX: [
            "jetson-xavier-nx",
            "p3668",
        ],
        JetsonPlatform.XAVIER_AGX: [
            "jetson-agx-xavier",
            "p2822",
        ],
        JetsonPlatform.ORIN_NANO: [
            "jetson-orin-nano",
            "p3768",
        ],
        JetsonPlatform.ORIN_NX: [
            "jetson-orin-nx",
            "p3767",
        ],
        JetsonPlatform.ORIN_AGX: [
            "jetson-agx-orin",
            "p3701",
        ],
    }

    # Hardware specifications database
    PLATFORM_SPECS = {
        JetsonPlatform.NANO: {
            "soc": "Tegra X1",
            "tegra_chip": "t210",
            "gpu": "Maxwell",
            "cuda_cores": 128,
            "tensor_cores": 0,
            "cuda_arch": "5.3",
            "cpu_cores": 4,
            "memory_gb": 4,
            "dla_count": 0,
        },
        JetsonPlatform.NANO_2GB: {
            "soc": "Tegra X1",
            "tegra_chip": "t210",
            "gpu": "Maxwell",
            "cuda_cores": 128,
            "tensor_cores": 0,
            "cuda_arch": "5.3",
            "cpu_cores": 4,
            "memory_gb": 2,
            "dla_count": 0,
        },
        JetsonPlatform.TX2: {
            "soc": "Tegra X2",
            "tegra_chip": "t186",
            "gpu": "Pascal",
            "cuda_cores": 256,
            "tensor_cores": 0,
            "cuda_arch": "6.2",
            "cpu_cores": 6,
            "memory_gb": 8,
            "dla_count": 0,
        },
        JetsonPlatform.XAVIER_NX: {
            "soc": "Tegra Xavier",
            "tegra_chip": "t194",
            "gpu": "Volta",
            "cuda_cores": 384,
            "tensor_cores": 48,
            "cuda_arch": "7.2",
            "cpu_cores": 6,
            "memory_gb": 8,
            "dla_count": 2,
        },
        JetsonPlatform.XAVIER_AGX: {
            "soc": "Tegra Xavier",
            "tegra_chip": "t194",
            "gpu": "Volta",
            "cuda_cores": 512,
            "tensor_cores": 64,
            "cuda_arch": "7.2",
            "cpu_cores": 8,
            "memory_gb": 32,
            "dla_count": 2,
        },
        JetsonPlatform.ORIN_NANO: {
            "soc": "Tegra Orin",
            "tegra_chip": "t234",
            "gpu": "Ampere",
            "cuda_cores": 1024,
            "tensor_cores": 32,
            "cuda_arch": "8.7",
            "cpu_cores": 6,
            "memory_gb": 8,
            "dla_count": 0,
        },
        JetsonPlatform.ORIN_NX: {
            "soc": "Tegra Orin",
            "tegra_chip": "t234",
            "gpu": "Ampere",
            "cuda_cores": 1024,
            "tensor_cores": 32,
            "cuda_arch": "8.7",
            "cpu_cores": 8,
            "memory_gb": 16,
            "dla_count": 1,
        },
        JetsonPlatform.ORIN_AGX: {
            "soc": "Tegra Orin",
            "tegra_chip": "t234",
            "gpu": "Ampere",
            "cuda_cores": 2048,
            "tensor_cores": 64,
            "cuda_arch": "8.7",
            "cpu_cores": 12,
            "memory_gb": 64,
            "dla_count": 2,
        },
    }

    def __init__(self):
        """Initialize detector."""
        self.is_jetson = self._is_running_on_jetson()

    def _is_running_on_jetson(self) -> bool:
        """Check if running on a Jetson device."""
        # Check for tegra in machine type
        try:
            with open('/proc/device-tree/compatible', 'r') as f:
                compatible = f.read().lower()
                return 'tegra' in compatible or 'nvidia' in compatible
        except FileNotFoundError:
            return False

    def _run_command(self, cmd: List[str]) -> Optional[str]:
        """Run shell command and return output."""
        try:
            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=5
            )
            return result.stdout.strip() if result.returncode == 0 else None
        except (subprocess.TimeoutExpired, FileNotFoundError):
            return None

    def detect_platform(self) -> JetsonPlatform:
        """
        Detect the Jetson platform.

        Returns:
            Detected JetsonPlatform
        """
        # Try reading device tree model
        try:
            with open('/proc/device-tree/model', 'r') as f:
                model = f.read().lower()

                for platform, patterns in self.PLATFORM_PATTERNS.items():
                    for pattern in patterns:
                        if pattern in model:
                            return platform
        except FileNotFoundError:
            pass

        # Try reading compatible string
        try:
            with open('/proc/device-tree/compatible', 'r') as f:
                compatible = f.read().lower()

                for platform, patterns in self.PLATFORM_PATTERNS.items():
                    for pattern in patterns:
                        if pattern in compatible:
                            return platform
        except FileNotFoundError:
            pass

        return JetsonPlatform.UNKNOWN

    def get_l4t_version(self) -> str:
        """Get L4T (Linux for Tegra) version."""
        try:
            with open('/etc/nv_tegra_release', 'r') as f:
                content = f.read()
                # Example: # R32 (release), REVISION: 7.1
                match = re.search(r'R(\d+)\s+.*REVISION:\s+([\d.]+)', content)
                if match:
                    return f"{match.group(1)}.{match.group(2)}"
        except FileNotFoundError:
            pass

        return "Unknown"

    def get_jetpack_version(self, l4t_version: str) -> str:
        """
        Map L4T version to JetPack version.

        Args:
            l4t_version: L4T version string

        Returns:
            JetPack version string
        """
        # L4T to JetPack mapping (approximate)
        l4t_to_jetpack = {
            "32.7": "4.6.1",
            "32.6": "4.6",
            "32.5": "4.5.1",
            "35.1": "5.0.2",
            "35.2": "5.1",
            "35.3": "5.1.1",
            "35.4": "5.1.2",
            "36.2": "6.0",
            "36.3": "6.0 DP",
        }

        # Try exact match first
        for l4t, jp in l4t_to_jetpack.items():
            if l4t_version.startswith(l4t):
                return jp

        return "Unknown"

    def get_cuda_version(self) -> str:
        """Get CUDA version if available."""
        nvcc_output = self._run_command(['nvcc', '--version'])
        if nvcc_output:
            match = re.search(r'release\s+([\d.]+)', nvcc_output)
            if match:
                return match.group(1)

        return "Unknown"

    def get_memory_info(self) -> float:
        """Get total memory in GB."""
        try:
            with open('/proc/meminfo', 'r') as f:
                for line in f:
                    if line.startswith('MemTotal:'):
                        # Extract KB value and convert to GB
                        kb = int(line.split()[1])
                        return round(kb / (1024 * 1024), 1)
        except FileNotFoundError:
            pass

        return 0.0

    def get_platform_info(self) -> PlatformInfo:
        """
        Get comprehensive platform information.

        Returns:
            PlatformInfo object
        """
        platform = self.detect_platform()
        l4t_version = self.get_l4t_version()
        jetpack_version = self.get_jetpack_version(l4t_version)
        cuda_version = self.get_cuda_version()
        memory_gb = self.get_memory_info()

        # Get specs from database
        specs = self.PLATFORM_SPECS.get(platform, {})

        return PlatformInfo(
            platform=platform,
            module=platform.value,
            soc=specs.get("soc", "Unknown"),
            tegra_chip=specs.get("tegra_chip", "unknown"),
            l4t_version=l4t_version,
            jetpack_version=jetpack_version,
            cuda_version=cuda_version,
            cuda_arch=specs.get("cuda_arch", "Unknown"),
            gpu_name=specs.get("gpu", "Unknown"),
            cuda_cores=specs.get("cuda_cores", 0),
            memory_gb=memory_gb or specs.get("memory_gb", 0),
            emmc_size_gb=0,  # Would need to query actual storage
            carrier_board="Unknown",
            device_mode="Unknown"
        )

    def get_hardware_capabilities(self, platform: JetsonPlatform) -> HardwareCapabilities:
        """
        Get hardware capabilities for platform.

        Args:
            platform: Jetson platform

        Returns:
            HardwareCapabilities object
        """
        specs = self.PLATFORM_SPECS.get(platform, {})

        caps = HardwareCapabilities(
            has_gpu=True,
            gpu_arch=specs.get("gpu", "Unknown"),
            cuda_cores=specs.get("cuda_cores", 0),
            tensor_cores=specs.get("tensor_cores", 0),
            cpu_cores=specs.get("cpu_cores", 0),
            cpu_arch="ARM64",
            has_dla=specs.get("dla_count", 0) > 0,
            dla_count=specs.get("dla_count", 0),
            has_nvenc=True,
            has_nvdec=True,
        )

        # Platform-specific capabilities
        if platform in [JetsonPlatform.ORIN_NANO, JetsonPlatform.ORIN_NX, JetsonPlatform.ORIN_AGX]:
            caps.has_pva = True
            caps.nvme_available = True

        return caps


class ConfigGenerator:
    """Generate configuration files for Yocto builds."""

    def __init__(self, platform_info: PlatformInfo):
        """Initialize with platform info."""
        self.platform_info = platform_info

    def generate_local_conf(self) -> str:
        """Generate local.conf snippet for Yocto."""
        conf = f"""# Jetson {self.platform_info.platform.value} Configuration
# Generated by Jetson Config Tool

# Machine selection
MACHINE = "{self.platform_info.platform.value}"

# Tegra SoC
SOC_FAMILY = "{self.platform_info.tegra_chip}"

# CUDA Configuration
CUDA_VERSION = "{self.platform_info.cuda_version}"
CUDA_ARCH = "sm_{self.platform_info.cuda_arch.replace('.', '')}"

# Optimize for this platform
# Enable parallel build
BB_NUMBER_THREADS ?= "{self.platform_info.platform_info.cpu_cores if hasattr(self.platform_info, 'cpu_cores') else '4'}"
PARALLEL_MAKE ?= "-j {self.platform_info.platform_info.cpu_cores if hasattr(self.platform_info, 'cpu_cores') else '4'}"

# Enable necessary distro features for Tegra
DISTRO_FEATURES:append = " opengl vulkan x11"

# Package management
PACKAGE_CLASSES ?= "package_deb"

# Additional image features
IMAGE_INSTALL:append = " \\
    cuda-toolkit \\
    tensorrt \\
    opencv \\
    python3-numpy \\
"
"""
        return conf

    def generate_machine_conf(self) -> str:
        """Generate machine configuration."""
        conf = f"""# Machine configuration for {self.platform_info.platform.value}

require conf/machine/include/tegra-common.inc

# SoC
SOC_FAMILY = "{self.platform_info.tegra_chip}"

# Kernel
PREFERRED_PROVIDER_virtual/kernel = "linux-tegra"
KERNEL_DEVICETREE = "{self.platform_info.tegra_chip}-{self.platform_info.platform.value}.dtb"

# U-Boot
PREFERRED_PROVIDER_virtual/bootloader = "u-boot-tegra"

# Graphics
PREFERRED_PROVIDER_virtual/egl = "nvidia-driver"
PREFERRED_PROVIDER_virtual/libgles1 = "nvidia-driver"
PREFERRED_PROVIDER_virtual/libgles2 = "nvidia-driver"
PREFERRED_PROVIDER_virtual/libgl = "nvidia-driver"

# Serial console
SERIAL_CONSOLES = "115200;ttyTCU0"
"""
        return conf

    def generate_layer_conf(self) -> str:
        """Generate layer configuration template."""
        conf = f"""# Layer configuration for {self.platform_info.platform.value}

# We have a conf and classes directory, add to BBPATH
BBPATH .= ":${{LAYERDIR}}"

# We have recipes-* directories, add to BBFILES
BBFILES += "${{LAYERDIR}}/recipes-*/*/*.bb \\
            ${{LAYERDIR}}/recipes-*/*/*.bbappend"

BBFILE_COLLECTIONS += "meta-jetson-custom"
BBFILE_PATTERN_meta-jetson-custom = "^${{LAYERDIR}}/"
BBFILE_PRIORITY_meta-jetson-custom = "10"

LAYERDEPENDS_meta-jetson-custom = "core tegra"
LAYERSERIES_COMPAT_meta-jetson-custom = "kirkstone langdale"
"""
        return conf


def main():
    """Main entry point for the Jetson config tool CLI."""
    parser = argparse.ArgumentParser(
        description="Jetson Configuration Tool - Platform detection and config generation",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Detect current platform
  %(prog)s --detect

  # Show detailed platform information
  %(prog)s --info

  # Show hardware capabilities
  %(prog)s --capabilities

  # Generate local.conf for specific platform
  %(prog)s --generate-config local.conf --platform jetson-xavier-nx

  # Generate all config files
  %(prog)s --generate-all --output-dir configs/

  # Check JetPack compatibility
  %(prog)s --check-jetpack-version 5.1

  # Query specific capability
  %(prog)s --query cuda_cores
        """
    )

    parser.add_argument(
        '--detect',
        action='store_true',
        help='Detect Jetson platform'
    )

    parser.add_argument(
        '--info',
        action='store_true',
        help='Show detailed platform information'
    )

    parser.add_argument(
        '--capabilities',
        action='store_true',
        help='Show hardware capabilities'
    )

    parser.add_argument(
        '--platform',
        type=str,
        choices=[p.value for p in JetsonPlatform if p != JetsonPlatform.UNKNOWN],
        help='Specify platform (for config generation)'
    )

    parser.add_argument(
        '--generate-config',
        type=str,
        choices=['local.conf', 'machine.conf', 'layer.conf', 'all'],
        help='Generate configuration file'
    )

    parser.add_argument(
        '--output-dir',
        type=str,
        default='.',
        help='Output directory for generated files'
    )

    parser.add_argument(
        '--json',
        action='store_true',
        help='Output in JSON format'
    )

    parser.add_argument(
        '--query',
        type=str,
        help='Query specific platform attribute'
    )

    args = parser.parse_args()

    detector = JetsonDetector()

    # Detect platform
    if args.detect or args.info or (not args.platform and not args.query):
        platform = detector.detect_platform()
        print(f"Detected Platform: {platform.value}")

        if platform == JetsonPlatform.UNKNOWN:
            if not detector.is_jetson:
                print("Warning: Not running on a Jetson device")
            else:
                print("Warning: Could not identify specific Jetson model")
            if not args.platform:
                return 1

    # Get platform info
    if args.info or args.capabilities or args.generate_config:
        if args.platform:
            # Use specified platform
            platform = JetsonPlatform(args.platform)
            # Create mock platform info for non-native execution
            specs = detector.PLATFORM_SPECS.get(platform, {})
            platform_info = PlatformInfo(
                platform=platform,
                module=platform.value,
                soc=specs.get("soc", "Unknown"),
                tegra_chip=specs.get("tegra_chip", "unknown"),
                l4t_version="Unknown",
                jetpack_version="Unknown",
                cuda_version="Unknown",
                cuda_arch=specs.get("cuda_arch", "Unknown"),
                gpu_name=specs.get("gpu", "Unknown"),
                cuda_cores=specs.get("cuda_cores", 0),
                memory_gb=specs.get("memory_gb", 0),
                emmc_size_gb=0,
                carrier_board="Unknown",
                device_mode="Unknown"
            )
        else:
            platform_info = detector.get_platform_info()

        if args.info:
            if args.json:
                info_dict = {
                    'platform': platform_info.platform.value,
                    'module': platform_info.module,
                    'soc': platform_info.soc,
                    'tegra_chip': platform_info.tegra_chip,
                    'l4t_version': platform_info.l4t_version,
                    'jetpack_version': platform_info.jetpack_version,
                    'cuda_version': platform_info.cuda_version,
                    'cuda_arch': platform_info.cuda_arch,
                    'gpu_name': platform_info.gpu_name,
                    'cuda_cores': platform_info.cuda_cores,
                    'memory_gb': platform_info.memory_gb,
                }
                print(json.dumps(info_dict, indent=2))
            else:
                print("\n=== Platform Information ===")
                print(f"Platform: {platform_info.platform.value}")
                print(f"SoC: {platform_info.soc}")
                print(f"Tegra Chip: {platform_info.tegra_chip}")
                print(f"L4T Version: {platform_info.l4t_version}")
                print(f"JetPack Version: {platform_info.jetpack_version}")
                print(f"CUDA Version: {platform_info.cuda_version}")
                print(f"CUDA Architecture: {platform_info.cuda_arch}")
                print(f"GPU: {platform_info.gpu_name}")
                print(f"CUDA Cores: {platform_info.cuda_cores}")
                print(f"Memory: {platform_info.memory_gb} GB")

        if args.capabilities:
            caps = detector.get_hardware_capabilities(platform_info.platform)
            print("\n=== Hardware Capabilities ===")
            print(f"GPU Architecture: {caps.gpu_arch}")
            print(f"CUDA Cores: {caps.cuda_cores}")
            print(f"Tensor Cores: {caps.tensor_cores}")
            print(f"CPU Cores: {caps.cpu_cores}")
            print(f"Deep Learning Accelerator (DLA): {'Yes' if caps.has_dla else 'No'}")
            if caps.has_dla:
                print(f"  DLA Engines: {caps.dla_count}")
            print(f"Video Encoder (NVENC): {'Yes' if caps.has_nvenc else 'No'}")
            print(f"Video Decoder (NVDEC): {'Yes' if caps.has_nvdec else 'No'}")

        if args.generate_config:
            generator = ConfigGenerator(platform_info)
            os.makedirs(args.output_dir, exist_ok=True)

            if args.generate_config in ['local.conf', 'all']:
                output_path = os.path.join(args.output_dir, 'local.conf.sample')
                with open(output_path, 'w') as f:
                    f.write(generator.generate_local_conf())
                print(f"Generated: {output_path}")

            if args.generate_config in ['machine.conf', 'all']:
                output_path = os.path.join(args.output_dir, f'{platform_info.platform.value}.conf')
                with open(output_path, 'w') as f:
                    f.write(generator.generate_machine_conf())
                print(f"Generated: {output_path}")

            if args.generate_config in ['layer.conf', 'all']:
                output_path = os.path.join(args.output_dir, 'layer.conf.sample')
                with open(output_path, 'w') as f:
                    f.write(generator.generate_layer_conf())
                print(f"Generated: {output_path}")

    return 0


if __name__ == '__main__':
    sys.exit(main())
