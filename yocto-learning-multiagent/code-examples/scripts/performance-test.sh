#!/bin/bash
# Performance Testing Script for NVIDIA Jetson
#
# This script runs comprehensive performance tests on Jetson devices:
# - CPU benchmarks
# - GPU/CUDA performance
# - Memory bandwidth
# - TensorRT inference
# - I/O performance
# - Power consumption
#
# Usage:
#   ./performance-test.sh [options]
#
# Options:
#   -t, --tests <list>     Comma-separated list of tests to run
#   -o, --output <file>    Output file for results (default: results.txt)
#   -j, --json             Output in JSON format
#   -h, --help             Show help
#
# Examples:
#   ./performance-test.sh
#   ./performance-test.sh -t cpu,gpu,memory
#   ./performance-test.sh --json -o results.json

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
OUTPUT_FILE="performance_results.txt"
JSON_OUTPUT=0
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Test results
declare -A TEST_RESULTS

# Functions
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_test() {
    echo -e "${BLUE}[TEST]${NC} $1"
}

print_usage() {
    cat << EOF
Usage: $0 [options]

Performance testing suite for NVIDIA Jetson platforms

Options:
  -t, --tests <list>     Run specific tests (comma-separated)
  -o, --output <file>    Output file for results
  -j, --json             Output in JSON format
  -h, --help             Show this help

Available tests:
  system    - System information
  cpu       - CPU benchmarks
  gpu       - GPU/CUDA performance
  memory    - Memory bandwidth tests
  inference - TensorRT inference benchmarks
  io        - Storage I/O performance
  network   - Network throughput
  power     - Power consumption monitoring
  all       - Run all tests (default)

Examples:
  $0
  $0 -t cpu,gpu,memory
  $0 --json -o results.json
  $0 -t inference -o tensorrt_results.txt

Requirements:
  - stress-ng (CPU stress testing)
  - cuda-samples (GPU testing)
  - TensorRT (inference testing)
  - iotop, iperf3 (I/O and network)

Install dependencies:
  sudo apt-get install stress-ng iotop iperf3 sysstat

EOF
}

get_system_info() {
    log_test "Gathering system information..."

    local soc_model=$(cat /proc/device-tree/model 2>/dev/null || echo "Unknown")
    local kernel=$(uname -r)
    local l4t_version=$(cat /etc/nv_tegra_release 2>/dev/null | grep "# R" | awk '{print $2" "$3}' || echo "Unknown")

    TEST_RESULTS[soc_model]="$soc_model"
    TEST_RESULTS[kernel]="$kernel"
    TEST_RESULTS[l4t_version]="$l4t_version"
    TEST_RESULTS[cpu_model]=$(lscpu | grep "Model name" | cut -d: -f2 | xargs)
    TEST_RESULTS[cpu_cores]=$(nproc)
    TEST_RESULTS[total_memory]=$(free -h | grep Mem | awk '{print $2}')

    log_info "SoC: $soc_model"
    log_info "Kernel: $kernel"
    log_info "L4T Version: $l4t_version"
}

test_cpu() {
    log_test "Running CPU benchmarks..."

    # Check if stress-ng is available
    if ! command -v stress-ng &> /dev/null; then
        log_warn "stress-ng not found, skipping CPU test"
        log_info "Install with: sudo apt-get install stress-ng"
        return 1
    fi

    # CPU stress test
    log_info "Running CPU stress test (30 seconds)..."
    local stress_result=$(stress-ng --cpu $(nproc) --metrics --timeout 30s 2>&1 | grep "bogo ops" | tail -n 1)

    TEST_RESULTS[cpu_stress]="$stress_result"

    # Single-thread performance
    log_info "Testing single-thread performance..."
    local single_thread=$(dd if=/dev/zero of=/dev/null bs=1M count=10000 2>&1 | grep copied | awk '{print $10 " " $11}')

    TEST_RESULTS[single_thread]="$single_thread"

    log_info "CPU test completed"
}

test_gpu() {
    log_test "Running GPU/CUDA benchmarks..."

    # Check CUDA availability
    if ! command -v nvidia-smi &> /dev/null; then
        log_warn "nvidia-smi not found, skipping GPU test"
        return 1
    fi

    # Get GPU info
    local gpu_info=$(nvidia-smi --query-gpu=name,driver_version,memory.total --format=csv,noheader 2>/dev/null || echo "Unknown")

    TEST_RESULTS[gpu_info]="$gpu_info"

    # Check for CUDA samples
    local cuda_sample_dir="/usr/local/cuda/samples/1_Utilities/deviceQuery"
    if [ -d "$cuda_sample_dir" ]; then
        log_info "Running CUDA deviceQuery..."
        cd $cuda_sample_dir
        if [ ! -f "deviceQuery" ]; then
            make > /dev/null 2>&1 || true
        fi

        if [ -f "deviceQuery" ]; then
            local cuda_info=$(./deviceQuery | grep -A 5 "Device 0:")
            TEST_RESULTS[cuda_info]="$cuda_info"
        fi
    fi

    # CUDA bandwidth test
    local bandwidth_dir="/usr/local/cuda/samples/1_Utilities/bandwidthTest"
    if [ -d "$bandwidth_dir" ]; then
        log_info "Running CUDA bandwidth test..."
        cd $bandwidth_dir
        if [ ! -f "bandwidthTest" ]; then
            make > /dev/null 2>&1 || true
        fi

        if [ -f "bandwidthTest" ]; then
            local bandwidth=$(./bandwidthTest | grep "Device to Host" | tail -n 1)
            TEST_RESULTS[gpu_bandwidth]="$bandwidth"
        fi
    fi

    log_info "GPU test completed"
}

test_memory() {
    log_test "Running memory benchmarks..."

    # Memory bandwidth test
    log_info "Testing memory bandwidth..."
    local mem_bandwidth=$(dd if=/dev/zero of=/dev/null bs=1M count=5000 2>&1 | grep copied | awk '{print $10 " " $11}')

    TEST_RESULTS[memory_bandwidth]="$mem_bandwidth"

    # Memory latency test (simplified)
    log_info "Testing memory access..."
    local start_time=$(date +%s%N)
    dd if=/dev/zero of=/tmp/test_mem bs=1M count=100 > /dev/null 2>&1
    dd if=/tmp/test_mem of=/dev/null bs=1M > /dev/null 2>&1
    rm -f /tmp/test_mem
    local end_time=$(date +%s%N)
    local duration=$(( (end_time - start_time) / 1000000 ))

    TEST_RESULTS[memory_access_time]="${duration}ms"

    log_info "Memory test completed"
}

test_inference() {
    log_test "Running TensorRT inference benchmark..."

    # Check for TensorRT
    if ! command -v trtexec &> /dev/null; then
        log_warn "trtexec not found, skipping inference test"
        log_info "Install TensorRT: sudo apt-get install tensorrt"
        return 1
    fi

    # Download sample model if not exists
    local model_path="/tmp/resnet50.onnx"
    if [ ! -f "$model_path" ]; then
        log_info "Sample model not found, inference test requires a model"
        log_warn "Place an ONNX model at $model_path to run this test"
        return 1
    fi

    log_info "Running inference benchmark (this may take a while)..."

    # Run TensorRT inference benchmark
    local inference_result=$(trtexec --onnx=$model_path --fp16 --iterations=100 2>&1 | grep "mean.*ms")

    TEST_RESULTS[inference_fp16]="$inference_result"

    log_info "Inference test completed"
}

test_io() {
    log_test "Running I/O performance tests..."

    # Create test file
    local test_file="/tmp/io_test_$$"
    local test_size=1000  # MB

    # Sequential write
    log_info "Testing sequential write performance..."
    local write_speed=$(dd if=/dev/zero of=$test_file bs=1M count=$test_size 2>&1 | grep copied | awk '{print $10 " " $11}')

    TEST_RESULTS[io_write_seq]="$write_speed"

    # Sequential read
    log_info "Testing sequential read performance..."
    sync && echo 3 | sudo tee /proc/sys/vm/drop_caches > /dev/null 2>&1 || true
    local read_speed=$(dd if=$test_file of=/dev/null bs=1M 2>&1 | grep copied | awk '{print $10 " " $11}')

    TEST_RESULTS[io_read_seq]="$read_speed"

    # Cleanup
    rm -f $test_file

    # IOPS test (if fio is available)
    if command -v fio &> /dev/null; then
        log_info "Running random I/O test..."
        local iops=$(fio --name=randread --ioengine=libaio --rw=randread --bs=4k --direct=1 --size=100M --numjobs=1 --runtime=10 --group_reporting 2>&1 | grep "iops" | head -n 1)
        TEST_RESULTS[io_random_iops]="$iops"
    fi

    log_info "I/O test completed"
}

test_network() {
    log_test "Running network performance tests..."

    # Get network interface
    local iface=$(ip route | grep default | awk '{print $5}' | head -n 1)

    if [ -z "$iface" ]; then
        log_warn "No default network interface found"
        return 1
    fi

    log_info "Testing on interface: $iface"

    # Network throughput (requires iperf3 server)
    if command -v iperf3 &> /dev/null; then
        log_info "Note: iperf3 test requires a server. Skipping automated test."
        log_info "To test manually: iperf3 -c <server_ip>"
    fi

    # Network stats
    local rx_bytes=$(cat /sys/class/net/$iface/statistics/rx_bytes)
    local tx_bytes=$(cat /sys/class/net/$iface/statistics/tx_bytes)

    TEST_RESULTS[network_interface]="$iface"
    TEST_RESULTS[network_rx]=$(( rx_bytes / 1024 / 1024 ))" MB"
    TEST_RESULTS[network_tx]=$(( tx_bytes / 1024 / 1024 ))" MB"

    log_info "Network test completed"
}

test_power() {
    log_test "Monitoring power consumption..."

    # Check for power monitoring tools
    local power_info=""

    # Try tegrastats (NVIDIA tool)
    if command -v tegrastats &> /dev/null; then
        log_info "Sampling power with tegrastats (10 seconds)..."
        timeout 10 tegrastats --interval 1000 > /tmp/tegrastats.log 2>&1 || true

        if [ -f /tmp/tegrastats.log ]; then
            local avg_power=$(grep "POM" /tmp/tegrastats.log | awk '{print $8}' | sed 's/mW//' | awk '{sum+=$1; count++} END {print sum/count}' 2>/dev/null || echo "N/A")
            TEST_RESULTS[power_avg]="${avg_power}mW"
            rm -f /tmp/tegrastats.log
        fi
    else
        log_warn "tegrastats not available"
    fi

    # Check temperature
    if [ -f /sys/devices/virtual/thermal/thermal_zone0/temp ]; then
        local temp=$(cat /sys/devices/virtual/thermal/thermal_zone0/temp)
        temp=$(( temp / 1000 ))
        TEST_RESULTS[temperature]="${temp}°C"
    fi

    log_info "Power monitoring completed"
}

save_results() {
    local output_file=$1

    log_info "Saving results to: $output_file"

    if [ $JSON_OUTPUT -eq 1 ]; then
        # JSON output
        echo "{" > $output_file
        echo "  \"timestamp\": \"$(date -Iseconds)\"," >> $output_file
        echo "  \"results\": {" >> $output_file

        local first=1
        for key in "${!TEST_RESULTS[@]}"; do
            if [ $first -eq 0 ]; then
                echo "," >> $output_file
            fi
            echo -n "    \"$key\": \"${TEST_RESULTS[$key]}\"" >> $output_file
            first=0
        done

        echo >> $output_file
        echo "  }" >> $output_file
        echo "}" >> $output_file
    else
        # Text output
        {
            echo "=================================================="
            echo "NVIDIA Jetson Performance Test Results"
            echo "=================================================="
            echo "Timestamp: $(date)"
            echo "=================================================="
            echo

            for key in "${!TEST_RESULTS[@]}"; do
                printf "%-20s: %s\n" "$key" "${TEST_RESULTS[$key]}"
            done

            echo
            echo "=================================================="
        } > $output_file
    fi

    log_info "Results saved successfully"
    cat $output_file
}

# Main script
main() {
    local tests_to_run="all"

    # Parse arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -t|--tests)
                tests_to_run="$2"
                shift 2
                ;;
            -o|--output)
                OUTPUT_FILE="$2"
                shift 2
                ;;
            -j|--json)
                JSON_OUTPUT=1
                shift
                ;;
            -h|--help)
                print_usage
                exit 0
                ;;
            *)
                log_error "Unknown option: $1"
                print_usage
                exit 1
                ;;
        esac
    done

    log_info "==================================================================="
    log_info "NVIDIA Jetson Performance Testing Suite"
    log_info "==================================================================="
    log_info "Output file: $OUTPUT_FILE"
    log_info "Tests to run: $tests_to_run"
    log_info "==================================================================="
    echo

    # Get system info first
    get_system_info

    # Run requested tests
    if [ "$tests_to_run" == "all" ]; then
        test_cpu
        test_gpu
        test_memory
        test_inference
        test_io
        test_network
        test_power
    else
        IFS=',' read -ra TESTS <<< "$tests_to_run"
        for test in "${TESTS[@]}"; do
            case $test in
                system)   get_system_info ;;
                cpu)      test_cpu ;;
                gpu)      test_gpu ;;
                memory)   test_memory ;;
                inference) test_inference ;;
                io)       test_io ;;
                network)  test_network ;;
                power)    test_power ;;
                *)
                    log_error "Unknown test: $test"
                    ;;
            esac
        done
    fi

    # Save results
    echo
    save_results "$OUTPUT_FILE"

    log_info "All tests completed!"
}

# Run main function
main "$@"
