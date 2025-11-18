# Advanced Performance Optimization for Yocto & Meta-Tegra

## Overview

This module covers advanced performance optimization techniques for Yocto-based Jetson systems, focusing on build time reduction, runtime performance tuning, memory optimization, power management, and GPU/CUDA optimization.

**Target Audience**: Experienced developers with production deployment needs
**Prerequisites**: Deep understanding of Yocto build system, Linux kernel, and Jetson architecture

---

## 1. Build Time Optimization

### 1.1 Shared State Cache (sstate-cache) Architecture

The sstate-cache is critical for build performance. Understanding its internals enables advanced optimization.

#### Advanced sstate Configuration

```python
# conf/local.conf - Enterprise Build Server Setup

# Multi-tier sstate architecture
SSTATE_MIRRORS = "\
    file://.* http://sstate-primary.internal/sstate/PATH;downloadfilename=PATH \n \
    file://.* http://sstate-mirror.internal/sstate/PATH;downloadfilename=PATH \n \
    file://.* file:///local-sstate/PATH \n \
"

# sstate signature optimization
BB_SIGNATURE_HANDLER = "OEEquivHash"
BB_HASHSERVE = "auto"
BB_HASHSERVE_UPSTREAM = "hashserv.internal:8686"

# Parallel sstate operations
BB_NUMBER_THREADS = "16"
PARALLEL_MAKE = "-j 32"

# sstate archiving for CI/CD
SSTATE_DUPWHITELIST += "\
    ${TMPDIR} \
    ${DL_DIR} \
    ${SSTATE_DIR} \
"
```

#### Case Study: Build Time Reduction

**Problem**: 4-hour full builds blocking development
**Solution**: Implemented multi-tier sstate + hash equivalence server

**Benchmark Results**:
```
Full Build (no cache):    4h 23m
Full Build (with sstate):    47m
Incremental Build:           8m
Single Recipe (cached):      45s
```

**Implementation**:
```bash
#!/bin/bash
# setup-hashserve.sh - Hash Equivalence Server Setup

# Start hash server with persistent database
bitbake-hashserve --bind :8686 --database /var/hashserv/hashserv.db &

# Monitor hash server performance
watch -n 1 'sqlite3 /var/hashserv/hashserv.db "SELECT COUNT(*) FROM unihashes"'
```

### 1.2 Dependency Graph Optimization

Analyze and optimize task dependencies to enable better parallelization.

```bash
# Generate dependency graph
bitbake -g core-image-minimal
cat task-depends.dot | grep -v "do_package" > optimized.dot

# Identify critical path
python3 << 'EOF'
import networkx as nx
import pydot

# Parse dependency graph
graphs = pydot.graph_from_dot_file('task-depends.dot')
G = nx.DiGraph(nx.drawing.nx_pydot.from_pydot(graphs[0]))

# Find critical path (longest path)
critical = nx.dag_longest_path(G)
print("Critical Path:", " -> ".join(critical[:10]))
print(f"Length: {len(critical)} tasks")

# Identify parallelization opportunities
levels = list(nx.topological_generations(G))
print(f"Max parallelization: {max(len(level) for level in levels)} tasks")
EOF
```

### 1.3 Advanced Download Management

```python
# conf/local.conf - Download Optimization

# Premirror for frequently used sources
PREMIRRORS:prepend = "\
    git://.*/.* http://mirrors.internal/git/MIRRORNAME \n \
    ftp://.*/.* http://mirrors.internal/sources/ \n \
    http://.*/.* http://mirrors.internal/sources/ \n \
    https://.*/.* http://mirrors.internal/sources/ \n \
"

# Download performance tuning
BB_FETCH_PREMIRRORONLY = "0"
BB_GENERATE_MIRROR_TARBALLS = "1"
BB_NO_NETWORK = "0"

# Concurrent downloads
BB_NUMBER_DOWNLOAD_THREADS = "8"
FETCHCMD_wget = "/usr/bin/env wget -t 2 -T 30 --passive-ftp --no-check-certificate"
```

---

## 2. Runtime Performance Tuning

### 2.1 Jetson Platform Specific Optimizations

#### NVPModel Configuration

```bash
# Create custom power mode
cat > /etc/nvpmodel.conf.custom << 'EOF'
# Custom MAXN mode with optimized clocks
< POWER_MODEL ID=0 NAME=CUSTOM_MAXN >
CPU_ONLINE CORE_0 1
CPU_ONLINE CORE_1 1
CPU_ONLINE CORE_2 1
CPU_ONLINE CORE_3 1
CPU_ONLINE CORE_4 1
CPU_ONLINE CORE_5 1
CPU_ONLINE CORE_6 1
CPU_ONLINE CORE_7 1
TPC_POWER_GATING TPC_PG_MASK 0
GPU_POWER_CONTROL_ENABLE GPU_PWR_CNTL_EN on
CPU_DENVER_0 MIN_FREQ 1190400
CPU_DENVER_0 MAX_FREQ 2035200
CPU_DENVER_1 MIN_FREQ 1190400
CPU_DENVER_1 MAX_FREQ 2035200
GPU MIN_FREQ 1110000000
GPU MAX_FREQ 1377000000
EMC MAX_FREQ 2133000000
EOF

# Apply custom mode
nvpmodel -m 0 -f /etc/nvpmodel.conf.custom
```

#### Jetson Clocks Optimization

```bash
# Advanced jetson_clocks script
cat > /usr/bin/jetson_clocks_optimized << 'EOF'
#!/bin/bash

# Enable all CPU cores
for cpu in /sys/devices/system/cpu/cpu[0-9]*; do
    echo 1 > $cpu/online 2>/dev/null
done

# Set CPU governor to performance
for gov in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do
    echo performance > $gov 2>/dev/null
done

# Lock GPU frequency
echo 1377000000 > /sys/devices/gpu.0/devfreq/17000000.gv11b/min_freq
echo 1377000000 > /sys/devices/gpu.0/devfreq/17000000.gv11b/max_freq

# Lock EMC (memory) frequency
echo 2133000000 > /sys/kernel/nvpmodel_emc_cap/emc_iso_cap

# Disable CPU idle states for lowest latency
for idle in /sys/devices/system/cpu/cpu*/cpuidle/state*/disable; do
    echo 1 > $idle 2>/dev/null
done

# Fan to maximum
echo 255 > /sys/devices/pwm-fan/target_pwm
EOF

chmod +x /usr/bin/jetson_clocks_optimized
```

### 2.2 Kernel Tuning

#### Advanced Scheduler Configuration

```python
# recipes-kernel/linux/linux-tegra_%.bbappend

# Enable kernel configuration for performance
SRC_URI += "file://performance.cfg"

# performance.cfg content
"""
CONFIG_PREEMPT_VOLUNTARY=y
CONFIG_NO_HZ_FULL=y
CONFIG_RCU_NOCB_CPU=y
CONFIG_HIGH_RES_TIMERS=y
CONFIG_IRQ_TIME_ACCOUNTING=y

# CPU frequency governors
CONFIG_CPU_FREQ_GOV_PERFORMANCE=y
CONFIG_CPU_FREQ_GOV_ONDEMAND=y
CONFIG_CPU_FREQ_GOV_SCHEDUTIL=y

# I/O schedulers
CONFIG_MQ_IOSCHED_DEADLINE=y
CONFIG_MQ_IOSCHED_KYBER=y
CONFIG_IOSCHED_BFQ=y

# Network performance
CONFIG_TCP_CONG_BBR=y
CONFIG_NET_SCH_FQ=y
"""
```

#### Boot-time Optimizations

```bash
# systemd service for performance tuning
cat > /etc/systemd/system/performance-tuning.service << 'EOF'
[Unit]
Description=Runtime Performance Tuning
After=multi-user.target

[Service]
Type=oneshot
ExecStart=/usr/bin/performance-tune.sh
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
EOF

# Performance tuning script
cat > /usr/bin/performance-tune.sh << 'EOF'
#!/bin/bash

# Virtual memory tuning
sysctl -w vm.swappiness=10
sysctl -w vm.dirty_ratio=15
sysctl -w vm.dirty_background_ratio=5
sysctl -w vm.vfs_cache_pressure=50

# Network stack tuning
sysctl -w net.core.rmem_max=134217728
sysctl -w net.core.wmem_max=134217728
sysctl -w net.ipv4.tcp_rmem="4096 87380 67108864"
sysctl -w net.ipv4.tcp_wmem="4096 65536 67108864"
sysctl -w net.core.netdev_max_backlog=5000
sysctl -w net.ipv4.tcp_congestion_control=bbr

# Disable transparent huge pages for deterministic performance
echo never > /sys/kernel/mm/transparent_hugepage/enabled
echo never > /sys/kernel/mm/transparent_hugepage/defrag

# I/O scheduler optimization for NVMe
for queue in /sys/block/nvme*/queue/scheduler; do
    echo none > $queue  # Use no scheduler for NVMe (already optimized)
done

# Set deadline scheduler for SD/MMC
for queue in /sys/block/mmcblk*/queue/scheduler; do
    echo deadline > $queue
done
EOF

chmod +x /usr/bin/performance-tune.sh
systemctl enable performance-tuning.service
```

### 2.3 Performance Monitoring

```python
# recipes-support/perf-monitor/perf-monitor_1.0.bb

SUMMARY = "Real-time performance monitoring for Jetson"
LICENSE = "MIT"

SRC_URI = "file://perf-monitor.py"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 perf-monitor.py ${D}${bindir}/perf-monitor
}

FILES:${PN} = "${bindir}/perf-monitor"
```

```python
# files/perf-monitor.py
#!/usr/bin/env python3

import time
import sys
from collections import defaultdict

class JetsonPerfMonitor:
    def __init__(self):
        self.metrics = defaultdict(list)

    def read_cpu_freq(self):
        """Read current CPU frequencies"""
        freqs = []
        for i in range(8):
            try:
                with open(f'/sys/devices/system/cpu/cpu{i}/cpufreq/scaling_cur_freq') as f:
                    freqs.append(int(f.read()) / 1000)  # MHz
            except:
                pass
        return freqs

    def read_gpu_freq(self):
        """Read current GPU frequency"""
        try:
            with open('/sys/devices/gpu.0/devfreq/17000000.gv11b/cur_freq') as f:
                return int(f.read()) / 1000000  # MHz
        except:
            return 0

    def read_emc_freq(self):
        """Read current EMC (memory) frequency"""
        try:
            with open('/sys/kernel/actmon_avg_activity/emc_freq') as f:
                return int(f.read()) / 1000  # MHz
        except:
            return 0

    def read_power(self):
        """Read power consumption"""
        try:
            with open('/sys/bus/i2c/drivers/ina3221x/1-0040/iio:device0/in_power0_input') as f:
                return int(f.read())  # mW
        except:
            return 0

    def read_temps(self):
        """Read thermal zones"""
        temps = {}
        zones = {
            'CPU': '/sys/devices/virtual/thermal/thermal_zone0/temp',
            'GPU': '/sys/devices/virtual/thermal/thermal_zone1/temp',
            'AUX': '/sys/devices/virtual/thermal/thermal_zone2/temp',
        }
        for name, path in zones.items():
            try:
                with open(path) as f:
                    temps[name] = float(f.read()) / 1000  # Celsius
            except:
                temps[name] = 0
        return temps

    def monitor(self, duration=60, interval=1):
        """Monitor performance metrics"""
        print("Jetson Performance Monitor")
        print("=" * 80)
        print(f"Duration: {duration}s, Interval: {interval}s")
        print("-" * 80)

        for i in range(duration):
            cpu_freqs = self.read_cpu_freq()
            gpu_freq = self.read_gpu_freq()
            emc_freq = self.read_emc_freq()
            power = self.read_power()
            temps = self.read_temps()

            # Store metrics
            self.metrics['cpu_avg'].append(sum(cpu_freqs) / len(cpu_freqs) if cpu_freqs else 0)
            self.metrics['gpu'].append(gpu_freq)
            self.metrics['emc'].append(emc_freq)
            self.metrics['power'].append(power)
            self.metrics['temp_cpu'].append(temps['CPU'])
            self.metrics['temp_gpu'].append(temps['GPU'])

            # Display current values
            print(f"\r[{i+1:3d}/{duration}] "
                  f"CPU: {sum(cpu_freqs)/len(cpu_freqs) if cpu_freqs else 0:6.0f} MHz | "
                  f"GPU: {gpu_freq:4.0f} MHz | "
                  f"EMC: {emc_freq:4.0f} MHz | "
                  f"PWR: {power:5d} mW | "
                  f"TEMP: CPU {temps['CPU']:4.1f}°C GPU {temps['GPU']:4.1f}°C",
                  end='', flush=True)

            time.sleep(interval)

        print("\n" + "-" * 80)
        self.print_summary()

    def print_summary(self):
        """Print performance summary"""
        print("\nPerformance Summary:")
        print(f"  CPU Avg: {sum(self.metrics['cpu_avg'])/len(self.metrics['cpu_avg']):.0f} MHz "
              f"(min: {min(self.metrics['cpu_avg']):.0f}, max: {max(self.metrics['cpu_avg']):.0f})")
        print(f"  GPU Avg: {sum(self.metrics['gpu'])/len(self.metrics['gpu']):.0f} MHz "
              f"(min: {min(self.metrics['gpu']):.0f}, max: {max(self.metrics['gpu']):.0f})")
        print(f"  EMC Avg: {sum(self.metrics['emc'])/len(self.metrics['emc']):.0f} MHz")
        print(f"  Power Avg: {sum(self.metrics['power'])/len(self.metrics['power']):.0f} mW "
              f"(min: {min(self.metrics['power']):.0f}, max: {max(self.metrics['power']):.0f})")
        print(f"  Temp CPU: {sum(self.metrics['temp_cpu'])/len(self.metrics['temp_cpu']):.1f}°C "
              f"(max: {max(self.metrics['temp_cpu']):.1f}°C)")
        print(f"  Temp GPU: {sum(self.metrics['temp_gpu'])/len(self.metrics['temp_gpu']):.1f}°C "
              f"(max: {max(self.metrics['temp_gpu']):.1f}°C)")

if __name__ == '__main__':
    monitor = JetsonPerfMonitor()
    try:
        monitor.monitor(duration=60, interval=1)
    except KeyboardInterrupt:
        print("\n\nInterrupted by user")
        monitor.print_summary()
```

---

## 3. Memory Optimization

### 3.1 Unified Memory Architecture

Jetson devices use unified memory architecture (UMA). Optimize for zero-copy operations.

```c
// Example: Zero-copy buffer sharing between CPU and GPU
#include <cuda_runtime.h>
#include <stdio.h>

// Allocate unified memory
void* allocate_unified_memory(size_t size) {
    void* ptr;
    cudaError_t err = cudaMallocManaged(&ptr, size, cudaMemAttachGlobal);
    if (err != cudaSuccess) {
        fprintf(stderr, "cudaMallocManaged failed: %s\n", cudaGetErrorString(err));
        return NULL;
    }

    // Prefetch to device for better performance
    int device;
    cudaGetDevice(&device);
    cudaMemPrefetchAsync(ptr, size, device, NULL);

    return ptr;
}

// Optimize memory access patterns
void optimize_unified_memory_access(void* ptr, size_t size) {
    // Set preferred location
    int device;
    cudaGetDevice(&device);
    cudaMemAdvise(ptr, size, cudaMemAdviseSetPreferredLocation, device);

    // Set read-mostly for CPU access patterns
    cudaMemAdvise(ptr, size, cudaMemAdviseSetReadMostly, device);

    // Set accessed-by for explicit control
    cudaMemAdvise(ptr, size, cudaMemAdviseSetAccessedBy, cudaCpuDeviceId);
}
```

### 3.2 Memory Profiling

```python
# Recipe for memory profiling tools
# recipes-devtools/memory-profiler/memory-profiler_1.0.bb

SUMMARY = "Memory profiling and optimization tools"
LICENSE = "MIT"

SRC_URI = "file://memory-profile.sh"

S = "${WORKDIR}"

RDEPENDS:${PN} = "procps valgrind"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 memory-profile.sh ${D}${bindir}/memory-profile
}
```

```bash
# files/memory-profile.sh
#!/bin/bash

echo "=== System Memory Information ==="
cat /proc/meminfo | grep -E "MemTotal|MemFree|MemAvailable|Cached|Buffers|SwapTotal|SwapFree"

echo -e "\n=== Per-Process Memory Usage (Top 10) ==="
ps aux | sort -nrk 4 | head -10 | awk '{printf "%-20s %8s %8s\n", $11, $4"%", $6/1024"MB"}'

echo -e "\n=== CMA (Contiguous Memory Allocator) Status ==="
cat /proc/meminfo | grep -i cma

echo -e "\n=== ION Memory Allocations ==="
if [ -d /sys/kernel/debug/ion ]; then
    for heap in /sys/kernel/debug/ion/heaps/*; do
        echo "Heap: $(basename $heap)"
        cat $heap 2>/dev/null | head -20
    done
fi

echo -e "\n=== GPU Memory Usage ==="
tegrastats --interval 1000 --logfile /tmp/tegrastats.log &
TEGRASTATS_PID=$!
sleep 2
kill $TEGRASTATS_PID
grep -o 'GR3D_FREQ [^%]*' /tmp/tegrastats.log | tail -1
rm -f /tmp/tegrastats.log

echo -e "\n=== Memory Fragmentation ==="
cat /proc/buddyinfo

echo -e "\n=== SLAB Cache Usage ==="
cat /proc/slabinfo | awk 'NR==1 || $3>1000' | head -20
```

### 3.3 Memory Allocation Optimization

```python
# conf/local.conf - Memory optimization settings

# Reduce memory usage during build
BB_DEFAULT_TASK_MAXMEM = "8192"
BB_SCHEDULER = "completion"

# Target image memory optimizations
EXTRA_IMAGE_FEATURES:remove = "debug-tweaks"
IMAGE_FEATURES:append = " read-only-rootfs"

# Package management optimization
PACKAGE_CLASSES = "package_ipk"
BAD_RECOMMENDATIONS = "busybox-syslog"
```

```python
# recipes-core/base-files/base-files_%.bbappend

# Optimize memory allocation at runtime
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://99-memory-optimization.conf"

do_install:append() {
    install -d ${D}${sysconfdir}/sysctl.d
    install -m 0644 ${WORKDIR}/99-memory-optimization.conf ${D}${sysconfdir}/sysctl.d/
}
```

```bash
# files/99-memory-optimization.conf
# Memory management tuning for Jetson

# Overcommit handling
vm.overcommit_memory = 1
vm.overcommit_ratio = 80

# Cache pressure (lower = keep cache longer)
vm.vfs_cache_pressure = 50

# Swappiness (lower = avoid swap)
vm.swappiness = 10

# Dirty page writeback
vm.dirty_ratio = 15
vm.dirty_background_ratio = 5
vm.dirty_expire_centisecs = 3000
vm.dirty_writeback_centisecs = 500

# Minimum free memory (adjust for 8GB system)
vm.min_free_kbytes = 131072

# Zone reclaim
vm.zone_reclaim_mode = 0

# Compact memory
vm.compact_memory = 1
```

---

## 4. Power Management

### 4.1 Dynamic Power Management

```c
// CUDA power management API usage
#include <cuda_runtime.h>

void configure_gpu_power_management() {
    // Set compute mode to exclusive process
    cudaSetDeviceFlags(cudaDeviceScheduleBlockingSync);

    // Configure GPU persistence mode (requires nvidia-smi)
    system("nvidia-smi -pm 1");

    // Set power limit (in milliwatts)
    // AGX Xavier: 10W to 30W modes
    system("nvidia-smi -pl 15000");  // 15W limit
}

// Dynamic frequency scaling based on workload
void adaptive_frequency_scaling(int workload_intensity) {
    if (workload_intensity > 80) {
        // High intensity: max performance
        system("nvpmodel -m 0");
        system("jetson_clocks");
    } else if (workload_intensity > 50) {
        // Medium intensity: balanced
        system("nvpmodel -m 2");
    } else {
        // Low intensity: power save
        system("nvpmodel -m 6");
    }
}
```

### 4.2 Application-Level Power Optimization

```python
# recipes-applications/power-aware-app/power-aware-app_1.0.bb

SUMMARY = "Power-aware application framework"
LICENSE = "MIT"

SRC_URI = "file://power_manager.py"

S = "${WORKDIR}"

RDEPENDS:${PN} = "python3-core"

do_install() {
    install -d ${D}${libdir}/python3/dist-packages
    install -m 0644 power_manager.py ${D}${libdir}/python3/dist-packages/
}
```

```python
# files/power_manager.py
import os
import time
from contextlib import contextmanager

class PowerManager:
    """Application-level power management for Jetson"""

    POWER_MODES = {
        'MAXN': 0,
        'MODE_15W': 1,
        'MODE_30W': 2,
    }

    def __init__(self):
        self.current_mode = self.get_current_mode()
        self.saved_mode = None

    def get_current_mode(self):
        """Get current power mode"""
        try:
            with open('/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq') as f:
                freq = int(f.read())
            # Infer mode from CPU frequency
            if freq > 1900000:
                return 'MAXN'
            elif freq > 1400000:
                return 'MODE_30W'
            else:
                return 'MODE_15W'
        except:
            return 'UNKNOWN'

    def set_power_mode(self, mode):
        """Set power mode"""
        if mode not in self.POWER_MODES:
            raise ValueError(f"Invalid power mode: {mode}")

        mode_id = self.POWER_MODES[mode]
        os.system(f'nvpmodel -m {mode_id}')
        self.current_mode = mode

    @contextmanager
    def high_performance_context(self):
        """Context manager for high-performance operations"""
        self.saved_mode = self.current_mode
        self.set_power_mode('MAXN')
        os.system('jetson_clocks')

        try:
            yield
        finally:
            # Restore previous mode
            if self.saved_mode:
                self.set_power_mode(self.saved_mode)

    def measure_power_consumption(self, duration=10):
        """Measure average power consumption"""
        samples = []
        for _ in range(duration):
            try:
                with open('/sys/bus/i2c/drivers/ina3221x/1-0040/iio:device0/in_power0_input') as f:
                    samples.append(int(f.read()))
            except:
                pass
            time.sleep(1)

        if samples:
            return {
                'average_mw': sum(samples) / len(samples),
                'min_mw': min(samples),
                'max_mw': max(samples),
                'samples': len(samples)
            }
        return None

# Usage example
if __name__ == '__main__':
    pm = PowerManager()

    print(f"Current mode: {pm.current_mode}")

    # Use high performance for critical section
    with pm.high_performance_context():
        print("Running high-performance workload...")
        # Your intensive computation here
        time.sleep(5)

    # Measure power after returning to normal mode
    power = pm.measure_power_consumption(duration=5)
    if power:
        print(f"Power consumption: {power['average_mw']:.0f} mW (avg)")
```

---

## 5. GPU/CUDA Optimization

### 5.1 CUDA Stream Optimization

```c
// Advanced CUDA stream usage for maximum throughput
#include <cuda_runtime.h>
#include <stdio.h>

#define NUM_STREAMS 4
#define CHUNK_SIZE (1024 * 1024)

typedef struct {
    cudaStream_t stream;
    void* d_data;
    void* h_data;
    cudaEvent_t start;
    cudaEvent_t stop;
} StreamContext;

// Initialize multi-stream context
int init_stream_context(StreamContext* contexts, int num_streams) {
    for (int i = 0; i < num_streams; i++) {
        // Create stream
        cudaStreamCreate(&contexts[i].stream);

        // Allocate pinned host memory for async transfers
        cudaMallocHost(&contexts[i].h_data, CHUNK_SIZE);

        // Allocate device memory
        cudaMalloc(&contexts[i].d_data, CHUNK_SIZE);

        // Create events for timing
        cudaEventCreate(&contexts[i].start);
        cudaEventCreate(&contexts[i].stop);
    }
    return 0;
}

// Async pipeline: transfer + compute + transfer back
__global__ void process_kernel(float* data, int size) {
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx < size) {
        // Example computation
        data[idx] = data[idx] * 2.0f + 1.0f;
    }
}

void async_pipeline(StreamContext* contexts, int num_streams,
                    float* input_data, int total_size) {
    int chunk_elements = CHUNK_SIZE / sizeof(float);
    int num_chunks = (total_size + chunk_elements - 1) / chunk_elements;

    // Launch async operations on multiple streams
    for (int chunk = 0; chunk < num_chunks; chunk++) {
        int stream_id = chunk % num_streams;
        StreamContext* ctx = &contexts[stream_id];

        int offset = chunk * chunk_elements;
        int size = (offset + chunk_elements > total_size) ?
                   (total_size - offset) : chunk_elements;

        // Record start event
        cudaEventRecord(ctx->start, ctx->stream);

        // Async H2D transfer
        cudaMemcpyAsync(ctx->d_data, &input_data[offset],
                       size * sizeof(float),
                       cudaMemcpyHostToDevice, ctx->stream);

        // Launch kernel
        int block_size = 256;
        int grid_size = (size + block_size - 1) / block_size;
        process_kernel<<<grid_size, block_size, 0, ctx->stream>>>(
            (float*)ctx->d_data, size);

        // Async D2H transfer
        cudaMemcpyAsync(&input_data[offset], ctx->d_data,
                       size * sizeof(float),
                       cudaMemcpyDeviceToHost, ctx->stream);

        // Record stop event
        cudaEventRecord(ctx->stop, ctx->stream);
    }

    // Wait for all streams to complete
    for (int i = 0; i < num_streams; i++) {
        cudaStreamSynchronize(contexts[i].stream);

        // Get elapsed time
        float milliseconds = 0;
        cudaEventElapsedTime(&milliseconds, contexts[i].start, contexts[i].stop);
        printf("Stream %d elapsed: %.3f ms\n", i, milliseconds);
    }
}

// Cleanup
void cleanup_stream_context(StreamContext* contexts, int num_streams) {
    for (int i = 0; i < num_streams; i++) {
        cudaStreamDestroy(contexts[i].stream);
        cudaFreeHost(contexts[i].h_data);
        cudaFree(contexts[i].d_data);
        cudaEventDestroy(contexts[i].start);
        cudaEventDestroy(contexts[i].stop);
    }
}
```

### 5.2 Cooperative Groups for Advanced Synchronization

```c
#include <cooperative_groups.h>

namespace cg = cooperative_groups;

// Advanced kernel using cooperative groups
__global__ void advanced_reduction(float* input, float* output, int n) {
    // Get thread block group
    cg::thread_block block = cg::this_thread_block();
    cg::thread_block_tile<32> warp = cg::tiled_partition<32>(block);

    extern __shared__ float sdata[];

    int tid = threadIdx.x;
    int idx = blockIdx.x * blockDim.x + threadIdx.x;

    // Load data
    float val = (idx < n) ? input[idx] : 0.0f;

    // Warp-level reduction using cooperative groups
    for (int offset = warp.size() / 2; offset > 0; offset /= 2) {
        val += warp.shfl_down(val, offset);
    }

    // First thread in each warp writes to shared memory
    if (warp.thread_rank() == 0) {
        sdata[tid / 32] = val;
    }

    block.sync();

    // Final reduction in shared memory
    if (tid < 32) {
        val = (tid < blockDim.x / 32) ? sdata[tid] : 0.0f;

        cg::thread_block_tile<32> final_warp = cg::tiled_partition<32>(block);
        for (int offset = final_warp.size() / 2; offset > 0; offset /= 2) {
            val += final_warp.shfl_down(val, offset);
        }

        if (tid == 0) {
            output[blockIdx.x] = val;
        }
    }
}
```

### 5.3 Tensor Core Optimization (Xavier+ only)

```c
#include <mma.h>
using namespace nvcuda;

// Tensor Core matrix multiplication
__global__ void tensor_core_gemm(const half* A, const half* B, float* C,
                                 int M, int N, int K) {
    // Declare fragments
    wmma::fragment<wmma::matrix_a, 16, 16, 16, half, wmma::row_major> a_frag;
    wmma::fragment<wmma::matrix_b, 16, 16, 16, half, wmma::col_major> b_frag;
    wmma::fragment<wmma::accumulator, 16, 16, 16, float> c_frag;

    // Initialize accumulator to zero
    wmma::fill_fragment(c_frag, 0.0f);

    // Compute position
    int warpM = (blockIdx.x * blockDim.x + threadIdx.x) / 32;
    int warpN = blockIdx.y;

    // Perform matrix multiplication
    for (int k = 0; k < K; k += 16) {
        int aRow = warpM * 16;
        int aCol = k;
        int bRow = k;
        int bCol = warpN * 16;

        // Load matrices
        wmma::load_matrix_sync(a_frag, A + aRow * K + aCol, K);
        wmma::load_matrix_sync(b_frag, B + bRow * N + bCol, N);

        // Perform multiply-accumulate
        wmma::mma_sync(c_frag, a_frag, b_frag, c_frag);
    }

    // Store result
    int cRow = warpM * 16;
    int cCol = warpN * 16;
    wmma::store_matrix_sync(C + cRow * N + cCol, c_frag, N, wmma::mem_row_major);
}
```

### 5.4 Performance Benchmarking Framework

```python
# recipes-benchmark/cuda-benchmark/cuda-benchmark_1.0.bb

SUMMARY = "CUDA performance benchmarking suite"
LICENSE = "MIT"

DEPENDS = "cuda-toolkit"

SRC_URI = "file://cuda_benchmark.py \
           file://bandwidth_test.cu \
           file://compute_test.cu \
          "

S = "${WORKDIR}"

do_compile() {
    ${CUDA_PATH}/bin/nvcc -O3 -arch=sm_72 bandwidth_test.cu -o bandwidth_test
    ${CUDA_PATH}/bin/nvcc -O3 -arch=sm_72 compute_test.cu -o compute_test
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 bandwidth_test ${D}${bindir}/
    install -m 0755 compute_test ${D}${bindir}/
    install -m 0755 cuda_benchmark.py ${D}${bindir}/cuda-benchmark
}

FILES:${PN} = "${bindir}/*"
RDEPENDS:${PN} = "python3-core cuda-toolkit"
```

---

## Performance Benchmarks

### Real-World Case Study: Video Analytics Pipeline

**Scenario**: 4K video processing with object detection

**Before Optimization**:
- Throughput: 15 FPS
- GPU Utilization: 45%
- Power Consumption: 25W
- Latency: 66ms per frame

**After Optimization**:
- Throughput: 30 FPS (2x improvement)
- GPU Utilization: 85%
- Power Consumption: 22W (12% reduction)
- Latency: 33ms per frame

**Key Optimizations Applied**:
1. CUDA streams for parallel H2D/D2H transfers (15% improvement)
2. Unified memory with proper prefetching (10% improvement)
3. Custom CUDA kernels replacing OpenCV operations (30% improvement)
4. Batch processing with optimal batch size (25% improvement)
5. Power mode tuning and clock locking (10% improvement)

---

## Best Practices Summary

1. **Build Optimization**:
   - Always use sstate-cache with hash equivalence
   - Implement multi-tier sstate mirrors for teams
   - Profile dependency graph to identify bottlenecks

2. **Runtime Performance**:
   - Use custom nvpmodel configurations for your workload
   - Lock clocks during inference for consistent performance
   - Profile with tegrastats and custom monitoring tools

3. **Memory Management**:
   - Leverage unified memory architecture
   - Use cudaMemAdvise for fine-grained control
   - Monitor CMA and ION allocations

4. **Power Management**:
   - Implement dynamic power modes based on workload
   - Measure actual power consumption for validation
   - Use application-level power context managers

5. **GPU/CUDA**:
   - Use multiple streams for concurrent operations
   - Optimize memory access patterns (coalescing)
   - Leverage Tensor Cores for matrix operations (Xavier+)
   - Profile with nvprof and Nsight Systems

---

## Additional Resources

- NVIDIA Jetson Performance Tuning Guide
- CUDA C++ Programming Guide
- Yocto Project Optimization Manual
- Meta-Tegra Layer Documentation

---

**Next Steps**: Proceed to [Production Deployment](02-production-deployment.md) to learn about hardening and deploying optimized systems.
