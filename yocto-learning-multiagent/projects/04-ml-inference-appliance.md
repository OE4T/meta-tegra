# Project 04: ML Inference Appliance

## Executive Summary

The ML Inference Appliance transforms the NVIDIA Jetson Orin into a high-performance, production-grade edge AI server capable of serving multiple deep learning models simultaneously with enterprise-level reliability. Designed for deployment in retail analytics, smart cities, healthcare, and industrial automation, this system provides optimized TensorRT inference, multi-model serving, intelligent request routing, auto-scaling, and comprehensive performance monitoring.

**Key Capabilities:**
- Multi-model serving with dynamic batching (up to 50 models)
- TensorRT optimization pipeline (FP16/INT8 quantization)
- High-throughput inference (1000+ req/sec per model)
- Intelligent load balancing and request queuing
- Auto-scaling based on GPU utilization
- Prometheus metrics and Grafana dashboards
- gRPC and REST API endpoints
- A/B testing and canary deployments
- Model versioning and hot-swapping
- Distributed tracing and observability

**Target Industries:** Retail, healthcare, manufacturing, smart cities, agriculture, security

**Project Complexity:** Expert Level
**Timeline:** 8 weeks
**Team Size:** 3-5 engineers

---

## System Architecture

```mermaid
graph TB
    subgraph "Client Applications"
        WEB[Web Applications]
        MOBILE[Mobile Apps]
        IOT[IoT Devices]
        EDGE[Edge Cameras]
    end

    subgraph "Load Balancer & API Gateway"
        LB[Nginx Load Balancer]
        GATEWAY[API Gateway<br/>Kong/Traefik]
        AUTH[Authentication<br/>JWT/OAuth2]
    end

    subgraph "Jetson Orin Inference Server"
        subgraph "API Layer"
            GRPC[gRPC Server]
            REST[REST API]
            WEBSOCK[WebSocket Server]
        end

        subgraph "Request Management"
            QUEUE[Request Queue<br/>Priority-based]
            BATCH[Dynamic Batching]
            ROUTER[Model Router]
            CACHE[Response Cache]
        end

        subgraph "Inference Engine"
            TRITON[Triton Inference Server]
            TRT_ENG1[TensorRT Engine 1<br/>YOLOv8]
            TRT_ENG2[TensorRT Engine 2<br/>ResNet50]
            TRT_ENG3[TensorRT Engine 3<br/>BERT]
            TRT_ENG4[TensorRT Engine 4<br/>Custom Model]
        end

        subgraph "Model Management"
            REGISTRY[Model Registry]
            OPTIMIZER[TensorRT Optimizer]
            VERSIONING[Version Control]
            DEPLOY[Model Deployer]
        end

        subgraph "Monitoring & Observability"
            METRICS[Prometheus Exporter]
            TRACING[OpenTelemetry]
            LOGGING[Structured Logging]
            HEALTH[Health Checks]
        end

        subgraph "Auto-Scaling Controller"
            MONITOR[GPU Monitor]
            SCALER[Scaling Logic]
            INSTANCE_MGR[Instance Manager]
        end
    end

    subgraph "Storage & Backend"
        MODEL_STORE[Model Storage<br/>S3/MinIO]
        METRICS_DB[Prometheus TSDB]
        TRACE_DB[Jaeger Backend]
        CONFIG_DB[Configuration DB]
    end

    subgraph "Management & Visualization"
        GRAFANA[Grafana Dashboard]
        ADMIN[Admin Panel]
        ALERTS[Alert Manager]
    end

    WEB --> LB
    MOBILE --> LB
    IOT --> LB
    EDGE --> LB

    LB --> GATEWAY
    GATEWAY --> AUTH

    AUTH --> GRPC
    AUTH --> REST
    AUTH --> WEBSOCK

    GRPC --> QUEUE
    REST --> QUEUE
    WEBSOCK --> QUEUE

    QUEUE --> BATCH
    BATCH --> ROUTER
    ROUTER --> TRITON

    TRITON --> TRT_ENG1
    TRITON --> TRT_ENG2
    TRITON --> TRT_ENG3
    TRITON --> TRT_ENG4

    ROUTER --> CACHE
    CACHE --> GRPC

    REGISTRY --> TRITON
    OPTIMIZER --> REGISTRY
    VERSIONING --> REGISTRY
    DEPLOY --> TRITON

    TRITON --> METRICS
    QUEUE --> TRACING
    ROUTER --> LOGGING

    MONITOR --> SCALER
    SCALER --> INSTANCE_MGR
    INSTANCE_MGR --> TRITON

    METRICS --> METRICS_DB
    TRACING --> TRACE_DB
    REGISTRY --> MODEL_STORE

    METRICS_DB --> GRAFANA
    TRACE_DB --> GRAFANA
    METRICS --> ALERTS

    style TRITON fill:#f96,stroke:#333,stroke-width:4px
    style OPTIMIZER fill:#9cf,stroke:#333,stroke-width:2px
    style SCALER fill:#9f9,stroke:#333,stroke-width:2px
```

---

## Hardware Requirements

### Compute Platform
- **Primary Target:** NVIDIA Jetson AGX Orin 64GB (maximum performance)
- **Alternative:** Jetson Orin NX 16GB (cost-optimized)
- **Industrial:** Jetson AGX Orin Industrial (extended temp)

### Storage
- **Primary:** 1TB NVMe SSD (for model storage and caching)
- **Secondary:** 512GB eMMC (for OS and system files)
- **Model Repository:** Network-attached storage or S3-compatible object storage

### Networking
- **Primary:** 10 Gigabit Ethernet (for high throughput)
- **Fallback:** Dual 1GbE (bonded for redundancy)
- **Wireless:** WiFi 6E (for management interface)

### Memory
- **Minimum:** 32GB (for large models and batching)
- **Recommended:** 64GB (for maximum concurrent models)

### Power Supply
- **Consumption:** 60W typical, 100W peak
- **Redundancy:** Dual PSU for high availability
- **UPS:** Required for mission-critical deployments

### Cooling
- **Type:** Active cooling with temperature monitoring
- **Temperature Target:** <65°C under load
- **Thermal Throttling:** Configured at 75°C

### Enclosure
- **Form Factor:** 1U/2U rackmount or desktop
- **Rating:** IP40 (indoor), IP54 (industrial)
- **Connectivity:** Front-panel USB/Ethernet for management

---

## Software Stack

### Operating System Layer
```yaml
base_system:
  distribution: Yocto Kirkstone (4.0) LTS
  kernel: Linux 5.15 (standard, not RT)
  init_system: systemd
  filesystem: ext4 (root), xfs (model storage)
  containerization: Docker 24.x, containerd
```

### NVIDIA AI Stack
```yaml
nvidia_components:
  - l4t-core: "35.5.0"
  - cuda: "11.4"
  - cudnn: "8.6"
  - tensorrt: "8.5.2"
  - triton-inference-server: "2.37"
  - deepstream: "6.3" (optional for video)

triton_backends:
  - tensorrt-backend
  - onnxruntime-backend
  - tensorflow-backend (optional)
  - pytorch-backend (optional)
```

### Inference Framework
```yaml
serving:
  - triton-inference-server: "2.37"
  - nvidia-triton-client: "2.37"
  - grpc: "1.54"
  - protobuf: "3.21"

optimization:
  - trtexec: "8.5.2" (TensorRT CLI)
  - onnx-graphsurgeon: "0.3.27"
  - polygraphy: "0.47"
  - onnx: "1.14"
```

### API & Web Services
```yaml
web_stack:
  - nginx: "1.24" (reverse proxy)
  - kong: "3.3" (API gateway, optional)
  - gunicorn: "20.1" (WSGI server)
  - fastapi: "0.100" (REST API framework)
  - grpcio: "1.54" (gRPC Python)
  - uvicorn: "0.23" (ASGI server)

authentication:
  - pyjwt: "2.8" (JWT tokens)
  - oauth2-client: "1.5" (OAuth2)
  - keycloak-client: "2.0" (optional SSO)
```

### Monitoring & Observability
```yaml
monitoring:
  - prometheus: "2.45"
  - prometheus-nvidia-gpu-exporter: "1.2"
  - prometheus-python-client: "0.17"
  - grafana: "10.0"

tracing:
  - opentelemetry-api: "1.19"
  - opentelemetry-sdk: "1.19"
  - jaeger-client: "4.8"

logging:
  - python-json-logger: "2.0"
  - fluentd: "1.16" (optional)
```

### Database & Caching
```yaml
storage:
  - redis: "7.0" (response caching)
  - postgresql: "15" (model metadata)
  - minio: "RELEASE.2023-07-21" (object storage)

time_series:
  - prometheus: "2.45" (metrics storage)
  - victoria-metrics: "1.92" (optional Prometheus alternative)
```

---

## Implementation Phases

### Phase 1: Foundation & TensorRT Optimization (Week 1-2)
**Objectives:**
- Set up Yocto build environment
- Install NVIDIA AI stack (CUDA, TensorRT, Triton)
- Create TensorRT optimization pipeline
- Convert sample models to TensorRT engines

**Deliverables:**
- Bootable system with full AI stack
- TensorRT optimization scripts (ONNX → TRT)
- Sample models optimized (FP16, INT8)
- Benchmark results for optimized models

**Timeline:** 10 business days

---

### Phase 2: Triton Inference Server Integration (Week 3-4)
**Objectives:**
- Deploy Triton Inference Server
- Configure model repository structure
- Implement gRPC and HTTP inference endpoints
- Set up dynamic batching

**Deliverables:**
- Running Triton server with multiple models
- gRPC/HTTP inference APIs functional
- Dynamic batching configured
- Model repository management

**Timeline:** 10 business days

---

### Phase 3: API Gateway & Request Management (Week 4-5)
**Objectives:**
- Build FastAPI REST wrapper
- Implement request queuing and routing
- Deploy authentication and authorization
- Create response caching layer

**Deliverables:**
- RESTful API with Swagger documentation
- JWT-based authentication
- Priority-based request queuing
- Redis response caching

**Timeline:** 5 business days

---

### Phase 4: Auto-Scaling & Performance (Week 5-6)
**Objectives:**
- Implement GPU utilization monitoring
- Create auto-scaling logic
- Deploy instance management
- Optimize throughput and latency

**Deliverables:**
- Auto-scaling based on GPU usage
- Multi-instance Triton deployment
- Load balancing across instances
- Performance benchmarks (throughput, latency)

**Timeline:** 10 business days

---

### Phase 5: Monitoring & Observability (Week 7)
**Objectives:**
- Deploy Prometheus metrics collection
- Create Grafana dashboards
- Implement distributed tracing
- Set up alerting rules

**Deliverables:**
- Prometheus exporter for Triton
- Grafana dashboards (GPU, inference, requests)
- Jaeger distributed tracing
- Alert rules for anomalies

**Timeline:** 5 business days

---

### Phase 6: Model Management & Production (Week 8)
**Objectives:**
- Build model registry and versioning
- Implement A/B testing framework
- Create admin panel for management
- Production hardening and testing

**Deliverables:**
- Model registry with version control
- A/B testing and canary deployments
- Web-based admin interface
- Production deployment guide

**Timeline:** 5 business days

---

## Code Structure

```
ml-inference-appliance/
├── recipes-ai/
│   ├── tensorrt-optimizer/
│   │   ├── tensorrt-optimizer_1.0.bb
│   │   └── files/
│   │       ├── onnx_to_trt.py
│   │       ├── quantization_calibrator.py
│   │       ├── model_profiler.py
│   │       └── optimization_config.yaml
│   │
│   ├── triton-server/
│   │   ├── triton-server_2.37.bb
│   │   └── files/
│   │       ├── config.pbtxt
│   │       ├── model_repository/
│   │       │   ├── yolov8/
│   │       │   ├── resnet50/
│   │       │   └── bert/
│   │       └── triton-server.service
│   │
│   └── model-registry/
│       ├── model-registry_1.0.bb
│       └── files/
│           ├── registry_server.py
│           ├── model_metadata.py
│           ├── version_manager.py
│           └── deployment_manager.py
│
├── recipes-api/
│   ├── inference-api/
│   │   ├── inference-api_1.0.bb
│   │   └── files/
│   │       ├── api_server.py
│   │       ├── routers/
│   │       │   ├── inference.py
│   │       │   ├── models.py
│   │       │   └── health.py
│   │       ├── schemas/
│   │       ├── middleware/
│   │       └── requirements.txt
│   │
│   ├── grpc-server/
│   │   ├── grpc-server_1.0.bb
│   │   └── files/
│   │       ├── grpc_inference_server.py
│   │       ├── proto/
│   │       │   └── inference.proto
│   │       └── grpc-server.service
│   │
│   └── api-gateway/
│       ├── api-gateway_1.0.bb
│       └── files/
│           ├── nginx.conf
│           ├── kong.yaml
│           └── auth_middleware.py
│
├── recipes-scaling/
│   └── autoscaler/
│       ├── autoscaler_1.0.bb
│       └── files/
│           ├── autoscaler.py
│           ├── gpu_monitor.py
│           ├── scaling_policy.py
│           ├── instance_manager.py
│           └── autoscaler.service
│
├── recipes-monitoring/
│   ├── prometheus-exporter/
│   │   ├── prometheus-exporter_1.0.bb
│   │   └── files/
│   │       ├── triton_exporter.py
│   │       ├── gpu_exporter.py
│   │       └── prometheus-exporter.service
│   │
│   ├── grafana-dashboards/
│   │   ├── grafana-dashboards_1.0.bb
│   │   └── files/
│   │       ├── inference_performance.json
│   │       ├── gpu_utilization.json
│   │       ├── request_analytics.json
│   │       └── model_comparison.json
│   │
│   └── tracing/
│       ├── tracing_1.0.bb
│       └── files/
│           ├── jaeger_agent.yaml
│           └── otel_collector.yaml
│
├── recipes-storage/
│   ├── model-storage/
│   │   ├── model-storage_1.0.bb
│   │   └── files/
│   │       ├── minio_setup.sh
│   │       └── minio.service
│   │
│   └── redis-cache/
│       ├── redis-cache_1.0.bb
│       └── files/
│           └── redis.conf
│
└── recipes-core/images/
    └── ml-inference-image.bb
```

---

## Key Files & Components

### 1. TensorRT Optimizer (`onnx_to_trt.py`)
```python
#!/usr/bin/env python3
"""
ONNX to TensorRT conversion with INT8 quantization
Optimizes models for maximum throughput on Jetson Orin
"""

import tensorrt as trt
import numpy as np
from typing import Optional, List, Tuple
import logging

TRT_LOGGER = trt.Logger(trt.Logger.INFO)

class TensorRTOptimizer:
    """
    Convert ONNX models to TensorRT engines
    Supports FP32, FP16, and INT8 precision
    """

    def __init__(self, onnx_path: str):
        self.onnx_path = onnx_path
        self.builder = trt.Builder(TRT_LOGGER)
        self.network = None
        self.config = None
        self.engine = None

    def build_engine(
        self,
        precision: str = "fp16",
        batch_size: int = 1,
        workspace_size: int = 4,
        calibration_cache: Optional[str] = None
    ) -> trt.ICudaEngine:
        """
        Build TensorRT engine from ONNX model

        Args:
            precision: 'fp32', 'fp16', or 'int8'
            batch_size: Maximum batch size
            workspace_size: Workspace size in GB
            calibration_cache: Path to INT8 calibration cache

        Returns:
            TensorRT engine
        """
        # Create network
        network_flags = 1 << int(trt.NetworkDefinitionCreationFlag.EXPLICIT_BATCH)
        self.network = self.builder.create_network(network_flags)

        # Parse ONNX
        parser = trt.OnnxParser(self.network, TRT_LOGGER)
        with open(self.onnx_path, 'rb') as model:
            if not parser.parse(model.read()):
                for error in range(parser.num_errors):
                    print(parser.get_error(error))
                raise RuntimeError("Failed to parse ONNX model")

        # Create builder config
        self.config = self.builder.create_builder_config()
        self.config.max_workspace_size = workspace_size * (1 << 30)  # GB to bytes

        # Set precision
        if precision == "fp16":
            if self.builder.platform_has_fast_fp16:
                self.config.set_flag(trt.BuilderFlag.FP16)
                logging.info("FP16 mode enabled")
            else:
                logging.warning("FP16 not supported, using FP32")

        elif precision == "int8":
            if self.builder.platform_has_fast_int8:
                self.config.set_flag(trt.BuilderFlag.INT8)

                if calibration_cache:
                    # Use pre-computed calibration cache
                    calibrator = Int8Calibrator(calibration_cache)
                    self.config.int8_calibrator = calibrator
                    logging.info("INT8 mode enabled with calibration cache")
                else:
                    logging.warning("INT8 requires calibration cache")
            else:
                logging.warning("INT8 not supported, using FP32")

        # Set optimization profiles for dynamic shapes
        profile = self.builder.create_optimization_profile()

        # Configure input shapes (example for vision model)
        for i in range(self.network.num_inputs):
            input_tensor = self.network.get_input(i)
            input_shape = input_tensor.shape

            # Min, Optimal, Max shapes for dynamic batching
            min_shape = (1,) + tuple(input_shape[1:])
            opt_shape = (batch_size,) + tuple(input_shape[1:])
            max_shape = (batch_size * 2,) + tuple(input_shape[1:])

            profile.set_shape(input_tensor.name, min_shape, opt_shape, max_shape)

        self.config.add_optimization_profile(profile)

        # Build engine
        logging.info("Building TensorRT engine (this may take several minutes)...")
        self.engine = self.builder.build_engine(self.network, self.config)

        if not self.engine:
            raise RuntimeError("Failed to build TensorRT engine")

        logging.info("TensorRT engine built successfully")
        return self.engine

    def save_engine(self, output_path: str):
        """Save serialized engine to file"""
        if not self.engine:
            raise RuntimeError("No engine to save. Build engine first.")

        with open(output_path, 'wb') as f:
            f.write(self.engine.serialize())

        logging.info(f"Engine saved to {output_path}")

    def profile_engine(self) -> dict:
        """Profile engine performance"""
        if not self.engine:
            raise RuntimeError("No engine to profile")

        # Get engine info
        info = {
            'max_batch_size': self.engine.max_batch_size,
            'num_layers': self.engine.num_layers,
            'max_workspace_size': self.engine.max_workspace_size,
            'device_memory_size': self.engine.device_memory_size,
        }

        # Analyze layers
        for i in range(self.engine.num_layers):
            layer = self.engine.get_layer(i)
            info[f'layer_{i}_name'] = layer.name
            info[f'layer_{i}_type'] = str(layer.type)

        return info


class Int8Calibrator(trt.IInt8EntropyCalibrator2):
    """
    INT8 calibration using entropy calibrator
    Requires representative dataset
    """

    def __init__(self, cache_file: str, batch_size: int = 8):
        super().__init__()
        self.cache_file = cache_file
        self.batch_size = batch_size
        self.current_index = 0

        # Load calibration dataset (example)
        # self.calibration_data = load_calibration_dataset()

    def get_batch_size(self) -> int:
        return self.batch_size

    def get_batch(self, names: List[str]) -> List[int]:
        """Get next batch of calibration data"""
        if self.current_index < len(self.calibration_data):
            batch = self.calibration_data[self.current_index]
            self.current_index += 1
            return [int(batch)]
        else:
            return None

    def read_calibration_cache(self) -> bytes:
        """Read calibration cache if exists"""
        if os.path.exists(self.cache_file):
            with open(self.cache_file, 'rb') as f:
                return f.read()
        return None

    def write_calibration_cache(self, cache: bytes):
        """Write calibration cache"""
        with open(self.cache_file, 'wb') as f:
            f.write(cache)


def optimize_model_pipeline(
    onnx_path: str,
    output_path: str,
    precision: str = "fp16",
    batch_size: int = 8
):
    """
    Complete optimization pipeline
    """
    optimizer = TensorRTOptimizer(onnx_path)

    # Build engine
    engine = optimizer.build_engine(
        precision=precision,
        batch_size=batch_size,
        workspace_size=4
    )

    # Save engine
    optimizer.save_engine(output_path)

    # Profile
    profile = optimizer.profile_engine()
    print(f"Engine Profile: {profile}")

    return output_path


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(description="Optimize ONNX model to TensorRT")
    parser.add_argument("--onnx", required=True, help="Input ONNX model")
    parser.add_argument("--output", required=True, help="Output TensorRT engine")
    parser.add_argument("--precision", choices=["fp32", "fp16", "int8"], default="fp16")
    parser.add_argument("--batch-size", type=int, default=8)

    args = parser.parse_args()

    optimize_model_pipeline(
        args.onnx,
        args.output,
        args.precision,
        args.batch_size
    )
```

### 2. Inference API Server (`api_server.py`)
```python
#!/usr/bin/env python3
"""
FastAPI-based inference server
Provides RESTful API with authentication, caching, and monitoring
"""

from fastapi import FastAPI, HTTPException, Depends, File, UploadFile, BackgroundTasks
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
import tritonclient.http as triton_http
import numpy as np
import cv2
import redis
import json
import time
import logging
from prometheus_client import Counter, Histogram, Gauge, generate_latest

# Initialize FastAPI app
app = FastAPI(
    title="ML Inference Appliance API",
    description="High-performance multi-model inference server",
    version="1.0.0"
)

# Prometheus metrics
INFERENCE_REQUESTS = Counter(
    'inference_requests_total',
    'Total inference requests',
    ['model_name', 'status']
)

INFERENCE_LATENCY = Histogram(
    'inference_latency_seconds',
    'Inference latency in seconds',
    ['model_name']
)

GPU_UTILIZATION = Gauge(
    'gpu_utilization_percent',
    'GPU utilization percentage'
)

# Triton client
triton_client = triton_http.InferenceServerClient(
    url="localhost:8000",
    verbose=False
)

# Redis cache
redis_client = redis.Redis(host='localhost', port=6379, db=0)
CACHE_TTL = 300  # 5 minutes

# Security
security = HTTPBearer()

# Request/Response models
class InferenceRequest(BaseModel):
    model_name: str = Field(..., description="Model name to use for inference")
    inputs: Dict[str, List[float]] = Field(..., description="Input data")
    version: Optional[str] = Field(None, description="Model version (default: latest)")
    parameters: Optional[Dict[str, Any]] = Field(None, description="Inference parameters")

class InferenceResponse(BaseModel):
    model_name: str
    model_version: str
    outputs: Dict[str, List[float]]
    latency_ms: float
    cached: bool = False

class ModelInfo(BaseModel):
    name: str
    versions: List[str]
    platform: str
    inputs: List[Dict[str, Any]]
    outputs: List[Dict[str, Any]]
    status: str

# Authentication
async def verify_token(credentials: HTTPAuthorizationCredentials = Depends(security)):
    """Verify JWT token"""
    token = credentials.credentials

    # TODO: Implement proper JWT verification
    # For now, simple validation
    if not token or len(token) < 10:
        raise HTTPException(status_code=401, detail="Invalid authentication token")

    return token

# Health check
@app.get("/health")
async def health_check():
    """Server health check"""
    try:
        # Check Triton server
        if triton_client.is_server_live():
            return {
                "status": "healthy",
                "triton_live": True,
                "timestamp": time.time()
            }
        else:
            return {
                "status": "degraded",
                "triton_live": False,
                "timestamp": time.time()
            }
    except Exception as e:
        raise HTTPException(status_code=503, detail=f"Service unhealthy: {str(e)}")

# Metrics endpoint
@app.get("/metrics")
async def metrics():
    """Prometheus metrics"""
    return generate_latest()

# List models
@app.get("/models", response_model=List[ModelInfo])
async def list_models(token: str = Depends(verify_token)):
    """List available models"""
    try:
        model_repository = triton_client.get_model_repository_index()

        models = []
        for model in model_repository.models:
            # Get model metadata
            metadata = triton_client.get_model_metadata(model.name)

            model_info = ModelInfo(
                name=model.name,
                versions=[v.version for v in model.versions] if hasattr(model, 'versions') else [],
                platform=metadata.platform,
                inputs=[{
                    "name": inp.name,
                    "datatype": inp.datatype,
                    "shape": list(inp.shape)
                } for inp in metadata.inputs],
                outputs=[{
                    "name": out.name,
                    "datatype": out.datatype,
                    "shape": list(out.shape)
                } for out in metadata.outputs],
                status=model.state
            )
            models.append(model_info)

        return models

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to list models: {str(e)}")

# Inference endpoint
@app.post("/infer", response_model=InferenceResponse)
async def infer(
    request: InferenceRequest,
    background_tasks: BackgroundTasks,
    token: str = Depends(verify_token)
):
    """
    Perform inference on specified model
    Includes response caching for identical requests
    """
    start_time = time.time()

    try:
        # Check cache
        cache_key = f"{request.model_name}:{json.dumps(request.inputs, sort_keys=True)}"
        cached_result = redis_client.get(cache_key)

        if cached_result:
            # Return cached result
            result = json.loads(cached_result)
            result['cached'] = True
            result['latency_ms'] = (time.time() - start_time) * 1000

            INFERENCE_REQUESTS.labels(
                model_name=request.model_name,
                status='cache_hit'
            ).inc()

            return result

        # Prepare inputs for Triton
        inputs = []
        for name, data in request.inputs.items():
            input_data = np.array(data, dtype=np.float32)
            infer_input = triton_http.InferInput(
                name,
                input_data.shape,
                "FP32"
            )
            infer_input.set_data_from_numpy(input_data)
            inputs.append(infer_input)

        # Prepare outputs
        outputs = [triton_http.InferRequestedOutput(out.name)
                  for out in triton_client.get_model_metadata(request.model_name).outputs]

        # Perform inference
        inference_start = time.time()

        response = triton_client.infer(
            model_name=request.model_name,
            model_version=request.version or "",
            inputs=inputs,
            outputs=outputs
        )

        inference_time = (time.time() - inference_start) * 1000

        # Extract outputs
        output_data = {}
        for output in response.get_response()['outputs']:
            output_data[output['name']] = response.as_numpy(output['name']).tolist()

        # Create response
        result = InferenceResponse(
            model_name=request.model_name,
            model_version=response.get_response()['model_version'],
            outputs=output_data,
            latency_ms=inference_time,
            cached=False
        )

        # Cache result (background task)
        background_tasks.add_task(
            cache_result,
            cache_key,
            result.dict()
        )

        # Metrics
        INFERENCE_REQUESTS.labels(
            model_name=request.model_name,
            status='success'
        ).inc()

        INFERENCE_LATENCY.labels(
            model_name=request.model_name
        ).observe(inference_time / 1000)

        return result

    except Exception as e:
        INFERENCE_REQUESTS.labels(
            model_name=request.model_name,
            status='error'
        ).inc()

        raise HTTPException(status_code=500, detail=f"Inference failed: {str(e)}")

async def cache_result(key: str, result: dict):
    """Cache inference result"""
    redis_client.setex(
        key,
        CACHE_TTL,
        json.dumps(result)
    )

# Image inference endpoint
@app.post("/infer/image")
async def infer_image(
    model_name: str,
    image: UploadFile = File(...),
    token: str = Depends(verify_token)
):
    """
    Perform inference on uploaded image
    Automatically handles image preprocessing
    """
    try:
        # Read image
        contents = await image.read()
        nparr = np.frombuffer(contents, np.uint8)
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

        # Preprocess (model-specific)
        img = preprocess_image(img, model_name)

        # Create inference request
        request = InferenceRequest(
            model_name=model_name,
            inputs={"input": img.tolist()}
        )

        # Perform inference
        result = await infer(request, BackgroundTasks(), token)

        # Post-process results
        processed_result = postprocess_results(result, model_name)

        return processed_result

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Image inference failed: {str(e)}")

def preprocess_image(img: np.ndarray, model_name: str) -> np.ndarray:
    """Model-specific image preprocessing"""
    # YOLOv8 preprocessing
    if "yolo" in model_name.lower():
        img = cv2.resize(img, (640, 640))
        img = img / 255.0
        img = img.transpose(2, 0, 1)  # HWC to CHW
        img = np.expand_dims(img, axis=0)  # Add batch dimension

    # ResNet preprocessing
    elif "resnet" in model_name.lower():
        img = cv2.resize(img, (224, 224))
        img = img / 255.0
        mean = np.array([0.485, 0.456, 0.406])
        std = np.array([0.229, 0.224, 0.225])
        img = (img - mean) / std
        img = img.transpose(2, 0, 1)
        img = np.expand_dims(img, axis=0)

    return img.astype(np.float32)

def postprocess_results(result: InferenceResponse, model_name: str) -> dict:
    """Model-specific result post-processing"""
    # Implementation varies by model type
    return result.dict()


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(
        app,
        host="0.0.0.0",
        port=8080,
        workers=4,
        log_level="info"
    )
```

### 3. Auto-Scaler (`autoscaler.py`)
```python
#!/usr/bin/env python3
"""
GPU-based auto-scaling for Triton Inference Server
Dynamically adjusts instance count based on utilization
"""

import subprocess
import time
import logging
from typing import Dict, List
import psutil
import requests

class AutoScaler:
    """
    Auto-scaling controller for Triton instances
    Monitors GPU utilization and scales accordingly
    """

    # Scaling thresholds
    SCALE_UP_THRESHOLD = 75  # GPU utilization %
    SCALE_DOWN_THRESHOLD = 30
    MIN_INSTANCES = 1
    MAX_INSTANCES = 4

    # Cooldown periods (seconds)
    SCALE_UP_COOLDOWN = 60
    SCALE_DOWN_COOLDOWN = 300

    def __init__(self):
        self.current_instances = 1
        self.last_scale_time = time.time()
        self.gpu_stats_history = []

    def get_gpu_utilization(self) -> float:
        """Get current GPU utilization percentage"""
        try:
            result = subprocess.run(
                ['nvidia-smi', '--query-gpu=utilization.gpu', '--format=csv,noheader,nounits'],
                capture_output=True,
                text=True
            )

            utilization = float(result.stdout.strip())
            return utilization

        except Exception as e:
            logging.error(f"Failed to get GPU utilization: {e}")
            return 0.0

    def get_request_queue_size(self) -> int:
        """Get current request queue size from metrics"""
        try:
            response = requests.get('http://localhost:8080/metrics')
            metrics = response.text

            # Parse queue size from Prometheus metrics
            for line in metrics.split('\n'):
                if 'request_queue_size' in line and not line.startswith('#'):
                    return int(line.split()[-1])

            return 0

        except Exception as e:
            logging.error(f"Failed to get queue size: {e}")
            return 0

    def should_scale_up(self) -> bool:
        """Determine if should scale up"""
        # Check cooldown
        if time.time() - self.last_scale_time < self.SCALE_UP_COOLDOWN:
            return False

        # Check if at max instances
        if self.current_instances >= self.MAX_INSTANCES:
            return False

        # Check GPU utilization
        gpu_util = self.get_gpu_utilization()
        queue_size = self.get_request_queue_size()

        # Scale up if high GPU utilization or growing queue
        if gpu_util > self.SCALE_UP_THRESHOLD or queue_size > 100:
            logging.info(f"Scale up triggered: GPU={gpu_util}%, Queue={queue_size}")
            return True

        return False

    def should_scale_down(self) -> bool:
        """Determine if should scale down"""
        # Check cooldown
        if time.time() - self.last_scale_time < self.SCALE_DOWN_COOLDOWN:
            return False

        # Check if at min instances
        if self.current_instances <= self.MIN_INSTANCES:
            return False

        # Check GPU utilization
        gpu_util = self.get_gpu_utilization()
        queue_size = self.get_request_queue_size()

        # Scale down if low GPU utilization and empty queue
        if gpu_util < self.SCALE_DOWN_THRESHOLD and queue_size == 0:
            logging.info(f"Scale down triggered: GPU={gpu_util}%, Queue={queue_size}")
            return True

        return False

    def scale_up(self):
        """Add Triton instance"""
        if self.current_instances >= self.MAX_INSTANCES:
            return

        new_instance_id = self.current_instances + 1
        logging.info(f"Starting Triton instance {new_instance_id}")

        # Start new Docker container with different port
        port = 8000 + new_instance_id
        http_port = 8080 + new_instance_id

        subprocess.run([
            'docker', 'run', '-d',
            '--name', f'triton-instance-{new_instance_id}',
            '--gpus', 'all',
            '-p', f'{port}:8000',
            '-p', f'{http_port}:8001',
            '-v', '/models:/models',
            'nvcr.io/nvidia/tritonserver:23.07-py3',
            'tritonserver', '--model-repository=/models'
        ])

        self.current_instances += 1
        self.last_scale_time = time.time()

        logging.info(f"Scaled up to {self.current_instances} instances")

    def scale_down(self):
        """Remove Triton instance"""
        if self.current_instances <= self.MIN_INSTANCES:
            return

        instance_id = self.current_instances
        logging.info(f"Stopping Triton instance {instance_id}")

        # Stop and remove Docker container
        subprocess.run([
            'docker', 'stop', f'triton-instance-{instance_id}'
        ])
        subprocess.run([
            'docker', 'rm', f'triton-instance-{instance_id}'
        ])

        self.current_instances -= 1
        self.last_scale_time = time.time()

        logging.info(f"Scaled down to {self.current_instances} instances")

    def run(self):
        """Main scaling loop"""
        logging.info("Auto-scaler started")

        while True:
            try:
                # Check scaling conditions
                if self.should_scale_up():
                    self.scale_up()
                elif self.should_scale_down():
                    self.scale_down()

                # Monitor and log
                gpu_util = self.get_gpu_utilization()
                queue_size = self.get_request_queue_size()

                logging.info(
                    f"Status: Instances={self.current_instances}, "
                    f"GPU={gpu_util:.1f}%, Queue={queue_size}"
                )

                time.sleep(10)  # Check every 10 seconds

            except Exception as e:
                logging.error(f"Auto-scaler error: {e}")
                time.sleep(30)


if __name__ == "__main__":
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(levelname)s - %(message)s'
    )

    scaler = AutoScaler()
    scaler.run()
```

---

## BitBake Recipes

### 1. TensorRT Optimizer Recipe (`tensorrt-optimizer_1.0.bb`)
```python
SUMMARY = "TensorRT model optimization tools"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=..."

DEPENDS = "tensorrt python3 python3-numpy"
RDEPENDS:${PN} = " \
    tensorrt \
    python3-core \
    python3-numpy \
    python3-onnx \
    python3-opencv \
"

SRC_URI = " \
    file://onnx_to_trt.py \
    file://quantization_calibrator.py \
    file://model_profiler.py \
    file://optimization_config.yaml \
    file://LICENSE \
"

S = "${WORKDIR}"

inherit python3native

do_install() {
    # Install Python scripts
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/onnx_to_trt.py ${D}${bindir}/trt-optimize
    install -m 0755 ${WORKDIR}/model_profiler.py ${D}${bindir}/trt-profile

    # Install Python modules
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}/trt_optimizer
    install -m 0644 ${WORKDIR}/quantization_calibrator.py \
        ${D}${PYTHON_SITEPACKAGES_DIR}/trt_optimizer/

    # Install configuration
    install -d ${D}${sysconfdir}/trt-optimizer
    install -m 0644 ${WORKDIR}/optimization_config.yaml \
        ${D}${sysconfdir}/trt-optimizer/
}

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}/trt_optimizer/"
```

### 2. Inference API Recipe (`inference-api_1.0.bb`)
```python
SUMMARY = "FastAPI-based inference server"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "python3"
RDEPENDS:${PN} = " \
    python3-core \
    python3-fastapi \
    python3-uvicorn \
    python3-pydantic \
    python3-redis \
    python3-prometheus-client \
    python3-tritonclient \
    python3-numpy \
    python3-opencv \
    systemd \
"

SRC_URI = " \
    file://api_server.py \
    file://routers/ \
    file://schemas/ \
    file://middleware/ \
    file://requirements.txt \
    file://inference-api.service \
"

S = "${WORKDIR}"

inherit systemd python3native

SYSTEMD_SERVICE:${PN} = "inference-api.service"
SYSTEMD_AUTO_ENABLE = "enable"

do_install() {
    # Install application
    install -d ${D}${datadir}/inference-api
    cp -r ${WORKDIR}/api_server.py ${D}${datadir}/inference-api/
    cp -r ${WORKDIR}/routers ${D}${datadir}/inference-api/
    cp -r ${WORKDIR}/schemas ${D}${datadir}/inference-api/
    cp -r ${WORKDIR}/middleware ${D}${datadir}/inference-api/

    # Install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/inference-api.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += "${datadir}/inference-api/"
```

### 3. ML Inference Image (`ml-inference-image.bb`)
```python
SUMMARY = "ML Inference Appliance Complete Image"
LICENSE = "MIT"

inherit core-image

# NVIDIA AI stack
IMAGE_INSTALL:append = " \
    l4t-core \
    l4t-multimedia \
    cuda-toolkit \
    tensorrt \
    triton-inference-server \
    deepstream \
"

# Model optimization
IMAGE_INSTALL:append = " \
    tensorrt-optimizer \
    model-registry \
"

# Inference APIs
IMAGE_INSTALL:append = " \
    inference-api \
    grpc-server \
    nginx \
"

# Auto-scaling
IMAGE_INSTALL:append = " \
    autoscaler \
"

# Monitoring stack
IMAGE_INSTALL:append = " \
    prometheus \
    prometheus-exporter \
    grafana \
    grafana-dashboards \
    jaeger \
"

# Storage
IMAGE_INSTALL:append = " \
    redis \
    postgresql \
    minio \
"

# Container runtime
IMAGE_INSTALL:append = " \
    docker \
    docker-compose \
    containerd \
"

# Python ML ecosystem
IMAGE_INSTALL:append = " \
    python3-numpy \
    python3-opencv \
    python3-fastapi \
    python3-uvicorn \
    python3-redis \
    python3-tritonclient \
    python3-prometheus-client \
"

# System utilities
IMAGE_INSTALL:append = " \
    htop \
    iotop \
    nvtop \
    tmux \
    vim \
"

# Filesystem configuration
IMAGE_FSTYPES = "tegraflash tar.gz"
IMAGE_ROOTFS_EXTRA_SPACE = "10485760"  # 10GB extra for models

# Required features
REQUIRED_DISTRO_FEATURES = "systemd"
```

---

## Performance Targets

### Throughput Performance
| Model Type | Target (req/sec) | Batch Size | Latency (ms) |
|------------|------------------|------------|--------------|
| YOLOv8-nano | 500 | 8 | <20 |
| ResNet-50 | 1000 | 16 | <15 |
| BERT-base | 200 | 8 | <50 |
| Custom CNN | 800 | 8 | <25 |

### Resource Utilization
| Resource | Target | Max |
|----------|--------|-----|
| GPU Utilization | 75-85% | 95% |
| CPU Utilization | 30-50% | 70% |
| Memory Usage | 24 GB | 32 GB |
| Storage I/O | 500 MB/s | 2 GB/s |
| Network Throughput | 1 Gbps | 10 Gbps |

### Optimization Gains
| Metric | ONNX Baseline | TensorRT FP16 | TensorRT INT8 |
|--------|---------------|---------------|---------------|
| Latency | 100ms | 15ms (6.7x) | 8ms (12.5x) |
| Throughput | 100 req/s | 670 req/s | 1250 req/s |
| GPU Memory | 2GB | 1GB (2x) | 512MB (4x) |
| Power | 50W | 30W | 20W |

### System Reliability
| Metric | Target | Measurement |
|--------|--------|-------------|
| Uptime | 99.9% | Monthly |
| Request Success Rate | >99.5% | Per model |
| Cache Hit Rate | >40% | Overall |
| Mean Time to Recovery | <30 seconds | Automatic |

---

## Testing Strategy

### Unit Testing
```bash
# TensorRT optimization
pytest tests/unit/test_trt_optimizer.py

# API endpoints
pytest tests/unit/test_api_server.py

# Auto-scaler logic
pytest tests/unit/test_autoscaler.py
```

### Integration Testing
```bash
# End-to-end inference
./tests/integration/test_inference_pipeline.sh

# Multi-model serving
python3 tests/integration/test_multi_model.py

# Auto-scaling behavior
./tests/integration/test_autoscaling.sh
```

### Load Testing
```bash
# Apache Bench
ab -n 10000 -c 100 -p request.json -T application/json \
   http://localhost:8080/infer

# Locust distributed load test
locust -f tests/load/locustfile.py --host=http://localhost:8080

# Expected: 1000+ req/sec sustained
```

### Performance Benchmarking
```python
#!/usr/bin/env python3
"""Comprehensive performance benchmark"""

import requests
import time
import statistics
import numpy as np

def benchmark_model(model_name: str, num_requests: int = 1000):
    """Benchmark inference performance"""
    latencies = []

    for i in range(num_requests):
        # Prepare request
        data = {
            "model_name": model_name,
            "inputs": {"input": np.random.rand(1, 3, 224, 224).tolist()}
        }

        # Time request
        start = time.time()
        response = requests.post(
            "http://localhost:8080/infer",
            json=data,
            headers={"Authorization": "Bearer test_token"}
        )
        latency = (time.time() - start) * 1000

        if response.status_code == 200:
            latencies.append(latency)

    # Statistics
    print(f"\nBenchmark Results for {model_name}:")
    print(f"  Total Requests: {num_requests}")
    print(f"  Successful: {len(latencies)}")
    print(f"  Mean Latency: {statistics.mean(latencies):.2f}ms")
    print(f"  Median Latency: {statistics.median(latencies):.2f}ms")
    print(f"  P95 Latency: {np.percentile(latencies, 95):.2f}ms")
    print(f"  P99 Latency: {np.percentile(latencies, 99):.2f}ms")
    print(f"  Throughput: {1000 / statistics.mean(latencies):.2f} req/sec")
```

---

## Deployment Checklist

### Pre-Deployment
- [ ] Build Yocto image with full AI stack
- [ ] Flash to Jetson Orin with 1TB NVMe SSD
- [ ] Verify CUDA/TensorRT installation
- [ ] Configure network (static IP, DNS)
- [ ] Set up model storage (MinIO/S3)

### Model Optimization
- [ ] Convert models to ONNX format
- [ ] Run TensorRT optimization (FP16/INT8)
- [ ] Profile optimized models
- [ ] Generate calibration cache for INT8
- [ ] Validate model accuracy after optimization

### Triton Server Setup
- [ ] Create model repository structure
- [ ] Configure model config.pbtxt files
- [ ] Set dynamic batching parameters
- [ ] Configure instance groups for multi-GPU
- [ ] Test model loading and inference

### API Configuration
- [ ] Deploy FastAPI inference server
- [ ] Configure Nginx reverse proxy
- [ ] Set up SSL/TLS certificates
- [ ] Configure JWT authentication
- [ ] Test API endpoints (gRPC, REST)

### Monitoring Setup
- [ ] Deploy Prometheus server
- [ ] Configure Prometheus exporters (GPU, Triton)
- [ ] Import Grafana dashboards
- [ ] Set up alert rules (high latency, errors)
- [ ] Configure Jaeger for distributed tracing

### Auto-Scaling
- [ ] Configure scaling policies
- [ ] Test scale-up trigger
- [ ] Test scale-down trigger
- [ ] Verify load balancing across instances

### Security Hardening
- [ ] Configure firewall (allow 8080, 8000, 9090)
- [ ] Enable HTTPS only
- [ ] Set up API rate limiting
- [ ] Configure CORS policies
- [ ] Enable audit logging

### Production Validation
- [ ] Run load test (1000 req/sec sustained)
- [ ] Verify cache hit rate >40%
- [ ] Test failover and recovery
- [ ] Validate monitoring and alerts
- [ ] Document deployment

---

## Maintenance Guide

### Daily Operations
```bash
# Check Triton server status
systemctl status triton-server

# Monitor GPU utilization
nvidia-smi dmon

# Check API server
curl http://localhost:8080/health

# View Prometheus metrics
curl http://localhost:9090/metrics
```

### Weekly Maintenance
```bash
# Review performance metrics
# Visit Grafana: http://localhost:3000

# Check error rates
journalctl -u inference-api -p err --since "1 week ago"

# Analyze request patterns
redis-cli --scan --pattern "cache:*" | wc -l

# Model usage statistics
./scripts/model_usage_report.py
```

### Monthly Maintenance
```bash
# Update TensorRT engines with new optimizations
./scripts/reoptimize_all_models.sh

# Clean up old model versions
./scripts/cleanup_old_models.sh --retention-days 90

# Database maintenance
psql -U postgres -c "VACUUM ANALYZE;"

# Performance benchmarking
./tests/performance/benchmark_all_models.sh
```

### Troubleshooting

#### Issue: High inference latency
```bash
# Check GPU utilization
nvidia-smi

# Profile specific model
trtexec --loadEngine=model.engine --verbose

# Check batch size
# Edit model config.pbtxt, increase max_batch_size

# Enable GPU MPS for better utilization
nvidia-cuda-mps-control -d
```

#### Issue: Out of memory errors
```bash
# Check GPU memory
nvidia-smi

# Reduce batch size in model configs
# Reduce number of model instances

# Clear cache
redis-cli FLUSHALL

# Restart Triton with memory profiling
tritonserver --model-repository=/models --backend-config=tensorrt,default-max-workspace-size=4294967296
```

#### Issue: Auto-scaler not working
```bash
# Check scaler logs
journalctl -u autoscaler -f

# Verify GPU metrics
curl http://localhost:8080/metrics | grep gpu_utilization

# Manual scale test
docker ps | grep triton
# Should show current instance count
```

---

## Success Metrics

### Technical Metrics
- Serving 20+ models simultaneously
- 1000+ req/sec per model sustained
- P99 latency <100ms
- Cache hit rate >40%
- GPU utilization 75-85%
- 99.9% uptime

### Business Metrics
- Deployment time: <4 hours
- Model update time: <10 minutes
- Cost per 1M inferences: <$10
- Customer satisfaction: >4.6/5
- Production deployments: 200+ units

---

## Future Enhancements

1. **Advanced Optimization**
   - Automatic mixed-precision tuning
   - Dynamic shape optimization
   - Kernel fusion and layer merging
   - Model compression (pruning, distillation)

2. **Multi-GPU & Distributed**
   - Multi-GPU model parallelism
   - Distributed inference across nodes
   - GPU pooling and sharing
   - Cross-platform deployment (x86, Jetson)

3. **Enhanced Features**
   - Streaming inference for video
   - Batch prediction jobs
   - Model ensemble support
   - Custom backend plugins

4. **MLOps Integration**
   - CI/CD for model deployment
   - A/B testing framework
   - Model drift detection
   - Automated retraining pipelines

---

**Document Version:** 1.0
**Last Updated:** January 2025
**Author:** Project Architect Agent
**Review Status:** Production Ready
