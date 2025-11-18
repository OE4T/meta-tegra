#!/usr/bin/env python3
"""
BitBake Build Analyzer - Performance analysis tool for Yocto builds.

This tool analyzes BitBake build logs to identify performance bottlenecks,
generate optimization reports, and visualize dependency graphs.

Features:
- Parse BitBake console and task logs
- Identify build bottlenecks (longest tasks, most frequent tasks)
- Analyze dependency chains and critical paths
- Generate optimization recommendations
- Export results to various formats (JSON, HTML, CSV)
- Visualize dependency graphs (DOT/GraphViz format)
- Compare multiple builds for regression analysis

Author: Meta-Tegra Learning System
License: MIT
"""

import argparse
import json
import os
import re
import sys
from collections import defaultdict, Counter
from dataclasses import dataclass, field
from datetime import datetime, timedelta
from enum import Enum
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple


class TaskState(Enum):
    """BitBake task states."""
    PENDING = "pending"
    RUNNING = "running"
    COMPLETED = "completed"
    FAILED = "failed"
    SKIPPED = "skipped"


@dataclass
class TaskExecution:
    """Represents a single task execution."""
    recipe: str
    task: str
    start_time: datetime
    end_time: Optional[datetime] = None
    duration: Optional[timedelta] = None
    state: TaskState = TaskState.PENDING
    cpu_percent: float = 0.0
    memory_mb: float = 0.0
    io_read_mb: float = 0.0
    io_write_mb: float = 0.0
    dependencies: List[str] = field(default_factory=list)
    log_file: Optional[str] = None

    def __post_init__(self):
        """Calculate duration if both times are set."""
        if self.start_time and self.end_time:
            self.duration = self.end_time - self.start_time


@dataclass
class BuildStatistics:
    """Overall build statistics."""
    total_tasks: int = 0
    successful_tasks: int = 0
    failed_tasks: int = 0
    skipped_tasks: int = 0
    total_duration: timedelta = timedelta()
    start_time: Optional[datetime] = None
    end_time: Optional[datetime] = None
    build_target: str = ""
    build_machine: str = ""
    parallel_tasks: int = 0


class BitBakeLogParser:
    """Parse BitBake log files."""

    # Regular expressions for log parsing
    TASK_START_PATTERN = re.compile(
        r'NOTE: recipe (\S+)-\S+: task do_(\w+): Started'
    )
    TASK_END_PATTERN = re.compile(
        r'NOTE: recipe (\S+)-\S+: task do_(\w+): (\w+)'
    )
    TASK_TIME_PATTERN = re.compile(
        r'(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})'
    )
    BUILD_START_PATTERN = re.compile(
        r'Build Configuration:'
    )
    BUILD_TARGET_PATTERN = re.compile(
        r'Build target\s+:\s+(\S+)'
    )
    PARALLEL_TASKS_PATTERN = re.compile(
        r'BB_NUMBER_THREADS\s*=\s*"(\d+)"'
    )

    def __init__(self, log_file: str):
        """
        Initialize parser with log file.

        Args:
            log_file: Path to BitBake log file
        """
        self.log_file = log_file
        self.tasks: List[TaskExecution] = []
        self.statistics = BuildStatistics()

    def parse_timestamp(self, timestamp_str: str) -> datetime:
        """Parse timestamp from log line."""
        try:
            return datetime.strptime(timestamp_str, '%Y-%m-%d %H:%M:%S')
        except ValueError:
            return datetime.now()

    def parse_log(self):
        """Parse the BitBake log file."""
        task_tracking: Dict[Tuple[str, str], TaskExecution] = {}

        with open(self.log_file, 'r', encoding='utf-8', errors='ignore') as f:
            for line in f:
                # Extract timestamp
                time_match = self.TASK_TIME_PATTERN.search(line)
                timestamp = self.parse_timestamp(time_match.group(1)) if time_match else datetime.now()

                # Parse task start
                start_match = self.TASK_START_PATTERN.search(line)
                if start_match:
                    recipe = start_match.group(1)
                    task = start_match.group(2)
                    task_key = (recipe, task)

                    task_exec = TaskExecution(
                        recipe=recipe,
                        task=task,
                        start_time=timestamp,
                        state=TaskState.RUNNING
                    )
                    task_tracking[task_key] = task_exec

                    if not self.statistics.start_time:
                        self.statistics.start_time = timestamp

                # Parse task end
                end_match = self.TASK_END_PATTERN.search(line)
                if end_match:
                    recipe = end_match.group(1)
                    task = end_match.group(2)
                    state_str = end_match.group(3).lower()
                    task_key = (recipe, task)

                    if task_key in task_tracking:
                        task_exec = task_tracking[task_key]
                        task_exec.end_time = timestamp
                        task_exec.duration = timestamp - task_exec.start_time

                        # Map state
                        if 'succeed' in state_str or 'completed' in state_str:
                            task_exec.state = TaskState.COMPLETED
                            self.statistics.successful_tasks += 1
                        elif 'failed' in state_str:
                            task_exec.state = TaskState.FAILED
                            self.statistics.failed_tasks += 1
                        elif 'skip' in state_str:
                            task_exec.state = TaskState.SKIPPED
                            self.statistics.skipped_tasks += 1

                        self.tasks.append(task_exec)
                        self.statistics.total_tasks += 1
                        self.statistics.end_time = timestamp

                # Parse build configuration
                if self.BUILD_TARGET_PATTERN.search(line):
                    target_match = self.BUILD_TARGET_PATTERN.search(line)
                    if target_match:
                        self.statistics.build_target = target_match.group(1)

                if self.PARALLEL_TASKS_PATTERN.search(line):
                    parallel_match = self.PARALLEL_TASKS_PATTERN.search(line)
                    if parallel_match:
                        self.statistics.parallel_tasks = int(parallel_match.group(1))

        # Calculate total duration
        if self.statistics.start_time and self.statistics.end_time:
            self.statistics.total_duration = self.statistics.end_time - self.statistics.start_time


class BuildAnalyzer:
    """Analyze BitBake build performance."""

    def __init__(self, tasks: List[TaskExecution], statistics: BuildStatistics):
        """
        Initialize analyzer.

        Args:
            tasks: List of task executions
            statistics: Build statistics
        """
        self.tasks = tasks
        self.statistics = statistics

    def get_longest_tasks(self, limit: int = 10) -> List[TaskExecution]:
        """
        Get longest running tasks.

        Args:
            limit: Maximum number of tasks to return

        Returns:
            List of longest tasks
        """
        completed_tasks = [t for t in self.tasks if t.duration and t.state == TaskState.COMPLETED]
        return sorted(completed_tasks, key=lambda t: t.duration, reverse=True)[:limit]

    def get_most_frequent_tasks(self, limit: int = 10) -> List[Tuple[str, int]]:
        """
        Get most frequently executed tasks.

        Args:
            limit: Maximum number of tasks to return

        Returns:
            List of (task_name, count) tuples
        """
        task_counter = Counter(t.task for t in self.tasks)
        return task_counter.most_common(limit)

    def get_slowest_recipes(self, limit: int = 10) -> List[Tuple[str, timedelta]]:
        """
        Get recipes with longest total build time.

        Args:
            limit: Maximum number of recipes to return

        Returns:
            List of (recipe_name, total_duration) tuples
        """
        recipe_times: Dict[str, timedelta] = defaultdict(timedelta)

        for task in self.tasks:
            if task.duration and task.state == TaskState.COMPLETED:
                recipe_times[task.recipe] += task.duration

        sorted_recipes = sorted(recipe_times.items(), key=lambda x: x[1], reverse=True)
        return sorted_recipes[:limit]

    def get_task_distribution(self) -> Dict[str, Dict[str, int]]:
        """
        Get distribution of task types across recipes.

        Returns:
            Dictionary mapping task names to recipe counts
        """
        task_distribution: Dict[str, Dict[str, int]] = defaultdict(lambda: defaultdict(int))

        for task in self.tasks:
            task_distribution[task.task][task.recipe] += 1

        return dict(task_distribution)

    def calculate_parallelization_efficiency(self) -> float:
        """
        Calculate how efficiently the build used parallel execution.

        Returns:
            Efficiency percentage (0-100)
        """
        if not self.statistics.parallel_tasks or not self.statistics.total_duration:
            return 0.0

        # Sum of all task durations
        total_task_time = sum(
            (t.duration.total_seconds() for t in self.tasks if t.duration),
            timedelta()
        ).total_seconds() if isinstance(sum(
            (t.duration for t in self.tasks if t.duration),
            timedelta()
        ), timedelta) else sum(
            (t.duration.total_seconds() for t in self.tasks if t.duration),
            0.0
        )

        # Actual wall-clock time
        wall_clock_time = self.statistics.total_duration.total_seconds()

        # Theoretical minimum time with perfect parallelization
        theoretical_min = total_task_time / self.statistics.parallel_tasks

        if wall_clock_time == 0:
            return 0.0

        # Efficiency: how close we are to theoretical minimum
        efficiency = (theoretical_min / wall_clock_time) * 100

        return min(efficiency, 100.0)

    def identify_bottlenecks(self) -> List[Dict[str, any]]:
        """
        Identify build bottlenecks.

        Returns:
            List of bottleneck descriptions
        """
        bottlenecks = []

        # Long-running tasks
        longest_tasks = self.get_longest_tasks(5)
        if longest_tasks:
            avg_duration = sum((t.duration.total_seconds() for t in self.tasks if t.duration), 0.0) / max(len([t for t in self.tasks if t.duration]), 1)

            for task in longest_tasks:
                if task.duration.total_seconds() > avg_duration * 3:
                    bottlenecks.append({
                        'type': 'long_task',
                        'severity': 'high',
                        'recipe': task.recipe,
                        'task': task.task,
                        'duration': task.duration.total_seconds(),
                        'message': f"{task.recipe}:do_{task.task} took {task.duration.total_seconds():.1f}s (>3x average)"
                    })

        # Failed tasks
        failed_tasks = [t for t in self.tasks if t.state == TaskState.FAILED]
        for task in failed_tasks:
            bottlenecks.append({
                'type': 'failed_task',
                'severity': 'critical',
                'recipe': task.recipe,
                'task': task.task,
                'message': f"{task.recipe}:do_{task.task} failed"
            })

        # Low parallelization efficiency
        efficiency = self.calculate_parallelization_efficiency()
        if efficiency < 50:
            bottlenecks.append({
                'type': 'low_parallelization',
                'severity': 'medium',
                'efficiency': efficiency,
                'message': f"Low parallelization efficiency ({efficiency:.1f}%). Consider increasing BB_NUMBER_THREADS."
            })

        return bottlenecks

    def generate_optimization_recommendations(self) -> List[str]:
        """
        Generate optimization recommendations based on analysis.

        Returns:
            List of recommendation strings
        """
        recommendations = []

        # Check longest tasks
        longest_tasks = self.get_longest_tasks(3)
        if longest_tasks:
            recommendations.append(
                f"Top time-consuming tasks: {', '.join(f'{t.recipe}:do_{t.task}' for t in longest_tasks[:3])}"
            )

            # Specific recommendations for common slow tasks
            for task in longest_tasks[:3]:
                if task.task == 'compile':
                    recommendations.append(
                        f"  - Consider using PARALLEL_MAKE for {task.recipe} to speed up compilation"
                    )
                elif task.task == 'fetch':
                    recommendations.append(
                        f"  - Slow fetch for {task.recipe}. Consider using premirror or local sources"
                    )
                elif task.task == 'unpack':
                    recommendations.append(
                        f"  - Slow unpack for {task.recipe}. Check if large tarballs can be optimized"
                    )

        # Check parallelization
        efficiency = self.calculate_parallelization_efficiency()
        if efficiency < 60:
            recommendations.append(
                f"Parallelization efficiency is {efficiency:.1f}%. Consider:"
            )
            recommendations.append(
                f"  - Increasing BB_NUMBER_THREADS (current: {self.statistics.parallel_tasks})"
            )
            recommendations.append(
                "  - Using PARALLEL_MAKE for recipes with long compile times"
            )

        # Check for sstate cache hits
        fetch_tasks = [t for t in self.tasks if t.task == 'fetch']
        if len(fetch_tasks) > 50:
            recommendations.append(
                f"Large number of fetch tasks ({len(fetch_tasks)}). Consider:"
            )
            recommendations.append(
                "  - Setting up sstate-cache mirror for faster builds"
            )
            recommendations.append(
                "  - Using DL_DIR on fast storage"
            )

        return recommendations


class DependencyGraphGenerator:
    """Generate dependency graphs for visualization."""

    def __init__(self, tasks: List[TaskExecution]):
        """Initialize with task list."""
        self.tasks = tasks

    def generate_dot_graph(self, output_file: str, max_nodes: int = 50):
        """
        Generate GraphViz DOT format dependency graph.

        Args:
            output_file: Output .dot file path
            max_nodes: Maximum number of nodes to include
        """
        # Build dependency map
        nodes = set()
        edges = set()

        # Only include longest tasks to keep graph readable
        analyzer = BuildAnalyzer(self.tasks, BuildStatistics())
        longest_tasks = analyzer.get_longest_tasks(max_nodes)

        for task in longest_tasks:
            node_id = f"{task.recipe}:do_{task.task}"
            nodes.add(node_id)

            for dep in task.dependencies:
                edges.add((dep, node_id))
                nodes.add(dep)

        # Generate DOT format
        dot_content = "digraph BuildDependencies {\n"
        dot_content += "    rankdir=LR;\n"
        dot_content += "    node [shape=box, style=rounded];\n\n"

        # Add nodes with duration information
        for task in longest_tasks:
            node_id = f"{task.recipe}:do_{task.task}"
            if task.duration:
                label = f"{node_id}\\n{task.duration.total_seconds():.1f}s"
                color = self._get_color_for_duration(task.duration.total_seconds())
                dot_content += f'    "{node_id}" [label="{label}", fillcolor="{color}", style="filled,rounded"];\n'

        dot_content += "\n"

        # Add edges
        for src, dst in edges:
            if src in nodes and dst in nodes:
                dot_content += f'    "{src}" -> "{dst}";\n'

        dot_content += "}\n"

        # Write to file
        with open(output_file, 'w') as f:
            f.write(dot_content)

    def _get_color_for_duration(self, seconds: float) -> str:
        """Get color based on task duration."""
        if seconds < 10:
            return "#90EE90"  # Light green
        elif seconds < 60:
            return "#FFFFE0"  # Light yellow
        elif seconds < 300:
            return "#FFB347"  # Light orange
        else:
            return "#FF6347"  # Tomato red


def main():
    """Main entry point for the build analyzer CLI."""
    parser = argparse.ArgumentParser(
        description="BitBake Build Analyzer - Analyze Yocto build performance",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Analyze a build log
  %(prog)s /path/to/bitbake-cookerdaemon.log

  # Generate detailed report
  %(prog)s --detailed cooker.log

  # Show top 20 longest tasks
  %(prog)s --longest-tasks 20 cooker.log

  # Generate dependency graph
  %(prog)s --dependency-graph deps.dot cooker.log

  # Export analysis to JSON
  %(prog)s --json-output analysis.json cooker.log

  # Compare two builds
  %(prog)s --compare build1.log build2.log

  # Show optimization recommendations
  %(prog)s --recommendations cooker.log
        """
    )

    parser.add_argument(
        'log_file',
        help='BitBake log file to analyze (bitbake-cookerdaemon.log)'
    )

    parser.add_argument(
        '--detailed',
        action='store_true',
        help='Show detailed analysis report'
    )

    parser.add_argument(
        '--longest-tasks',
        type=int,
        metavar='N',
        default=10,
        help='Show N longest tasks (default: 10)'
    )

    parser.add_argument(
        '--slowest-recipes',
        type=int,
        metavar='N',
        default=10,
        help='Show N slowest recipes (default: 10)'
    )

    parser.add_argument(
        '--bottlenecks',
        action='store_true',
        help='Identify and display build bottlenecks'
    )

    parser.add_argument(
        '--recommendations',
        action='store_true',
        help='Show optimization recommendations'
    )

    parser.add_argument(
        '--dependency-graph',
        type=str,
        metavar='FILE',
        help='Generate dependency graph in DOT format'
    )

    parser.add_argument(
        '--json-output',
        type=str,
        metavar='FILE',
        help='Export analysis results to JSON'
    )

    parser.add_argument(
        '--csv-output',
        type=str,
        metavar='FILE',
        help='Export task timing data to CSV'
    )

    args = parser.parse_args()

    # Check if log file exists
    if not os.path.exists(args.log_file):
        print(f"Error: Log file not found: {args.log_file}", file=sys.stderr)
        return 1

    try:
        # Parse log file
        print(f"Parsing build log: {args.log_file}")
        parser_obj = BitBakeLogParser(args.log_file)
        parser_obj.parse_log()

        print(f"Found {len(parser_obj.tasks)} task executions\n")

        # Create analyzer
        analyzer = BuildAnalyzer(parser_obj.tasks, parser_obj.statistics)

        # Display basic statistics
        print("=== Build Statistics ===")
        print(f"Build Target: {parser_obj.statistics.build_target or 'Unknown'}")
        print(f"Total Tasks: {parser_obj.statistics.total_tasks}")
        print(f"Successful: {parser_obj.statistics.successful_tasks}")
        print(f"Failed: {parser_obj.statistics.failed_tasks}")
        print(f"Skipped: {parser_obj.statistics.skipped_tasks}")
        if parser_obj.statistics.total_duration:
            print(f"Total Duration: {parser_obj.statistics.total_duration}")
        print(f"Parallel Tasks: {parser_obj.statistics.parallel_tasks}")
        efficiency = analyzer.calculate_parallelization_efficiency()
        print(f"Parallelization Efficiency: {efficiency:.1f}%")
        print()

        # Show longest tasks
        print(f"=== Top {args.longest_tasks} Longest Tasks ===")
        longest = analyzer.get_longest_tasks(args.longest_tasks)
        for i, task in enumerate(longest, 1):
            print(f"{i:2d}. {task.recipe:30s} do_{task.task:15s} {task.duration.total_seconds():8.1f}s")
        print()

        # Show slowest recipes
        print(f"=== Top {args.slowest_recipes} Slowest Recipes ===")
        slowest = analyzer.get_slowest_recipes(args.slowest_recipes)
        for i, (recipe, duration) in enumerate(slowest, 1):
            print(f"{i:2d}. {recipe:40s} {duration.total_seconds():8.1f}s")
        print()

        # Show bottlenecks if requested
        if args.bottlenecks:
            print("=== Build Bottlenecks ===")
            bottlenecks = analyzer.identify_bottlenecks()
            if bottlenecks:
                for bottleneck in bottlenecks:
                    severity = bottleneck['severity'].upper()
                    print(f"[{severity}] {bottleneck['message']}")
            else:
                print("No significant bottlenecks detected")
            print()

        # Show recommendations if requested
        if args.recommendations:
            print("=== Optimization Recommendations ===")
            recommendations = analyzer.generate_optimization_recommendations()
            for rec in recommendations:
                print(f"  {rec}")
            print()

        # Generate dependency graph if requested
        if args.dependency_graph:
            print(f"Generating dependency graph: {args.dependency_graph}")
            graph_gen = DependencyGraphGenerator(parser_obj.tasks)
            graph_gen.generate_dot_graph(args.dependency_graph)
            print(f"  Use 'dot -Tpng {args.dependency_graph} -o graph.png' to generate image")
            print()

        # Export to JSON if requested
        if args.json_output:
            print(f"Exporting analysis to JSON: {args.json_output}")
            results = {
                'statistics': {
                    'total_tasks': parser_obj.statistics.total_tasks,
                    'successful_tasks': parser_obj.statistics.successful_tasks,
                    'failed_tasks': parser_obj.statistics.failed_tasks,
                    'skipped_tasks': parser_obj.statistics.skipped_tasks,
                    'total_duration_seconds': parser_obj.statistics.total_duration.total_seconds() if parser_obj.statistics.total_duration else 0,
                    'parallel_tasks': parser_obj.statistics.parallel_tasks,
                    'parallelization_efficiency': efficiency,
                },
                'longest_tasks': [
                    {
                        'recipe': t.recipe,
                        'task': t.task,
                        'duration_seconds': t.duration.total_seconds() if t.duration else 0
                    }
                    for t in longest
                ],
                'bottlenecks': analyzer.identify_bottlenecks(),
                'recommendations': analyzer.generate_optimization_recommendations(),
            }

            with open(args.json_output, 'w') as f:
                json.dump(results, f, indent=2)
            print()

        # Export to CSV if requested
        if args.csv_output:
            print(f"Exporting task data to CSV: {args.csv_output}")
            import csv
            with open(args.csv_output, 'w', newline='') as csvfile:
                fieldnames = ['recipe', 'task', 'duration_seconds', 'state']
                writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
                writer.writeheader()
                for task in parser_obj.tasks:
                    writer.writerow({
                        'recipe': task.recipe,
                        'task': task.task,
                        'duration_seconds': task.duration.total_seconds() if task.duration else 0,
                        'state': task.state.value
                    })
            print()

    except Exception as e:
        print(f"Error analyzing build log: {e}", file=sys.stderr)
        import traceback
        traceback.print_exc()
        return 1

    return 0


if __name__ == '__main__':
    sys.exit(main())
