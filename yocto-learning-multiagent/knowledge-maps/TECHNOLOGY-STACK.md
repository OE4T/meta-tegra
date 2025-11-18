# Technology Stack
## Yocto & Meta-Tegra Learning System

Complete technology stack overview including version compatibility, tool dependencies, and alternative approaches for Jetson-based development.

---

## Technology Stack Overview

```mermaid
graph TB
    subgraph "Layer 1: Build System"
        L1A[Yocto Project]
        L1B[BitBake]
        L1C[OpenEmbedded-Core]
    end

    subgraph "Layer 2: BSP"
        L2A[meta-tegra]
        L2B[NVIDIA L4T]
        L2C[Device Trees]
    end

    subgraph "Layer 3: Kernel"
        L3A[Linux Kernel]
        L3B[Tegra Drivers]
        L3C[Kernel Modules]
    end

    subgraph "Layer 4: System Libraries"
        L4A[glibc/musl]
        L4B[CUDA Runtime]
        L4C[TensorRT]
        L4D[tegra-libraries]
    end

    subgraph "Layer 5: Frameworks"
        L5A[GStreamer]
        L5B[DeepStream]
        L5C[VPI]
        L5D[Multimedia API]
    end

    subgraph "Layer 6: Applications"
        L6A[AI Models]
        L6B[Vision Apps]
        L6C[Robotics Stack]
        L6D[System Services]
    end

    subgraph "Layer 7: Tools"
        L7A[SDK Manager]
        L7B[Nsight Systems]
        L7C[Development Tools]
    end

    L1A --> L1B
    L1B --> L1C
    L1C --> L2A

    L2A --> L2B
    L2B --> L2C
    L2C --> L3A

    L3A --> L3B
    L3B --> L3C
    L3C --> L4A

    L4A --> L4B
    L4B --> L4C
    L4C --> L4D
    L4D --> L5A

    L5A --> L5B
    L5B --> L5C
    L5C --> L5D
    L5D --> L6A

    L6A --> L6B
    L6B --> L6C
    L6C --> L6D

    L7A -.-> L2A
    L7B -.-> L6A
    L7C -.-> L1A

    style L1A fill:#3498db
    style L2A fill:#76b900
    style L3A fill:#2ecc71
    style L4C fill:#ff6600
    style L5B fill:#e74c3c
    style L6A fill:#9b59b6
```

---

## Version Compatibility Matrix

### Yocto Release Compatibility

| Yocto Release | Codename | Release Date | meta-tegra Branch | Support Status | EOL Date |
|---------------|----------|--------------|-------------------|----------------|----------|
| 5.0 | Scarthgap | May 2024 | scarthgap | Current LTS | Apr 2028 |
| 4.3 | Nanbield | Oct 2023 | nanbield | Stable | Oct 2025 |
| 4.2 | Mickledore | May 2023 | mickledore | Stable | May 2024 |
| 4.0 | Kirkstone | May 2022 | kirkstone | LTS | Apr 2026 |
| 3.4 | Honister | Oct 2021 | honister | Maintenance | Apr 2024 |
| 3.3 | Hardknott | Apr 2021 | hardknott | EOL | Oct 2022 |

**Recommended**: Kirkstone (LTS) or Scarthgap (Latest LTS)

---

### JetPack & L4T Version Matrix

| JetPack | L4T Version | Jetson Modules | Linux Kernel | CUDA | TensorRT | Release Date |
|---------|-------------|----------------|--------------|------|----------|--------------|
| 6.1 | 36.4.0 | Orin, AGX Orin | 5.15 | 12.6 | 10.3 | Q2 2024 |
| 6.0 | 36.3.0 | Orin, AGX Orin | 5.15 | 12.2 | 8.6 | Q4 2023 |
| 5.1.3 | 35.5.0 | Orin, Xavier | 5.10 | 11.4 | 8.5 | Q3 2023 |
| 5.1.2 | 35.4.1 | Orin, Xavier | 5.10 | 11.4 | 8.5 | Q2 2023 |
| 5.1.1 | 35.3.1 | Orin, Xavier | 5.10 | 11.4 | 8.5 | Q1 2023 |
| 5.1 | 35.2.1 | Orin, Xavier | 5.10 | 11.4 | 8.5 | Q4 2022 |
| 5.0.2 | 35.1.0 | Orin, Xavier | 5.10 | 11.4 | 8.4 | Q3 2022 |
| 4.6.4 | 32.7.4 | Xavier, Nano, TX2 | 4.9 | 10.2 | 8.2 | Q3 2022 |
| 4.6 | 32.6.1 | Xavier, Nano, TX2 | 4.9 | 10.2 | 8.0 | Q1 2021 |

**Current Production Recommendation**: JetPack 5.1.3 (L4T 35.5.0) or JetPack 6.1

---

### meta-tegra Version Compatibility

| meta-tegra Branch | Yocto Release | JetPack/L4T | Supported Modules | Status |
|-------------------|---------------|-------------|-------------------|--------|
| scarthgap | 5.0 | JP 6.1 (36.4) | Orin, AGX Orin | Active |
| nanbield | 4.3 | JP 6.0 (36.3) | Orin, AGX Orin, Xavier | Active |
| mickledore | 4.2 | JP 5.1.3 (35.5) | Orin, Xavier, Nano | Active |
| kirkstone | 4.0 LTS | JP 5.1.2 (35.4) | Orin, Xavier, Nano | LTS |
| honister | 3.4 | JP 4.6.4 (32.7) | Xavier, Nano, TX2 | Maintenance |

---

## Core Technology Versions

### Build System Stack

```mermaid
graph LR
    subgraph "Build Host Requirements"
        H1[Ubuntu 22.04 LTS]
        H2[Python 3.8+]
        H3[Git 2.25+]
        H4[GCC 11.x]
    end

    subgraph "Yocto Components"
        Y1[BitBake 2.0+]
        Y2[OE-Core]
        Y3[Poky 4.0+]
    end

    subgraph "Meta Layers"
        M1[meta-openembedded]
        M2[meta-tegra]
        M3[meta-virtualization]
    end

    H1 --> H2
    H2 --> H3
    H3 --> H4
    H4 --> Y1

    Y1 --> Y2
    Y2 --> Y3
    Y3 --> M1

    M1 --> M2
    M2 --> M3

    style H1 fill:#e1f5e1
    style Y1 fill:#3498db
    style M2 fill:#76b900
```

#### Detailed Versions

**Build Host Operating Systems**:
- **Recommended**: Ubuntu 22.04 LTS
- **Supported**: Ubuntu 20.04 LTS, Debian 11, Fedora 38+
- **Minimum Requirements**:
  - 8GB RAM (16GB+ recommended)
  - 200GB free disk space
  - 4+ CPU cores

**Python Environment**:
- Python: 3.8, 3.9, 3.10, 3.11
- pip: 21.0+
- Required packages: `python3-pip python3-pexpect python3-git python3-jinja2`

**Toolchain**:
- GCC: 11.x, 12.x (cross-compiler)
- Binutils: 2.38+
- glibc: 2.35+ (for target)
- musl: 1.2.3+ (alternative to glibc)

**Build Tools**:
- Git: 2.25+
- tar: 1.28+
- chrpath: 0.16+
- diffstat: 1.64+
- kas: 3.0+ (optional, for project setup)
- repo: 2.29+ (optional, for manifest)

---

### Kernel & Driver Stack

```mermaid
graph TB
    subgraph "Kernel Components"
        K1[Linux Kernel 5.15 LTS]
        K2[NVIDIA Patches]
        K3[RT-PREEMPT Optional]
    end

    subgraph "Core Drivers"
        D1[Tegra GPIO]
        D2[Tegra I2C]
        D3[Tegra SPI]
        D4[Tegra PCIe]
    end

    subgraph "Graphics & Display"
        G1[NVIDIA Display Driver]
        G2[DRM/KMS]
        G3[Frame Buffer]
    end

    subgraph "Multimedia"
        MM1[V4L2 Camera]
        MM2[NVDEC/NVENC]
        MM3[VIC]
    end

    K1 --> K2
    K2 --> K3
    K2 --> D1

    D1 --> D2
    D2 --> D3
    D3 --> D4

    K2 --> G1
    G1 --> G2
    G2 --> G3

    K2 --> MM1
    MM1 --> MM2
    MM2 --> MM3

    style K1 fill:#2ecc71
    style G1 fill:#76b900
    style MM1 fill:#00b4d8
```

#### Kernel Versions

**Linux Kernel**:
- **JetPack 6.x**: Linux 5.15 LTS
- **JetPack 5.x**: Linux 5.10 LTS
- **JetPack 4.6**: Linux 4.9 LTS
- **RT-PREEMPT**: Patches available for 5.10, 5.15

**Key Kernel Configurations**:
- CONFIG_TEGRA_HOST1X
- CONFIG_TEGRA_FUSE
- CONFIG_TEGRA_GPIO
- CONFIG_TEGRA_IOMMU_SMMU
- CONFIG_TEGRA_MC
- CONFIG_DRM_TEGRA

---

### NVIDIA Software Stack

```mermaid
graph LR
    subgraph "CUDA Ecosystem"
        C1[CUDA Toolkit 12.6]
        C2[cuDNN 8.9]
        C3[cuBLAS]
        C4[cuSPARSE]
    end

    subgraph "Inference"
        I1[TensorRT 10.3]
        I2[ONNX Runtime 1.17]
        I3[Triton Server 2.46]
    end

    subgraph "Vision"
        V1[VPI 3.2]
        V2[DeepStream 7.0]
        V3[OpenCV 4.8 CUDA]
    end

    subgraph "Multimedia"
        M1[Multimedia API]
        M2[GStreamer 1.20]
        M3[V4L2 Codec]
    end

    C1 --> C2
    C2 --> C3
    C3 --> C4

    C1 --> I1
    I1 --> I2
    I2 --> I3

    C1 --> V1
    V1 --> V2
    V2 --> V3

    M1 --> M2
    M2 --> M3
    C1 --> M1

    style C1 fill:#76b900
    style I1 fill:#ff6600
    style V2 fill:#00b4d8
```

#### CUDA & Deep Learning

**CUDA Versions by JetPack**:
| JetPack | CUDA | cuDNN | TensorRT | DeepStream |
|---------|------|-------|----------|------------|
| 6.1 | 12.6 | 8.9 | 10.3 | 7.0 |
| 6.0 | 12.2 | 8.9 | 8.6 | 6.4 |
| 5.1.3 | 11.4 | 8.6 | 8.5.2 | 6.3 |
| 5.1 | 11.4 | 8.6 | 8.5.1 | 6.2 |
| 5.0.2 | 11.4 | 8.6 | 8.4.1 | 6.1 |

**CUDA Compute Capabilities**:
- Jetson Orin: SM 8.7 (Ampere)
- Jetson AGX Orin: SM 8.7 (Ampere)
- Jetson AGX Xavier: SM 7.2 (Volta)
- Jetson Xavier NX: SM 7.2 (Volta)
- Jetson Nano: SM 5.3 (Maxwell)

**TensorRT Supported Frameworks**:
- PyTorch: 2.0, 2.1, 2.2
- TensorFlow: 2.12, 2.13, 2.14
- ONNX: 1.14, 1.15, 1.16
- Caffe: 1.0
- MXNet: 1.9

---

### Multimedia & Vision Stack

```mermaid
graph TB
    subgraph "GStreamer Plugins"
        G1[gst-plugins-base]
        G2[gst-plugins-good]
        G3[gst-plugins-bad]
        G4[gst-plugins-ugly]
        G5[gst-nvvideo4linux2]
        G6[gst-nvarguscamera]
    end

    subgraph "DeepStream Components"
        D1[Gst-nvinfer]
        D2[Gst-nvtracker]
        D3[Gst-nvmultistreamtiler]
        D4[Gst-nvdsosd]
    end

    subgraph "VPI Modules"
        V1[Image Processing]
        V2[Computer Vision]
        V3[Stereo Disparity]
        V4[KLT Tracker]
    end

    G1 --> G2
    G2 --> G3
    G3 --> G4
    G4 --> G5
    G5 --> G6

    G6 --> D1
    D1 --> D2
    D2 --> D3
    D3 --> D4

    V1 --> V2
    V2 --> V3
    V3 --> V4

    style G6 fill:#76b900
    style D1 fill:#ff6600
    style V1 fill:#00b4d8
```

#### Version Details

**GStreamer Stack**:
- GStreamer Core: 1.20.3 (JetPack 6.x), 1.16.3 (JetPack 5.x)
- gst-plugins-base: 1.20.3
- gst-plugins-good: 1.20.3
- gst-plugins-bad: 1.20.3
- gst-plugins-ugly: 1.20.3
- gst-libav: 1.20.3

**NVIDIA GStreamer Plugins**:
- nvvideoconvert: Hardware-accelerated format conversion
- nvv4l2decoder/encoder: V4L2-based codec
- nvarguscamera: Argus camera source
- nvvideoconvert: Color space conversion
- nvdsosd: On-screen display

**DeepStream SDK**:
- Version: 7.0 (JP 6.1), 6.3 (JP 5.1.3)
- Triton Integration: Yes
- Multi-stream: Up to 32 streams (hardware dependent)
- Tracking: NvDCF, DeepSORT, IOU

**VPI (Vision Programming Interface)**:
- Version: 3.2 (JP 6.1), 2.3 (JP 5.1)
- Backends: CPU, GPU, PVA (Programmable Vision Accelerator), VIC
- Algorithms: 30+ (filtering, transforms, feature detection, etc.)

**OpenCV**:
- Version: 4.8.0 (with CUDA), 4.5.4 (JetPack 5.x)
- CUDA Acceleration: Yes
- DNN Module: Yes (supports TensorFlow, PyTorch, ONNX)

---

### Container & Runtime Stack

```mermaid
graph LR
    subgraph "Container Runtime"
        CR1[Docker 24.0]
        CR2[containerd 1.7]
        CR3[nvidia-container-runtime]
    end

    subgraph "Orchestration"
        O1[Docker Compose 2.20]
        O2[Kubernetes 1.28]
        O3[K3s 1.28]
    end

    subgraph "Base Images"
        B1[l4t-base]
        B2[l4t-cuda]
        B3[l4t-tensorrt]
        B4[l4t-ml]
    end

    CR1 --> CR2
    CR2 --> CR3
    CR3 --> O1

    O1 --> O2
    O2 --> O3

    CR3 --> B1
    B1 --> B2
    B2 --> B3
    B3 --> B4

    style CR3 fill:#76b900
    style O3 fill:#3498db
    style B4 fill:#ff6600
```

#### Container Technologies

**Docker & Runtime**:
- Docker: 24.0.5+ (with buildx)
- containerd: 1.7.2+
- nvidia-container-runtime: 3.13.0+
- nvidia-docker: 2.13.0+

**NVIDIA Container Images**:
- l4t-base: Minimal L4T base
- l4t-cuda: Base + CUDA toolkit
- l4t-tensorrt: CUDA + TensorRT
- l4t-ml: Full ML stack (PyTorch, TensorFlow)
- l4t-pytorch: PyTorch optimized
- l4t-tensorflow: TensorFlow optimized

**Registry**:
- nvcr.io/nvidia/l4t-base
- nvcr.io/nvidia/l4t-cuda
- nvcr.io/nvidia/l4t-tensorrt
- nvcr.io/nvidia/l4t-ml

**Kubernetes**:
- K8s: 1.28, 1.27, 1.26
- K3s: 1.28 (lightweight)
- NVIDIA Device Plugin: 0.14.0

---

### Development Tools

```mermaid
graph TB
    subgraph "NVIDIA Tools"
        N1[SDK Manager]
        N2[Nsight Systems]
        N3[Nsight Compute]
        N4[Nsight Graphics]
    end

    subgraph "Debugging"
        D1[GDB 12.1]
        D2[Valgrind 3.19]
        D3[perf]
        D4[ftrace]
    end

    subgraph "Profiling"
        P1[perf]
        P2[oprofile]
        P3[gperftools]
        P4[CUDA Profiler]
    end

    subgraph "Analysis"
        A1[Nsight Systems]
        A2[tegrastats]
        A3[jetson_clocks]
        A4[nvpmodel]
    end

    N1 --> N2
    N2 --> N3
    N3 --> N4

    D1 --> D2
    D2 --> D3
    D3 --> D4

    P1 --> P2
    P2 --> P3
    P3 --> P4

    A1 --> A2
    A2 --> A3
    A3 --> A4

    N2 --> A1
    P4 --> N3

    style N2 fill:#76b900
    style D1 fill:#3498db
    style P1 fill:#e74c3c
```

#### Tool Versions

**NVIDIA Development Tools**:
- SDK Manager: 2.1.0
- Nsight Systems: 2024.5
- Nsight Compute: 2024.3
- Nsight Graphics: 2024.4
- Visual Profiler: 11.8 (deprecated, use Nsight)

**Cross-Compilation**:
- GCC ARM64: 11.4, 12.3, 13.1
- Clang: 15.0, 16.0, 17.0
- CMake: 3.22+
- Meson: 0.63+

**Debugging & Profiling**:
- GDB: 12.1
- Valgrind: 3.19
- perf: 5.15
- strace: 5.16
- ltrace: 0.7.3

**Jetson Utilities**:
- tegrastats: Built-in system monitor
- jetson_clocks: Performance mode
- nvpmodel: Power model selection
- jtop: Python-based monitoring (unofficial)

---

## Alternative Technology Approaches

### Build System Alternatives

```mermaid
graph LR
    subgraph "Yocto/OE"
        Y1[Full Control]
        Y2[Reproducible]
        Y3[Long Build]
    end

    subgraph "Buildroot"
        B1[Simpler]
        B2[Faster Build]
        B3[Less Flexible]
    end

    subgraph "Ubuntu Base"
        U1[Familiar]
        U2[Quick Start]
        U3[Less Custom]
    end

    subgraph "Containers"
        C1[Portable]
        C2[Easy Deploy]
        C3[Runtime Overhead]
    end

    Y1 -.->|"vs"| B1
    Y2 -.->|"vs"| U1
    Y3 -.->|"vs"| C1

    style Y1 fill:#3498db
    style B1 fill:#e74c3c
    style U1 fill:#2ecc71
    style C1 fill:#f39c12
```

#### Comparison Matrix

| Aspect | Yocto/OE | Buildroot | Ubuntu Base | Container-only |
|--------|----------|-----------|-------------|----------------|
| **Flexibility** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐ |
| **Build Time** | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Customization** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐ |
| **Learning Curve** | ⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Reproducibility** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Image Size** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐ |
| **OTA Updates** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Production Ready** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |

**Recommendation**:
- **Production/Custom**: Yocto/OE with meta-tegra
- **Quick Prototyping**: Ubuntu Base + Docker
- **Small Footprint**: Buildroot (if meta-tegra unavailable)
- **Cloud-Native**: Container-based deployment

---

### AI Framework Alternatives

```mermaid
graph TB
    subgraph "TensorRT Native"
        T1[Best Performance]
        T2[Low Latency]
        T3[Complex Setup]
    end

    subgraph "ONNX Runtime"
        O1[Framework Agnostic]
        O2[Good Performance]
        O3[Easy Integration]
    end

    subgraph "PyTorch/TF Direct"
        P1[Familiar API]
        P2[Slower Inference]
        P3[Easier Debug]
    end

    subgraph "Triton Server"
        TR1[Multi-Model]
        TR2[HTTP/gRPC API]
        TR3[Resource Overhead]
    end

    T1 -.->|"vs"| O1
    T2 -.->|"vs"| P1
    T3 -.->|"vs"| TR1

    style T1 fill:#76b900
    style O1 fill:#3498db
    style P1 fill:#e74c3c
    style TR1 fill:#f39c12
```

#### AI Inference Comparison

| Framework | Throughput | Latency | Flexibility | Ease of Use | Production |
|-----------|------------|---------|-------------|-------------|------------|
| **TensorRT** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| **ONNX Runtime** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **PyTorch JIT** | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |
| **TensorFlow Lite** | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **Triton Server** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

**Use Cases**:
- **Maximum Performance**: TensorRT with INT8
- **Multi-Framework**: ONNX Runtime
- **Rapid Development**: PyTorch/TensorFlow direct
- **Microservices**: Triton Inference Server
- **Edge Devices**: TensorFlow Lite or TensorRT

---

### Vision Pipeline Alternatives

```mermaid
graph LR
    subgraph "DeepStream"
        D1[Multi-Stream]
        D2[HW Accelerated]
        D3[Learning Curve]
    end

    subgraph "GStreamer Custom"
        G1[Flexible]
        G2[Custom Plugins]
        G3[More Code]
    end

    subgraph "OpenCV + CUDA"
        O1[Familiar API]
        O2[CPU/GPU Hybrid]
        O3[Good Performance]
    end

    subgraph "VPI"
        V1[Optimized]
        V2[Limited Algos]
        V3[Easy to Use]
    end

    D1 -.->|"vs"| G1
    D2 -.->|"vs"| O1
    D3 -.->|"vs"| V1

    style D1 fill:#76b900
    style G1 fill:#3498db
    style O1 fill:#e74c3c
    style V1 fill:#f39c12
```

#### Vision Framework Comparison

| Framework | Performance | Flexibility | Complexity | Multi-Stream | HW Accel |
|-----------|-------------|-------------|------------|--------------|----------|
| **DeepStream** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **GStreamer** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **OpenCV CUDA** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐ |
| **VPI** | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐ | ⭐⭐⭐⭐⭐ |
| **Custom CUDA** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## Dependency Tree

### Critical Path Dependencies

```mermaid
graph TB
    START[Project Start]

    START --> HOST[Build Host Setup]
    HOST --> YOCTO[Yocto Environment]
    YOCTO --> LAYERS[Meta Layers]
    LAYERS --> BUILD[Image Build]
    BUILD --> FLASH[Flash Device]
    FLASH --> DEV[Development]

    HOST -.->|Requires| H1[Ubuntu 22.04]
    HOST -.->|Requires| H2[Python 3.8+]
    HOST -.->|Requires| H3[200GB Disk]

    YOCTO -.->|Requires| Y1[BitBake 2.0]
    YOCTO -.->|Requires| Y2[OE-Core]

    LAYERS -.->|Requires| L1[meta-tegra]
    LAYERS -.->|Requires| L2[meta-openembedded]

    BUILD -.->|Produces| B1[Bootloader]
    BUILD -.->|Produces| B2[Kernel]
    BUILD -.->|Produces| B3[RootFS]

    FLASH -.->|Requires| F1[SDK Manager]
    FLASH -.->|OR| F2[flash.sh]

    style START fill:#ffd60a
    style BUILD fill:#76b900
    style FLASH fill:#ff6600
```

---

## Software Bill of Materials (SBOM)

### Core Components

**Build System**:
- Yocto Project: 4.0+ (Apache-2.0, GPL-2.0, MIT)
- BitBake: 2.0+ (GPL-2.0)
- OpenEmbedded-Core: 4.0+ (Various)

**BSP & Drivers**:
- meta-tegra: Latest (MIT, GPL-2.0)
- NVIDIA L4T: 35.x/36.x (Proprietary, GPL-2.0 for kernel)
- Linux Kernel: 5.10/5.15 (GPL-2.0)

**AI/ML**:
- CUDA: 11.4/12.6 (Proprietary)
- TensorRT: 8.5/10.3 (Proprietary)
- cuDNN: 8.6/8.9 (Proprietary)
- ONNX Runtime: 1.17 (MIT)

**Multimedia**:
- GStreamer: 1.20 (LGPL-2.1)
- DeepStream: 6.3/7.0 (Proprietary)
- VPI: 2.3/3.2 (Proprietary)
- OpenCV: 4.8 (Apache-2.0)

---

## Version Update Strategy

### Staying Current

**Quarterly Updates**:
- Review Yocto Project releases
- Check meta-tegra updates
- Monitor JetPack releases
- Update dependency versions

**LTS Strategy**:
- Stick with LTS for production (Kirkstone, Scarthgap)
- Test newer releases in development
- Plan migration 6 months before EOL

**Security Updates**:
- Subscribe to security mailing lists
- Apply CVE patches promptly
- Regular dependency scanning

---

*Generated by Knowledge Integration Agent*
*Part of Yocto & Meta-Tegra Multi-Agent Learning System*
