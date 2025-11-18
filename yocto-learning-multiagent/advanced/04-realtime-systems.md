# Real-Time Systems on Yocto & Meta-Tegra

## Overview

This module covers real-time system configuration for Jetson platforms, including RT-PREEMPT kernel integration, real-time scheduling policies, latency analysis, interrupt handling optimization, and achieving deterministic behavior.

**Target Audience**: Real-time system developers and safety-critical application engineers
**Prerequisites**: Deep understanding of Linux kernel, scheduling, and interrupt handling

---

## 1. RT-PREEMPT Kernel Integration

### 1.1 RT-PREEMPT Patch Application

```python
# recipes-kernel/linux/linux-tegra-rt_5.10.bb

require recipes-kernel/linux/linux-tegra_5.10.bb

SUMMARY = "Real-Time Linux kernel for Tegra"
LINUX_VERSION = "5.10.120"
RT_PATCH_VERSION = "rt70"

SRC_URI += " \
    https://cdn.kernel.org/pub/linux/kernel/projects/rt/5.10/patch-${LINUX_VERSION}-${RT_PATCH_VERSION}.patch.gz;name=rt \
    file://rt-config.cfg \
    file://0001-tegra-rt-disable-conflicting-options.patch \
"

SRC_URI[rt.sha256sum] = "..."

# RT-specific kernel configuration
KERNEL_FEATURES:append = " features/rt/rt.scc"

# Disable incompatible options
KERNEL_CONFIG_COMMAND = "oe_runmake_call -C ${S} CC="${KERNEL_CC}" O=${B} olddefconfig"

python do_patch:append() {
    import subprocess

    # Apply RT patch
    rt_patch = d.expand("${WORKDIR}/patch-${LINUX_VERSION}-${RT_PATCH_VERSION}.patch")
    kernel_dir = d.getVar('S')

    subprocess.run(['patch', '-p1', '-d', kernel_dir, '-i', rt_patch], check=True)
}

# RT kernel requires specific configuration
COMPATIBLE_MACHINE = "jetson-xavier-nx|jetson-xavier|jetson-orin"
```

### 1.2 RT Kernel Configuration

```
# rt-config.cfg - Real-time kernel configuration

# RT-PREEMPT core
CONFIG_PREEMPT_RT=y
CONFIG_PREEMPT=y
CONFIG_PREEMPT_COUNT=y
CONFIG_PREEMPTION=y

# Disable voluntary preemption
# CONFIG_PREEMPT_VOLUNTARY is not set
# CONFIG_PREEMPT_NONE is not set

# High resolution timers
CONFIG_HIGH_RES_TIMERS=y
CONFIG_HZ_1000=y
CONFIG_HZ=1000

# Tickless system (dynamic ticks)
CONFIG_NO_HZ_FULL=y
CONFIG_NO_HZ_IDLE=y
CONFIG_NO_HZ=y
CONFIG_NO_HZ_COMMON=y

# RCU configuration for RT
CONFIG_RCU_NOCB_CPU=y
CONFIG_RCU_BOOST=y
CONFIG_RCU_BOOST_DELAY=500

# Threaded IRQs
CONFIG_IRQ_FORCED_THREADING=y
CONFIG_IRQ_FORCED_THREADING_DEFAULT=y

# CPU isolation
CONFIG_CPU_ISOLATION=y
CONFIG_IRQ_TIME_ACCOUNTING=y

# Disable CPU frequency scaling for determinism
# CONFIG_CPU_FREQ is not set
# CONFIG_CPU_IDLE is not set

# Disable transparent huge pages
# CONFIG_TRANSPARENT_HUGEPAGE is not set

# Real-time scheduling
CONFIG_RT_GROUP_SCHED=y

# Debugging (disable in production)
CONFIG_DEBUG_PREEMPT=y
CONFIG_DEBUG_RT_MUTEXES=y
CONFIG_PROVE_LOCKING=y
CONFIG_DEBUG_ATOMIC_SLEEP=y

# Disable conflicting options
# CONFIG_FTRACE is not set
# CONFIG_KPROBES is not set
# CONFIG_UPROBES is not set
```

### 1.3 Machine Configuration for RT

```python
# conf/machine/jetson-xavier-nx-rt.conf

require conf/machine/jetson-xavier-nx-devkit.conf

MACHINEOVERRIDES .= ":rt-kernel"

# Use RT kernel
PREFERRED_PROVIDER_virtual/kernel = "linux-tegra-rt"
PREFERRED_VERSION_linux-tegra-rt = "5.10%"

# Kernel command line for RT
KERNEL_ARGS += " \
    isolcpus=2,3,4,5 \
    nohz_full=2,3,4,5 \
    rcu_nocbs=2,3,4,5 \
    rcu_nocb_poll \
    irqaffinity=0,1 \
    skew_tick=1 \
"

# RT-specific image features
IMAGE_FEATURES:append = " rt-tools rt-tests"
```

---

## 2. Real-Time Scheduling

### 2.1 CPU Isolation and Affinity

```c
// rt-task-manager.c - Real-time task management utilities

#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sched.h>
#include <pthread.h>
#include <sys/mman.h>
#include <sys/resource.h>
#include <errno.h>
#include <string.h>

/* RT priority levels */
#define RT_PRIORITY_CRITICAL    99  /* Highest priority */
#define RT_PRIORITY_HIGH        80
#define RT_PRIORITY_MEDIUM      50
#define RT_PRIORITY_LOW         20

/* CPU allocation */
#define RT_CPU_MASK_CRITICAL    0x04  /* CPU 2 */
#define RT_CPU_MASK_HIGH        0x08  /* CPU 3 */
#define RT_CPU_MASK_MEDIUM      0x30  /* CPU 4-5 */
#define RT_CPU_MASK_GENERAL     0x03  /* CPU 0-1 */

typedef struct {
    int policy;          /* SCHED_FIFO, SCHED_RR, SCHED_DEADLINE */
    int priority;        /* RT priority (1-99) */
    unsigned long cpu_mask;  /* CPU affinity mask */
    size_t stack_size;   /* Thread stack size */
    int lock_memory;     /* Lock memory to RAM */
} rt_task_config_t;

/* Initialize RT environment */
int rt_init_environment(void) {
    struct rlimit rlim;

    /* Lock all current and future memory */
    if (mlockall(MCL_CURRENT | MCL_FUTURE) != 0) {
        perror("mlockall failed");
        return -1;
    }

    /* Set unlimited stack size */
    rlim.rlim_cur = RLIM_INFINITY;
    rlim.rlim_max = RLIM_INFINITY;
    if (setrlimit(RLIMIT_STACK, &rlim) != 0) {
        perror("setrlimit(STACK) failed");
        return -1;
    }

    /* Set unlimited memlock */
    if (setrlimit(RLIMIT_MEMLOCK, &rlim) != 0) {
        perror("setrlimit(MEMLOCK) failed");
        return -1;
    }

    /* Disable malloc trimming */
    mallopt(M_TRIM_THRESHOLD, -1);

    /* Disable mmap usage for large allocations */
    mallopt(M_MMAP_MAX, 0);

    return 0;
}

/* Set thread to real-time scheduling */
int rt_set_thread_priority(pthread_t thread, const rt_task_config_t *config) {
    struct sched_param param;
    cpu_set_t cpuset;
    int ret;

    /* Set CPU affinity */
    CPU_ZERO(&cpuset);
    for (int i = 0; i < 8; i++) {
        if (config->cpu_mask & (1 << i)) {
            CPU_SET(i, &cpuset);
        }
    }

    ret = pthread_setaffinity_np(thread, sizeof(cpuset), &cpuset);
    if (ret != 0) {
        fprintf(stderr, "pthread_setaffinity_np failed: %s\n", strerror(ret));
        return -1;
    }

    /* Set scheduling policy and priority */
    memset(&param, 0, sizeof(param));
    param.sched_priority = config->priority;

    ret = pthread_setschedparam(thread, config->policy, &param);
    if (ret != 0) {
        fprintf(stderr, "pthread_setschedparam failed: %s\n", strerror(ret));
        return -1;
    }

    return 0;
}

/* Pre-fault stack to avoid page faults during RT execution */
void rt_prefault_stack(size_t size) {
    unsigned char dummy[size];
    memset(dummy, 0, size);
}

/* Example: Critical real-time task */
void* critical_rt_task(void *arg) {
    rt_task_config_t config = {
        .policy = SCHED_FIFO,
        .priority = RT_PRIORITY_CRITICAL,
        .cpu_mask = RT_CPU_MASK_CRITICAL,
        .stack_size = 8 * 1024 * 1024,  /* 8 MB */
        .lock_memory = 1,
    };

    /* Configure RT parameters */
    if (rt_set_thread_priority(pthread_self(), &config) != 0) {
        fprintf(stderr, "Failed to set RT priority\n");
        return NULL;
    }

    /* Pre-fault stack */
    rt_prefault_stack(config.stack_size);

    printf("Critical RT task running on CPU %d with priority %d\n",
           sched_getcpu(), config.priority);

    /* RT task loop */
    struct timespec next;
    clock_gettime(CLOCK_MONOTONIC, &next);

    while (1) {
        /* Wait for next period (1ms) */
        next.tv_nsec += 1000000;
        if (next.tv_nsec >= 1000000000) {
            next.tv_nsec -= 1000000000;
            next.tv_sec++;
        }
        clock_nanosleep(CLOCK_MONOTONIC, TIMER_ABSTIME, &next, NULL);

        /* Critical real-time work here */
        // ...
    }

    return NULL;
}

/* SCHED_DEADLINE example */
int rt_set_deadline_schedule(pthread_t thread,
                             uint64_t runtime_ns,
                             uint64_t deadline_ns,
                             uint64_t period_ns) {
#ifdef __NR_sched_setattr
    struct sched_attr {
        uint32_t size;
        uint32_t sched_policy;
        uint64_t sched_flags;
        int32_t sched_nice;
        uint32_t sched_priority;
        uint64_t sched_runtime;
        uint64_t sched_deadline;
        uint64_t sched_period;
    } attr;

    memset(&attr, 0, sizeof(attr));
    attr.size = sizeof(attr);
    attr.sched_policy = SCHED_DEADLINE;
    attr.sched_runtime = runtime_ns;
    attr.sched_deadline = deadline_ns;
    attr.sched_period = period_ns;

    if (syscall(__NR_sched_setattr, 0, &attr, 0) != 0) {
        perror("sched_setattr failed");
        return -1;
    }

    return 0;
#else
    fprintf(stderr, "SCHED_DEADLINE not supported\n");
    return -1;
#endif
}

int main(int argc, char *argv[]) {
    pthread_t thread;
    pthread_attr_t attr;

    /* Initialize RT environment */
    if (rt_init_environment() != 0) {
        fprintf(stderr, "Failed to initialize RT environment\n");
        return 1;
    }

    /* Create RT thread */
    pthread_attr_init(&attr);
    pthread_attr_setstacksize(&attr, 8 * 1024 * 1024);
    pthread_attr_setschedpolicy(&attr, SCHED_FIFO);

    struct sched_param param;
    param.sched_priority = RT_PRIORITY_CRITICAL;
    pthread_attr_setschedparam(&attr, &param);
    pthread_attr_setinheritsched(&attr, PTHREAD_EXPLICIT_SCHED);

    if (pthread_create(&thread, &attr, critical_rt_task, NULL) != 0) {
        perror("pthread_create failed");
        return 1;
    }

    pthread_attr_destroy(&attr);

    /* Wait for thread */
    pthread_join(thread, NULL);

    return 0;
}
```

### 2.2 RT Scheduling Recipe

```python
# recipes-support/rt-utils/rt-utils_1.0.bb

SUMMARY = "Real-time utilities and examples"
LICENSE = "MIT"

SRC_URI = " \
    file://rt-task-manager.c \
    file://rt-latency-test.c \
    file://rt-periodic-task.c \
"

S = "${WORKDIR}"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} -pthread -lrt rt-task-manager.c -o rt-task-manager
    ${CC} ${CFLAGS} ${LDFLAGS} -pthread -lrt rt-latency-test.c -o rt-latency-test
    ${CC} ${CFLAGS} ${LDFLAGS} -pthread -lrt rt-periodic-task.c -o rt-periodic-task
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 rt-task-manager ${D}${bindir}/
    install -m 0755 rt-latency-test ${D}${bindir}/
    install -m 0755 rt-periodic-task ${D}${bindir}/
}

FILES:${PN} = "${bindir}/*"
```

---

## 3. Latency Analysis

### 3.3 Cyclictest - Latency Measurement

```bash
# rt-latency-benchmark.sh - Comprehensive latency testing

#!/bin/bash

DURATION=3600  # 1 hour
INTERVAL=1000  # 1ms (1000us)
PRIORITY=95
CPUS="2,3,4,5"  # Isolated CPUs

OUTPUT_DIR="/var/log/rt-tests"
TIMESTAMP=$(date +%Y%m%d-%H%M%S)

mkdir -p "${OUTPUT_DIR}"

echo "Starting RT latency benchmark..."
echo "Duration: ${DURATION} seconds"
echo "Interval: ${INTERVAL} us"
echo "CPUs: ${CPUS}"

# Run cyclictest
cyclictest \
    --smp \
    --priority=${PRIORITY} \
    --interval=${INTERVAL} \
    --duration=${DURATION} \
    --affinity=${CPUS} \
    --mlockall \
    --histogram=1000 \
    --histfile="${OUTPUT_DIR}/histogram-${TIMESTAMP}.txt" \
    --output="${OUTPUT_DIR}/cyclictest-${TIMESTAMP}.txt" \
    --quiet

# Parse results
echo ""
echo "========================================="
echo "Latency Test Results"
echo "========================================="
cat "${OUTPUT_DIR}/cyclictest-${TIMESTAMP}.txt" | tail -10

# Generate histogram plot
python3 << 'EOF'
import sys
import numpy as np
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

# Read histogram data
hist_file = f"${OUTPUT_DIR}/histogram-${TIMESTAMP}.txt"
data = {}

with open(hist_file) as f:
    for line in f:
        if line.startswith('#'):
            continue
        parts = line.strip().split()
        if len(parts) >= 2:
            latency = int(parts[0])
            count = int(parts[1])
            data[latency] = count

# Create histogram plot
fig, ax = plt.subplots(figsize=(12, 6))
latencies = sorted(data.keys())
counts = [data[l] for l in latencies]

ax.bar(latencies, counts, width=1.0, edgecolor='black', alpha=0.7)
ax.set_xlabel('Latency (microseconds)')
ax.set_ylabel('Count')
ax.set_title('RT Latency Distribution')
ax.grid(True, alpha=0.3)

# Add statistics
p50 = np.percentile(latencies, 50)
p95 = np.percentile(latencies, 95)
p99 = np.percentile(latencies, 99)
max_lat = max(latencies)

stats_text = f'P50: {p50:.1f}us\nP95: {p95:.1f}us\nP99: {p99:.1f}us\nMax: {max_lat}us'
ax.text(0.95, 0.95, stats_text, transform=ax.transAxes,
        verticalalignment='top', horizontalalignment='right',
        bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.5))

plt.savefig(f"${OUTPUT_DIR}/histogram-${TIMESTAMP}.png", dpi=150)
print(f"Histogram saved to ${OUTPUT_DIR}/histogram-${TIMESTAMP}.png")
EOF

echo "========================================="
```

### 3.2 Custom Latency Profiler

```c
// rt-latency-profiler.c - Custom latency profiling tool

#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <time.h>
#include <sched.h>
#include <pthread.h>
#include <signal.h>
#include <string.h>
#include <unistd.h>
#include <sys/mman.h>

#define NSEC_PER_SEC    1000000000ULL
#define USEC_PER_SEC    1000000ULL
#define HIST_BUCKETS    1000

typedef struct {
    uint64_t min_latency;
    uint64_t max_latency;
    uint64_t total_latency;
    uint64_t samples;
    uint64_t overruns;
    uint64_t histogram[HIST_BUCKETS];
} latency_stats_t;

static volatile int running = 1;
static latency_stats_t stats = {
    .min_latency = UINT64_MAX,
    .max_latency = 0,
    .total_latency = 0,
    .samples = 0,
    .overruns = 0,
};

/* Get monotonic time in nanoseconds */
static inline uint64_t get_time_ns(void) {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return (uint64_t)ts.tv_sec * NSEC_PER_SEC + ts.tv_nsec;
}

/* Calculate time difference in nanoseconds */
static inline uint64_t timespec_diff_ns(struct timespec *start, struct timespec *end) {
    uint64_t start_ns = (uint64_t)start->tv_sec * NSEC_PER_SEC + start->tv_nsec;
    uint64_t end_ns = (uint64_t)end->tv_sec * NSEC_PER_SEC + end->tv_nsec;
    return end_ns - start_ns;
}

/* Update statistics */
static void update_stats(uint64_t latency_ns) {
    uint64_t latency_us = latency_ns / 1000;

    stats.samples++;
    stats.total_latency += latency_ns;

    if (latency_ns < stats.min_latency)
        stats.min_latency = latency_ns;
    if (latency_ns > stats.max_latency)
        stats.max_latency = latency_ns;

    /* Update histogram */
    if (latency_us < HIST_BUCKETS)
        stats.histogram[latency_us]++;
    else
        stats.histogram[HIST_BUCKETS - 1]++;
}

/* Print statistics */
static void print_stats(void) {
    if (stats.samples == 0)
        return;

    uint64_t avg_ns = stats.total_latency / stats.samples;

    printf("\n========================================\n");
    printf("Latency Statistics\n");
    printf("========================================\n");
    printf("Samples:      %lu\n", stats.samples);
    printf("Min latency:  %lu ns (%.2f us)\n",
           stats.min_latency, stats.min_latency / 1000.0);
    printf("Max latency:  %lu ns (%.2f us)\n",
           stats.max_latency, stats.max_latency / 1000.0);
    printf("Avg latency:  %lu ns (%.2f us)\n",
           avg_ns, avg_ns / 1000.0);
    printf("Overruns:     %lu\n", stats.overruns);

    /* Calculate percentiles from histogram */
    uint64_t p50_count = stats.samples * 50 / 100;
    uint64_t p95_count = stats.samples * 95 / 100;
    uint64_t p99_count = stats.samples * 99 / 100;

    uint64_t cumulative = 0;
    int p50 = -1, p95 = -1, p99 = -1;

    for (int i = 0; i < HIST_BUCKETS; i++) {
        cumulative += stats.histogram[i];
        if (p50 < 0 && cumulative >= p50_count)
            p50 = i;
        if (p95 < 0 && cumulative >= p95_count)
            p95 = i;
        if (p99 < 0 && cumulative >= p99_count)
            p99 = i;
    }

    printf("\nPercentiles:\n");
    printf("  P50: %d us\n", p50);
    printf("  P95: %d us\n", p95);
    printf("  P99: %d us\n", p99);
    printf("========================================\n");
}

/* Signal handler */
static void signal_handler(int sig) {
    running = 0;
}

/* RT periodic task */
static void* rt_periodic_task(void *arg) {
    int period_us = *(int *)arg;
    struct timespec next, now, wake_time;
    uint64_t period_ns = period_us * 1000ULL;

    /* Get initial time */
    clock_gettime(CLOCK_MONOTONIC, &next);

    while (running) {
        /* Calculate next wake time */
        next.tv_nsec += period_ns;
        while (next.tv_nsec >= NSEC_PER_SEC) {
            next.tv_nsec -= NSEC_PER_SEC;
            next.tv_sec++;
        }

        /* Sleep until next period */
        clock_nanosleep(CLOCK_MONOTONIC, TIMER_ABSTIME, &next, NULL);

        /* Measure actual wake time */
        clock_gettime(CLOCK_MONOTONIC, &wake_time);

        /* Calculate latency */
        uint64_t latency_ns = timespec_diff_ns(&next, &wake_time);

        /* Check for overrun */
        if (latency_ns > period_ns) {
            stats.overruns++;
        }

        /* Update statistics */
        update_stats(latency_ns);
    }

    return NULL;
}

int main(int argc, char *argv[]) {
    pthread_t thread;
    pthread_attr_t attr;
    struct sched_param param;
    cpu_set_t cpuset;
    int period_us = 1000;  /* 1ms default */
    int priority = 95;
    int cpu = 2;  /* Isolated CPU */

    /* Parse arguments */
    if (argc > 1)
        period_us = atoi(argv[1]);
    if (argc > 2)
        priority = atoi(argv[2]);
    if (argc > 3)
        cpu = atoi(argv[3]);

    printf("RT Latency Profiler\n");
    printf("Period: %d us\n", period_us);
    printf("Priority: %d\n", priority);
    printf("CPU: %d\n", cpu);

    /* Lock memory */
    if (mlockall(MCL_CURRENT | MCL_FUTURE) != 0) {
        perror("mlockall");
        return 1;
    }

    /* Setup signal handler */
    signal(SIGINT, signal_handler);
    signal(SIGTERM, signal_handler);

    /* Create RT thread */
    pthread_attr_init(&attr);

    /* Set CPU affinity */
    CPU_ZERO(&cpuset);
    CPU_SET(cpu, &cpuset);
    pthread_attr_setaffinity_np(&attr, sizeof(cpuset), &cpuset);

    /* Set scheduling parameters */
    pthread_attr_setschedpolicy(&attr, SCHED_FIFO);
    param.sched_priority = priority;
    pthread_attr_setschedparam(&attr, &param);
    pthread_attr_setinheritsched(&attr, PTHREAD_EXPLICIT_SCHED);

    /* Create thread */
    if (pthread_create(&thread, &attr, rt_periodic_task, &period_us) != 0) {
        perror("pthread_create");
        return 1;
    }

    pthread_attr_destroy(&attr);

    /* Wait for thread */
    pthread_join(thread, NULL);

    /* Print final statistics */
    print_stats();

    return 0;
}
```

---

## 4. Interrupt Handling Optimization

### 4.1 IRQ Threading and Affinity

```bash
#!/bin/bash
# configure-irq-affinity.sh - Optimize IRQ handling for RT

echo "Configuring IRQ affinity for real-time performance..."

# Get list of isolated CPUs (for RT tasks)
RT_CPUS="2,3,4,5"
SYSTEM_CPUS="0,1"  # CPUs for system tasks and IRQs

# Convert CPU list to mask
cpu_list_to_mask() {
    local cpus=$1
    local mask=0
    IFS=',' read -ra CPU_ARRAY <<< "$cpus"
    for cpu in "${CPU_ARRAY[@]}"; do
        mask=$((mask | (1 << cpu)))
    done
    printf "0x%x" $mask
}

SYSTEM_MASK=$(cpu_list_to_mask "$SYSTEM_CPUS")
RT_MASK=$(cpu_list_to_mask "$RT_CPUS")

echo "System CPU mask: $SYSTEM_MASK"
echo "RT CPU mask: $RT_MASK"

# Move all IRQs to system CPUs
for irq in /proc/irq/*/smp_affinity; do
    echo "$SYSTEM_MASK" > "$irq" 2>/dev/null
done

# Configure specific high-priority IRQs
# GPU IRQs - keep on system CPUs
for irq in $(grep "nvidia" /proc/interrupts | awk '{print $1}' | tr -d ':'); do
    echo "$SYSTEM_MASK" > "/proc/irq/$irq/smp_affinity"
    echo "GPU IRQ $irq -> CPUs $SYSTEM_CPUS"
done

# Network IRQs - distribute based on importance
for irq in $(grep "eth0" /proc/interrupts | awk '{print $1}' | tr -d ':'); do
    echo "$SYSTEM_MASK" > "/proc/irq/$irq/smp_affinity"
    echo "Network IRQ $irq -> CPUs $SYSTEM_CPUS"
done

# Timer IRQs - critical, keep on system CPUs
for irq in $(grep "timer" /proc/interrupts | awk '{print $1}' | tr -d ':'); do
    echo "$SYSTEM_MASK" > "/proc/irq/$irq/smp_affinity"
    echo "Timer IRQ $irq -> CPUs $SYSTEM_CPUS"
done

# Threaded IRQs - adjust priorities
for irq_thread in $(ps aux | grep '\[irq/' | awk '{print $2}'); do
    # Lower priority for threaded IRQs on system CPUs
    chrt -f -p 50 "$irq_thread" 2>/dev/null
done

echo "IRQ affinity configuration complete"
```

### 4.2 Custom IRQ Handler Example

```c
// custom-rt-driver.c - RT-optimized driver with IRQ handling

#include <linux/module.h>
#include <linux/interrupt.h>
#include <linux/platform_device.h>
#include <linux/of.h>
#include <linux/kthread.h>
#include <linux/sched.h>
#include <linux/sched/rt.h>

#define DRIVER_NAME "custom-rt-device"

struct custom_rt_device {
    void __iomem *base;
    int irq;
    struct task_struct *irq_thread;
    wait_queue_head_t wait_queue;
    atomic_t data_ready;
    spinlock_t lock;
    u32 *buffer;
    size_t buffer_size;
};

/* Hard IRQ handler - minimal work */
static irqreturn_t custom_rt_irq_handler(int irq, void *dev_id)
{
    struct custom_rt_device *dev = dev_id;
    unsigned long flags;

    /* Quick check and signal */
    spin_lock_irqsave(&dev->lock, flags);

    /* Read hardware status register */
    u32 status = readl(dev->base + 0x04);

    if (status & 0x01) {
        /* Data available */
        atomic_set(&dev->data_ready, 1);
        wake_up(&dev->wait_queue);

        /* Clear interrupt */
        writel(status, dev->base + 0x04);

        spin_unlock_irqrestore(&dev->lock, flags);
        return IRQ_HANDLED;
    }

    spin_unlock_irqrestore(&dev->lock, flags);
    return IRQ_NONE;
}

/* RT kernel thread for IRQ processing */
static int custom_rt_irq_thread(void *data)
{
    struct custom_rt_device *dev = data;
    struct sched_param param = { .sched_priority = 90 };

    /* Set real-time priority */
    sched_setscheduler(current, SCHED_FIFO, &param);

    while (!kthread_should_stop()) {
        /* Wait for interrupt signal */
        wait_event_interruptible(dev->wait_queue,
                                 atomic_read(&dev->data_ready) ||
                                 kthread_should_stop());

        if (kthread_should_stop())
            break;

        if (atomic_read(&dev->data_ready)) {
            /* Process interrupt data */
            unsigned long flags;
            spin_lock_irqsave(&dev->lock, flags);

            /* Read data from device */
            for (int i = 0; i < dev->buffer_size / 4; i++) {
                dev->buffer[i] = readl(dev->base + 0x100 + i * 4);
            }

            atomic_set(&dev->data_ready, 0);
            spin_unlock_irqrestore(&dev->lock, flags);

            /* Further processing here */
            // ...
        }
    }

    return 0;
}

static int custom_rt_probe(struct platform_device *pdev)
{
    struct custom_rt_device *dev;
    struct resource *res;
    int ret;

    dev = devm_kzalloc(&pdev->dev, sizeof(*dev), GFP_KERNEL);
    if (!dev)
        return -ENOMEM;

    /* Get hardware resources */
    res = platform_get_resource(pdev, IORESOURCE_MEM, 0);
    dev->base = devm_ioremap_resource(&pdev->dev, res);
    if (IS_ERR(dev->base))
        return PTR_ERR(dev->base);

    dev->irq = platform_get_irq(pdev, 0);
    if (dev->irq < 0)
        return dev->irq;

    /* Initialize */
    spin_lock_init(&dev->lock);
    init_waitqueue_head(&dev->wait_queue);
    atomic_set(&dev->data_ready, 0);

    /* Allocate buffer */
    dev->buffer_size = 4096;
    dev->buffer = devm_kmalloc(&pdev->dev, dev->buffer_size, GFP_KERNEL);
    if (!dev->buffer)
        return -ENOMEM;

    /* Create RT thread */
    dev->irq_thread = kthread_create(custom_rt_irq_thread, dev,
                                     "custom-rt-irq");
    if (IS_ERR(dev->irq_thread)) {
        dev_err(&pdev->dev, "Failed to create IRQ thread\n");
        return PTR_ERR(dev->irq_thread);
    }

    /* Bind thread to specific CPU (isolated CPU 2) */
    kthread_bind(dev->irq_thread, 2);

    /* Request IRQ */
    ret = devm_request_irq(&pdev->dev, dev->irq,
                          custom_rt_irq_handler,
                          IRQF_TRIGGER_HIGH,
                          DRIVER_NAME, dev);
    if (ret) {
        dev_err(&pdev->dev, "Failed to request IRQ\n");
        kthread_stop(dev->irq_thread);
        return ret;
    }

    /* Start IRQ thread */
    wake_up_process(dev->irq_thread);

    platform_set_drvdata(pdev, dev);

    dev_info(&pdev->dev, "RT device initialized\n");
    return 0;
}

static int custom_rt_remove(struct platform_device *pdev)
{
    struct custom_rt_device *dev = platform_get_drvdata(pdev);

    if (dev->irq_thread) {
        kthread_stop(dev->irq_thread);
    }

    return 0;
}

static const struct of_device_id custom_rt_of_match[] = {
    { .compatible = "custom,rt-device", },
    { }
};
MODULE_DEVICE_TABLE(of, custom_rt_of_match);

static struct platform_driver custom_rt_driver = {
    .driver = {
        .name = DRIVER_NAME,
        .of_match_table = custom_rt_of_match,
    },
    .probe = custom_rt_probe,
    .remove = custom_rt_remove,
};

module_platform_driver(custom_rt_driver);

MODULE_AUTHOR("Your Name");
MODULE_DESCRIPTION("RT-optimized device driver");
MODULE_LICENSE("GPL v2");
```

---

## 5. Deterministic Behavior

### 5.1 System Configuration for Determinism

```python
# recipes-core/systemd/systemd-rt-config_1.0.bb

SUMMARY = "Systemd configuration for RT systems"
LICENSE = "MIT"

SRC_URI = " \
    file://rt-system.conf \
    file://disable-services.sh \
"

S = "${WORKDIR}"

do_install() {
    # Systemd configuration
    install -d ${D}${sysconfdir}/systemd/system.conf.d
    install -m 0644 rt-system.conf ${D}${sysconfdir}/systemd/system.conf.d/

    # Service management script
    install -d ${D}${sbindir}
    install -m 0755 disable-services.sh ${D}${sbindir}/rt-configure-services
}

FILES:${PN} = " \
    ${sysconfdir}/systemd/system.conf.d/rt-system.conf \
    ${sbindir}/rt-configure-services \
"
```

```ini
# rt-system.conf - Systemd configuration for RT
[Manager]
# CPU affinity for systemd and spawned services
CPUAffinity=0 1

# Limit number of tasks
DefaultTasksMax=512

# Resource limits
DefaultLimitCPU=infinity
DefaultLimitNOFILE=65536
DefaultLimitMEMLOCK=infinity

# Timeouts
DefaultTimeoutStartSec=10s
DefaultTimeoutStopSec=10s
```

```bash
# disable-services.sh - Disable non-essential services
#!/bin/bash

# Services to disable for RT systems
DISABLE_SERVICES="
    avahi-daemon
    bluetooth
    cups
    ModemManager
    wpa_supplicant
    packagekit
"

echo "Disabling non-essential services for RT..."

for service in $DISABLE_SERVICES; do
    if systemctl is-enabled "$service" 2>/dev/null; then
        systemctl disable "$service"
        systemctl stop "$service"
        echo "Disabled: $service"
    fi
done

# Mask unwanted targets
systemctl mask sleep.target suspend.target hibernate.target hybrid-sleep.target

echo "Service configuration complete"
```

### 5.2 RT Application Template

```c
// rt-application-template.c - Template for RT applications

#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <unistd.h>
#include <sched.h>
#include <pthread.h>
#include <signal.h>
#include <time.h>
#include <sys/mman.h>
#include <sys/resource.h>
#include <errno.h>

#define NSEC_PER_SEC 1000000000ULL
#define STACK_SIZE (8 * 1024 * 1024)

/* RT task configuration */
typedef struct {
    pthread_t thread;
    int cpu;
    int priority;
    uint64_t period_ns;
    void (*task_func)(void *);
    void *task_arg;
    volatile int running;
} rt_task_t;

/* Initialize RT environment */
static int init_rt_environment(void) {
    struct rlimit rlim;

    /* Lock all memory */
    if (mlockall(MCL_CURRENT | MCL_FUTURE) != 0) {
        perror("mlockall");
        return -1;
    }

    /* Set unlimited stack */
    rlim.rlim_cur = RLIM_INFINITY;
    rlim.rlim_max = RLIM_INFINITY;
    setrlimit(RLIMIT_STACK, &rlim);
    setrlimit(RLIMIT_MEMLOCK, &rlim);

    /* Disable malloc trimming */
    mallopt(M_TRIM_THRESHOLD, -1);
    mallopt(M_MMAP_MAX, 0);

    return 0;
}

/* RT task thread function */
static void* rt_task_thread(void *arg) {
    rt_task_t *task = (rt_task_t *)arg;
    struct timespec next;
    cpu_set_t cpuset;
    struct sched_param param;

    /* Set CPU affinity */
    CPU_ZERO(&cpuset);
    CPU_SET(task->cpu, &cpuset);
    if (pthread_setaffinity_np(pthread_self(), sizeof(cpuset), &cpuset) != 0) {
        perror("pthread_setaffinity_np");
        return NULL;
    }

    /* Set RT priority */
    param.sched_priority = task->priority;
    if (pthread_setschedparam(pthread_self(), SCHED_FIFO, &param) != 0) {
        perror("pthread_setschedparam");
        return NULL;
    }

    /* Pre-fault stack */
    unsigned char stack[STACK_SIZE];
    memset(stack, 0, STACK_SIZE);

    /* Get start time */
    clock_gettime(CLOCK_MONOTONIC, &next);

    printf("RT task started on CPU %d with priority %d\n",
           sched_getcpu(), task->priority);

    /* Periodic execution loop */
    while (task->running) {
        /* Execute task function */
        if (task->task_func) {
            task->task_func(task->task_arg);
        }

        /* Calculate next period */
        next.tv_nsec += task->period_ns;
        while (next.tv_nsec >= NSEC_PER_SEC) {
            next.tv_nsec -= NSEC_PER_SEC;
            next.tv_sec++;
        }

        /* Wait for next period */
        clock_nanosleep(CLOCK_MONOTONIC, TIMER_ABSTIME, &next, NULL);
    }

    return NULL;
}

/* Create RT task */
static int create_rt_task(rt_task_t *task) {
    pthread_attr_t attr;

    pthread_attr_init(&attr);
    pthread_attr_setstacksize(&attr, STACK_SIZE);
    pthread_attr_setschedpolicy(&attr, SCHED_FIFO);

    struct sched_param param;
    param.sched_priority = task->priority;
    pthread_attr_setschedparam(&attr, &param);
    pthread_attr_setinheritsched(&attr, PTHREAD_EXPLICIT_SCHED);

    task->running = 1;

    int ret = pthread_create(&task->thread, &attr, rt_task_thread, task);
    pthread_attr_destroy(&attr);

    return ret;
}

/* Example task functions */
static void control_loop_task(void *arg) {
    /* High-frequency control loop (1kHz) */
    static uint64_t counter = 0;
    counter++;

    /* Read sensors */
    // ...

    /* Compute control output */
    // ...

    /* Write actuators */
    // ...

    if (counter % 1000 == 0) {
        printf("Control loop: %lu iterations\n", counter);
    }
}

static void data_processing_task(void *arg) {
    /* Medium-frequency data processing (100Hz) */
    /* Process sensor data, filtering, etc. */
    // ...
}

static void communication_task(void *arg) {
    /* Low-frequency communication (10Hz) */
    /* Send telemetry, receive commands, etc. */
    // ...
}

/* Signal handler */
static volatile int shutdown_requested = 0;

static void signal_handler(int sig) {
    shutdown_requested = 1;
}

/* Main application */
int main(int argc, char *argv[]) {
    rt_task_t tasks[3];

    /* Initialize RT environment */
    if (init_rt_environment() != 0) {
        fprintf(stderr, "Failed to initialize RT environment\n");
        return 1;
    }

    /* Setup signal handlers */
    signal(SIGINT, signal_handler);
    signal(SIGTERM, signal_handler);

    /* Configure RT tasks */
    /* Task 1: High-priority control loop - 1kHz */
    tasks[0] = (rt_task_t){
        .cpu = 2,
        .priority = 95,
        .period_ns = 1000000,  /* 1ms */
        .task_func = control_loop_task,
        .task_arg = NULL,
    };

    /* Task 2: Medium-priority data processing - 100Hz */
    tasks[1] = (rt_task_t){
        .cpu = 3,
        .priority = 80,
        .period_ns = 10000000,  /* 10ms */
        .task_func = data_processing_task,
        .task_arg = NULL,
    };

    /* Task 3: Low-priority communication - 10Hz */
    tasks[2] = (rt_task_t){
        .cpu = 4,
        .priority = 60,
        .period_ns = 100000000,  /* 100ms */
        .task_func = communication_task,
        .task_arg = NULL,
    };

    /* Create all tasks */
    for (int i = 0; i < 3; i++) {
        if (create_rt_task(&tasks[i]) != 0) {
            fprintf(stderr, "Failed to create task %d\n", i);
            return 1;
        }
    }

    printf("RT application running. Press Ctrl+C to stop.\n");

    /* Wait for shutdown signal */
    while (!shutdown_requested) {
        sleep(1);
    }

    printf("\nShutting down...\n");

    /* Stop all tasks */
    for (int i = 0; i < 3; i++) {
        tasks[i].running = 0;
        pthread_join(tasks[i].thread, NULL);
    }

    printf("Shutdown complete.\n");
    return 0;
}
```

---

## 6. Case Study: Industrial Motion Control System

**Application**: 6-axis robot arm control system

**Requirements**:
- 1kHz control loop frequency
- < 50μs maximum latency
- Jitter < 10μs
- Safety-critical (SIL-2)

**System Configuration**:
- Jetson Xavier NX with RT-PREEMPT kernel
- CPU isolation: CPUs 2-5 for RT tasks
- Control loop on CPU 2 (priority 99)
- Sensor fusion on CPU 3 (priority 90)
- Communication on CPUs 0-1 (non-RT)

**Results**:
- Achieved latency: P99 = 28μs, Max = 45μs
- Jitter: < 5μs
- Control loop frequency: 1000 Hz ± 0.1%
- System uptime: > 10,000 hours without RT violations

**Key Optimizations**:
1. RT-PREEMPT kernel with custom configuration
2. CPU and IRQ isolation
3. Memory locking and pre-allocation
4. Optimized device drivers with threaded IRQs
5. Systemd service affinity configuration

---

## Best Practices Summary

1. **Always use RT-PREEMPT**: Stock kernel is insufficient for hard RT
2. **Isolate CPUs**: Dedicate CPUs to RT tasks
3. **Lock Memory**: Prevent page faults during RT execution
4. **Measure Everything**: Use cyclictest and custom profiling
5. **Optimize IRQs**: Thread IRQs and set proper affinity
6. **Test Under Load**: Stress test with worst-case scenarios
7. **Document Guarantees**: Clearly specify RT guarantees
8. **Plan for Failures**: Implement watchdogs and safety mechanisms

---

**Next Steps**: Proceed to [Debugging Techniques](05-debugging-techniques.md) for advanced debugging and profiling.
