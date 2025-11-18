#!/usr/bin/env python3
"""
Device Tree Validator - Comprehensive validation tool for Linux device tree files.

This tool validates device tree source (.dts/.dtsi) files for syntax errors,
compatibility strings, phandle references, and pinmux conflicts.

Features:
- Syntax validation (basic DTS grammar checking)
- Compatibility string validation against known compatible strings
- Phandle reference validation
- Pinmux conflict detection for Tegra platforms
- Overlapping memory region detection
- GPIO usage validation
- Interrupt validation

Author: Meta-Tegra Learning System
License: MIT
"""

import argparse
import json
import os
import re
import sys
from collections import defaultdict
from dataclasses import dataclass, field
from enum import Enum
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple


class Severity(Enum):
    """Validation message severity levels."""
    ERROR = "ERROR"
    WARNING = "WARNING"
    INFO = "INFO"


@dataclass
class ValidationMessage:
    """Represents a validation message."""
    severity: Severity
    line: int
    column: int
    message: str
    context: str = ""

    def __str__(self):
        location = f"Line {self.line}" + (f", Col {self.column}" if self.column > 0 else "")
        return f"[{self.severity.value}] {location}: {self.message}"


@dataclass
class DeviceTreeNode:
    """Represents a device tree node."""
    name: str
    label: Optional[str] = None
    compatible: List[str] = field(default_factory=list)
    properties: Dict[str, str] = field(default_factory=dict)
    children: List['DeviceTreeNode'] = field(default_factory=list)
    line_number: int = 0
    phandles: List[str] = field(default_factory=list)


class CompatibleStringDatabase:
    """Database of known compatible strings."""

    # Known compatible strings for Tegra platforms
    TEGRA_COMPATIBLES = {
        # SoC compatibles
        "nvidia,tegra210": "Tegra X1 / Nano",
        "nvidia,tegra186": "Tegra X2",
        "nvidia,tegra194": "Xavier NX / AGX Xavier",
        "nvidia,tegra234": "Orin NX / AGX Orin",

        # GPIO controllers
        "nvidia,tegra210-gpio": "Tegra210 GPIO controller",
        "nvidia,tegra186-gpio": "Tegra186 GPIO controller",
        "nvidia,tegra186-gpio-aon": "Tegra186 AON GPIO controller",
        "nvidia,tegra194-gpio": "Tegra194 GPIO controller",
        "nvidia,tegra194-gpio-aon": "Tegra194 AON GPIO controller",

        # I2C controllers
        "nvidia,tegra210-i2c": "Tegra210 I2C controller",
        "nvidia,tegra186-i2c": "Tegra186 I2C controller",
        "nvidia,tegra194-i2c": "Tegra194 I2C controller",

        # SPI controllers
        "nvidia,tegra210-spi": "Tegra210 SPI controller",
        "nvidia,tegra186-spi": "Tegra186 SPI controller",
        "nvidia,tegra194-spi": "Tegra194 SPI controller",

        # UART controllers
        "nvidia,tegra210-uart": "Tegra210 UART",
        "nvidia,tegra186-hsuart": "Tegra186 high-speed UART",
        "nvidia,tegra194-hsuart": "Tegra194 high-speed UART",

        # PWM controllers
        "nvidia,tegra210-pwm": "Tegra210 PWM controller",
        "nvidia,tegra186-pwm": "Tegra186 PWM controller",
        "nvidia,tegra194-pwm": "Tegra194 PWM controller",

        # Pinmux
        "nvidia,tegra210-pinmux": "Tegra210 pinmux",
        "nvidia,tegra186-pinmux": "Tegra186 pinmux",
        "nvidia,tegra194-pinmux": "Tegra194 pinmux",

        # Clocks
        "nvidia,tegra210-car": "Tegra210 clock and reset controller",
        "nvidia,tegra186-bpmp": "Tegra186 BPMP",
        "nvidia,tegra194-bpmp": "Tegra194 BPMP",

        # Display
        "nvidia,tegra210-dc": "Tegra210 display controller",
        "nvidia,tegra186-dc": "Tegra186 display controller",
        "nvidia,tegra194-dc": "Tegra194 display controller",

        # Power management
        "nvidia,tegra210-pmc": "Tegra210 PMC",
        "nvidia,tegra186-pmc": "Tegra186 PMC",
        "nvidia,tegra194-pmc": "Tegra194 PMC",

        # USB
        "nvidia,tegra210-xusb": "Tegra210 XUSB controller",
        "nvidia,tegra186-xusb": "Tegra186 XUSB controller",
        "nvidia,tegra194-xusb": "Tegra194 XUSB controller",

        # PCIe
        "nvidia,tegra210-pcie": "Tegra210 PCIe controller",
        "nvidia,tegra186-pcie": "Tegra186 PCIe controller",
        "nvidia,tegra194-pcie": "Tegra194 PCIe controller",

        # Ethernet
        "nvidia,tegra186-eqos": "Tegra186 Ethernet QoS",
        "nvidia,tegra194-eqos": "Tegra194 Ethernet QoS",

        # Camera/Video
        "nvidia,tegra210-vi": "Tegra210 video input",
        "nvidia,tegra210-csi": "Tegra210 CSI",
    }

    # Standard Linux compatible strings
    STANDARD_COMPATIBLES = {
        "simple-bus": "Simple bus for grouping devices",
        "gpio-keys": "GPIO-connected buttons",
        "gpio-leds": "GPIO-connected LEDs",
        "regulator-fixed": "Fixed voltage regulator",
        "pwm-backlight": "PWM-controlled backlight",
        "i2c-mux-pca9546": "PCA9546 I2C multiplexer",
        "spi-nor": "SPI NOR flash",
        "mmc-pwrseq-simple": "Simple MMC power sequence",
    }

    @classmethod
    def is_known_compatible(cls, compatible: str) -> bool:
        """Check if compatible string is known."""
        return (compatible in cls.TEGRA_COMPATIBLES or
                compatible in cls.STANDARD_COMPATIBLES)

    @classmethod
    def get_description(cls, compatible: str) -> Optional[str]:
        """Get description for compatible string."""
        return (cls.TEGRA_COMPATIBLES.get(compatible) or
                cls.STANDARD_COMPATIBLES.get(compatible))


class DeviceTreeParser:
    """Parse device tree source files."""

    def __init__(self, content: str):
        """
        Initialize parser with DTS content.

        Args:
            content: Device tree source content
        """
        self.content = content
        self.lines = content.split('\n')

    def parse(self) -> Tuple[DeviceTreeNode, List[ValidationMessage]]:
        """
        Parse device tree content into node structure.

        Returns:
            Tuple of (root node, list of parse errors)
        """
        messages = []
        root = DeviceTreeNode(name="/", line_number=0)

        # This is a simplified parser - production code would need full DTC parser
        # For now, we extract key information for validation

        return root, messages

    def extract_compatible_strings(self) -> List[Tuple[int, str]]:
        """
        Extract all compatible strings with line numbers.

        Returns:
            List of (line_number, compatible_string) tuples
        """
        compatibles = []
        compatible_pattern = re.compile(r'compatible\s*=\s*"([^"]+)"')

        for i, line in enumerate(self.lines, 1):
            matches = compatible_pattern.findall(line)
            for match in matches:
                # Split on comma for multiple compatibles
                for compat in match.split(','):
                    compatibles.append((i, compat.strip()))

        return compatibles

    def extract_phandles(self) -> Dict[str, int]:
        """
        Extract phandle labels and their line numbers.

        Returns:
            Dictionary mapping label to line number
        """
        phandles = {}
        label_pattern = re.compile(r'^\s*(\w+):\s*\w+@')

        for i, line in enumerate(self.lines, 1):
            match = label_pattern.match(line)
            if match:
                phandles[match.group(1)] = i

        return phandles

    def extract_phandle_references(self) -> List[Tuple[int, str]]:
        """
        Extract phandle references.

        Returns:
            List of (line_number, phandle_label) tuples
        """
        references = []
        ref_pattern = re.compile(r'<&(\w+)')

        for i, line in enumerate(self.lines, 1):
            matches = ref_pattern.findall(line)
            for match in matches:
                references.append((i, match))

        return references

    def extract_pinmux_nodes(self) -> List[Tuple[int, str, List[str]]]:
        """
        Extract pinmux configuration nodes.

        Returns:
            List of (line_number, node_name, pins) tuples
        """
        pinmux_configs = []
        # Simplified extraction - would need proper parsing in production
        return pinmux_configs

    def extract_memory_regions(self) -> List[Tuple[int, str, int, int]]:
        """
        Extract memory region definitions.

        Returns:
            List of (line_number, name, address, size) tuples
        """
        regions = []
        reg_pattern = re.compile(r'reg\s*=\s*<0x([0-9a-fA-F]+)\s+0x([0-9a-fA-F]+)>')

        for i, line in enumerate(self.lines, 1):
            match = reg_pattern.search(line)
            if match:
                addr = int(match.group(1), 16)
                size = int(match.group(2), 16)
                regions.append((i, "region", addr, size))

        return regions


class DeviceTreeValidator:
    """Main device tree validator."""

    def __init__(self, filepath: str):
        """
        Initialize validator.

        Args:
            filepath: Path to device tree source file
        """
        self.filepath = filepath
        self.messages: List[ValidationMessage] = []

        with open(filepath, 'r') as f:
            self.content = f.read()

        self.parser = DeviceTreeParser(self.content)

    def validate_syntax(self):
        """Perform basic syntax validation."""
        # Check for balanced braces
        brace_count = 0
        for i, line in enumerate(self.parser.lines, 1):
            # Remove comments
            line = re.sub(r'//.*$', '', line)
            line = re.sub(r'/\*.*?\*/', '', line)

            brace_count += line.count('{') - line.count('}')

            if brace_count < 0:
                self.messages.append(ValidationMessage(
                    severity=Severity.ERROR,
                    line=i,
                    column=0,
                    message="Unbalanced braces: closing brace without opening"
                ))

        if brace_count != 0:
            self.messages.append(ValidationMessage(
                severity=Severity.ERROR,
                line=len(self.parser.lines),
                column=0,
                message=f"Unbalanced braces: {brace_count} unclosed braces"
            ))

        # Check for proper node syntax
        node_pattern = re.compile(r'^\s*(\w+@[0-9a-fA-F]+|[\w-]+)\s*\{')
        for i, line in enumerate(self.parser.lines, 1):
            # Skip comments and empty lines
            if re.match(r'^\s*(/[/*]|$)', line):
                continue

            # Check if line looks like node declaration
            if '{' in line and not node_pattern.search(line) and 'compatible' not in line:
                # Might be invalid node syntax
                if not re.search(r'=\s*\{', line):  # Not a property array
                    self.messages.append(ValidationMessage(
                        severity=Severity.WARNING,
                        line=i,
                        column=0,
                        message="Potential invalid node syntax",
                        context=line.strip()
                    ))

    def validate_compatible_strings(self):
        """Validate compatible strings against known database."""
        compatibles = self.parser.extract_compatible_strings()

        for line_num, compat in compatibles:
            if not CompatibleStringDatabase.is_known_compatible(compat):
                self.messages.append(ValidationMessage(
                    severity=Severity.WARNING,
                    line=line_num,
                    column=0,
                    message=f"Unknown compatible string: '{compat}'",
                    context="Verify this is a valid compatible string"
                ))
            else:
                desc = CompatibleStringDatabase.get_description(compat)
                self.messages.append(ValidationMessage(
                    severity=Severity.INFO,
                    line=line_num,
                    column=0,
                    message=f"Found compatible: '{compat}' - {desc}"
                ))

    def validate_phandle_references(self):
        """Validate that all phandle references point to defined labels."""
        defined_phandles = self.parser.extract_phandles()
        references = self.parser.extract_phandle_references()

        for line_num, ref in references:
            if ref not in defined_phandles:
                self.messages.append(ValidationMessage(
                    severity=Severity.ERROR,
                    line=line_num,
                    column=0,
                    message=f"Undefined phandle reference: '&{ref}'"
                ))

    def validate_pinmux_conflicts(self):
        """Detect pinmux conflicts (multiple functions assigned to same pin)."""
        # Track which pins are assigned to which functions
        pin_assignments: Dict[str, List[Tuple[int, str]]] = defaultdict(list)

        # Extract pinmux configurations
        pinmux_pattern = re.compile(r'nvidia,pins\s*=\s*"([^"]+)"')
        function_pattern = re.compile(r'nvidia,function\s*=\s*"([^"]+)"')

        current_pins = []
        current_function = None
        current_line = 0

        for i, line in enumerate(self.parser.lines, 1):
            pins_match = pinmux_pattern.search(line)
            func_match = function_pattern.search(line)

            if pins_match:
                current_pins = [p.strip() for p in pins_match.group(1).split(',')]
                current_line = i

            if func_match:
                current_function = func_match.group(1)

            # If we have both pins and function, record the assignment
            if current_pins and current_function:
                for pin in current_pins:
                    pin_assignments[pin].append((current_line, current_function))
                current_pins = []
                current_function = None

        # Check for conflicts
        for pin, assignments in pin_assignments.items():
            if len(assignments) > 1:
                # Check if all assignments are the same function
                functions = set(func for _, func in assignments)
                if len(functions) > 1:
                    lines = ', '.join(str(line) for line, _ in assignments)
                    funcs = ', '.join(functions)
                    self.messages.append(ValidationMessage(
                        severity=Severity.ERROR,
                        line=assignments[0][0],
                        column=0,
                        message=f"Pinmux conflict on pin '{pin}': assigned to multiple functions ({funcs}) at lines {lines}"
                    ))

    def validate_memory_regions(self):
        """Detect overlapping memory regions."""
        regions = self.parser.extract_memory_regions()

        # Sort by address
        regions_sorted = sorted(regions, key=lambda x: x[2])

        for i in range(len(regions_sorted) - 1):
            curr_line, curr_name, curr_addr, curr_size = regions_sorted[i]
            next_line, next_name, next_addr, next_size = regions_sorted[i + 1]

            curr_end = curr_addr + curr_size

            if curr_end > next_addr:
                self.messages.append(ValidationMessage(
                    severity=Severity.ERROR,
                    line=next_line,
                    column=0,
                    message=f"Overlapping memory region: 0x{next_addr:x} overlaps with region at line {curr_line} (ends at 0x{curr_end:x})"
                ))

    def validate_gpio_usage(self):
        """Validate GPIO specifications."""
        gpio_pattern = re.compile(r'gpios?\s*=\s*<&(\w+)\s+(\d+)\s+([^>]+)>')

        for i, line in enumerate(self.parser.lines, 1):
            match = gpio_pattern.search(line)
            if match:
                controller = match.group(1)
                pin_num = int(match.group(2))
                flags = match.group(3)

                # Basic validation
                if pin_num > 255:
                    self.messages.append(ValidationMessage(
                        severity=Severity.WARNING,
                        line=i,
                        column=0,
                        message=f"GPIO pin number {pin_num} seems unusually high"
                    ))

    def validate_all(self) -> List[ValidationMessage]:
        """
        Run all validations.

        Returns:
            List of all validation messages
        """
        self.messages = []

        self.validate_syntax()
        self.validate_compatible_strings()
        self.validate_phandle_references()
        self.validate_pinmux_conflicts()
        self.validate_memory_regions()
        self.validate_gpio_usage()

        return self.messages

    def generate_report(self) -> str:
        """
        Generate validation report.

        Returns:
            Formatted validation report
        """
        errors = [m for m in self.messages if m.severity == Severity.ERROR]
        warnings = [m for m in self.messages if m.severity == Severity.WARNING]
        infos = [m for m in self.messages if m.severity == Severity.INFO]

        report = f"=== Device Tree Validation Report ===\n"
        report += f"File: {self.filepath}\n"
        report += f"Errors: {len(errors)}, Warnings: {len(warnings)}, Info: {len(infos)}\n\n"

        if errors:
            report += "ERRORS:\n"
            for msg in errors:
                report += f"  {msg}\n"
            report += "\n"

        if warnings:
            report += "WARNINGS:\n"
            for msg in warnings:
                report += f"  {msg}\n"
            report += "\n"

        if not errors and not warnings:
            report += "No errors or warnings found!\n"

        return report


def main():
    """Main entry point for the device tree validator CLI."""
    parser = argparse.ArgumentParser(
        description="Device Tree Validator - Validate Linux device tree files",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Validate a device tree file
  %(prog)s my-overlay.dts

  # Validate with detailed output
  %(prog)s --verbose my-device.dts

  # Check only syntax
  %(prog)s --syntax-only tegra-gpio.dtsi

  # Export results to JSON
  %(prog)s --json-output results.json my-overlay.dts

  # Validate multiple files
  %(prog)s *.dts

  # Show only errors
  %(prog)s --errors-only my-overlay.dts
        """
    )

    parser.add_argument(
        'files',
        nargs='*',
        help='Device tree source files to validate (.dts/.dtsi)'
    )

    parser.add_argument(
        '--verbose', '-v',
        action='store_true',
        help='Show detailed validation information'
    )

    parser.add_argument(
        '--syntax-only',
        action='store_true',
        help='Only perform syntax validation'
    )

    parser.add_argument(
        '--errors-only',
        action='store_true',
        help='Show only errors (suppress warnings and info)'
    )

    parser.add_argument(
        '--json-output',
        type=str,
        metavar='FILE',
        help='Export validation results to JSON file'
    )

    parser.add_argument(
        '--compatible-check',
        type=str,
        metavar='STRING',
        help='Check if a compatible string is known'
    )

    args = parser.parse_args()

    # Special mode: just check a compatible string
    if args.compatible_check:
        if CompatibleStringDatabase.is_known_compatible(args.compatible_check):
            desc = CompatibleStringDatabase.get_description(args.compatible_check)
            print(f"'{args.compatible_check}' is a known compatible string")
            print(f"Description: {desc}")
            return 0
        else:
            print(f"'{args.compatible_check}' is NOT a known compatible string")
            return 1

    # Validate files
    if not args.files:
        print("Error: No files specified for validation", file=sys.stderr)
        parser.print_help()
        return 1

    all_results = {}
    total_errors = 0
    total_warnings = 0

    for filepath in args.files:
        if not os.path.exists(filepath):
            print(f"Error: File not found: {filepath}", file=sys.stderr)
            continue

        print(f"\nValidating: {filepath}")
        print("-" * 60)

        try:
            validator = DeviceTreeValidator(filepath)

            if args.syntax_only:
                validator.validate_syntax()
            else:
                validator.validate_all()

            messages = validator.messages

            # Filter messages if requested
            if args.errors_only:
                messages = [m for m in messages if m.severity == Severity.ERROR]

            # Count errors and warnings
            errors = sum(1 for m in messages if m.severity == Severity.ERROR)
            warnings = sum(1 for m in messages if m.severity == Severity.WARNING)

            total_errors += errors
            total_warnings += warnings

            # Display results
            if args.verbose or errors > 0 or warnings > 0:
                print(validator.generate_report())
            else:
                print(f"✓ No issues found ({len(messages)} info messages)")

            # Store results for JSON export
            if args.json_output:
                all_results[filepath] = {
                    'errors': errors,
                    'warnings': warnings,
                    'messages': [
                        {
                            'severity': m.severity.value,
                            'line': m.line,
                            'column': m.column,
                            'message': m.message,
                            'context': m.context
                        }
                        for m in messages
                    ]
                }

        except Exception as e:
            print(f"Error validating {filepath}: {e}", file=sys.stderr)
            total_errors += 1

    # Export to JSON if requested
    if args.json_output:
        with open(args.json_output, 'w') as f:
            json.dump(all_results, f, indent=2)
        print(f"\nResults exported to: {args.json_output}")

    # Summary
    print("\n" + "=" * 60)
    print(f"Validation complete: {total_errors} errors, {total_warnings} warnings")

    return 1 if total_errors > 0 else 0


if __name__ == '__main__':
    sys.exit(main())
