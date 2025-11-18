#!/usr/bin/env python3
"""
BitBake Recipe Validator for Yocto & Meta-Tegra
Validates recipe syntax, variables, dependencies, and license compliance.
"""

import re
import logging
from pathlib import Path
from typing import Dict, List, Optional, Tuple, Set
from dataclasses import dataclass, field
import json

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@dataclass
class ValidationResult:
    """Result of recipe validation"""
    file: str
    valid: bool
    errors: List[str] = field(default_factory=list)
    warnings: List[str] = field(default_factory=list)
    info: Dict = field(default_factory=dict)


class RecipeValidator:
    """Validates BitBake recipes for correctness and compliance"""

    # Required variables for a valid recipe
    REQUIRED_VARS = ['DESCRIPTION', 'LICENSE']

    # Common optional variables
    OPTIONAL_VARS = [
        'SUMMARY', 'HOMEPAGE', 'SECTION', 'PRIORITY',
        'DEPENDS', 'RDEPENDS', 'PROVIDES', 'RPROVIDES',
        'PACKAGES', 'FILES', 'SRC_URI', 'SRCREV',
        'S', 'B', 'D', 'WORKDIR',
        'PACKAGECONFIG', 'EXTRA_OECONF', 'EXTRA_OECMAKE',
        'COMPATIBLE_MACHINE', 'PACKAGE_ARCH'
    ]

    # Valid license identifiers (SPDX)
    VALID_LICENSES = [
        'MIT', 'GPL-2.0', 'GPL-2.0-only', 'GPL-2.0-or-later',
        'GPL-3.0', 'GPL-3.0-only', 'GPL-3.0-or-later',
        'LGPL-2.1', 'LGPL-2.1-only', 'LGPL-2.1-or-later',
        'LGPL-3.0', 'LGPL-3.0-only', 'LGPL-3.0-or-later',
        'Apache-2.0', 'BSD-2-Clause', 'BSD-3-Clause',
        'MPL-2.0', 'ISC', 'CLOSED', 'Proprietary'
    ]

    # Common inherit classes
    VALID_INHERIT = [
        'autotools', 'cmake', 'meson', 'setuptools3', 'python3-setuptools',
        'systemd', 'update-rc.d', 'pkgconfig', 'gettext',
        'kernel', 'module', 'allarch', 'native', 'cross',
        'image', 'core-image', 'extrausers',
        'tegra-nvidia-sources', 'l4t_deb_pkgfeed', 'cuda'
    ]

    def __init__(self):
        """Initialize recipe validator"""
        self.validation_stats = {
            'total': 0,
            'passed': 0,
            'failed': 0,
            'warnings': 0
        }

    def validate_file(self, recipe_path: Path) -> ValidationResult:
        """
        Validate a single BitBake recipe file

        Args:
            recipe_path: Path to the recipe file (.bb or .bbappend)

        Returns:
            ValidationResult object
        """
        self.validation_stats['total'] += 1

        result = ValidationResult(
            file=str(recipe_path),
            valid=True
        )

        try:
            # Read recipe content
            with open(recipe_path, 'r', encoding='utf-8') as f:
                content = f.read()

            # Run validation checks
            self._check_syntax(content, result)
            self._check_required_variables(content, result)
            self._check_license_compliance(content, result)
            self._check_dependencies(content, result)
            self._check_inherit_classes(content, result)
            self._check_function_definitions(content, result)
            self._check_src_uri(content, result)
            self._check_common_issues(content, result)

            # Extract metadata
            result.info = self._extract_metadata(content)

            # Update statistics
            if result.valid:
                self.validation_stats['passed'] += 1
            else:
                self.validation_stats['failed'] += 1

            if result.warnings:
                self.validation_stats['warnings'] += len(result.warnings)

        except Exception as e:
            result.valid = False
            result.errors.append(f"Exception during validation: {str(e)}")
            self.validation_stats['failed'] += 1

        return result

    def validate_directory(self, directory: Path) -> List[ValidationResult]:
        """
        Validate all recipes in a directory

        Args:
            directory: Directory containing recipes

        Returns:
            List of ValidationResult objects
        """
        results = []

        # Find all .bb and .bbappend files
        recipe_files = list(directory.rglob('*.bb')) + list(directory.rglob('*.bbappend'))

        logger.info(f"Found {len(recipe_files)} recipe files in {directory}")

        for recipe_file in recipe_files:
            logger.info(f"Validating {recipe_file.name}...")
            result = self.validate_file(recipe_file)
            results.append(result)

            if not result.valid:
                logger.error(f"  FAILED: {', '.join(result.errors)}")
            elif result.warnings:
                logger.warning(f"  WARNINGS: {', '.join(result.warnings)}")
            else:
                logger.info(f"  PASSED")

        return results

    def _check_syntax(self, content: str, result: ValidationResult):
        """Check basic syntax issues"""
        lines = content.split('\n')

        for i, line in enumerate(lines, 1):
            # Check for tabs (should use spaces or proper continuation)
            if '\t' in line and not line.strip().startswith('#'):
                # Tabs are sometimes valid in shell functions
                if not re.match(r'^\s*(do_\w+|python\s+\w+)\s*\(\)', line):
                    result.warnings.append(f"Line {i}: Contains tab character")

            # Check for trailing whitespace
            if line.endswith(' ') or line.endswith('\t'):
                result.warnings.append(f"Line {i}: Trailing whitespace")

            # Check for proper variable assignment
            if '=' in line and not line.strip().startswith('#'):
                # Should have format: VAR = "value" or VAR += "value"
                if not re.match(r'^\s*[\w\-\{\}\.]+\s*[\+\?\:]?=\s*.+', line):
                    # Could be a shell script line
                    if not any(keyword in line for keyword in ['if', 'then', 'else', 'fi', 'for', 'do', 'done']):
                        if '==' not in line and '!=' not in line:  # Not a comparison
                            result.warnings.append(f"Line {i}: Possibly malformed assignment")

    def _check_required_variables(self, content: str, result: ValidationResult):
        """Check that required variables are present"""
        for var in self.REQUIRED_VARS:
            # Look for variable assignment
            pattern = rf'^\s*{var}\s*[\+\?:]?=\s*.+'
            if not re.search(pattern, content, re.MULTILINE):
                result.errors.append(f"Missing required variable: {var}")
                result.valid = False

    def _check_license_compliance(self, content: str, result: ValidationResult):
        """Check license compliance"""
        # Extract LICENSE variable
        license_match = re.search(r'^\s*LICENSE\s*=\s*["\']([^"\']+)["\']', content, re.MULTILINE)

        if license_match:
            license_value = license_match.group(1)

            # Handle multiple licenses with & or |
            licenses = re.split(r'\s*[&|]\s*', license_value)

            for lic in licenses:
                lic = lic.strip()
                if lic not in self.VALID_LICENSES:
                    result.warnings.append(f"Unknown license: {lic} (consider using SPDX identifier)")

            # Check for LIC_FILES_CHKSUM
            if not re.search(r'^\s*LIC_FILES_CHKSUM\s*=', content, re.MULTILINE):
                result.warnings.append("Missing LIC_FILES_CHKSUM variable")
        else:
            result.errors.append("LICENSE variable format error")
            result.valid = False

    def _check_dependencies(self, content: str, result: ValidationResult):
        """Check dependency declarations"""
        # Check DEPENDS
        depends_match = re.search(r'^\s*DEPENDS\s*[\+\?:]?=\s*["\']([^"\']+)["\']', content, re.MULTILINE)
        if depends_match:
            deps = depends_match.group(1).split()
            result.info['depends'] = deps

            # Check for common mistakes
            for dep in deps:
                if dep.endswith('-native') and dep.startswith('virtual/'):
                    result.warnings.append(f"Unusual dependency: {dep}")

        # Check RDEPENDS
        rdepends_matches = re.finditer(
            r'^\s*RDEPENDS[_:\$\{]*[^=]*\s*[\+\?:]?=\s*["\']([^"\']+)["\']',
            content,
            re.MULTILINE
        )
        rdepends = []
        for match in rdepends_matches:
            rdepends.extend(match.group(1).split())
        if rdepends:
            result.info['rdepends'] = rdepends

    def _check_inherit_classes(self, content: str, result: ValidationResult):
        """Check inherit statements"""
        inherit_matches = re.finditer(r'^\s*inherit\s+(.+)$', content, re.MULTILINE)

        inherits = []
        for match in inherit_matches:
            classes = match.group(1).split()
            inherits.extend(classes)

        if inherits:
            result.info['inherits'] = inherits

            # Check for unknown classes (warnings only)
            for cls in inherits:
                if cls not in self.VALID_INHERIT and not cls.startswith('${'):
                    result.warnings.append(f"Uncommon inherit class: {cls}")

    def _check_function_definitions(self, content: str, result: ValidationResult):
        """Check function definitions"""
        # BitBake function pattern: do_taskname() {
        func_pattern = r'^\s*(do_\w+|python\s+\w+)\s*\(\)\s*\{'
        func_matches = re.finditer(func_pattern, content, re.MULTILINE)

        functions = []
        for match in func_matches:
            func_name = match.group(1).strip()
            functions.append(func_name)

        if functions:
            result.info['functions'] = functions

        # Check for unbalanced braces in functions
        brace_count = content.count('{') - content.count('}')
        if brace_count != 0:
            result.errors.append(f"Unbalanced braces (diff: {brace_count})")
            result.valid = False

    def _check_src_uri(self, content: str, result: ValidationResult):
        """Check SRC_URI variable"""
        src_uri_match = re.search(
            r'^\s*SRC_URI\s*[\+\?:]?=\s*["\']([^"\']+)["\']',
            content,
            re.MULTILINE
        )

        if src_uri_match:
            src_uri = src_uri_match.group(1)
            result.info['src_uri'] = src_uri

            # Check for valid URI schemes
            valid_schemes = ['http://', 'https://', 'ftp://', 'git://', 'file://', 'ssh://']
            if not any(scheme in src_uri for scheme in valid_schemes) and not src_uri.startswith('${'):
                result.warnings.append("SRC_URI should contain a valid URI scheme")

            # Check for git repos with SRCREV
            if 'git://' in src_uri or 'git@' in src_uri:
                if not re.search(r'^\s*SRCREV\s*=', content, re.MULTILINE):
                    result.warnings.append("Git source URI should have SRCREV defined")

    def _check_common_issues(self, content: str, result: ValidationResult):
        """Check for common recipe issues"""
        # Check for hardcoded paths
        hardcoded_paths = ['/usr/local', '/opt', '/home']
        for path in hardcoded_paths:
            if path in content and not re.search(rf'#.*{path}', content):
                result.warnings.append(f"Hardcoded path detected: {path}")

        # Check for recipe-specific variable overrides
        if re.search(r'_pn-', content):
            result.warnings.append("Recipe-specific override detected (_pn-), ensure it's intentional")

        # Check for proper version specification
        filename = result.file
        if '_' in Path(filename).stem:
            # Recipe should have version in filename
            result.info['has_version'] = True
        else:
            result.warnings.append("Recipe filename should include version (e.g., recipe_1.0.bb)")

    def _extract_metadata(self, content: str) -> Dict:
        """Extract metadata from recipe"""
        metadata = {}

        # Common variables to extract
        vars_to_extract = ['DESCRIPTION', 'SUMMARY', 'HOMEPAGE', 'SECTION', 'LICENSE']

        for var in vars_to_extract:
            match = re.search(rf'^\s*{var}\s*=\s*["\']([^"\']+)["\']', content, re.MULTILINE)
            if match:
                metadata[var.lower()] = match.group(1)

        return metadata

    def generate_report(self, results: List[ValidationResult], output_file: Optional[str] = None) -> Dict:
        """
        Generate validation report

        Args:
            results: List of validation results
            output_file: Optional file to write report to

        Returns:
            Report dictionary
        """
        report = {
            'summary': self.validation_stats.copy(),
            'results': []
        }

        for result in results:
            report['results'].append({
                'file': result.file,
                'valid': result.valid,
                'errors': result.errors,
                'warnings': result.warnings,
                'info': result.info
            })

        if output_file:
            with open(output_file, 'w') as f:
                json.dump(report, f, indent=2)
            logger.info(f"Report written to {output_file}")

        return report


def main():
    """Main entry point for recipe validator"""
    import argparse

    parser = argparse.ArgumentParser(description='BitBake Recipe Validator')
    parser.add_argument('path', help='Recipe file or directory to validate')
    parser.add_argument('-r', '--report', help='Output report file (JSON)')
    parser.add_argument('-v', '--verbose', action='store_true', help='Verbose output')

    args = parser.parse_args()

    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)

    validator = RecipeValidator()
    path = Path(args.path)

    if path.is_file():
        results = [validator.validate_file(path)]
    elif path.is_dir():
        results = validator.validate_directory(path)
    else:
        logger.error(f"Path not found: {path}")
        return 1

    # Generate report
    report = validator.generate_report(results, args.report)

    # Print summary
    print("\n" + "="*60)
    print("VALIDATION SUMMARY")
    print("="*60)
    print(f"Total recipes: {report['summary']['total']}")
    print(f"Passed: {report['summary']['passed']}")
    print(f"Failed: {report['summary']['failed']}")
    print(f"Warnings: {report['summary']['warnings']}")
    print("="*60)

    return 0 if report['summary']['failed'] == 0 else 1


if __name__ == '__main__':
    import sys
    sys.exit(main())
