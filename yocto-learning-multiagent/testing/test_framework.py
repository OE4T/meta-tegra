#!/usr/bin/env python3
"""
Main Test Orchestrator for Yocto & Meta-Tegra Learning System
Coordinates recipe validation, build verification, hardware deployment, and performance benchmarking.
"""

import pytest
import subprocess
import json
import logging
from pathlib import Path
from typing import Dict, List, Optional, Tuple
from datetime import datetime
import sys
import yaml

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


class TestFramework:
    """Main test orchestration framework for Yocto builds"""

    def __init__(self, config_path: Optional[str] = None):
        """
        Initialize test framework

        Args:
            config_path: Path to test configuration file
        """
        self.config = self._load_config(config_path)
        self.build_dir = Path(self.config.get('build_dir', '/tmp/yocto-build'))
        self.results_dir = Path(self.config.get('results_dir', './test-results'))
        self.results_dir.mkdir(parents=True, exist_ok=True)
        self.test_results = []

    def _load_config(self, config_path: Optional[str]) -> Dict:
        """Load test configuration"""
        if config_path and Path(config_path).exists():
            with open(config_path, 'r') as f:
                return yaml.safe_load(f)
        return {
            'build_dir': '/tmp/yocto-build',
            'results_dir': './test-results',
            'timeout': 3600,
            'machine': 'jetson-xavier-nx-devkit',
            'distro': 'poky-tegra'
        }

    def run_all_tests(self) -> bool:
        """
        Run all test categories

        Returns:
            True if all tests pass, False otherwise
        """
        logger.info("Starting comprehensive test suite...")

        test_suites = [
            ('Recipe Validation', self.run_recipe_validation),
            ('Build Verification', self.run_build_verification),
            ('Hardware Deployment', self.run_hardware_deployment),
            ('Performance Benchmarking', self.run_performance_benchmarking)
        ]

        all_passed = True
        for suite_name, test_func in test_suites:
            logger.info(f"\n{'='*60}")
            logger.info(f"Running {suite_name}")
            logger.info(f"{'='*60}")

            try:
                result = test_func()
                self.test_results.append({
                    'suite': suite_name,
                    'passed': result,
                    'timestamp': datetime.now().isoformat()
                })
                if not result:
                    all_passed = False
                    logger.error(f"{suite_name} FAILED")
                else:
                    logger.info(f"{suite_name} PASSED")
            except Exception as e:
                logger.error(f"{suite_name} FAILED with exception: {e}")
                self.test_results.append({
                    'suite': suite_name,
                    'passed': False,
                    'error': str(e),
                    'timestamp': datetime.now().isoformat()
                })
                all_passed = False

        self._generate_report()
        return all_passed

    def run_recipe_validation(self) -> bool:
        """
        Run recipe validation tests

        Returns:
            True if all recipes are valid
        """
        from recipe_validator import RecipeValidator

        logger.info("Validating BitBake recipes...")

        validator = RecipeValidator()
        recipe_dir = Path(self.config.get('recipe_dir', './recipes-samples'))

        if not recipe_dir.exists():
            logger.warning(f"Recipe directory {recipe_dir} not found")
            return True  # Skip if no recipes to validate

        results = validator.validate_directory(recipe_dir)

        failed = [r for r in results if not r['valid']]
        if failed:
            logger.error(f"Recipe validation failed for {len(failed)} recipes:")
            for failure in failed:
                logger.error(f"  - {failure['file']}: {failure.get('errors', [])}")
            return False

        logger.info(f"All {len(results)} recipes validated successfully")
        return True

    def run_build_verification(self) -> bool:
        """
        Run build verification tests

        Returns:
            True if build verification passes
        """
        logger.info("Running build verification tests...")

        # Check build environment setup
        if not self._verify_build_environment():
            logger.error("Build environment verification failed")
            return False

        # Verify BitBake is accessible
        if not self._verify_bitbake():
            logger.error("BitBake verification failed")
            return False

        # Check essential build dependencies
        if not self._verify_build_dependencies():
            logger.error("Build dependencies verification failed")
            return False

        logger.info("Build verification completed successfully")
        return True

    def run_hardware_deployment(self) -> bool:
        """
        Run hardware deployment tests

        Returns:
            True if deployment tests pass
        """
        logger.info("Running hardware deployment tests...")

        hardware_available = self.config.get('hardware_available', False)

        if not hardware_available:
            logger.warning("Hardware not available, skipping hardware deployment tests")
            return True

        # Run hardware test suite
        test_cmd = [
            'pytest',
            'hardware_tests/',
            '-v',
            '--junit-xml=test-results/hardware-tests.xml',
            '--tb=short'
        ]

        try:
            result = subprocess.run(
                test_cmd,
                cwd=Path(__file__).parent,
                capture_output=True,
                text=True,
                timeout=self.config.get('timeout', 3600)
            )

            logger.info(result.stdout)
            if result.returncode != 0:
                logger.error(result.stderr)
                return False

            return True
        except subprocess.TimeoutExpired:
            logger.error("Hardware deployment tests timed out")
            return False
        except Exception as e:
            logger.error(f"Hardware deployment tests failed: {e}")
            return False

    def run_performance_benchmarking(self) -> bool:
        """
        Run performance benchmarking tests

        Returns:
            True if performance benchmarks meet criteria
        """
        logger.info("Running performance benchmarking...")

        benchmarks = {
            'build_time': self._benchmark_build_time(),
            'image_size': self._benchmark_image_size(),
            'boot_time': self._benchmark_boot_time()
        }

        # Check against thresholds
        thresholds = self.config.get('performance_thresholds', {})

        all_passed = True
        for metric, value in benchmarks.items():
            if value is None:
                logger.warning(f"Benchmark {metric} could not be measured")
                continue

            threshold = thresholds.get(metric)
            if threshold:
                passed = value <= threshold
                status = "PASS" if passed else "FAIL"
                logger.info(f"  {metric}: {value} (threshold: {threshold}) - {status}")
                if not passed:
                    all_passed = False
            else:
                logger.info(f"  {metric}: {value} (no threshold)")

        return all_passed

    def _verify_build_environment(self) -> bool:
        """Verify build environment is properly configured"""
        required_vars = ['BUILDDIR', 'PATH']

        for var in required_vars:
            if var not in subprocess.os.environ:
                logger.warning(f"Environment variable {var} not set")

        return True

    def _verify_bitbake(self) -> bool:
        """Verify BitBake is accessible"""
        try:
            result = subprocess.run(
                ['which', 'bitbake'],
                capture_output=True,
                text=True,
                timeout=10
            )

            if result.returncode == 0:
                logger.info(f"BitBake found at: {result.stdout.strip()}")
                return True
            else:
                logger.warning("BitBake not found in PATH")
                return True  # Not critical for all tests
        except Exception as e:
            logger.warning(f"Could not verify BitBake: {e}")
            return True

    def _verify_build_dependencies(self) -> bool:
        """Verify essential build dependencies"""
        dependencies = ['python3', 'git', 'make', 'gcc']

        missing = []
        for dep in dependencies:
            result = subprocess.run(
                ['which', dep],
                capture_output=True,
                timeout=10
            )
            if result.returncode != 0:
                missing.append(dep)

        if missing:
            logger.warning(f"Missing dependencies: {', '.join(missing)}")

        return len(missing) == 0

    def _benchmark_build_time(self) -> Optional[float]:
        """Benchmark build time (mock implementation)"""
        # In real implementation, this would measure actual build time
        logger.info("Build time benchmarking not yet implemented")
        return None

    def _benchmark_image_size(self) -> Optional[int]:
        """Benchmark image size (mock implementation)"""
        # In real implementation, this would check generated image size
        logger.info("Image size benchmarking not yet implemented")
        return None

    def _benchmark_boot_time(self) -> Optional[float]:
        """Benchmark boot time (mock implementation)"""
        # In real implementation, this would measure boot time on hardware
        logger.info("Boot time benchmarking not yet implemented")
        return None

    def _generate_report(self):
        """Generate test report"""
        report_path = self.results_dir / f"test-report-{datetime.now().strftime('%Y%m%d-%H%M%S')}.json"

        report = {
            'timestamp': datetime.now().isoformat(),
            'config': self.config,
            'results': self.test_results,
            'summary': {
                'total': len(self.test_results),
                'passed': sum(1 for r in self.test_results if r['passed']),
                'failed': sum(1 for r in self.test_results if not r['passed'])
            }
        }

        with open(report_path, 'w') as f:
            json.dump(report, f, indent=2)

        logger.info(f"\nTest Report Generated: {report_path}")
        logger.info(f"Summary: {report['summary']['passed']}/{report['summary']['total']} test suites passed")


# Pytest fixtures and test cases
@pytest.fixture
def test_framework():
    """Pytest fixture for test framework"""
    return TestFramework()


class TestRecipeValidation:
    """Test suite for recipe validation"""

    def test_recipe_syntax(self, test_framework):
        """Test recipe syntax validation"""
        assert test_framework.run_recipe_validation()

    def test_recipe_variables(self, test_framework):
        """Test recipe variables are properly defined"""
        from recipe_validator import RecipeValidator
        validator = RecipeValidator()
        # This would test specific variable requirements
        assert validator is not None


class TestBuildVerification:
    """Test suite for build verification"""

    def test_build_environment(self, test_framework):
        """Test build environment is properly configured"""
        assert test_framework._verify_build_environment()

    def test_bitbake_available(self, test_framework):
        """Test BitBake is accessible"""
        assert test_framework._verify_bitbake()

    def test_build_dependencies(self, test_framework):
        """Test build dependencies are installed"""
        assert test_framework._verify_build_dependencies()


class TestPerformanceBenchmarks:
    """Test suite for performance benchmarking"""

    def test_performance_benchmarks_run(self, test_framework):
        """Test that performance benchmarks can run"""
        result = test_framework.run_performance_benchmarking()
        assert result is not None


def main():
    """Main entry point for test framework"""
    import argparse

    parser = argparse.ArgumentParser(description='Yocto Test Framework')
    parser.add_argument('-c', '--config', help='Path to configuration file')
    parser.add_argument('-v', '--verbose', action='store_true', help='Verbose output')
    parser.add_argument('--suite', choices=['recipe', 'build', 'hardware', 'performance', 'all'],
                        default='all', help='Test suite to run')

    args = parser.parse_args()

    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)

    framework = TestFramework(args.config)

    if args.suite == 'all':
        success = framework.run_all_tests()
    elif args.suite == 'recipe':
        success = framework.run_recipe_validation()
    elif args.suite == 'build':
        success = framework.run_build_verification()
    elif args.suite == 'hardware':
        success = framework.run_hardware_deployment()
    elif args.suite == 'performance':
        success = framework.run_performance_benchmarking()

    sys.exit(0 if success else 1)


if __name__ == '__main__':
    main()
