#!/usr/bin/env python3
"""
Tegra GPIO Calculator - Enhanced GPIO conversion tool for NVIDIA Tegra platforms.

This tool provides comprehensive GPIO pin calculations, conversions, and validations
for Tegra SoCs used in Jetson development platforms.

Features:
- Tegra GPIO to Linux GPIO number conversion
- Pin header mapping (40-pin, etc.)
- Pinmux validation and conflict detection
- CSV export for documentation
- Support for multiple Tegra platforms (T210, T186, T194, T234)

Author: Meta-Tegra Learning System
License: MIT
"""

import argparse
import csv
import json
import sys
from dataclasses import dataclass
from enum import Enum
from typing import Dict, List, Optional, Tuple


class TegraPlatform(Enum):
    """Supported Tegra platforms."""
    T210 = "tegra210"  # Jetson Nano, TX1
    T186 = "tegra186"  # Jetson TX2
    T194 = "tegra194"  # Jetson Xavier NX, AGX Xavier
    T234 = "tegra234"  # Jetson Orin


@dataclass
class GPIOPin:
    """Represents a GPIO pin with all its properties."""
    tegra_gpio: str
    linux_gpio: int
    pin_header: Optional[int]
    function: str
    pinmux_group: str
    platform: TegraPlatform
    conflicts: List[str]


class TegraGPIOCalculator:
    """
    Calculate and convert GPIO pins for Tegra platforms.

    The Tegra GPIO naming follows the pattern: GPIO<port><pin>
    where port is A-Z and pin is 0-7.

    Linux GPIO number calculation varies by platform:
    - Tegra210: base + (port * 8) + pin
    - Tegra186/194/234: Different base addresses per controller
    """

    # GPIO base addresses for different platforms
    GPIO_BASES = {
        TegraPlatform.T210: {
            'main': 0,  # TEGRA_MAIN_GPIO starts at 0
        },
        TegraPlatform.T186: {
            'main': 0,
            'aon': 256,  # AON GPIO starts at 256
        },
        TegraPlatform.T194: {
            'main': 288,
            'aon': 0,
        },
        TegraPlatform.T234: {
            'main': 348,
            'aon': 0,
        },
    }

    # 40-pin header mappings for Jetson platforms
    JETSON_40PIN_HEADER = {
        TegraPlatform.T210: {  # Jetson Nano
            7: "GPIO216",   # AUDIO_MCLK
            11: "GPIO50",   # UART2_RTS
            12: "GPIO79",   # I2S0_SCLK
            13: "GPIO14",   # SPI1_SCK
            15: "GPIO194",  # LCD_TE
            16: "GPIO232",  # SPI1_CS1
            18: "GPIO15",   # SPI1_CS0
            19: "GPIO16",   # SPI0_MOSI
            21: "GPIO17",   # SPI0_MISO
            22: "GPIO13",   # SPI1_MISO
            23: "GPIO18",   # SPI0_SCK
            24: "GPIO19",   # SPI0_CS0
            26: "GPIO20",   # SPI0_CS1
            29: "GPIO149",  # CAM_AF_EN
            31: "GPIO200",  # GPIO_PZ0
            32: "GPIO168",  # LCD_BL_PWM
            33: "GPIO38",   # GPIO_PE6
            35: "GPIO76",   # I2S0_LRCLK
            36: "GPIO51",   # UART2_CTS
            37: "GPIO12",   # SPI1_MOSI
            38: "GPIO77",   # I2S0_SDIN
            40: "GPIO78",   # I2S0_SDOUT
        },
        TegraPlatform.T194: {  # Jetson Xavier NX
            7: "GPIO424",   # AUDIO_MCLK
            11: "GPIO428",  # UART1_RTS
            12: "GPIO351",  # I2S0_SCLK
            13: "GPIO353",  # SPI1_SCK
            15: "GPIO395",  # GPIO27
            16: "GPIO389",  # SPI1_CS1
            18: "GPIO417",  # SPI1_CS0
            19: "GPIO366",  # SPI0_MOSI
            21: "GPIO365",  # SPI0_MISO
            22: "GPIO354",  # SPI1_MISO
            23: "GPIO367",  # SPI0_SCK
            24: "GPIO364",  # SPI0_CS0
            26: "GPIO388",  # SPI0_CS1
            29: "GPIO393",  # GPIO05
            31: "GPIO397",  # GPIO09
            32: "GPIO421",  # GPIO13 (PWM)
            33: "GPIO422",  # GPIO14 (PWM)
            35: "GPIO350",  # I2S0_FS
            36: "GPIO429",  # UART1_CTS
            37: "GPIO355",  # SPI1_MOSI
            38: "GPIO352",  # I2S0_SDIN
            40: "GPIO349",  # I2S0_SDOUT
        },
    }

    def __init__(self, platform: TegraPlatform):
        """
        Initialize the GPIO calculator for a specific platform.

        Args:
            platform: The Tegra platform to use for calculations
        """
        self.platform = platform
        self.gpio_base = self.GPIO_BASES.get(platform, {})

    def parse_tegra_gpio(self, gpio_string: str) -> Tuple[str, int, str]:
        """
        Parse Tegra GPIO string into components.

        Args:
            gpio_string: GPIO string like "GPIO216" or "PZ0"

        Returns:
            Tuple of (controller, port, pin)

        Raises:
            ValueError: If GPIO string format is invalid
        """
        gpio_string = gpio_string.upper().strip()

        # Handle "GPIOxxx" format
        if gpio_string.startswith("GPIO"):
            gpio_num = int(gpio_string[4:])
            port = gpio_num // 8
            pin = gpio_num % 8
            port_letter = chr(ord('A') + port)
            return "main", port, pin

        # Handle "Pxy" format (e.g., "PZ0")
        if gpio_string.startswith("P") and len(gpio_string) >= 3:
            port_letter = gpio_string[1]
            pin = int(gpio_string[2])
            port = ord(port_letter) - ord('A')
            return "main", port, pin

        # Handle "AON:Pxy" format
        if ":" in gpio_string:
            controller, pin_part = gpio_string.split(":")
            controller = controller.lower()
            port_letter = pin_part[1]
            pin = int(pin_part[2])
            port = ord(port_letter) - ord('A')
            return controller, port, pin

        raise ValueError(f"Invalid GPIO format: {gpio_string}")

    def tegra_to_linux_gpio(self, gpio_string: str) -> int:
        """
        Convert Tegra GPIO name to Linux GPIO number.

        Args:
            gpio_string: Tegra GPIO name (e.g., "GPIO216", "PZ0", "AON:PEE0")

        Returns:
            Linux GPIO number

        Raises:
            ValueError: If conversion fails
        """
        controller, port, pin = self.parse_tegra_gpio(gpio_string)

        if controller not in self.gpio_base:
            raise ValueError(f"Unknown GPIO controller: {controller}")

        base = self.gpio_base[controller]
        linux_gpio = base + (port * 8) + pin

        return linux_gpio

    def linux_to_tegra_gpio(self, linux_gpio: int) -> str:
        """
        Convert Linux GPIO number to Tegra GPIO name.

        Args:
            linux_gpio: Linux GPIO number

        Returns:
            Tegra GPIO name (e.g., "GPIO216")
        """
        # Try to find which controller this belongs to
        for controller, base in self.gpio_base.items():
            if linux_gpio >= base:
                offset = linux_gpio - base
                port = offset // 8
                pin = offset % 8

                if controller == "main":
                    return f"GPIO{linux_gpio}"
                else:
                    port_letter = chr(ord('A') + port)
                    return f"{controller.upper()}:P{port_letter}{pin}"

        return f"GPIO{linux_gpio}"

    def get_pin_header_mapping(self, pin_number: int) -> Optional[str]:
        """
        Get Tegra GPIO for a given 40-pin header pin number.

        Args:
            pin_number: Physical pin number on 40-pin header (1-40)

        Returns:
            Tegra GPIO name or None if not a GPIO pin
        """
        header_map = self.JETSON_40PIN_HEADER.get(self.platform, {})
        return header_map.get(pin_number)

    def validate_pinmux(self, gpio_pins: List[str]) -> List[str]:
        """
        Validate pinmux configuration and detect conflicts.

        Args:
            gpio_pins: List of GPIO pins to validate

        Returns:
            List of conflict warnings
        """
        conflicts = []

        # Check for duplicate pins
        if len(gpio_pins) != len(set(gpio_pins)):
            conflicts.append("Duplicate GPIO pins detected")

        # Platform-specific conflict checks
        for pin in gpio_pins:
            try:
                linux_gpio = self.tegra_to_linux_gpio(pin)

                # Check if pin is used by critical functions
                if self.platform == TegraPlatform.T210:
                    if linux_gpio in [168]:  # LCD_BL_PWM
                        conflicts.append(f"{pin}: May conflict with display backlight")
                    if linux_gpio in [216]:  # AUDIO_MCLK
                        conflicts.append(f"{pin}: May conflict with audio subsystem")

            except ValueError as e:
                conflicts.append(f"Invalid GPIO: {pin} - {str(e)}")

        return conflicts

    def export_to_csv(self, output_file: str, include_header: bool = True):
        """
        Export GPIO mapping to CSV file.

        Args:
            output_file: Path to output CSV file
            include_header: Include 40-pin header mappings
        """
        with open(output_file, 'w', newline='') as csvfile:
            fieldnames = ['Pin', 'Tegra GPIO', 'Linux GPIO', 'Function', 'Platform']
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

            writer.writeheader()

            if include_header and self.platform in self.JETSON_40PIN_HEADER:
                header_map = self.JETSON_40PIN_HEADER[self.platform]

                for pin_num in sorted(header_map.keys()):
                    tegra_gpio = header_map[pin_num]
                    try:
                        linux_gpio = self.tegra_to_linux_gpio(tegra_gpio)
                        writer.writerow({
                            'Pin': pin_num,
                            'Tegra GPIO': tegra_gpio,
                            'Linux GPIO': linux_gpio,
                            'Function': 'GPIO',
                            'Platform': self.platform.value
                        })
                    except ValueError:
                        pass


def main():
    """Main entry point for the GPIO calculator CLI."""
    parser = argparse.ArgumentParser(
        description="Tegra GPIO Calculator - Convert and validate GPIO pins",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Convert Tegra GPIO to Linux GPIO number
  %(prog)s --platform t210 --tegra-to-linux GPIO216

  # Convert Linux GPIO to Tegra GPIO name
  %(prog)s --platform t210 --linux-to-tegra 216

  # Get GPIO for pin header position
  %(prog)s --platform t210 --pin-header 7

  # Validate pinmux configuration
  %(prog)s --platform t210 --validate GPIO216 GPIO50 GPIO79

  # Export GPIO mapping to CSV
  %(prog)s --platform t210 --export gpio_mapping.csv
        """
    )

    parser.add_argument(
        '--platform',
        type=str,
        required=True,
        choices=['t210', 't186', 't194', 't234'],
        help='Tegra platform (t210=Nano/TX1, t186=TX2, t194=Xavier, t234=Orin)'
    )

    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument(
        '--tegra-to-linux',
        type=str,
        metavar='GPIO',
        help='Convert Tegra GPIO to Linux GPIO number (e.g., GPIO216, PZ0)'
    )
    group.add_argument(
        '--linux-to-tegra',
        type=int,
        metavar='NUM',
        help='Convert Linux GPIO number to Tegra GPIO name'
    )
    group.add_argument(
        '--pin-header',
        type=int,
        metavar='PIN',
        help='Get GPIO for 40-pin header pin number (1-40)'
    )
    group.add_argument(
        '--validate',
        nargs='+',
        metavar='GPIO',
        help='Validate pinmux configuration for given GPIOs'
    )
    group.add_argument(
        '--export',
        type=str,
        metavar='FILE',
        help='Export GPIO mapping to CSV file'
    )

    args = parser.parse_args()

    # Map platform string to enum
    platform_map = {
        't210': TegraPlatform.T210,
        't186': TegraPlatform.T186,
        't194': TegraPlatform.T194,
        't234': TegraPlatform.T234,
    }

    platform = platform_map[args.platform]
    calculator = TegraGPIOCalculator(platform)

    try:
        if args.tegra_to_linux:
            linux_gpio = calculator.tegra_to_linux_gpio(args.tegra_to_linux)
            print(f"Tegra GPIO: {args.tegra_to_linux}")
            print(f"Linux GPIO: {linux_gpio}")
            print(f"\nUsage in device tree:")
            print(f"  gpios = <&gpio {linux_gpio} GPIO_ACTIVE_HIGH>;")
            print(f"\nUsage in sysfs:")
            print(f"  echo {linux_gpio} > /sys/class/gpio/export")

        elif args.linux_to_tegra:
            tegra_gpio = calculator.linux_to_tegra_gpio(args.linux_to_tegra)
            print(f"Linux GPIO: {args.linux_to_tegra}")
            print(f"Tegra GPIO: {tegra_gpio}")

        elif args.pin_header:
            tegra_gpio = calculator.get_pin_header_mapping(args.pin_header)
            if tegra_gpio:
                linux_gpio = calculator.tegra_to_linux_gpio(tegra_gpio)
                print(f"Pin Header: {args.pin_header}")
                print(f"Tegra GPIO: {tegra_gpio}")
                print(f"Linux GPIO: {linux_gpio}")
            else:
                print(f"Pin {args.pin_header} is not a GPIO pin or not supported on this platform")
                return 1

        elif args.validate:
            print(f"Validating {len(args.validate)} GPIO pins...")
            conflicts = calculator.validate_pinmux(args.validate)

            if conflicts:
                print("\nWarnings/Conflicts detected:")
                for conflict in conflicts:
                    print(f"  - {conflict}")
                return 1
            else:
                print("\nNo conflicts detected. Configuration is valid.")
                print("\nGPIO Summary:")
                for gpio in args.validate:
                    try:
                        linux_gpio = calculator.tegra_to_linux_gpio(gpio)
                        print(f"  {gpio:12} -> Linux GPIO {linux_gpio}")
                    except ValueError as e:
                        print(f"  {gpio:12} -> Error: {e}")

        elif args.export:
            calculator.export_to_csv(args.export)
            print(f"GPIO mapping exported to: {args.export}")

    except ValueError as e:
        print(f"Error: {e}", file=sys.stderr)
        return 1
    except Exception as e:
        print(f"Unexpected error: {e}", file=sys.stderr)
        return 2

    return 0


if __name__ == '__main__':
    sys.exit(main())
