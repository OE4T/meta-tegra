# Advanced Debugging Techniques for Yocto & Meta-Tegra

## Overview

This module covers advanced debugging techniques for Jetson systems, including kernel debugging with GDB, tracing with ftrace, performance profiling, memory leak detection, and remote debugging setups.

**Target Audience**: System engineers and developers debugging complex issues
**Prerequisites**: Strong understanding of Linux internals, debugging tools, and development workflows

---

## 1. GDB Kernel Debugging

### 1.1 Kernel Debug Configuration

```python
# recipes-kernel/linux/linux-tegra-debug_5.10.bb

require recipes-kernel/linux/linux-tegra_5.10.bb

SUMMARY = "Debug-enabled Linux kernel for Tegra"

SRC_URI += "file://debug.cfg"

# Enable debug symbols
KERNEL_EXTRA_ARGS += "CONFIG_DEBUG_INFO=y"

# debug.cfg content
```

```
# Kernel debugging configuration

# Debug info
CONFIG_DEBUG_INFO=y
CONFIG_DEBUG_INFO_DWARF4=y
CONFIG_DEBUG_INFO_BTF=y
CONFIG_GDB_SCRIPTS=y

# Frame pointers for better stack traces
CONFIG_FRAME_POINTER=y
CONFIG_STACK_VALIDATION=y

# Debug features
CONFIG_DEBUG_KERNEL=y
CONFIG_DEBUG_MISC=y
CONFIG_DEBUG_SHIRQ=y
CONFIG_DEBUG_TIMEKEEPING=y

# Memory debugging
CONFIG_DEBUG_SLAB=y
CONFIG_DEBUG_SLAB_LEAK=y
CONFIG_DEBUG_KMEMLEAK=y
CONFIG_DEBUG_KMEMLEAK_DEFAULT_OFF=y
CONFIG_DEBUG_PAGEALLOC=y
CONFIG_PAGE_POISONING=y

# Lock debugging
CONFIG_DEBUG_MUTEXES=y
CONFIG_DEBUG_SPINLOCK=y
CONFIG_DEBUG_ATOMIC_SLEEP=y
CONFIG_PROVE_LOCKING=y
CONFIG_LOCKDEP=y
CONFIG_LOCK_STAT=y

# RCU debugging
CONFIG_PROVE_RCU=y
CONFIG_RCU_CPU_STALL_TIMEOUT=60
CONFIG_RCU_TRACE=y

# Stack debugging
CONFIG_DEBUG_STACK_USAGE=y
CONFIG_STACKTRACE=y

# Ftrace
CONFIG_FTRACE=y
CONFIG_FUNCTION_TRACER=y
CONFIG_FUNCTION_GRAPH_TRACER=y
CONFIG_STACK_TRACER=y
CONFIG_DYNAMIC_FTRACE=y
CONFIG_FUNCTION_PROFILER=y

# Kprobes
CONFIG_KPROBES=y
CONFIG_KPROBE_EVENTS=y
CONFIG_UPROBES=y
CONFIG_UPROBE_EVENTS=y

# Performance events
CONFIG_PERF_EVENTS=y
CONFIG_HW_PERF_EVENTS=y

# KGDB
CONFIG_KGDB=y
CONFIG_KGDB_SERIAL_CONSOLE=y
CONFIG_KGDB_KDB=y
CONFIG_KDB_KEYBOARD=y

# Magic SysRq
CONFIG_MAGIC_SYSRQ=y
CONFIG_MAGIC_SYSRQ_DEFAULT_ENABLE=0x1

# Kernel address space layout randomization (disable for easier debugging)
# CONFIG_RANDOMIZE_BASE is not set
```

### 1.2 KGDB over Serial Setup

```python
# recipes-bsp/u-boot/u-boot-tegra_%.bbappend

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://kgdb-bootargs.cfg"

# Add KGDB boot parameters
UBOOT_EXTLINUX_KERNEL_ARGS:append = " kgdboc=ttyTCU0,115200 kgdbwait"
```

```bash
# Remote debugging session setup script
#!/bin/bash
# setup-kgdb.sh - Setup KGDB remote debugging

SERIAL_PORT="/dev/ttyUSB0"
KERNEL_BUILD_DIR="/build/tmp/work/jetson-xavier-nx-poky-linux/linux-tegra-debug/5.10"
VMLINUX="${KERNEL_BUILD_DIR}/linux-jetson-xavier-nx-standard-build/vmlinux"

# Start GDB with kernel symbols
arm64-linux-gdb "$VMLINUX" << 'EOF'
# Connect to target via serial
target remote /dev/ttyUSB0

# Load kernel symbols
symbol-file vmlinux

# Set breakpoint
break do_sys_open

# Continue execution
continue
EOF
```

### 1.3 KGDB over Ethernet (KGDBoE)

```python
# recipes-kernel/kgdboe/kgdboe_git.bb

SUMMARY = "KGDB over Ethernet kernel module"
LICENSE = "GPLv2"

SRC_URI = "git://github.com/sysprogs/kgdboe.git;branch=master;protocol=https"
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

inherit module

EXTRA_OEMAKE:append = " \
    KDIR=${STAGING_KERNEL_DIR} \
"

do_compile() {
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    oe_runmake KERNEL_PATH=${STAGING_KERNEL_DIR}
}

do_install() {
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0644 kgdboe.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/
}

FILES:${PN} = "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/kgdboe.ko"
```

```bash
# kgdb-ethernet-setup.sh
#!/bin/bash

TARGET_IP="192.168.1.100"
HOST_IP="192.168.1.10"
ETH_DEVICE="eth0"

# Load KGDBoE module on target
echo "Loading KGDBoE module..."
ssh root@${TARGET_IP} "modprobe kgdboe device=${ETH_DEVICE} local_ip=${TARGET_IP} remote_ip=${HOST_IP}"

# Connect from host
echo "Connecting to target..."
gdb-multiarch vmlinux \
    -ex "target remote udp:${TARGET_IP}:31337" \
    -ex "set pagination off" \
    -ex "set print pretty on"
```

### 1.4 Advanced GDB Scripts

```python
# GDB helper scripts for kernel debugging
# .gdbinit

# Pretty printing
set print pretty on
set print array on
set print array-indexes on

# Pagination
set pagination off

# History
set history save on
set history filename ~/.gdb_history

# Kernel-specific commands
define dmesg
    set $__log_buf = log_buf
    set $__log_len = log_end - log_start
    dump binary memory /tmp/dmesg.log $__log_buf $__log_buf+$__log_len
    shell cat /tmp/dmesg.log
end

document dmesg
Dump kernel ring buffer
end

define ps
    set $tasks = &init_task
    set $task = $tasks

    printf "PID    COMM\n"
    while $task != 0
        printf "%5d  %s\n", $task->pid, $task->comm
        set $task = $task->tasks.next
        if $task == $tasks
            set $task = 0
        end
    end
end

document ps
Show all running processes
end

define lsmod
    set $mod = modules.next
    set $done = 0

    printf "Module                  Size  Used by\n"
    while !$done
        set $m = (struct module *)((void *)$mod - (void *)&((struct module *)0)->list)
        printf "%-22s %5d  %d\n", $m->name, $m->core_size, (int)$m->refcnt.counter
        set $mod = $mod->next
        if $mod == &modules
            set $done = 1
        end
    end
end

document lsmod
List loaded kernel modules
end

define btall
    thread apply all bt
end

document btall
Show backtraces for all threads
end
```

---

## 2. ftrace and Tracing

### 2.1 Function Tracing

```bash
#!/bin/bash
# ftrace-function-trace.sh - Function tracing with ftrace

TRACE_DIR="/sys/kernel/debug/tracing"

# Enable tracing
echo 0 > ${TRACE_DIR}/tracing_on

# Clear previous trace
echo > ${TRACE_DIR}/trace

# Set function tracer
echo function > ${TRACE_DIR}/current_tracer

# Filter specific functions (optional)
echo 'schedule*' > ${TRACE_DIR}/set_ftrace_filter
echo 'sys_read' >> ${TRACE_DIR}/set_ftrace_filter
echo 'sys_write' >> ${TRACE_DIR}/set_ftrace_filter

# Set buffer size
echo 10240 > ${TRACE_DIR}/buffer_size_kb

# Enable tracing
echo 1 > ${TRACE_DIR}/tracing_on

# Run workload
sleep 5

# Disable tracing
echo 0 > ${TRACE_DIR}/tracing_on

# Display trace
cat ${TRACE_DIR}/trace

# Save trace to file
cat ${TRACE_DIR}/trace > /tmp/function-trace.txt
echo "Trace saved to /tmp/function-trace.txt"
```

### 2.2 Function Graph Tracer

```bash
#!/bin/bash
# ftrace-graph-trace.sh - Call graph tracing

TRACE_DIR="/sys/kernel/debug/tracing"

echo 0 > ${TRACE_DIR}/tracing_on
echo > ${TRACE_DIR}/trace

# Set function graph tracer
echo function_graph > ${TRACE_DIR}/current_tracer

# Configure graph tracer
echo 10 > ${TRACE_DIR}/max_graph_depth
echo > ${TRACE_DIR}/set_graph_function

# Filter by process
echo $$ > ${TRACE_DIR}/set_ftrace_pid

# Enable tracing
echo 1 > ${TRACE_DIR}/tracing_on

# Run command to trace
$@

# Disable tracing
echo 0 > ${TRACE_DIR}/tracing_on

# Display trace with timing
cat ${TRACE_DIR}/trace
```

### 2.3 Event Tracing

```bash
#!/bin/bash
# trace-events.sh - Kernel event tracing

TRACE_DIR="/sys/kernel/debug/tracing"

echo 0 > ${TRACE_DIR}/tracing_on
echo > ${TRACE_DIR}/trace

# Enable specific events
echo 1 > ${TRACE_DIR}/events/sched/sched_switch/enable
echo 1 > ${TRACE_DIR}/events/sched/sched_wakeup/enable
echo 1 > ${TRACE_DIR}/events/irq/irq_handler_entry/enable
echo 1 > ${TRACE_DIR}/events/irq/irq_handler_exit/enable

# Or enable all events in a category
# echo 1 > ${TRACE_DIR}/events/sched/enable

# Start tracing
echo 1 > ${TRACE_DIR}/tracing_on

sleep 10

echo 0 > ${TRACE_DIR}/tracing_on

# Analyze trace
cat ${TRACE_DIR}/trace | head -100

# Convert to Chrome trace format for visualization
python3 << 'EOF'
import json
import re

events = []
with open('/sys/kernel/debug/tracing/trace') as f:
    for line in f:
        if line.startswith('#'):
            continue
        # Parse trace line
        match = re.match(r'\s*(\S+)-(\d+)\s+\[(\d+)\]\s+(\S+)\s+(\d+\.\d+):\s+(\S+):', line)
        if match:
            comm, pid, cpu, flags, ts, event = match.groups()
            events.append({
                'name': event,
                'cat': 'kernel',
                'ph': 'i',
                'ts': float(ts) * 1000000,
                'pid': int(pid),
                'tid': int(cpu),
                's': 't'
            })

with open('/tmp/trace.json', 'w') as f:
    json.dump({'traceEvents': events}, f)

print("Chrome trace saved to /tmp/trace.json")
print("Open in chrome://tracing")
EOF
```

### 2.4 Custom Trace Points

```c
// custom-tracepoint.h - Define custom tracepoints

#undef TRACE_SYSTEM
#define TRACE_SYSTEM custom_module

#if !defined(_TRACE_CUSTOM_MODULE_H) || defined(TRACE_HEADER_MULTI_READ)
#define _TRACE_CUSTOM_MODULE_H

#include <linux/tracepoint.h>

TRACE_EVENT(custom_operation_start,
    TP_PROTO(int id, const char *name),
    TP_ARGS(id, name),
    TP_STRUCT__entry(
        __field(int, id)
        __string(name, name)
    ),
    TP_fast_assign(
        __entry->id = id;
        __assign_str(name, name);
    ),
    TP_printk("Operation %d (%s) started", __entry->id, __get_str(name))
);

TRACE_EVENT(custom_operation_end,
    TP_PROTO(int id, int result, unsigned long duration_us),
    TP_ARGS(id, result, duration_us),
    TP_STRUCT__entry(
        __field(int, id)
        __field(int, result)
        __field(unsigned long, duration_us)
    ),
    TP_fast_assign(
        __entry->id = id;
        __entry->result = result;
        __entry->duration_us = duration_us;
    ),
    TP_printk("Operation %d completed: result=%d, duration=%lu us",
              __entry->id, __entry->result, __entry->duration_us)
);

#endif /* _TRACE_CUSTOM_MODULE_H */

#undef TRACE_INCLUDE_PATH
#define TRACE_INCLUDE_PATH .
#define TRACE_INCLUDE_FILE custom-tracepoint

#include <trace/define_trace.h>
```

```c
// custom-module.c - Use custom tracepoints

#include <linux/module.h>
#include <linux/ktime.h>

#define CREATE_TRACE_POINTS
#include "custom-tracepoint.h"

static int custom_operation(int id, const char *name) {
    ktime_t start, end;
    int result;
    unsigned long duration_us;

    /* Trace operation start */
    trace_custom_operation_start(id, name);

    start = ktime_get();

    /* Perform operation */
    msleep(10);  /* Simulated work */
    result = id * 2;

    end = ktime_get();
    duration_us = ktime_us_delta(end, start);

    /* Trace operation end */
    trace_custom_operation_end(id, result, duration_us);

    return result;
}

static int __init custom_module_init(void) {
    pr_info("Custom module loaded\n");
    custom_operation(1, "test_operation");
    return 0;
}

static void __exit custom_module_exit(void) {
    pr_info("Custom module unloaded\n");
}

module_init(custom_module_init);
module_exit(custom_module_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Your Name");
MODULE_DESCRIPTION("Custom tracepoint example");
```

---

## 3. Performance Profiling

### 3.1 perf Tool Usage

```bash
#!/bin/bash
# perf-profiling.sh - Performance profiling with perf

# Record system-wide profile for 10 seconds
perf record -a -g -F 1000 sleep 10

# Generate report
perf report --stdio > perf-report.txt

# Generate flame graph
perf script | ./FlameGraph/stackcollapse-perf.pl | ./FlameGraph/flamegraph.pl > flamegraph.svg

echo "Flamegraph saved to flamegraph.svg"

# Profile specific process
perf record -g -p $(pidof my_application) sleep 10
perf report

# Profile with call graph
perf record --call-graph dwarf -p $(pidof my_application) sleep 10
perf report --call-graph

# CPU profiling with annotations
perf record -e cycles:pp -a -g sleep 10
perf annotate --stdio

# Memory access profiling
perf record -e mem:0x<address>:rw -p $(pidof my_application)

# Cache miss profiling
perf record -e cache-misses,cache-references -a -g sleep 10
perf report

# Branch misprediction profiling
perf record -e branches,branch-misses -a -g sleep 10
perf report
```

### 3.2 Custom Performance Monitoring

```python
# recipes-devtools/perf-monitor/perf-monitor_1.0.bb

SUMMARY = "Custom performance monitoring tool"
LICENSE = "MIT"

SRC_URI = "file://perf-monitor.c"

S = "${WORKDIR}"

DEPENDS = "papi"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} perf-monitor.c -o perf-monitor -lpapi
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 perf-monitor ${D}${bindir}/
}

FILES:${PN} = "${bindir}/perf-monitor"
```

```c
// perf-monitor.c - Hardware performance counter monitoring

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <papi.h>

#define MAX_EVENTS 10

typedef struct {
    int event_set;
    int events[MAX_EVENTS];
    long_long values[MAX_EVENTS];
    int num_events;
    const char *event_names[MAX_EVENTS];
} perf_monitor_t;

int perf_monitor_init(perf_monitor_t *mon) {
    int retval;

    /* Initialize PAPI library */
    retval = PAPI_library_init(PAPI_VER_CURRENT);
    if (retval != PAPI_VER_CURRENT) {
        fprintf(stderr, "PAPI library init error\n");
        return -1;
    }

    /* Create event set */
    mon->event_set = PAPI_NULL;
    retval = PAPI_create_eventset(&mon->event_set);
    if (retval != PAPI_OK) {
        fprintf(stderr, "PAPI_create_eventset failed\n");
        return -1;
    }

    /* Add events to monitor */
    mon->num_events = 0;

    /* CPU cycles */
    mon->events[mon->num_events] = PAPI_TOT_CYC;
    mon->event_names[mon->num_events] = "Total Cycles";
    PAPI_add_event(mon->event_set, mon->events[mon->num_events]);
    mon->num_events++;

    /* Instructions */
    mon->events[mon->num_events] = PAPI_TOT_INS;
    mon->event_names[mon->num_events] = "Total Instructions";
    PAPI_add_event(mon->event_set, mon->events[mon->num_events]);
    mon->num_events++;

    /* L1 cache misses */
    mon->events[mon->num_events] = PAPI_L1_DCM;
    mon->event_names[mon->num_events] = "L1 Data Cache Misses";
    PAPI_add_event(mon->event_set, mon->events[mon->num_events]);
    mon->num_events++;

    /* L2 cache misses */
    mon->events[mon->num_events] = PAPI_L2_DCM;
    mon->event_names[mon->num_events] = "L2 Data Cache Misses";
    PAPI_add_event(mon->event_set, mon->events[mon->num_events]);
    mon->num_events++;

    /* Branch mispredictions */
    mon->events[mon->num_events] = PAPI_BR_MSP;
    mon->event_names[mon->num_events] = "Branch Mispredictions";
    PAPI_add_event(mon->event_set, mon->events[mon->num_events]);
    mon->num_events++;

    return 0;
}

int perf_monitor_start(perf_monitor_t *mon) {
    return PAPI_start(mon->event_set);
}

int perf_monitor_stop(perf_monitor_t *mon) {
    return PAPI_stop(mon->event_set, mon->values);
}

void perf_monitor_print(perf_monitor_t *mon) {
    printf("\nPerformance Counters:\n");
    printf("=====================\n");

    for (int i = 0; i < mon->num_events; i++) {
        printf("%-30s: %lld\n", mon->event_names[i], mon->values[i]);
    }

    /* Calculate derived metrics */
    if (mon->num_events >= 2) {
        double ipc = (double)mon->values[1] / (double)mon->values[0];
        printf("%-30s: %.3f\n", "IPC (Instructions per Cycle)", ipc);
    }
}

void perf_monitor_cleanup(perf_monitor_t *mon) {
    PAPI_cleanup_eventset(mon->event_set);
    PAPI_destroy_eventset(&mon->event_set);
    PAPI_shutdown();
}

/* Example usage */
int main(int argc, char *argv[]) {
    perf_monitor_t mon;

    if (perf_monitor_init(&mon) != 0) {
        fprintf(stderr, "Failed to initialize performance monitor\n");
        return 1;
    }

    printf("Starting performance monitoring...\n");

    perf_monitor_start(&mon);

    /* Application code to profile */
    for (volatile int i = 0; i < 100000000; i++) {
        /* Work */
    }

    perf_monitor_stop(&mon);

    perf_monitor_print(&mon);
    perf_monitor_cleanup(&mon);

    return 0;
}
```

### 3.3 GPU Profiling with Nsight

```bash
#!/bin/bash
# gpu-profile.sh - NVIDIA Nsight Systems profiling

APP_BINARY="/usr/bin/my_cuda_app"
OUTPUT_FILE="/tmp/nsight-report"

# Profile with Nsight Systems
nsys profile \
    --trace=cuda,nvtx,osrt,cublas,cudnn \
    --duration=30 \
    --sample=cpu \
    --cpuctxsw=true \
    --force-overwrite=true \
    --output=${OUTPUT_FILE} \
    ${APP_BINARY}

# Generate report
nsys stats ${OUTPUT_FILE}.qdrep

# Profile specific CUDA kernels with Nsight Compute
nv-nsight-cu-cli \
    --metrics=sm__cycles_elapsed.avg,dram__bytes.sum \
    --kernel-name="my_kernel" \
    ${APP_BINARY}

echo "Profile saved to ${OUTPUT_FILE}.qdrep"
echo "View with: nsys-ui ${OUTPUT_FILE}.qdrep"
```

---

## 4. Memory Leak Detection

### 4.1 Valgrind Integration

```python
# recipes-devtools/valgrind/valgrind_%.bbappend

# Enable additional features
PACKAGECONFIG:append = " mpi"

# Install valgrind scripts
SRC_URI += "file://valgrind-wrapper.sh"

do_install:append() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/valgrind-wrapper.sh ${D}${bindir}/
}
```

```bash
# valgrind-wrapper.sh - Valgrind testing wrapper
#!/bin/bash

APP=$1
shift
ARGS=$@

SUPPRESSIONS="/usr/share/valgrind/nvidia.supp"

# Memory leak detection
echo "Running memory leak detection..."
valgrind \
    --leak-check=full \
    --show-leak-kinds=all \
    --track-origins=yes \
    --verbose \
    --log-file=/tmp/valgrind-memcheck.log \
    --suppressions=${SUPPRESSIONS} \
    ${APP} ${ARGS}

# Thread error detection (Helgrind)
echo "Running thread error detection..."
valgrind \
    --tool=helgrind \
    --log-file=/tmp/valgrind-helgrind.log \
    ${APP} ${ARGS}

# Cache profiling (Cachegrind)
echo "Running cache profiling..."
valgrind \
    --tool=cachegrind \
    --cache-sim=yes \
    --branch-sim=yes \
    --cachegrind-out-file=/tmp/cachegrind.out \
    ${APP} ${ARGS}

# Annotate source with cache statistics
cg_annotate /tmp/cachegrind.out > /tmp/cachegrind-annotated.txt

echo "Results:"
echo "  Memory leaks: /tmp/valgrind-memcheck.log"
echo "  Thread errors: /tmp/valgrind-helgrind.log"
echo "  Cache stats: /tmp/cachegrind-annotated.txt"
```

### 4.2 Kernel Memory Leak Detection

```bash
#!/bin/bash
# kmemleak-check.sh - Kernel memory leak detection

# Enable kmemleak
echo "scan=on" > /sys/kernel/debug/kmemleak

# Clear previous leaks
echo "clear" > /sys/kernel/debug/kmemleak

# Run workload
echo "Running workload..."
sleep 60

# Trigger scan
echo "scan" > /sys/kernel/debug/kmemleak

# Wait for scan to complete
sleep 10

# Check for leaks
echo "Checking for memory leaks..."
cat /sys/kernel/debug/kmemleak

# Save to file
cat /sys/kernel/debug/kmemleak > /tmp/kmemleak-report.txt

if [ -s /tmp/kmemleak-report.txt ]; then
    echo "Memory leaks detected! See /tmp/kmemleak-report.txt"
    exit 1
else
    echo "No memory leaks detected"
    exit 0
fi
```

### 4.3 CUDA Memory Leak Detection

```bash
#!/bin/bash
# cuda-memcheck.sh - CUDA memory error detection

APP=$1

# Run cuda-memcheck for memory errors
cuda-memcheck \
    --leak-check full \
    --report-api-errors all \
    --tool memcheck \
    --log-file /tmp/cuda-memcheck.log \
    ${APP}

# Check for race conditions
cuda-memcheck \
    --tool racecheck \
    --racecheck-report all \
    --log-file /tmp/cuda-racecheck.log \
    ${APP}

# Check for initialization errors
cuda-memcheck \
    --tool initcheck \
    --log-file /tmp/cuda-initcheck.log \
    ${APP}

echo "CUDA memory check results:"
echo "  Memory errors: /tmp/cuda-memcheck.log"
echo "  Race conditions: /tmp/cuda-racecheck.log"
echo "  Initialization: /tmp/cuda-initcheck.log"
```

---

## 5. Remote Debugging

### 5.1 GDB Remote Debugging Setup

```python
# recipes-devtools/gdb/gdb-cross-remote_%.bbappend

# Enable gdbserver
PACKAGECONFIG:append = " python tui"

# Install debug helper scripts
SRC_URI += " \
    file://remote-debug.sh \
    file://gdbserver-startup.service \
"

do_install:append() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/remote-debug.sh ${D}${bindir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/gdbserver-startup.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += "${systemd_system_unitdir}/gdbserver-startup.service"

SYSTEMD_SERVICE:${PN} = "gdbserver-startup.service"
```

```bash
# remote-debug.sh - Remote debugging helper
#!/bin/bash

TARGET_IP=$1
TARGET_PORT=${2:-2345}
BINARY=$3

if [ $# -lt 2 ]; then
    echo "Usage: $0 <target-ip> [port] [binary]"
    exit 1
fi

# Start gdbserver on target
ssh root@${TARGET_IP} "killall gdbserver 2>/dev/null; gdbserver :${TARGET_PORT} ${BINARY}" &

sleep 2

# Connect from host
gdb-multiarch ${BINARY} \
    -ex "target remote ${TARGET_IP}:${TARGET_PORT}" \
    -ex "set sysroot /build/tmp/work/jetson-xavier-nx-poky-linux/core-image-minimal/1.0-r0/rootfs" \
    -ex "set solib-search-path /build/tmp/work/jetson-xavier-nx-poky-linux/core-image-minimal/1.0-r0/rootfs/lib:/build/tmp/work/jetson-xavier-nx-poky-linux/core-image-minimal/1.0-r0/rootfs/usr/lib"
```

```ini
# gdbserver-startup.service - Auto-start gdbserver
[Unit]
Description=GDB Server for Remote Debugging
After=network.target

[Service]
Type=simple
ExecStart=/usr/bin/gdbserver --multi :2345
Restart=always

[Install]
WantedBy=multi-user.target
```

### 5.2 VS Code Remote Debugging

```json
// .vscode/launch.json - VS Code debugging configuration
{
    "version": "0.2.0",
    "configurations": [
        {
            "name": "Remote Debug (Jetson)",
            "type": "cppdbg",
            "request": "launch",
            "program": "${workspaceFolder}/build/my_application",
            "args": [],
            "stopAtEntry": false,
            "cwd": "${workspaceFolder}",
            "environment": [],
            "externalConsole": false,
            "MIMode": "gdb",
            "miDebuggerPath": "/usr/bin/gdb-multiarch",
            "miDebuggerServerAddress": "192.168.1.100:2345",
            "setupCommands": [
                {
                    "description": "Enable pretty-printing",
                    "text": "-enable-pretty-printing",
                    "ignoreFailures": true
                },
                {
                    "description": "Set sysroot",
                    "text": "set sysroot /build/sysroot",
                    "ignoreFailures": false
                }
            ],
            "sourceFileMap": {
                "/build/workspace": "${workspaceFolder}"
            }
        },
        {
            "name": "Attach to Process (Jetson)",
            "type": "cppdbg",
            "request": "attach",
            "program": "${workspaceFolder}/build/my_application",
            "processId": "${command:pickRemoteProcess}",
            "MIMode": "gdb",
            "miDebuggerPath": "/usr/bin/gdb-multiarch",
            "miDebuggerServerAddress": "192.168.1.100:2345",
            "setupCommands": [
                {
                    "description": "Enable pretty-printing",
                    "text": "-enable-pretty-printing",
                    "ignoreFailures": true
                }
            ]
        },
        {
            "name": "CUDA-GDB Remote",
            "type": "cuda-gdb",
            "request": "launch",
            "program": "${workspaceFolder}/build/cuda_app",
            "stopAtEntry": false,
            "cwd": "${workspaceFolder}",
            "externalConsole": false,
            "MIMode": "gdb",
            "miDebuggerPath": "/usr/local/cuda/bin/cuda-gdb",
            "miDebuggerServerAddress": "192.168.1.100:2346"
        }
    ]
}
```

### 5.3 Core Dump Analysis

```bash
#!/bin/bash
# setup-coredump.sh - Configure core dump collection

# Enable core dumps
ulimit -c unlimited
echo "ulimit -c unlimited" >> /etc/profile

# Configure core dump pattern
echo "/var/crashes/core.%e.%p.%t" > /proc/sys/kernel/core_pattern

# Enable core dump with full memory
echo 1 > /proc/sys/kernel/core_uses_pid
echo 2 > /proc/sys/fs/suid_dumpable

# Create crash directory
mkdir -p /var/crashes
chmod 1777 /var/crashes

# Configure systemd coredump
mkdir -p /etc/systemd/coredump.conf.d
cat > /etc/systemd/coredump.conf.d/custom.conf << 'EOF'
[Coredump]
Storage=external
Compress=yes
ProcessSizeMax=8G
ExternalSizeMax=8G
MaxUse=32G
EOF

systemctl daemon-reload

echo "Core dump configuration complete"
```

```bash
#!/bin/bash
# analyze-coredump.sh - Analyze core dump
CORE_FILE=$1
BINARY=$2

if [ $# -ne 2 ]; then
    echo "Usage: $0 <core-file> <binary>"
    exit 1
fi

echo "Analyzing core dump: $CORE_FILE"

gdb-multiarch $BINARY $CORE_FILE << 'EOF'
# Print backtrace
bt full

# Print thread backtraces
thread apply all bt full

# Print registers
info registers

# Print memory maps
info proc mappings

# Print shared libraries
info sharedlibrary

# Examine crash location
frame 0
list

# Print variables
info locals
info args

# Generate report
set logging file /tmp/coredump-analysis.txt
set logging on
bt full
thread apply all bt full
info registers
info locals
set logging off

quit
EOF

echo "Analysis saved to /tmp/coredump-analysis.txt"
```

---

## 6. Debugging Recipes and Tools

### 6.1 Debug Image Configuration

```python
# recipes-core/images/debug-image.bb

require recipes-core/images/core-image-minimal.bb

SUMMARY = "Debug-enabled image with all debugging tools"

# Enable debug features
IMAGE_FEATURES:append = " \
    debug-tweaks \
    tools-debug \
    tools-profile \
    tools-sdk \
    dbg-pkgs \
    dev-pkgs \
"

# Additional debugging packages
IMAGE_INSTALL:append = " \
    gdb \
    gdbserver \
    strace \
    ltrace \
    valgrind \
    perf \
    lttng-tools \
    lttng-modules \
    systemtap \
    crash \
    kexec-tools \
    kdump \
    openssh-sftp-server \
    binutils \
    file \
    lsof \
    tcpdump \
    wireshark \
"

# Kernel debugging
IMAGE_INSTALL:append = " \
    kernel-dev \
    kernel-devsrc \
    kernel-modules \
"

# Build environment on target
IMAGE_INSTALL:append = " \
    gcc \
    g++ \
    make \
    cmake \
    python3-dev \
"

# CUDA debugging tools
IMAGE_INSTALL:append = " \
    cuda-gdb \
    cuda-memcheck \
    nsight-systems \
    nsight-compute \
"
```

---

## Best Practices Summary

1. **Use Appropriate Tools**: Match tool to problem (ftrace for kernel, valgrind for userspace)
2. **Enable Debug Symbols**: Always compile with -g for debugging builds
3. **Reproduce Reliably**: Ensure bugs can be reproduced before debugging
4. **Start Simple**: Use printk/printf before complex tools
5. **Log Everything**: Comprehensive logging aids debugging
6. **Automate Testing**: Use automated tests to catch regressions
7. **Remote Debugging**: Essential for embedded systems
8. **Core Dumps**: Configure and analyze for post-mortem debugging
9. **Performance Profiling**: Regular profiling prevents performance regressions
10. **Document Findings**: Keep debugging notes for future reference

---

**Next Steps**: Proceed to [AI/ML Optimization](06-ai-ml-optimization.md) for TensorRT and inference optimization.
