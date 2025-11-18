# Yocto & Meta-Tegra Testing Guide

Comprehensive testing documentation for the Yocto & Meta-Tegra learning system.

## Table of Contents

1. [Overview](#overview)
2. [Test Environment Setup](#test-environment-setup)
3. [Running Tests](#running-tests)
4. [Test Suites](#test-suites)
5. [Hardware Testing](#hardware-testing)
6. [Interpreting Results](#interpreting-results)
7. [Adding New Tests](#adding-new-tests)
8. [CI/CD Integration](#cicd-integration)
9. [Troubleshooting](#troubleshooting)

---

## Overview

The testing framework provides comprehensive validation for:

- **BitBake Recipe Validation** - Syntax, variables, dependencies, license compliance
- **Build Verification** - Environment setup, build process, artifact validation
- **Hardware Testing** - GPIO, I2C, Camera, AI performance on Jetson platforms
- **Integration Testing** - Build workflows, deployment, OTA updates, system boot
- **Continuous Validation** - Automated testing for CI/CD pipelines

### Framework Components

```
testing/
├── test_framework.py           # Main test orchestrator
├── recipe_validator.py         # BitBake recipe validator
├── continuous_validation.py    # CI/CD validation framework
├── hardware_tests/             # Hardware-specific tests
│   ├── gpio_tests.py
│   ├── i2c_tests.py
│   ├── camera_tests.py
│   └── ai_performance_tests.py
├── integration_tests/          # Integration tests
│   ├── test_build_workflow.py
│   ├── test_deployment.py
│   ├── test_ota_update.py
│   └── test_system_boot.py
└── TEST-GUIDE.md              # This guide
```

---

## Test Environment Setup

### Prerequisites

Install required Python packages:

```bash
pip3 install pytest pytest-html pyyaml
```

Install system dependencies:

```bash
# For Ubuntu/Debian
sudo apt-get install -y \
    python3-pip \
    git \
    build-essential \
    i2c-tools \
    v4l-utils

# For hardware testing
sudo apt-get install -y \
    gstreamer1.0-tools \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good
```

### Environment Variables

Configure test environment variables:

```bash
# Required for hardware tests
export DEVICE_IP="192.168.1.100"           # Target device IP
export TEST_GPIO_PIN="216"                  # GPIO pin for testing
export TEST_GPIO_OUTPUT_PIN="216"
export TEST_GPIO_INPUT_PIN="218"
export TEST_I2C_BUS="0"                     # I2C bus number
export TEST_I2C_DEVICE_ADDR="0x50"         # I2C device address
export TEST_CAMERA_DEVICE="/dev/video0"    # Camera device

# Optional for build tests
export YOCTO_DIR="/opt/yocto"
export MACHINE="jetson-xavier-nx-devkit"
export DISTRO="poky-tegra"
```

### Configuration File

Create a test configuration file `test_config.yaml`:

```yaml
build_dir: /tmp/yocto-build
results_dir: ./test-results
timeout: 3600
machine: jetson-xavier-nx-devkit
distro: poky-tegra
hardware_available: false

performance_thresholds:
  build_time: 7200
  image_size: 2147483648  # 2GB
  boot_time: 60

test_suites:
  - recipe
  - build
  - integration
```

---

## Running Tests

### Quick Start

Run all tests:

```bash
cd testing/
pytest -v
```

Run specific test suite:

```bash
# Recipe validation
pytest recipe_validator.py -v

# Build workflow tests
pytest integration_tests/test_build_workflow.py -v

# Hardware tests (requires hardware)
pytest hardware_tests/ -v -m hardware
```

### Using the Test Framework

Run the main test orchestrator:

```bash
# Run all test suites
python3 test_framework.py

# Run specific suite
python3 test_framework.py --suite recipe
python3 test_framework.py --suite build
python3 test_framework.py --suite hardware

# Use custom configuration
python3 test_framework.py -c test_config.yaml -v
```

### Recipe Validation

Validate BitBake recipes:

```bash
# Validate single recipe
python3 recipe_validator.py path/to/recipe.bb

# Validate directory of recipes
python3 recipe_validator.py path/to/recipes/ -r validation-report.json

# Verbose output
python3 recipe_validator.py path/to/recipes/ -v
```

---

## Test Suites

### 1. Recipe Validation Tests

**Purpose**: Validate BitBake recipe syntax and compliance

**Location**: `recipe_validator.py`

**Tests**:
- Syntax checking
- Required variables (LICENSE, DESCRIPTION)
- License compliance (SPDX identifiers)
- Dependency verification
- Inherit class validation
- Function definitions
- SRC_URI validation

**Example**:
```bash
pytest recipe_validator.py -v
```

### 2. Build Workflow Tests

**Purpose**: Test BitBake build process

**Location**: `integration_tests/test_build_workflow.py`

**Tests**:
- Build environment setup
- BitBake availability
- Configuration file generation
- Recipe parsing
- Build artifact verification

**Example**:
```bash
pytest integration_tests/test_build_workflow.py -v
```

### 3. Hardware Tests

**Purpose**: Validate hardware functionality on Jetson platforms

**Location**: `hardware_tests/`

#### GPIO Tests (`gpio_tests.py`)

Tests GPIO functionality including export, direction, read/write, and performance.

**Environment Variables**:
- `TEST_GPIO_PIN` - GPIO pin number for testing

**Example**:
```bash
export TEST_GPIO_PIN=216
pytest hardware_tests/gpio_tests.py -v
```

#### I2C Tests (`i2c_tests.py`)

Tests I2C bus communication and device detection.

**Prerequisites**:
- Install `i2c-tools`: `sudo apt-get install i2c-tools`

**Environment Variables**:
- `TEST_I2C_BUS` - I2C bus number
- `TEST_I2C_DEVICE_ADDR` - I2C device address (hex)

**Example**:
```bash
export TEST_I2C_BUS=0
export TEST_I2C_DEVICE_ADDR=0x50
pytest hardware_tests/i2c_tests.py -v
```

#### Camera Tests (`camera_tests.py`)

Tests camera pipeline including V4L2, libargus, and GStreamer.

**Prerequisites**:
- Install `v4l-utils`: `sudo apt-get install v4l-utils`
- Install GStreamer: `sudo apt-get install gstreamer1.0-tools`

**Example**:
```bash
export TEST_CAMERA_DEVICE=/dev/video0
pytest hardware_tests/camera_tests.py -v
```

#### AI Performance Tests (`ai_performance_tests.py`)

Tests AI/ML inference performance, CUDA, TensorRT, and DLA.

**Example**:
```bash
pytest hardware_tests/ai_performance_tests.py -v
```

### 4. Integration Tests

#### Deployment Tests (`test_deployment.py`)

Tests image deployment and device flashing.

**Environment Variables**:
- `DEVICE_IP` - Target device IP address
- `DEVICE_AVAILABLE` - Set to "true" for actual hardware tests

**Example**:
```bash
export DEVICE_IP=192.168.1.100
pytest integration_tests/test_deployment.py -v -m hardware
```

#### OTA Update Tests (`test_ota_update.py`)

Tests over-the-air update functionality.

**Example**:
```bash
export DEVICE_IP=192.168.1.100
pytest integration_tests/test_ota_update.py -v
```

#### System Boot Tests (`test_system_boot.py`)

Tests boot process, bootloader, and system initialization.

**Example**:
```bash
export DEVICE_IP=192.168.1.100
pytest integration_tests/test_system_boot.py -v -m hardware
```

---

## Hardware Testing

### Hardware Test Markers

Tests are marked for hardware requirements:

```python
@pytest.mark.hardware  # Requires physical hardware
@pytest.mark.slow      # Long-running test
```

### Running Hardware Tests

```bash
# Run only hardware tests
pytest -m hardware -v

# Skip hardware tests
pytest -m "not hardware" -v

# Run slow tests
pytest -m slow -v
```

### Hardware Setup Checklist

Before running hardware tests:

1. ✓ Connect to target device network
2. ✓ Configure SSH access (passwordless recommended)
3. ✓ Set DEVICE_IP environment variable
4. ✓ Verify device is accessible: `ping $DEVICE_IP`
5. ✓ Test SSH connection: `ssh root@$DEVICE_IP uname -a`
6. ✓ Configure hardware-specific pins/devices

---

## Interpreting Results

### Test Output

Pytest provides detailed output:

```
tests/gpio_tests.py::TestGPIOBasics::test_gpio_export_unexport PASSED  [ 10%]
tests/gpio_tests.py::TestGPIOBasics::test_gpio_direction PASSED         [ 20%]
tests/gpio_tests.py::TestGPIOOutput::test_gpio_output_high_low PASSED   [ 30%]
...

========================== 10 passed in 5.23s ==========================
```

### Test Reports

Generate HTML report:

```bash
pytest --html=report.html --self-contained-html
```

Generate JUnit XML (for CI/CD):

```bash
pytest --junit-xml=results.xml
```

### Understanding Failures

Common failure patterns:

1. **Environment Issues**
   ```
   FAILED: Environment variable TEST_GPIO_PIN not configured
   ```
   **Solution**: Set required environment variables

2. **Hardware Not Available**
   ```
   SKIPPED: Hardware device not available
   ```
   **Solution**: Connect hardware or skip hardware tests

3. **Permission Issues**
   ```
   ERROR: Permission denied accessing /sys/class/gpio
   ```
   **Solution**: Run with appropriate permissions or configure udev rules

---

## Adding New Tests

### Creating a New Test File

1. Create test file in appropriate directory:

```python
#!/usr/bin/env python3
"""
Description of test module
"""

import pytest
import logging

logger = logging.getLogger(__name__)


class TestMyFeature:
    """Test suite for my feature"""

    def test_basic_functionality(self):
        """Test basic functionality"""
        assert True

    @pytest.mark.hardware
    def test_hardware_feature(self):
        """Test that requires hardware"""
        assert True
```

2. Add test to appropriate test suite in `test_framework.py`

### Test Best Practices

1. **Use descriptive names**: `test_gpio_export_unexport` not `test_1`
2. **Add docstrings**: Explain what the test validates
3. **Use fixtures**: Share setup/teardown code
4. **Clean up resources**: Always cleanup in fixture or test
5. **Mark appropriately**: Use `@pytest.mark.hardware` for hardware tests
6. **Handle errors gracefully**: Skip tests when prerequisites missing

### Example Test with Cleanup

```python
@pytest.fixture
def my_resource():
    """Fixture with cleanup"""
    resource = setup_resource()
    yield resource
    cleanup_resource(resource)


def test_with_cleanup(my_resource):
    """Test using fixture with cleanup"""
    assert my_resource.is_valid()
```

---

## CI/CD Integration

### Using Continuous Validation

Run the continuous validation framework:

```bash
# Standard run
python3 continuous_validation.py

# CI mode with configuration
python3 continuous_validation.py --ci -c ci_config.yaml

# Fail-fast mode
python3 continuous_validation.py --fail-fast
```

### CI Configuration

Create `ci_config.yaml`:

```yaml
results_dir: ./ci-results
build_dir: /tmp/ci-build
timeout: 7200
hardware_in_loop: false

test_suites:
  - recipe
  - build
  - integration

fail_fast: true
fail_on_regression: true

regression_threshold: 0.1  # 10%

performance_baseline:
  build_time: 3600
  image_size: 1073741824

notification:
  enabled: true
  email: team@example.com
  slack_webhook: https://hooks.slack.com/...
```

### GitHub Actions Integration

Example `.github/workflows/yocto-tests.yml`:

```yaml
name: Yocto Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Setup Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.10'

      - name: Install dependencies
        run: |
          pip install pytest pytest-html pyyaml

      - name: Run tests
        run: |
          cd yocto-learning-multiagent/testing
          python3 continuous_validation.py --ci

      - name: Upload test results
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: test-results
          path: yocto-learning-multiagent/testing/ci-results/
```

### Jenkins Integration

```groovy
pipeline {
    agent any

    stages {
        stage('Setup') {
            steps {
                sh 'pip3 install pytest pyyaml'
            }
        }

        stage('Run Tests') {
            steps {
                sh '''
                    cd yocto-learning-multiagent/testing
                    python3 continuous_validation.py --ci -c jenkins_config.yaml
                '''
            }
        }

        stage('Publish Results') {
            steps {
                junit 'yocto-learning-multiagent/testing/ci-results/*.xml'
                publishHTML([
                    reportDir: 'yocto-learning-multiagent/testing/ci-results',
                    reportFiles: 'validation-report-*.html',
                    reportName: 'Validation Report'
                ])
            }
        }
    }
}
```

---

## Troubleshooting

### Common Issues

#### 1. GPIO Tests Fail

**Problem**: Permission denied accessing `/sys/class/gpio`

**Solutions**:
```bash
# Add user to gpio group
sudo usermod -a -G gpio $USER

# Or configure udev rules
sudo sh -c 'echo "SUBSYSTEM==\"gpio\", GROUP=\"gpio\", MODE=\"0660\"" > /etc/udev/rules.d/90-gpio.rules'
sudo udevadm control --reload-rules
```

#### 2. I2C Tests Fail

**Problem**: I2C device not found

**Solutions**:
```bash
# Load i2c-dev kernel module
sudo modprobe i2c-dev

# Check I2C devices
ls -l /dev/i2c-*

# Detect devices on bus
i2cdetect -y 0
```

#### 3. Camera Tests Fail

**Problem**: Camera device not accessible

**Solutions**:
```bash
# Check video devices
ls -l /dev/video*

# Add user to video group
sudo usermod -a -G video $USER

# Check camera with v4l2
v4l2-ctl --list-devices
```

#### 4. SSH Connection Issues

**Problem**: Cannot connect to target device

**Solutions**:
```bash
# Test connectivity
ping $DEVICE_IP

# Test SSH
ssh -v root@$DEVICE_IP

# Setup SSH keys
ssh-copy-id root@$DEVICE_IP
```

#### 5. Import Errors

**Problem**: `ModuleNotFoundError`

**Solutions**:
```bash
# Install required packages
pip3 install pytest pyyaml

# Check Python path
export PYTHONPATH="${PYTHONPATH}:$(pwd)"
```

### Debug Mode

Enable debug logging:

```bash
pytest -v --log-cli-level=DEBUG
```

or in test framework:

```bash
python3 test_framework.py -v --suite all
```

---

## Quick Reference

### Common Commands

```bash
# Run all tests
pytest -v

# Run specific test file
pytest hardware_tests/gpio_tests.py -v

# Run specific test class
pytest hardware_tests/gpio_tests.py::TestGPIOBasics -v

# Run specific test
pytest hardware_tests/gpio_tests.py::TestGPIOBasics::test_gpio_export_unexport -v

# Skip slow tests
pytest -m "not slow" -v

# Run only hardware tests
pytest -m hardware -v

# Generate HTML report
pytest --html=report.html --self-contained-html

# Run with coverage
pytest --cov=. --cov-report=html

# Run continuous validation
python3 continuous_validation.py --ci
```

### Environment Variables Quick Reference

```bash
# Hardware testing
export DEVICE_IP="192.168.1.100"
export TEST_GPIO_PIN="216"
export TEST_I2C_BUS="0"
export TEST_CAMERA_DEVICE="/dev/video0"

# Build testing
export YOCTO_DIR="/opt/yocto"
export MACHINE="jetson-xavier-nx-devkit"
export DISTRO="poky-tegra"
```

---

## Additional Resources

- [Pytest Documentation](https://docs.pytest.org/)
- [Yocto Project Documentation](https://docs.yoctoproject.org/)
- [Meta-Tegra Documentation](https://github.com/OE4T/meta-tegra)
- [Jetson Linux Documentation](https://docs.nvidia.com/jetson/)

---

**Last Updated**: 2025-11-18

**Version**: 1.0.0
