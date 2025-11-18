"""
Yocto & Meta-Tegra Python Tools Package

This package provides production-ready tools for working with Yocto Project
and NVIDIA Jetson platforms using meta-tegra.

Available tools:
- gpio_calculator: GPIO conversion and validation
- recipe_generator: BitBake recipe generation
- device_tree_validator: Device tree validation
- build_analyzer: Build performance analysis
- jetson_config_tool: Jetson platform configuration

Author: Meta-Tegra Learning System
License: MIT
Version: 1.0.0
"""

__version__ = "1.0.0"
__author__ = "Meta-Tegra Learning System"
__license__ = "MIT"

__all__ = [
    "gpio_calculator",
    "recipe_generator",
    "device_tree_validator",
    "build_analyzer",
    "jetson_config_tool",
]
