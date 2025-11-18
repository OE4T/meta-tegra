#!/usr/bin/env python3
"""
Continuous Validation Framework for Yocto & Meta-Tegra
Provides automated build testing, hardware-in-loop integration, performance regression detection,
and report generation for CI/CD pipelines.
"""

import subprocess
import json
import logging
import os
import sys
from pathlib import Path
from typing import Dict, List, Optional, Tuple
from datetime import datetime
import time
import yaml
import argparse

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


class ContinuousValidation:
    """Continuous validation framework for CI/CD"""

    def __init__(self, config_path: Optional[str] = None):
        """
        Initialize continuous validation

        Args:
            config_path: Path to configuration file
        """
        self.config = self._load_config(config_path)
        self.results_dir = Path(self.config.get('results_dir', './ci-results'))
        self.results_dir.mkdir(parents=True, exist_ok=True)
        self.test_results = []
        self.performance_metrics = []
        self.ci_mode = os.environ.get('CI', 'false').lower() == 'true'

    def _load_config(self, config_path: Optional[str]) -> Dict:
        """Load configuration from file or use defaults"""
        default_config = {
            'results_dir': './ci-results',
            'build_dir': '/tmp/ci-build',
            'timeout': 7200,
            'hardware_in_loop': False,
            'machine': 'jetson-xavier-nx-devkit',
            'test_suites': ['recipe', 'build', 'integration'],
            'performance_baseline': {},
            'notification': {
                'enabled': False,
                'email': None,
                'slack_webhook': None
            }
        }

        if config_path and Path(config_path).exists():
            try:
                with open(config_path, 'r') as f:
                    user_config = yaml.safe_load(f)
                    default_config.update(user_config)
            except Exception as e:
                logger.error(f"Error loading config: {e}")

        return default_config

    def run_validation_pipeline(self) -> bool:
        """
        Run complete validation pipeline

        Returns:
            True if all validations pass
        """
        logger.info("="*70)
        logger.info("STARTING CONTINUOUS VALIDATION PIPELINE")
        logger.info("="*70)
        logger.info(f"CI Mode: {self.ci_mode}")
        logger.info(f"Configuration: {self.config}")

        pipeline_start = time.time()

        # Pipeline stages
        stages = [
            ('Environment Setup', self._stage_environment_setup),
            ('Recipe Validation', self._stage_recipe_validation),
            ('Build Testing', self._stage_build_testing),
            ('Integration Testing', self._stage_integration_testing),
            ('Performance Testing', self._stage_performance_testing),
            ('Hardware-in-Loop Testing', self._stage_hardware_testing),
            ('Report Generation', self._stage_report_generation)
        ]

        all_passed = True
        stage_results = []

        for stage_name, stage_func in stages:
            logger.info(f"\n{'='*70}")
            logger.info(f"STAGE: {stage_name}")
            logger.info(f"{'='*70}")

            stage_start = time.time()

            try:
                result = stage_func()
                stage_duration = time.time() - stage_start

                stage_results.append({
                    'stage': stage_name,
                    'passed': result,
                    'duration': stage_duration,
                    'timestamp': datetime.now().isoformat()
                })

                status = "✓ PASSED" if result else "✗ FAILED"
                logger.info(f"{stage_name}: {status} (Duration: {stage_duration:.2f}s)")

                if not result:
                    all_passed = False
                    if self.config.get('fail_fast', False):
                        logger.error("Fail-fast enabled, stopping pipeline")
                        break

            except Exception as e:
                logger.error(f"{stage_name} FAILED with exception: {e}")
                stage_results.append({
                    'stage': stage_name,
                    'passed': False,
                    'error': str(e),
                    'timestamp': datetime.now().isoformat()
                })
                all_passed = False

                if self.config.get('fail_fast', False):
                    break

        pipeline_duration = time.time() - pipeline_start

        # Final report
        logger.info(f"\n{'='*70}")
        logger.info("PIPELINE SUMMARY")
        logger.info(f"{'='*70}")
        logger.info(f"Total Duration: {pipeline_duration:.2f}s")
        logger.info(f"Stages Passed: {sum(1 for r in stage_results if r['passed'])}/{len(stage_results)}")
        logger.info(f"Overall Status: {'PASSED' if all_passed else 'FAILED'}")

        # Save pipeline results
        self._save_pipeline_results(stage_results, all_passed, pipeline_duration)

        # Send notifications
        if self.config.get('notification', {}).get('enabled'):
            self._send_notifications(all_passed, stage_results)

        return all_passed

    def _stage_environment_setup(self) -> bool:
        """Setup stage: verify environment and dependencies"""
        logger.info("Verifying build environment...")

        # Check required tools
        required_tools = ['python3', 'git', 'pytest']

        for tool in required_tools:
            result = subprocess.run(['which', tool], capture_output=True)
            if result.returncode != 0:
                logger.error(f"Required tool not found: {tool}")
                return False

        logger.info("Environment setup completed")
        return True

    def _stage_recipe_validation(self) -> bool:
        """Recipe validation stage"""
        if 'recipe' not in self.config.get('test_suites', []):
            logger.info("Recipe validation skipped (not in test suites)")
            return True

        logger.info("Running recipe validation tests...")

        try:
            result = subprocess.run(
                ['pytest', 'recipe_validator.py', '-v', '--tb=short',
                 '--junit-xml=ci-results/recipe-validation.xml'],
                capture_output=True,
                text=True,
                timeout=self.config.get('timeout', 3600),
                cwd=Path(__file__).parent
            )

            logger.info(result.stdout)
            if result.returncode != 0:
                logger.error(result.stderr)

            return result.returncode == 0

        except subprocess.TimeoutExpired:
            logger.error("Recipe validation timed out")
            return False
        except Exception as e:
            logger.error(f"Recipe validation failed: {e}")
            return False

    def _stage_build_testing(self) -> bool:
        """Build testing stage"""
        if 'build' not in self.config.get('test_suites', []):
            logger.info("Build testing skipped (not in test suites)")
            return True

        logger.info("Running build tests...")

        try:
            result = subprocess.run(
                ['pytest', 'integration_tests/test_build_workflow.py', '-v', '--tb=short',
                 '--junit-xml=ci-results/build-tests.xml'],
                capture_output=True,
                text=True,
                timeout=self.config.get('timeout', 3600),
                cwd=Path(__file__).parent
            )

            logger.info(result.stdout)
            if result.returncode != 0:
                logger.error(result.stderr)

            return result.returncode == 0

        except subprocess.TimeoutExpired:
            logger.error("Build testing timed out")
            return False
        except Exception as e:
            logger.error(f"Build testing failed: {e}")
            return False

    def _stage_integration_testing(self) -> bool:
        """Integration testing stage"""
        if 'integration' not in self.config.get('test_suites', []):
            logger.info("Integration testing skipped (not in test suites)")
            return True

        logger.info("Running integration tests...")

        try:
            result = subprocess.run(
                ['pytest', 'integration_tests/', '-v', '--tb=short',
                 '-m', 'not hardware',  # Skip hardware tests in CI
                 '--junit-xml=ci-results/integration-tests.xml'],
                capture_output=True,
                text=True,
                timeout=self.config.get('timeout', 3600),
                cwd=Path(__file__).parent
            )

            logger.info(result.stdout)
            if result.returncode not in [0, 5]:  # 5 = no tests collected
                logger.error(result.stderr)

            return result.returncode in [0, 5]

        except subprocess.TimeoutExpired:
            logger.error("Integration testing timed out")
            return False
        except Exception as e:
            logger.error(f"Integration testing failed: {e}")
            return False

    def _stage_performance_testing(self) -> bool:
        """Performance testing and regression detection stage"""
        if 'performance' not in self.config.get('test_suites', []):
            logger.info("Performance testing skipped (not in test suites)")
            return True

        logger.info("Running performance tests...")

        # Collect performance metrics
        metrics = self._collect_performance_metrics()

        # Check for regressions
        baseline = self.config.get('performance_baseline', {})
        regressions = self._detect_regressions(metrics, baseline)

        if regressions:
            logger.warning(f"Performance regressions detected: {regressions}")
            # Optionally fail on regression
            if self.config.get('fail_on_regression', False):
                return False

        self.performance_metrics = metrics
        return True

    def _stage_hardware_testing(self) -> bool:
        """Hardware-in-loop testing stage"""
        if not self.config.get('hardware_in_loop', False):
            logger.info("Hardware-in-loop testing skipped (not enabled)")
            return True

        logger.info("Running hardware-in-loop tests...")

        try:
            result = subprocess.run(
                ['pytest', 'hardware_tests/', '-v', '--tb=short',
                 '-m', 'hardware',
                 '--junit-xml=ci-results/hardware-tests.xml'],
                capture_output=True,
                text=True,
                timeout=self.config.get('timeout', 3600),
                cwd=Path(__file__).parent
            )

            logger.info(result.stdout)
            if result.returncode not in [0, 5]:
                logger.error(result.stderr)

            return result.returncode in [0, 5]

        except subprocess.TimeoutExpired:
            logger.error("Hardware testing timed out")
            return False
        except Exception as e:
            logger.error(f"Hardware testing failed: {e}")
            return False

    def _stage_report_generation(self) -> bool:
        """Report generation stage"""
        logger.info("Generating validation reports...")

        try:
            report_path = self.results_dir / f"validation-report-{datetime.now().strftime('%Y%m%d-%H%M%S')}.html"

            self._generate_html_report(report_path)

            logger.info(f"Report generated: {report_path}")
            return True

        except Exception as e:
            logger.error(f"Report generation failed: {e}")
            return False

    def _collect_performance_metrics(self) -> Dict:
        """Collect performance metrics"""
        metrics = {
            'timestamp': datetime.now().isoformat(),
            'build_time': None,
            'image_size': None,
            'boot_time': None
        }

        # Mock implementation - would collect real metrics
        logger.info("Collecting performance metrics...")

        return metrics

    def _detect_regressions(self, current: Dict, baseline: Dict) -> List[str]:
        """
        Detect performance regressions

        Args:
            current: Current performance metrics
            baseline: Baseline performance metrics

        Returns:
            List of regression descriptions
        """
        regressions = []

        threshold = self.config.get('regression_threshold', 0.1)  # 10% threshold

        for metric, value in current.items():
            if metric in baseline and value is not None:
                baseline_value = baseline[metric]
                if isinstance(value, (int, float)) and isinstance(baseline_value, (int, float)):
                    change = (value - baseline_value) / baseline_value

                    if change > threshold:
                        regressions.append(
                            f"{metric}: {change*100:.2f}% increase (current: {value}, baseline: {baseline_value})"
                        )

        return regressions

    def _save_pipeline_results(self, stage_results: List[Dict], passed: bool, duration: float):
        """Save pipeline results to JSON file"""
        results = {
            'timestamp': datetime.now().isoformat(),
            'passed': passed,
            'duration': duration,
            'ci_mode': self.ci_mode,
            'config': self.config,
            'stages': stage_results,
            'performance_metrics': self.performance_metrics
        }

        results_file = self.results_dir / 'latest-pipeline-results.json'

        with open(results_file, 'w') as f:
            json.dump(results, f, indent=2)

        logger.info(f"Pipeline results saved to: {results_file}")

    def _generate_html_report(self, output_path: Path):
        """Generate HTML report"""
        html_content = f"""
<!DOCTYPE html>
<html>
<head>
    <title>Yocto Validation Report</title>
    <style>
        body {{ font-family: Arial, sans-serif; margin: 20px; }}
        h1 {{ color: #333; }}
        .passed {{ color: green; }}
        .failed {{ color: red; }}
        table {{ border-collapse: collapse; width: 100%; margin-top: 20px; }}
        th, td {{ border: 1px solid #ddd; padding: 8px; text-align: left; }}
        th {{ background-color: #4CAF50; color: white; }}
    </style>
</head>
<body>
    <h1>Yocto & Meta-Tegra Validation Report</h1>
    <p>Generated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
    <p>Machine: {self.config.get('machine', 'Unknown')}</p>

    <h2>Summary</h2>
    <p>CI Mode: {self.ci_mode}</p>

    <h2>Test Results</h2>
    <p>See detailed test results in JUnit XML files in the results directory.</p>

    <h2>Performance Metrics</h2>
    <table>
        <tr>
            <th>Metric</th>
            <th>Value</th>
        </tr>
        {''.join(f'<tr><td>{k}</td><td>{v}</td></tr>' for k, v in self.performance_metrics.items())}
    </table>
</body>
</html>
"""

        with open(output_path, 'w') as f:
            f.write(html_content)

    def _send_notifications(self, success: bool, results: List[Dict]):
        """Send notifications about pipeline results"""
        notification_config = self.config.get('notification', {})

        status = "SUCCESS" if success else "FAILURE"
        message = f"Yocto Validation Pipeline: {status}"

        # Email notification
        if notification_config.get('email'):
            logger.info(f"Would send email to: {notification_config['email']}")

        # Slack notification
        if notification_config.get('slack_webhook'):
            logger.info(f"Would send Slack notification: {message}")


def main():
    """Main entry point for continuous validation"""
    parser = argparse.ArgumentParser(description='Continuous Validation Framework')
    parser.add_argument('-c', '--config', help='Path to configuration file')
    parser.add_argument('--ci', action='store_true', help='Run in CI mode')
    parser.add_argument('--fail-fast', action='store_true', help='Stop on first failure')

    args = parser.parse_args()

    # Set CI environment variable if --ci flag is used
    if args.ci:
        os.environ['CI'] = 'true'

    validator = ContinuousValidation(args.config)

    if args.fail_fast:
        validator.config['fail_fast'] = True

    success = validator.run_validation_pipeline()

    sys.exit(0 if success else 1)


if __name__ == '__main__':
    main()
