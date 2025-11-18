# Yocto & Meta-Tegra Python Tools

This directory contains production-ready Python tools for working with Yocto Project and NVIDIA Jetson platforms using meta-tegra.

## Tools Overview

### 1. GPIO Calculator (`gpio_calculator.py`)
Enhanced GPIO conversion and validation tool for Tegra platforms.

**Features:**
- Convert between Tegra GPIO names and Linux GPIO numbers
- Map GPIO pins to 40-pin header positions
- Validate pinmux configurations
- Detect GPIO conflicts
- Export GPIO mappings to CSV

**Usage Examples:**
```bash
# Convert Tegra GPIO to Linux GPIO number
./gpio_calculator.py --platform t210 --tegra-to-linux GPIO216

# Get GPIO for pin header position 7
./gpio_calculator.py --platform t210 --pin-header 7

# Validate pinmux configuration
./gpio_calculator.py --platform t210 --validate GPIO216 GPIO50 GPIO79

# Export mapping to CSV
./gpio_calculator.py --platform t210 --export gpio_mapping.csv
```

**Supported Platforms:**
- `t210`: Tegra210 (Jetson Nano, TX1)
- `t186`: Tegra186 (Jetson TX2)
- `t194`: Tegra194 (Jetson Xavier NX, AGX Xavier)
- `t234`: Tegra234 (Jetson Orin NX, AGX Orin)

---

### 2. Recipe Generator (`recipe_generator.py`)
Interactive BitBake recipe generator with templates and validation.

**Features:**
- Interactive CLI for recipe creation
- Pre-built templates for common patterns
- SPDX license validation
- Automatic dependency detection
- Support for multiple build systems

**Usage Examples:**
```bash
# Interactive recipe creation
./recipe_generator.py --interactive

# Generate from JSON metadata
./recipe_generator.py --from-json recipe-metadata.json --output myapp_1.0.bb

# Validate existing recipe
./recipe_generator.py --validate myapp_1.0.bb
```

**Supported Build Systems:**
- Autotools
- CMake
- Meson
- Python (setuptools, poetry)
- Makefile
- Custom

**Recipe Types:**
- Application
- Library
- Kernel module
- Python package
- Systemd service
- Firmware

---

### 3. Device Tree Validator (`device_tree_validator.py`)
Comprehensive validation for device tree source files.

**Features:**
- Syntax validation
- Compatible string validation
- Phandle reference checking
- Pinmux conflict detection
- Memory region overlap detection
- GPIO usage validation

**Usage Examples:**
```bash
# Validate a device tree file
./device_tree_validator.py my-overlay.dts

# Validate with verbose output
./device_tree_validator.py --verbose my-device.dts

# Check only syntax
./device_tree_validator.py --syntax-only tegra-gpio.dtsi

# Export results to JSON
./device_tree_validator.py --json-output results.json my-overlay.dts

# Validate multiple files
./device_tree_validator.py *.dts

# Check if a compatible string is known
./device_tree_validator.py --compatible-check "nvidia,tegra210-gpio"
```

**Validated Elements:**
- Node syntax
- Compatible strings (Tegra and standard Linux)
- Phandle references
- Pinmux configurations
- Memory region definitions
- GPIO specifications

---

### 4. Build Analyzer (`build_analyzer.py`)
Performance analysis tool for BitBake builds.

**Features:**
- Parse BitBake build logs
- Identify bottlenecks and long-running tasks
- Calculate parallelization efficiency
- Generate optimization recommendations
- Visualize dependency graphs
- Export analysis results (JSON, CSV)

**Usage Examples:**
```bash
# Analyze a build log
./build_analyzer.py /path/to/bitbake-cookerdaemon.log

# Show top 20 longest tasks
./build_analyzer.py --longest-tasks 20 cooker.log

# Identify bottlenecks
./build_analyzer.py --bottlenecks cooker.log

# Generate optimization recommendations
./build_analyzer.py --recommendations cooker.log

# Generate dependency graph (DOT format)
./build_analyzer.py --dependency-graph deps.dot cooker.log

# Convert DOT to PNG (requires graphviz)
dot -Tpng deps.dot -o graph.png

# Export analysis to JSON
./build_analyzer.py --json-output analysis.json cooker.log

# Export task data to CSV
./build_analyzer.py --csv-output tasks.csv cooker.log
```

**Metrics Analyzed:**
- Total build duration
- Task execution times
- Parallelization efficiency
- Recipe build times
- Task distribution
- Build bottlenecks

---

### 5. Jetson Config Tool (`jetson_config_tool.py`)
Platform detection and configuration helper for NVIDIA Jetson.

**Features:**
- Automatic platform detection
- JetPack/L4T version detection
- Hardware capability queries
- Configuration file generation for Yocto
- BSP compatibility checking

**Usage Examples:**
```bash
# Detect current platform
./jetson_config_tool.py --detect

# Show detailed platform information
./jetson_config_tool.py --info

# Show hardware capabilities
./jetson_config_tool.py --capabilities

# Output in JSON format
./jetson_config_tool.py --info --json

# Generate local.conf for Xavier NX
./jetson_config_tool.py --generate-config local.conf --platform jetson-xavier-nx

# Generate all config files
./jetson_config_tool.py --generate-config all --platform jetson-orin-agx --output-dir configs/

# Query specific attribute
./jetson_config_tool.py --query cuda_cores --platform jetson-xavier-nx
```

**Supported Platforms:**
- Jetson Nano (4GB, 2GB)
- Jetson TX1, TX2, TX2 NX
- Jetson Xavier NX, AGX Xavier
- Jetson Orin Nano, Orin NX, AGX Orin

**Hardware Information Detected:**
- SoC and Tegra chip version
- L4T and JetPack versions
- GPU architecture and CUDA cores
- CPU cores and architecture
- Memory configuration
- DLA, PVA availability
- CUDA architecture

---

## Installation

### Prerequisites

All tools require Python 3.8 or later. No external dependencies are required for basic functionality.

### Optional Dependencies

For enhanced functionality:

```bash
# For dependency graph visualization
sudo apt-get install graphviz

# For JSON processing (usually pre-installed)
python3 -c "import json"
```

### Setup

Make all scripts executable:

```bash
chmod +x *.py
```

Or run with Python:

```bash
python3 gpio_calculator.py --help
```

## Integration with Yocto Builds

### Using in BitBake Recipes

You can call these tools from BitBake recipes:

```bitbake
do_configure:prepend() {
    # Validate device tree overlay
    ${WORKDIR}/device_tree_validator.py ${WORKDIR}/my-overlay.dts
}
```

### Build-time Analysis

Add to your build workflow:

```bash
# Start build with logging
bitbake my-image 2>&1 | tee build.log

# Analyze after build
./build_analyzer.py build.log --recommendations
```

### Configuration Generation

Generate machine configurations:

```bash
# Create configs for new platform
./jetson_config_tool.py --generate-config all \
    --platform jetson-xavier-nx \
    --output-dir conf/machine/
```

## Development

### Adding New Features

Each tool is self-contained and follows this structure:

```
tool_name.py
├── Docstring (module-level)
├── Imports
├── Enums and Constants
├── Data Classes
├── Core Classes
│   ├── __init__
│   ├── Public methods
│   └── Private methods
├── main() function
└── if __name__ == '__main__'
```

### Code Style

- Follow PEP 8
- Use type hints
- Comprehensive docstrings
- Error handling with try/except
- Logging for debugging

### Testing

Each tool includes usage examples in its help text:

```bash
./tool_name.py --help
```

## Contributing

When adding new tools:

1. Follow the existing structure and style
2. Include comprehensive docstrings
3. Add argparse CLI with examples
4. Handle errors gracefully
5. Update this README with usage examples
6. Test on actual Jetson hardware when applicable

## License

MIT License - See individual tool headers for details.

## Author

Meta-Tegra Learning System - Yocto Multi-Agent Learning Project

## Support

For issues and questions:
- Check tool help: `./tool_name.py --help`
- Review examples in epilog
- See main project documentation in `../`

## Version History

- v1.0.0 (2025-11-18): Initial release with 5 production-ready tools
  - GPIO Calculator
  - Recipe Generator
  - Device Tree Validator
  - Build Analyzer
  - Jetson Config Tool
