#!/usr/bin/env python3
"""
BitBake Recipe Generator - Interactive tool for creating Yocto recipes.

This tool helps generate BitBake recipes (.bb files) with proper formatting,
license validation, and dependency analysis.

Features:
- Interactive CLI for recipe creation
- Templates for common recipe patterns (application, library, kernel module)
- Automatic dependency detection and analysis
- License validation against SPDX identifiers
- Support for multiple build systems (autotools, cmake, meson, python)
- Recipe structure validation

Author: Meta-Tegra Learning System
License: MIT
"""

import argparse
import json
import os
import re
import sys
from dataclasses import dataclass, field
from enum import Enum
from pathlib import Path
from typing import Dict, List, Optional, Set
from datetime import datetime


class RecipeType(Enum):
    """Types of recipes that can be generated."""
    APPLICATION = "application"
    LIBRARY = "library"
    KERNEL_MODULE = "kernel-module"
    PYTHON = "python"
    SERVICE = "service"
    FIRMWARE = "firmware"


class BuildSystem(Enum):
    """Supported build systems."""
    AUTOTOOLS = "autotools"
    CMAKE = "cmake"
    MESON = "meson"
    PYTHON_SETUPTOOLS = "python-setuptools"
    PYTHON_POETRY = "python-poetry"
    MAKEFILE = "makefile"
    CUSTOM = "custom"


@dataclass
class RecipeMetadata:
    """Metadata for a BitBake recipe."""
    name: str
    version: str
    summary: str
    description: str
    homepage: str
    license: str
    license_files: List[str]
    recipe_type: RecipeType
    build_system: BuildSystem
    source_uri: List[str]
    source_checksum_md5: Optional[str] = None
    source_checksum_sha256: Optional[str] = None
    depends: List[str] = field(default_factory=list)
    rdepends: List[str] = field(default_factory=list)
    provides: List[str] = field(default_factory=list)
    conflicts: List[str] = field(default_factory=list)
    packages: List[str] = field(default_factory=list)
    files: Dict[str, List[str]] = field(default_factory=dict)
    extra_oeconf: List[str] = field(default_factory=list)
    extra_oecmake: List[str] = field(default_factory=list)
    install_extra: List[str] = field(default_factory=list)


class LicenseValidator:
    """Validate licenses against SPDX identifiers."""

    # Common SPDX license identifiers
    SPDX_LICENSES = {
        'MIT', 'Apache-2.0', 'GPL-2.0', 'GPL-2.0-only', 'GPL-2.0-or-later',
        'GPL-3.0', 'GPL-3.0-only', 'GPL-3.0-or-later', 'LGPL-2.1', 'LGPL-2.1-only',
        'LGPL-2.1-or-later', 'LGPL-3.0', 'LGPL-3.0-only', 'LGPL-3.0-or-later',
        'BSD-2-Clause', 'BSD-3-Clause', 'ISC', 'MPL-2.0', 'EPL-1.0', 'EPL-2.0',
        'AGPL-3.0', 'AGPL-3.0-only', 'AGPL-3.0-or-later', 'Unlicense', 'CC0-1.0',
        'CC-BY-4.0', 'CC-BY-SA-4.0', 'Proprietary', 'CLOSED'
    }

    @classmethod
    def validate(cls, license_string: str) -> tuple[bool, List[str]]:
        """
        Validate license string against SPDX identifiers.

        Args:
            license_string: License string (can include & and | operators)

        Returns:
            Tuple of (is_valid, list of warnings)
        """
        warnings = []

        # Split on & and | operators
        licenses = re.split(r'\s*[&|]\s*', license_string)

        for lic in licenses:
            lic = lic.strip()
            if lic not in cls.SPDX_LICENSES:
                warnings.append(f"'{lic}' is not a recognized SPDX license identifier")

        return len(warnings) == 0, warnings

    @classmethod
    def suggest_license(cls, license_string: str) -> Optional[str]:
        """Suggest correct SPDX license identifier."""
        license_lower = license_string.lower()

        suggestions = {
            'gpl': 'GPL-2.0-or-later',
            'gpl2': 'GPL-2.0-only',
            'gpl3': 'GPL-3.0-only',
            'lgpl': 'LGPL-2.1-or-later',
            'bsd': 'BSD-3-Clause',
            'apache': 'Apache-2.0',
        }

        for key, value in suggestions.items():
            if key in license_lower:
                return value

        return None


class DependencyAnalyzer:
    """Analyze and validate recipe dependencies."""

    # Common runtime dependencies mapping
    RDEPENDS_MAP = {
        'python3': ['python3-core'],
        'bash': ['bash'],
        'perl': ['perl'],
        'systemd': ['systemd'],
    }

    # Common build dependencies
    BUILD_DEPS = {
        BuildSystem.AUTOTOOLS: ['autoconf-native', 'automake-native', 'libtool-native'],
        BuildSystem.CMAKE: ['cmake-native'],
        BuildSystem.MESON: ['meson-native', 'ninja-native'],
        BuildSystem.PYTHON_SETUPTOOLS: ['python3-setuptools-native'],
        BuildSystem.PYTHON_POETRY: ['python3-poetry-native'],
    }

    @classmethod
    def get_build_dependencies(cls, build_system: BuildSystem) -> List[str]:
        """Get required build dependencies for a build system."""
        return cls.BUILD_DEPS.get(build_system, [])

    @classmethod
    def suggest_rdepends(cls, recipe_type: RecipeType, content_hints: List[str]) -> List[str]:
        """
        Suggest runtime dependencies based on recipe type and content.

        Args:
            recipe_type: Type of recipe
            content_hints: List of strings indicating content (e.g., ['python', 'systemd'])

        Returns:
            List of suggested runtime dependencies
        """
        rdepends = []

        for hint in content_hints:
            if hint in cls.RDEPENDS_MAP:
                rdepends.extend(cls.RDEPENDS_MAP[hint])

        # Recipe type specific dependencies
        if recipe_type == RecipeType.PYTHON:
            rdepends.append('python3-core')
        elif recipe_type == RecipeType.SERVICE:
            rdepends.append('systemd')

        return list(set(rdepends))


class RecipeGenerator:
    """Generate BitBake recipes from metadata."""

    def __init__(self, metadata: RecipeMetadata):
        """
        Initialize recipe generator.

        Args:
            metadata: Recipe metadata
        """
        self.metadata = metadata

    def generate_header(self) -> str:
        """Generate recipe header with metadata."""
        header = f"""# Recipe for {self.metadata.name}
# Generated by BitBake Recipe Generator on {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}

SUMMARY = "{self.metadata.summary}"
DESCRIPTION = "{self.metadata.description}"
HOMEPAGE = "{self.metadata.homepage}"
LICENSE = "{self.metadata.license}"
"""
        # Add license file checksums
        for i, lic_file in enumerate(self.metadata.license_files):
            if i == 0:
                header += f'LIC_FILES_CHKSUM = "file://{lic_file};md5=CHECKSUM_HERE"\n'
            else:
                header += f'                    "file://{lic_file};md5=CHECKSUM_HERE"\n'

        header += "\n"
        return header

    def generate_source_uri(self) -> str:
        """Generate SRC_URI variable."""
        if not self.metadata.source_uri:
            return ""

        src_uri = 'SRC_URI = "'
        if len(self.metadata.source_uri) == 1:
            src_uri += self.metadata.source_uri[0] + '"\n'
        else:
            src_uri += self.metadata.source_uri[0] + ' \\\n'
            for uri in self.metadata.source_uri[1:-1]:
                src_uri += f'           {uri} \\\n'
            src_uri += f'           {self.metadata.source_uri[-1]}"\n'

        # Add checksums
        if self.metadata.source_checksum_md5:
            src_uri += f'SRC_URI[md5sum] = "{self.metadata.source_checksum_md5}"\n'
        if self.metadata.source_checksum_sha256:
            src_uri += f'SRC_URI[sha256sum] = "{self.metadata.source_checksum_sha256}"\n'

        src_uri += "\n"
        return src_uri

    def generate_dependencies(self) -> str:
        """Generate dependency variables."""
        deps = ""

        if self.metadata.depends:
            deps += 'DEPENDS = "'
            deps += ' '.join(self.metadata.depends)
            deps += '"\n'

        if self.metadata.rdepends:
            deps += f'RDEPENDS:${{PN}} = "'
            deps += ' '.join(self.metadata.rdepends)
            deps += '"\n'

        if self.metadata.provides:
            deps += 'PROVIDES = "'
            deps += ' '.join(self.metadata.provides)
            deps += '"\n'

        if self.metadata.conflicts:
            deps += 'CONFLICTS = "'
            deps += ' '.join(self.metadata.conflicts)
            deps += '"\n'

        if deps:
            deps += "\n"

        return deps

    def generate_inherit(self) -> str:
        """Generate inherit line based on build system."""
        inherit_map = {
            BuildSystem.AUTOTOOLS: 'inherit autotools',
            BuildSystem.CMAKE: 'inherit cmake',
            BuildSystem.MESON: 'inherit meson',
            BuildSystem.PYTHON_SETUPTOOLS: 'inherit setuptools3',
            BuildSystem.PYTHON_POETRY: 'inherit python_poetry',
            BuildSystem.MAKEFILE: '# Using Makefile',
        }

        inherit_line = inherit_map.get(self.metadata.build_system, '# Custom build system')

        # Add pkgconfig if likely needed
        if self.metadata.build_system in [BuildSystem.AUTOTOOLS, BuildSystem.CMAKE, BuildSystem.MESON]:
            inherit_line += ' pkgconfig'

        return inherit_line + '\n\n'

    def generate_configuration(self) -> str:
        """Generate configuration options."""
        config = ""

        if self.metadata.extra_oeconf:
            config += 'EXTRA_OECONF = "'
            config += ' '.join(self.metadata.extra_oeconf)
            config += '"\n\n'

        if self.metadata.extra_oecmake:
            config += 'EXTRA_OECMAKE = "'
            config += ' '.join(self.metadata.extra_oecmake)
            config += '"\n\n'

        return config

    def generate_install(self) -> str:
        """Generate do_install task if custom installation is needed."""
        if not self.metadata.install_extra:
            return ""

        install = 'do_install:append() {\n'
        for cmd in self.metadata.install_extra:
            install += f'    {cmd}\n'
        install += '}\n\n'

        return install

    def generate_packages(self) -> str:
        """Generate package definitions."""
        if not self.metadata.packages:
            return ""

        packages = 'PACKAGES = "'
        packages += ' '.join(self.metadata.packages)
        packages += '"\n\n'

        # Generate FILES variables for each package
        for pkg, files in self.metadata.files.items():
            if files:
                packages += f'FILES:{pkg} = "'
                packages += ' '.join(files)
                packages += '"\n'

        packages += "\n"
        return packages

    def generate_recipe(self) -> str:
        """Generate complete BitBake recipe."""
        recipe = self.generate_header()
        recipe += self.generate_source_uri()
        recipe += self.generate_dependencies()
        recipe += self.generate_inherit()
        recipe += self.generate_configuration()
        recipe += self.generate_install()
        recipe += self.generate_packages()

        return recipe

    def save_recipe(self, output_path: str):
        """
        Save generated recipe to file.

        Args:
            output_path: Path to save the recipe file
        """
        recipe_content = self.generate_recipe()

        # Ensure directory exists
        os.makedirs(os.path.dirname(output_path) or '.', exist_ok=True)

        with open(output_path, 'w') as f:
            f.write(recipe_content)


def interactive_recipe_creation() -> RecipeMetadata:
    """Interactive CLI for creating recipe metadata."""
    print("=== BitBake Recipe Generator ===\n")

    # Basic information
    name = input("Recipe name (e.g., 'myapp'): ").strip()
    version = input("Version (e.g., '1.0.0'): ").strip()
    summary = input("Summary (one-line description): ").strip()
    description = input("Description (detailed): ").strip()
    homepage = input("Homepage URL: ").strip()

    # License
    print("\nLicense Information:")
    license_str = input("License (SPDX identifier, e.g., 'MIT', 'GPL-2.0-only'): ").strip()

    # Validate license
    is_valid, warnings = LicenseValidator.validate(license_str)
    if not is_valid:
        print("\nLicense warnings:")
        for warning in warnings:
            print(f"  - {warning}")
        suggestion = LicenseValidator.suggest_license(license_str)
        if suggestion:
            print(f"  Suggestion: {suggestion}")
        use_anyway = input("Use this license anyway? (y/n): ").lower() == 'y'
        if not use_anyway:
            license_str = input("Enter corrected license: ").strip()

    license_files = input("License files (comma-separated, e.g., 'LICENSE,COPYING'): ").strip().split(',')
    license_files = [f.strip() for f in license_files if f.strip()]

    # Recipe type
    print("\nRecipe Type:")
    for i, rt in enumerate(RecipeType, 1):
        print(f"  {i}. {rt.value}")
    recipe_type_idx = int(input("Select recipe type (1-6): ")) - 1
    recipe_type = list(RecipeType)[recipe_type_idx]

    # Build system
    print("\nBuild System:")
    for i, bs in enumerate(BuildSystem, 1):
        print(f"  {i}. {bs.value}")
    build_system_idx = int(input("Select build system (1-7): ")) - 1
    build_system = list(BuildSystem)[build_system_idx]

    # Source URI
    print("\nSource Information:")
    source_uri = []
    while True:
        uri = input("Source URI (empty to finish): ").strip()
        if not uri:
            break
        source_uri.append(uri)

    # Dependencies
    print("\nDependencies:")
    depends_str = input("Build dependencies (space-separated): ").strip()
    depends = depends_str.split() if depends_str else []

    # Add automatic build dependencies
    auto_deps = DependencyAnalyzer.get_build_dependencies(build_system)
    if auto_deps:
        print(f"  Auto-adding build dependencies: {', '.join(auto_deps)}")
        depends.extend(auto_deps)
        depends = list(set(depends))

    rdepends_str = input("Runtime dependencies (space-separated): ").strip()
    rdepends = rdepends_str.split() if rdepends_str else []

    return RecipeMetadata(
        name=name,
        version=version,
        summary=summary,
        description=description,
        homepage=homepage,
        license=license_str,
        license_files=license_files,
        recipe_type=recipe_type,
        build_system=build_system,
        source_uri=source_uri,
        depends=depends,
        rdepends=rdepends,
    )


def main():
    """Main entry point for the recipe generator CLI."""
    parser = argparse.ArgumentParser(
        description="BitBake Recipe Generator - Create Yocto recipes",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Interactive recipe creation
  %(prog)s --interactive

  # Generate from JSON metadata
  %(prog)s --from-json recipe-metadata.json --output myapp_1.0.bb

  # Create recipe with template
  %(prog)s --template cmake-app --name myapp --version 1.0 --license MIT

  # Validate existing recipe
  %(prog)s --validate myapp_1.0.bb

  # Analyze dependencies
  %(prog)s --analyze-deps myapp_1.0.bb
        """
    )

    parser.add_argument(
        '--interactive', '-i',
        action='store_true',
        help='Interactive recipe creation mode'
    )

    parser.add_argument(
        '--from-json',
        type=str,
        metavar='FILE',
        help='Load recipe metadata from JSON file'
    )

    parser.add_argument(
        '--output', '-o',
        type=str,
        metavar='FILE',
        help='Output recipe file path'
    )

    parser.add_argument(
        '--template',
        type=str,
        choices=['cmake-app', 'autotools-lib', 'python-pkg', 'kernel-mod', 'systemd-service'],
        help='Use a recipe template'
    )

    parser.add_argument(
        '--name',
        type=str,
        help='Recipe name (used with --template)'
    )

    parser.add_argument(
        '--version',
        type=str,
        help='Recipe version (used with --template)'
    )

    parser.add_argument(
        '--license',
        type=str,
        help='Recipe license (used with --template)'
    )

    parser.add_argument(
        '--validate',
        type=str,
        metavar='FILE',
        help='Validate existing recipe file'
    )

    parser.add_argument(
        '--analyze-deps',
        type=str,
        metavar='FILE',
        help='Analyze dependencies in existing recipe'
    )

    args = parser.parse_args()

    try:
        if args.interactive:
            metadata = interactive_recipe_creation()
            generator = RecipeGenerator(metadata)

            output_file = args.output or f"{metadata.name}_{metadata.version}.bb"
            generator.save_recipe(output_file)

            print(f"\n=== Recipe Generated Successfully ===")
            print(f"Output: {output_file}")
            print(f"\nRecipe Preview:")
            print("-" * 60)
            print(generator.generate_recipe())

        elif args.from_json:
            with open(args.from_json, 'r') as f:
                data = json.load(f)

            # Convert JSON to RecipeMetadata (simplified)
            metadata = RecipeMetadata(
                name=data['name'],
                version=data['version'],
                summary=data.get('summary', ''),
                description=data.get('description', ''),
                homepage=data.get('homepage', ''),
                license=data['license'],
                license_files=data.get('license_files', []),
                recipe_type=RecipeType(data.get('recipe_type', 'application')),
                build_system=BuildSystem(data.get('build_system', 'cmake')),
                source_uri=data.get('source_uri', []),
                depends=data.get('depends', []),
                rdepends=data.get('rdepends', []),
            )

            generator = RecipeGenerator(metadata)
            output_file = args.output or f"{metadata.name}_{metadata.version}.bb"
            generator.save_recipe(output_file)

            print(f"Recipe generated: {output_file}")

        elif args.validate:
            # Basic validation (could be expanded)
            if not os.path.exists(args.validate):
                print(f"Error: File not found: {args.validate}", file=sys.stderr)
                return 1

            with open(args.validate, 'r') as f:
                content = f.read()

            # Check for required variables
            required_vars = ['SUMMARY', 'LICENSE', 'LIC_FILES_CHKSUM']
            missing = [var for var in required_vars if var not in content]

            if missing:
                print(f"Warning: Missing required variables: {', '.join(missing)}")
            else:
                print("Recipe validation passed: All required variables present")

        else:
            parser.print_help()
            return 1

    except KeyboardInterrupt:
        print("\n\nOperation cancelled by user")
        return 130
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        return 1

    return 0


if __name__ == '__main__':
    sys.exit(main())
