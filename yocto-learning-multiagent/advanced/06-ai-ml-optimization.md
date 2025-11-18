# AI/ML Optimization for Yocto & Meta-Tegra

## Overview

This module covers advanced AI/ML optimization techniques for Jetson platforms, including TensorRT optimization, multi-model inference, quantization techniques, custom CUDA kernels, and end-to-end pipeline optimization.

**Target Audience**: AI/ML engineers deploying models on Jetson platforms
**Prerequisites**: Deep understanding of deep learning, CUDA programming, and inference optimization

---

## 1. TensorRT Optimization

### 1.1 TensorRT Integration in Yocto

```python
# recipes-devtools/tensorrt/tensorrt_8.5.bb

SUMMARY = "NVIDIA TensorRT inference optimizer and runtime"
HOMEPAGE = "https://developer.nvidia.com/tensorrt"
LICENSE = "CLOSED"

DEPENDS = "cuda-toolkit cudnn"

# TensorRT is provided by nvidia-container-runtime or can be extracted from JetPack
SRC_URI = "file://TensorRT-${PV}-Linux-aarch64.tar.gz"

S = "${WORKDIR}/TensorRT-${PV}"

COMPATIBLE_MACHINE = "jetson-xavier-nx|jetson-xavier|jetson-orin"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    install -d ${D}${bindir}
    install -d ${D}${datadir}/tensorrt

    # Libraries
    cp -P ${S}/lib/*.so* ${D}${libdir}/

    # Headers
    cp -r ${S}/include/* ${D}${includedir}/

    # Binaries (trtexec, etc.)
    install -m 0755 ${S}/bin/* ${D}${bindir}/

    # Python bindings
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}/tensorrt
    cp -r ${S}/python/tensorrt/* ${D}${PYTHON_SITEPACKAGES_DIR}/tensorrt/

    # Samples
    cp -r ${S}/samples ${D}${datadir}/tensorrt/
}

FILES:${PN} = " \
    ${libdir}/*.so* \
    ${bindir}/* \
    ${PYTHON_SITEPACKAGES_DIR}/tensorrt/* \
    ${datadir}/tensorrt/samples/* \
"

FILES:${PN}-dev = "${includedir}/*"

RDEPENDS:${PN} = "cuda-toolkit cudnn python3"
INSANE_SKIP:${PN} = "dev-so already-stripped ldflags"
```

### 1.2 Model Conversion and Optimization

```python
#!/usr/bin/env python3
# tensorrt-optimizer.py - Comprehensive TensorRT model optimization

import tensorrt as trt
import numpy as np
from cuda import cudart
import sys
import os

TRT_LOGGER = trt.Logger(trt.Logger.INFO)

class TensorRTOptimizer:
    """Advanced TensorRT model optimizer"""

    def __init__(self, onnx_file, engine_file, precision='fp16'):
        self.onnx_file = onnx_file
        self.engine_file = engine_file
        self.precision = precision
        self.builder = None
        self.network = None
        self.config = None
        self.engine = None

    def build_engine(self, max_batch_size=1, workspace_size=4096):
        """Build optimized TensorRT engine"""

        # Create builder
        self.builder = trt.Builder(TRT_LOGGER)
        network_flags = 1 << int(trt.NetworkDefinitionCreationFlag.EXPLICIT_BATCH)
        self.network = self.builder.create_network(network_flags)

        # Parse ONNX model
        parser = trt.OnnxParser(self.network, TRT_LOGGER)

        print(f"Parsing ONNX model: {self.onnx_file}")
        with open(self.onnx_file, 'rb') as f:
            if not parser.parse(f.read()):
                print('ERROR: Failed to parse ONNX file')
                for error in range(parser.num_errors):
                    print(parser.get_error(error))
                return False

        # Configure builder
        self.config = self.builder.create_builder_config()

        # Set workspace size (in MB)
        self.config.set_memory_pool_limit(
            trt.MemoryPoolType.WORKSPACE,
            workspace_size * (1 << 20)
        )

        # Set precision
        if self.precision == 'fp16':
            if self.builder.platform_has_fast_fp16:
                self.config.set_flag(trt.BuilderFlag.FP16)
                print("FP16 mode enabled")
            else:
                print("WARNING: FP16 not supported on this platform")

        elif self.precision == 'int8':
            if self.builder.platform_has_fast_int8:
                self.config.set_flag(trt.BuilderFlag.INT8)
                # Set calibrator for INT8 (not shown here)
                print("INT8 mode enabled")
            else:
                print("WARNING: INT8 not supported on this platform")

        # Enable optimizations
        self.config.set_flag(trt.BuilderFlag.STRICT_TYPES)
        self.config.set_flag(trt.BuilderFlag.PREFER_PRECISION_CONSTRAINTS)

        # Optimization profiles for dynamic shapes
        profile = self.builder.create_optimization_profile()
        for i in range(self.network.num_inputs):
            input_tensor = self.network.get_input(i)
            input_shape = input_tensor.shape

            # Set min, optimal, and max shapes
            # Example for dynamic batch size and spatial dimensions
            min_shape = (1, input_shape[1], 224, 224)
            opt_shape = (4, input_shape[1], 224, 224)
            max_shape = (8, input_shape[1], 224, 224)

            profile.set_shape(input_tensor.name, min_shape, opt_shape, max_shape)

        self.config.add_optimization_profile(profile)

        # Enable layer timing for profiling
        self.config.profiling_verbosity = trt.ProfilingVerbosity.DETAILED

        # Build engine
        print("Building TensorRT engine... This may take a while")
        serialized_engine = self.builder.build_serialized_network(self.network, self.config)

        if serialized_engine is None:
            print("ERROR: Failed to build engine")
            return False

        # Save engine
        with open(self.engine_file, 'wb') as f:
            f.write(serialized_engine)

        print(f"Engine saved to: {self.engine_file}")
        return True

    def profile_layers(self):
        """Profile individual layers"""
        runtime = trt.Runtime(TRT_LOGGER)

        with open(self.engine_file, 'rb') as f:
            engine = runtime.deserialize_cuda_engine(f.read())

        inspector = engine.create_engine_inspector()
        print("\n=== Layer Profiling ===")
        print(inspector.get_engine_information(trt.LayerInformationFormat.JSON))

    def analyze_precision(self):
        """Analyze layer precisions"""
        runtime = trt.Runtime(TRT_LOGGER)

        with open(self.engine_file, 'rb') as f:
            engine = runtime.deserialize_cuda_engine(f.read())

        print("\n=== Precision Analysis ===")
        for layer_index in range(engine.num_layers):
            layer = engine.get_layer(layer_index)
            print(f"Layer {layer_index}: {layer.name}")
            print(f"  Type: {layer.type}")
            print(f"  Precision: {layer.precision}")
            print(f"  Output Type: {layer.get_output(0).dtype}")


class TensorRTInference:
    """TensorRT inference engine wrapper"""

    def __init__(self, engine_file):
        self.engine_file = engine_file
        self.runtime = None
        self.engine = None
        self.context = None
        self.inputs = []
        self.outputs = []
        self.bindings = []
        self.stream = None

    def load_engine(self):
        """Load TensorRT engine"""
        self.runtime = trt.Runtime(TRT_LOGGER)

        with open(self.engine_file, 'rb') as f:
            self.engine = self.runtime.deserialize_cuda_engine(f.read())

        self.context = self.engine.create_execution_context()

        # Create CUDA stream
        self.stream = cudart.cudaStreamCreate()[1]

        # Allocate buffers
        for i in range(self.engine.num_io_tensors):
            tensor_name = self.engine.get_tensor_name(i)
            dtype = trt.nptype(self.engine.get_tensor_dtype(tensor_name))
            shape = self.context.get_tensor_shape(tensor_name)
            size = trt.volume(shape)

            # Allocate host and device buffers
            host_mem = np.empty(size, dtype=dtype)
            device_mem = cudart.cudaMalloc(host_mem.nbytes)[1]

            binding = {
                'name': tensor_name,
                'host': host_mem,
                'device': device_mem,
                'shape': shape,
                'dtype': dtype
            }

            if self.engine.get_tensor_mode(tensor_name) == trt.TensorIOMode.INPUT:
                self.inputs.append(binding)
            else:
                self.outputs.append(binding)

            self.bindings.append(device_mem)

        print(f"Loaded engine: {self.engine_file}")
        print(f"Inputs: {[inp['name'] for inp in self.inputs]}")
        print(f"Outputs: {[out['name'] for out in self.outputs]}")

    def infer(self, input_data):
        """Run inference"""
        # Copy input data to device
        for i, inp in enumerate(self.inputs):
            np.copyto(inp['host'], input_data[i].ravel())
            cudart.cudaMemcpyAsync(
                inp['device'],
                inp['host'].ctypes.data,
                inp['host'].nbytes,
                cudart.cudaMemcpyKind.cudaMemcpyHostToDevice,
                self.stream
            )

        # Set tensor addresses
        for i, inp in enumerate(self.inputs):
            self.context.set_tensor_address(inp['name'], inp['device'])

        for i, out in enumerate(self.outputs):
            self.context.set_tensor_address(out['name'], out['device'])

        # Execute inference
        self.context.execute_async_v3(self.stream)

        # Copy output data from device
        outputs = []
        for out in self.outputs:
            cudart.cudaMemcpyAsync(
                out['host'].ctypes.data,
                out['device'],
                out['host'].nbytes,
                cudart.cudaMemcpyKind.cudaMemcpyDeviceToHost,
                self.stream
            )
            outputs.append(out['host'].reshape(out['shape']))

        # Synchronize stream
        cudart.cudaStreamSynchronize(self.stream)

        return outputs

    def benchmark(self, input_data, iterations=100):
        """Benchmark inference performance"""
        import time

        # Warmup
        for _ in range(10):
            self.infer(input_data)

        # Benchmark
        start = time.time()
        for _ in range(iterations):
            self.infer(input_data)
        end = time.time()

        avg_time_ms = (end - start) / iterations * 1000
        throughput = 1000.0 / avg_time_ms

        print(f"\n=== Benchmark Results ===")
        print(f"Iterations: {iterations}")
        print(f"Average latency: {avg_time_ms:.2f} ms")
        print(f"Throughput: {throughput:.2f} FPS")

        return avg_time_ms

    def cleanup(self):
        """Free resources"""
        for binding in self.inputs + self.outputs:
            cudart.cudaFree(binding['device'])
        cudart.cudaStreamDestroy(self.stream)


# Example usage
if __name__ == '__main__':
    # Optimize model
    optimizer = TensorRTOptimizer(
        onnx_file='model.onnx',
        engine_file='model_fp16.engine',
        precision='fp16'
    )

    optimizer.build_engine(max_batch_size=4, workspace_size=4096)
    optimizer.profile_layers()
    optimizer.analyze_precision()

    # Run inference
    inference = TensorRTInference('model_fp16.engine')
    inference.load_engine()

    # Dummy input
    input_data = [np.random.randn(1, 3, 224, 224).astype(np.float32)]

    # Benchmark
    inference.benchmark(input_data, iterations=100)

    # Cleanup
    inference.cleanup()
```

### 1.3 TensorRT Optimization Recipe

```python
# recipes-ai/tensorrt-models/tensorrt-model-optimizer_1.0.bb

SUMMARY = "TensorRT model optimization tools"
LICENSE = "MIT"

SRC_URI = " \
    file://tensorrt-optimizer.py \
    file://model-benchmark.py \
    file://calibration-dataset.py \
"

S = "${WORKDIR}"

RDEPENDS:${PN} = " \
    python3-tensorrt \
    python3-numpy \
    python3-pycuda \
"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 tensorrt-optimizer.py ${D}${bindir}/tensorrt-optimize
    install -m 0755 model-benchmark.py ${D}${bindir}/model-benchmark
    install -m 0755 calibration-dataset.py ${D}${bindir}/calibration-dataset
}

FILES:${PN} = "${bindir}/*"
```

---

## 2. Multi-Model Inference

### 2.1 Concurrent Model Execution

```cpp
// multi-model-inference.cpp - Concurrent multi-model inference

#include <NvInfer.h>
#include <NvInferRuntime.h>
#include <cuda_runtime_api.h>
#include <thread>
#include <vector>
#include <queue>
#include <mutex>
#include <condition_variable>
#include <memory>

using namespace nvinfer1;

class Logger : public ILogger {
    void log(Severity severity, const char* msg) noexcept override {
        if (severity <= Severity::kWARNING)
            std::cout << msg << std::endl;
    }
} gLogger;

class TensorRTModel {
private:
    std::unique_ptr<IRuntime> runtime;
    std::unique_ptr<ICudaEngine> engine;
    std::unique_ptr<IExecutionContext> context;
    cudaStream_t stream;

    std::vector<void*> bindings;
    std::vector<size_t> binding_sizes;

public:
    TensorRTModel(const std::string& engine_file) {
        // Load engine
        std::ifstream file(engine_file, std::ios::binary);
        if (!file.good()) {
            throw std::runtime_error("Failed to load engine file");
        }

        file.seekg(0, file.end);
        size_t size = file.tellg();
        file.seekg(0, file.beg);

        std::vector<char> engine_data(size);
        file.read(engine_data.data(), size);

        runtime.reset(createInferRuntime(gLogger));
        engine.reset(runtime->deserializeCudaEngine(engine_data.data(), size));
        context.reset(engine->createExecutionContext());

        // Create CUDA stream
        cudaStreamCreate(&stream);

        // Allocate buffers
        int num_bindings = engine->getNbBindings();
        bindings.resize(num_bindings);
        binding_sizes.resize(num_bindings);

        for (int i = 0; i < num_bindings; i++) {
            Dims dims = engine->getBindingDimensions(i);
            DataType dtype = engine->getBindingDataType(i);

            size_t vol = 1;
            for (int j = 0; j < dims.nbDims; j++) {
                vol *= dims.d[j];
            }

            size_t element_size = dtype == DataType::kFLOAT ? 4 :
                                 dtype == DataType::kHALF ? 2 : 1;
            binding_sizes[i] = vol * element_size;

            cudaMalloc(&bindings[i], binding_sizes[i]);
        }
    }

    ~TensorRTModel() {
        for (auto binding : bindings) {
            cudaFree(binding);
        }
        cudaStreamDestroy(stream);
    }

    void infer(const std::vector<void*>& inputs, std::vector<void*>& outputs) {
        int num_inputs = 0;
        int num_outputs = 0;

        // Copy inputs to device
        for (int i = 0; i < engine->getNbBindings(); i++) {
            if (engine->bindingIsInput(i)) {
                cudaMemcpyAsync(bindings[i], inputs[num_inputs],
                              binding_sizes[i],
                              cudaMemcpyHostToDevice, stream);
                num_inputs++;
            }
        }

        // Execute inference
        context->enqueueV2(bindings.data(), stream, nullptr);

        // Copy outputs from device
        for (int i = 0; i < engine->getNbBindings(); i++) {
            if (!engine->bindingIsInput(i)) {
                cudaMemcpyAsync(outputs[num_outputs], bindings[i],
                              binding_sizes[i],
                              cudaMemcpyDeviceToHost, stream);
                num_outputs++;
            }
        }

        // Synchronize
        cudaStreamSynchronize(stream);
    }

    cudaStream_t getStream() const { return stream; }
};

// Multi-model manager with thread pool
class MultiModelManager {
private:
    struct InferenceRequest {
        int model_id;
        std::vector<void*> inputs;
        std::vector<void*> outputs;
        std::promise<void> promise;
    };

    std::vector<std::unique_ptr<TensorRTModel>> models;
    std::vector<std::thread> workers;
    std::queue<InferenceRequest> request_queue;
    std::mutex queue_mutex;
    std::condition_variable queue_cv;
    bool shutdown;

    void worker_thread(int worker_id) {
        while (true) {
            InferenceRequest req;

            {
                std::unique_lock<std::mutex> lock(queue_mutex);
                queue_cv.wait(lock, [this] {
                    return !request_queue.empty() || shutdown;
                });

                if (shutdown && request_queue.empty()) {
                    return;
                }

                req = std::move(request_queue.front());
                request_queue.pop();
            }

            // Execute inference
            try {
                models[req.model_id]->infer(req.inputs, req.outputs);
                req.promise.set_value();
            } catch (const std::exception& e) {
                req.promise.set_exception(std::current_exception());
            }
        }
    }

public:
    MultiModelManager(int num_workers = 4) : shutdown(false) {
        // Create worker threads
        for (int i = 0; i < num_workers; i++) {
            workers.emplace_back(&MultiModelManager::worker_thread, this, i);
        }
    }

    ~MultiModelManager() {
        {
            std::lock_guard<std::mutex> lock(queue_mutex);
            shutdown = true;
        }
        queue_cv.notify_all();

        for (auto& worker : workers) {
            worker.join();
        }
    }

    int load_model(const std::string& engine_file) {
        int model_id = models.size();
        models.emplace_back(std::make_unique<TensorRTModel>(engine_file));
        return model_id;
    }

    std::future<void> infer_async(int model_id,
                                  const std::vector<void*>& inputs,
                                  std::vector<void*>& outputs) {
        InferenceRequest req;
        req.model_id = model_id;
        req.inputs = inputs;
        req.outputs = outputs;

        auto future = req.promise.get_future();

        {
            std::lock_guard<std::mutex> lock(queue_mutex);
            request_queue.push(std::move(req));
        }
        queue_cv.notify_one();

        return future;
    }
};

// Example: Multi-model pipeline
class VisionPipeline {
private:
    MultiModelManager manager;
    int detection_model;
    int classification_model;
    int tracking_model;

public:
    VisionPipeline() : manager(4) {
        // Load models
        detection_model = manager.load_model("detection.engine");
        classification_model = manager.load_model("classification.engine");
        tracking_model = manager.load_model("tracking.engine");
    }

    void process_frame(void* frame_data) {
        // Stage 1: Object detection
        std::vector<void*> det_inputs = {frame_data};
        std::vector<void*> det_outputs(1);
        auto det_future = manager.infer_async(detection_model, det_inputs, det_outputs);

        // Wait for detection to complete
        det_future.get();

        // Stage 2: Object classification (parallel for each detection)
        std::vector<std::future<void>> class_futures;
        // Assuming det_outputs[0] contains bounding boxes
        // Extract and classify each object...

        // Stage 3: Tracking
        std::vector<void*> track_inputs = {det_outputs[0]};
        std::vector<void*> track_outputs(1);
        auto track_future = manager.infer_async(tracking_model, track_inputs, track_outputs);

        // Wait for all to complete
        for (auto& future : class_futures) {
            future.get();
        }
        track_future.get();
    }
};

int main() {
    VisionPipeline pipeline;

    // Process video frames
    for (int frame = 0; frame < 1000; frame++) {
        void* frame_data = nullptr; // Load frame data
        pipeline.process_frame(frame_data);
    }

    return 0;
}
```

### 2.2 Model Ensemble Strategy

```python
# recipes-ai/model-ensemble/model-ensemble_1.0.bb

SUMMARY = "Multi-model ensemble inference"
LICENSE = "MIT"

SRC_URI = "file://ensemble.cpp \
           file://CMakeLists.txt \
          "

S = "${WORKDIR}"

DEPENDS = "cuda-toolkit tensorrt opencv"

inherit cmake

EXTRA_OECMAKE = " \
    -DCUDA_TOOLKIT_ROOT_DIR=${STAGING_DIR_NATIVE}/usr/local/cuda \
    -DTensorRT_DIR=${STAGING_DIR_TARGET}/usr/lib \
"

FILES:${PN} = "${bindir}/*"
```

---

## 3. Quantization Techniques

### 3.1 Post-Training Quantization (PTQ)

```python
#!/usr/bin/env python3
# ptq-quantization.py - Post-training quantization for INT8

import tensorrt as trt
import numpy as np
from typing import List
import pycuda.driver as cuda
import pycuda.autoinit

class Int8Calibrator(trt.IInt8EntropyCalibrator2):
    """INT8 calibrator for post-training quantization"""

    def __init__(self, calibration_dataset, batch_size=8, cache_file="calibration.cache"):
        trt.IInt8EntropyCalibrator2.__init__(self)

        self.calibration_dataset = calibration_dataset
        self.batch_size = batch_size
        self.cache_file = cache_file
        self.current_index = 0

        # Allocate device memory for calibration batch
        self.device_input = cuda.mem_alloc(
            calibration_dataset[0].nbytes * batch_size
        )

    def get_batch_size(self):
        return self.batch_size

    def get_batch(self, names):
        """Get next calibration batch"""
        if self.current_index + self.batch_size > len(self.calibration_dataset):
            return None

        # Get batch
        batch = []
        for i in range(self.batch_size):
            batch.append(self.calibration_dataset[self.current_index + i])
        self.current_index += self.batch_size

        # Copy to device
        batch_array = np.ascontiguousarray(batch)
        cuda.memcpy_htod(self.device_input, batch_array)

        return [int(self.device_input)]

    def read_calibration_cache(self):
        """Read calibration cache if exists"""
        if os.path.exists(self.cache_file):
            with open(self.cache_file, "rb") as f:
                return f.read()
        return None

    def write_calibration_cache(self, cache):
        """Write calibration cache"""
        with open(self.cache_file, "wb") as f:
            f.write(cache)


def build_int8_engine(onnx_file, calibration_dataset, engine_file):
    """Build INT8 quantized TensorRT engine"""

    TRT_LOGGER = trt.Logger(trt.Logger.INFO)
    builder = trt.Builder(TRT_LOGGER)

    network_flags = 1 << int(trt.NetworkDefinitionCreationFlag.EXPLICIT_BATCH)
    network = builder.create_network(network_flags)

    # Parse ONNX
    parser = trt.OnnxParser(network, TRT_LOGGER)
    with open(onnx_file, 'rb') as f:
        if not parser.parse(f.read()):
            print('ERROR: Failed to parse ONNX file')
            return False

    # Configure builder
    config = builder.create_builder_config()
    config.set_memory_pool_limit(trt.MemoryPoolType.WORKSPACE, 4 << 30)  # 4GB

    # Enable INT8
    config.set_flag(trt.BuilderFlag.INT8)

    # Set calibrator
    calibrator = Int8Calibrator(calibration_dataset)
    config.int8_calibrator = calibrator

    # Build engine
    print("Building INT8 engine with calibration...")
    serialized_engine = builder.build_serialized_network(network, config)

    # Save engine
    with open(engine_file, 'wb') as f:
        f.write(serialized_engine)

    print(f"INT8 engine saved to: {engine_file}")
    return True


def compare_accuracy(fp32_engine, int8_engine, test_dataset):
    """Compare accuracy between FP32 and INT8 models"""

    def run_inference(engine_file, inputs):
        # Load and run inference (simplified)
        runtime = trt.Runtime(trt.Logger())
        with open(engine_file, 'rb') as f:
            engine = runtime.deserialize_cuda_engine(f.read())

        context = engine.create_execution_context()
        # Run inference and return outputs
        # ...
        return outputs

    fp32_outputs = []
    int8_outputs = []

    for input_data in test_dataset:
        fp32_out = run_inference(fp32_engine, input_data)
        int8_out = run_inference(int8_engine, input_data)

        fp32_outputs.append(fp32_out)
        int8_outputs.append(int8_out)

    # Calculate accuracy metrics
    mse = np.mean([(fp32 - int8)**2 for fp32, int8 in zip(fp32_outputs, int8_outputs)])
    print(f"Mean Squared Error: {mse}")

    return mse
```

### 3.2 Quantization-Aware Training (QAT)

```python
#!/usr/bin/env python3
# qat-training.py - Quantization-aware training

import torch
import torch.nn as nn
import torch.quantization as quantization

class QuantizedModel(nn.Module):
    """Model with quantization-aware training"""

    def __init__(self, model):
        super().__init__()
        self.model = model
        self.quant = quantization.QuantStub()
        self.dequant = quantization.DeQuantStub()

    def forward(self, x):
        x = self.quant(x)
        x = self.model(x)
        x = self.dequant(x)
        return x

def prepare_qat_model(model):
    """Prepare model for QAT"""

    # Wrap model
    qat_model = QuantizedModel(model)

    # Configure quantization
    qat_model.qconfig = quantization.get_default_qat_qconfig('fbgemm')

    # Prepare for QAT
    quantization.prepare_qat(qat_model, inplace=True)

    return qat_model

def train_qat(qat_model, train_loader, epochs=10):
    """Train with quantization-aware training"""

    optimizer = torch.optim.Adam(qat_model.parameters(), lr=0.001)
    criterion = nn.CrossEntropyLoss()

    qat_model.train()

    for epoch in range(epochs):
        for inputs, labels in train_loader:
            optimizer.zero_grad()

            outputs = qat_model(inputs)
            loss = criterion(outputs, labels)

            loss.backward()
            optimizer.step()

        print(f"Epoch {epoch+1}/{epochs}, Loss: {loss.item():.4f}")

    return qat_model

def convert_to_quantized(qat_model):
    """Convert QAT model to fully quantized model"""

    qat_model.eval()
    quantized_model = quantization.convert(qat_model, inplace=False)

    return quantized_model

# Example usage
if __name__ == '__main__':
    # Load pretrained model
    model = torch.load('model.pth')

    # Prepare for QAT
    qat_model = prepare_qat_model(model)

    # Train with QAT
    train_qat(qat_model, train_loader, epochs=10)

    # Convert to quantized
    quantized_model = convert_to_quantized(qat_model)

    # Export to ONNX
    torch.onnx.export(quantized_model, dummy_input, 'quantized_model.onnx')

    # Convert to TensorRT INT8
    build_int8_engine('quantized_model.onnx', calibration_dataset, 'quantized.engine')
```

---

## 4. Custom CUDA Kernels

### 4.1 Optimized Preprocessing Kernel

```cuda
// preprocessing_kernels.cu - Custom CUDA kernels for preprocessing

#include <cuda_runtime.h>
#include <cuda_fp16.h>

// Fused preprocessing: resize, normalize, and convert to FP16
__global__ void preprocess_kernel(
    const uint8_t* __restrict__ input,
    __half* __restrict__ output,
    int input_height, int input_width,
    int output_height, int output_width,
    float scale_y, float scale_x,
    float mean_r, float mean_g, float mean_b,
    float std_r, float std_g, float std_b)
{
    int out_x = blockIdx.x * blockDim.x + threadIdx.x;
    int out_y = blockIdx.y * blockDim.y + threadIdx.y;

    if (out_x >= output_width || out_y >= output_height)
        return;

    // Calculate source coordinates (bilinear interpolation)
    float src_x = out_x * scale_x;
    float src_y = out_y * scale_y;

    int x0 = (int)src_x;
    int y0 = (int)src_y;
    int x1 = min(x0 + 1, input_width - 1);
    int y1 = min(y0 + 1, input_height - 1);

    float dx = src_x - x0;
    float dy = src_y - y0;

    // Load 4 pixels for bilinear interpolation
    int idx00 = (y0 * input_width + x0) * 3;
    int idx01 = (y0 * input_width + x1) * 3;
    int idx10 = (y1 * input_width + x0) * 3;
    int idx11 = (y1 * input_width + x1) * 3;

    // Interpolate each channel
    float r = (1-dx)*(1-dy)*input[idx00+0] + dx*(1-dy)*input[idx01+0] +
              (1-dx)*dy*input[idx10+0] + dx*dy*input[idx11+0];
    float g = (1-dx)*(1-dy)*input[idx00+1] + dx*(1-dy)*input[idx01+1] +
              (1-dx)*dy*input[idx10+1] + dx*dy*input[idx11+1];
    float b = (1-dx)*(1-dy)*input[idx00+2] + dx*(1-dy)*input[idx01+2] +
              (1-dx)*dy*input[idx10+2] + dx*dy*input[idx11+2];

    // Normalize
    r = (r / 255.0f - mean_r) / std_r;
    g = (g / 255.0f - mean_g) / std_g;
    b = (b / 255.0f - mean_b) / std_b;

    // Convert to FP16 and store in CHW format
    int out_idx = out_y * output_width + out_x;
    output[0 * output_height * output_width + out_idx] = __float2half(r);
    output[1 * output_height * output_width + out_idx] = __float2half(g);
    output[2 * output_height * output_width + out_idx] = __float2half(b);
}

// Optimized NMS (Non-Maximum Suppression)
__global__ void nms_kernel(
    const float* __restrict__ boxes,
    const float* __restrict__ scores,
    bool* __restrict__ keep,
    int num_boxes,
    float iou_threshold)
{
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx >= num_boxes || !keep[idx])
        return;

    float x1 = boxes[idx * 4 + 0];
    float y1 = boxes[idx * 4 + 1];
    float x2 = boxes[idx * 4 + 2];
    float y2 = boxes[idx * 4 + 3];
    float area = (x2 - x1) * (y2 - y1);

    for (int i = idx + 1; i < num_boxes; i++) {
        if (!keep[i])
            continue;

        float x1_i = boxes[i * 4 + 0];
        float y1_i = boxes[i * 4 + 1];
        float x2_i = boxes[i * 4 + 2];
        float y2_i = boxes[i * 4 + 3];

        // Calculate intersection
        float xx1 = max(x1, x1_i);
        float yy1 = max(y1, y1_i);
        float xx2 = min(x2, x2_i);
        float yy2 = min(y2, y2_i);

        float w = max(0.0f, xx2 - xx1);
        float h = max(0.0f, yy2 - yy1);
        float intersection = w * h;

        // Calculate IoU
        float area_i = (x2_i - x1_i) * (y2_i - y1_i);
        float iou = intersection / (area + area_i - intersection);

        if (iou > iou_threshold) {
            keep[i] = false;
        }
    }
}

// Host interface
extern "C" {

void launch_preprocess_kernel(
    const uint8_t* input,
    __half* output,
    int input_height, int input_width,
    int output_height, int output_width,
    float* mean, float* std,
    cudaStream_t stream)
{
    float scale_y = (float)input_height / output_height;
    float scale_x = (float)input_width / output_width;

    dim3 block(16, 16);
    dim3 grid(
        (output_width + block.x - 1) / block.x,
        (output_height + block.y - 1) / block.y
    );

    preprocess_kernel<<<grid, block, 0, stream>>>(
        input, output,
        input_height, input_width,
        output_height, output_width,
        scale_y, scale_x,
        mean[0], mean[1], mean[2],
        std[0], std[1], std[2]
    );
}

void launch_nms_kernel(
    const float* boxes,
    const float* scores,
    bool* keep,
    int num_boxes,
    float iou_threshold,
    cudaStream_t stream)
{
    dim3 block(256);
    dim3 grid((num_boxes + block.x - 1) / block.x);

    nms_kernel<<<grid, block, 0, stream>>>(
        boxes, scores, keep, num_boxes, iou_threshold
    );
}

} // extern "C"
```

### 4.2 Custom Layer Plugin

```cpp
// custom_plugin.cpp - TensorRT custom layer plugin

#include "NvInfer.h"
#include "NvInferPlugin.h"
#include <cuda_runtime.h>
#include <vector>
#include <string>
#include <cassert>

using namespace nvinfer1;
using namespace nvinfer1::plugin;

// Custom layer implementation
class CustomLayerPlugin : public IPluginV2IOExt {
private:
    std::string mNamespace;
    int mNumOutputs;
    DataType mDataType;

public:
    CustomLayerPlugin() : mNumOutputs(1), mDataType(DataType::kFLOAT) {}

    CustomLayerPlugin(const void* data, size_t length) {
        const char* d = static_cast<const char*>(data);
        const char* a = d;

        mNumOutputs = read<int>(d);
        mDataType = read<DataType>(d);

        assert(d == a + length);
    }

    // IPluginV2 methods
    const char* getPluginType() const noexcept override {
        return "CustomLayer";
    }

    const char* getPluginVersion() const noexcept override {
        return "1";
    }

    int getNbOutputs() const noexcept override {
        return mNumOutputs;
    }

    Dims getOutputDimensions(int index, const Dims* inputs, int nbInputDims) noexcept override {
        // Define output dimensions based on input
        assert(nbInputDims == 1);
        return inputs[0];
    }

    int initialize() noexcept override {
        return 0;
    }

    void terminate() noexcept override {}

    size_t getWorkspaceSize(int maxBatchSize) const noexcept override {
        return 0;
    }

    int enqueue(int batchSize, const void* const* inputs, void* const* outputs,
                void* workspace, cudaStream_t stream) noexcept override {
        // Launch custom CUDA kernel
        const float* input = static_cast<const float*>(inputs[0]);
        float* output = static_cast<float*>(outputs[0]);

        // Custom kernel launch
        // custom_kernel<<<grid, block, 0, stream>>>(input, output, ...);

        return 0;
    }

    size_t getSerializationSize() const noexcept override {
        return sizeof(mNumOutputs) + sizeof(mDataType);
    }

    void serialize(void* buffer) const noexcept override {
        char* d = static_cast<char*>(buffer);
        const char* a = d;

        write(d, mNumOutputs);
        write(d, mDataType);

        assert(d == a + getSerializationSize());
    }

    void configurePlugin(const PluginTensorDesc* in, int nbInput,
                        const PluginTensorDesc* out, int nbOutput) noexcept override {
        assert(nbInput == 1);
        assert(nbOutput == 1);
        mDataType = in[0].type;
    }

    bool supportsFormatCombination(int pos, const PluginTensorDesc* inOut,
                                  int nbInputs, int nbOutputs) const noexcept override {
        assert(pos < nbInputs + nbOutputs);
        return inOut[pos].type == DataType::kFLOAT && inOut[pos].format == TensorFormat::kLINEAR;
    }

    DataType getOutputDataType(int index, const DataType* inputTypes, int nbInputs) const noexcept override {
        return inputTypes[0];
    }

    void destroy() noexcept override {
        delete this;
    }

    IPluginV2Ext* clone() const noexcept override {
        auto* plugin = new CustomLayerPlugin(*this);
        return plugin;
    }

    void setPluginNamespace(const char* pluginNamespace) noexcept override {
        mNamespace = pluginNamespace;
    }

    const char* getPluginNamespace() const noexcept override {
        return mNamespace.c_str();
    }

private:
    template<typename T>
    void write(char*& buffer, const T& val) const {
        *reinterpret_cast<T*>(buffer) = val;
        buffer += sizeof(T);
    }

    template<typename T>
    T read(const char*& buffer) {
        T val = *reinterpret_cast<const T*>(buffer);
        buffer += sizeof(T);
        return val;
    }
};

// Plugin creator
class CustomLayerPluginCreator : public IPluginCreator {
public:
    const char* getPluginName() const noexcept override {
        return "CustomLayer";
    }

    const char* getPluginVersion() const noexcept override {
        return "1";
    }

    const PluginFieldCollection* getFieldNames() noexcept override {
        return nullptr;
    }

    IPluginV2* createPlugin(const char* name, const PluginFieldCollection* fc) noexcept override {
        return new CustomLayerPlugin();
    }

    IPluginV2* deserializePlugin(const char* name, const void* serialData, size_t serialLength) noexcept override {
        return new CustomLayerPlugin(serialData, serialLength);
    }

    void setPluginNamespace(const char* pluginNamespace) noexcept override {}

    const char* getPluginNamespace() const noexcept override {
        return "";
    }
};

// Register plugin
REGISTER_TENSORRT_PLUGIN(CustomLayerPluginCreator);
```

---

## 5. Pipeline Optimization

### 5.1 End-to-End Inference Pipeline

```python
#!/usr/bin/env python3
# optimized-pipeline.py - Optimized inference pipeline

import numpy as np
import cv2
import tensorrt as trt
import pycuda.driver as cuda
import pycuda.autoinit
from threading import Thread
from queue import Queue
import time

class OptimizedInferencePipeline:
    """End-to-end optimized inference pipeline"""

    def __init__(self, engine_file, num_streams=4):
        self.engine_file = engine_file
        self.num_streams = num_streams

        # Load TensorRT engine
        self.runtime = trt.Runtime(trt.Logger())
        with open(engine_file, 'rb') as f:
            self.engine = self.runtime.deserialize_cuda_engine(f.read())

        # Create execution contexts (one per stream)
        self.contexts = [self.engine.create_execution_context()
                        for _ in range(num_streams)]

        # Create CUDA streams
        self.streams = [cuda.Stream() for _ in range(num_streams)]

        # Allocate device buffers
        self.device_buffers = []
        for _ in range(num_streams):
            buffers = self._allocate_buffers()
            self.device_buffers.append(buffers)

        # Processing queues
        self.input_queue = Queue(maxsize=num_streams * 2)
        self.output_queue = Queue()

        # Start worker threads
        self.workers = []
        for i in range(num_streams):
            worker = Thread(target=self._worker, args=(i,))
            worker.daemon = True
            worker.start()
            self.workers.append(worker)

    def _allocate_buffers(self):
        """Allocate device buffers for one stream"""
        buffers = {'inputs': [], 'outputs': []}

        for i in range(self.engine.num_io_tensors):
            tensor_name = self.engine.get_tensor_name(i)
            dtype = trt.nptype(self.engine.get_tensor_dtype(tensor_name))
            shape = self.engine.get_tensor_shape(tensor_name)
            size = trt.volume(shape) * dtype().itemsize

            device_mem = cuda.mem_alloc(size)

            if self.engine.get_tensor_mode(tensor_name) == trt.TensorIOMode.INPUT:
                buffers['inputs'].append((tensor_name, device_mem, dtype, shape))
            else:
                buffers['outputs'].append((tensor_name, device_mem, dtype, shape))

        return buffers

    def _worker(self, worker_id):
        """Worker thread for inference"""
        context = self.contexts[worker_id]
        stream = self.streams[worker_id]
        buffers = self.device_buffers[worker_id]

        while True:
            # Get input from queue
            item = self.input_queue.get()
            if item is None:  # Poison pill
                break

            frame_id, frame_data = item

            # Preprocess and copy to device
            preprocessed = self._preprocess(frame_data)
            for i, (name, device_mem, dtype, shape) in enumerate(buffers['inputs']):
                host_mem = np.ascontiguousarray(preprocessed[i])
                cuda.memcpy_htod_async(device_mem, host_mem, stream)
                context.set_tensor_address(name, int(device_mem))

            # Set output addresses
            for name, device_mem, dtype, shape in buffers['outputs']:
                context.set_tensor_address(name, int(device_mem))

            # Execute
            context.execute_async_v3(stream.handle)

            # Copy outputs from device
            outputs = []
            for name, device_mem, dtype, shape in buffers['outputs']:
                host_mem = np.empty(shape, dtype=dtype)
                cuda.memcpy_dtoh_async(host_mem, device_mem, stream)
                outputs.append(host_mem)

            # Synchronize
            stream.synchronize()

            # Postprocess
            result = self._postprocess(outputs)

            # Put result in output queue
            self.output_queue.put((frame_id, result))

    def _preprocess(self, frame):
        """Preprocess input frame"""
        # Resize
        resized = cv2.resize(frame, (224, 224))

        # Normalize
        normalized = (resized.astype(np.float32) / 255.0 - 0.5) / 0.5

        # Convert to CHW format
        transposed = np.transpose(normalized, (2, 0, 1))

        # Add batch dimension
        batched = np.expand_dims(transposed, axis=0)

        return [batched]

    def _postprocess(self, outputs):
        """Postprocess model outputs"""
        # Example: classification
        predictions = outputs[0]
        class_id = np.argmax(predictions)
        confidence = predictions[0][class_id]

        return {'class_id': int(class_id), 'confidence': float(confidence)}

    def submit_frame(self, frame_id, frame):
        """Submit frame for processing"""
        self.input_queue.put((frame_id, frame))

    def get_result(self, timeout=None):
        """Get processed result"""
        return self.output_queue.get(timeout=timeout)

    def shutdown(self):
        """Shutdown pipeline"""
        for _ in range(self.num_streams):
            self.input_queue.put(None)

        for worker in self.workers:
            worker.join()

# Example usage
if __name__ == '__main__':
    pipeline = OptimizedInferencePipeline('model.engine', num_streams=4)

    # Video capture
    cap = cv2.VideoCapture('video.mp4')

    frame_id = 0
    start_time = time.time()

    while True:
        ret, frame = cap.read()
        if not ret:
            break

        # Submit frame
        pipeline.submit_frame(frame_id, frame)
        frame_id += 1

        # Get result (non-blocking)
        try:
            result_id, result = pipeline.get_result(timeout=0.001)
            print(f"Frame {result_id}: Class {result['class_id']}, "
                  f"Confidence: {result['confidence']:.2f}")
        except:
            pass

    # Wait for remaining results
    while frame_id > 0:
        result_id, result = pipeline.get_result()
        frame_id -= 1

    elapsed = time.time() - start_time
    fps = frame_id / elapsed
    print(f"Processed {frame_id} frames in {elapsed:.2f}s ({fps:.2f} FPS)")

    pipeline.shutdown()
```

---

## 6. Performance Benchmarking

### 6.1 Comprehensive Benchmark Suite

```bash
#!/bin/bash
# benchmark-inference.sh - Comprehensive inference benchmarking

MODELS_DIR="/opt/models"
RESULTS_DIR="/var/log/benchmarks"
TIMESTAMP=$(date +%Y%m%d-%H%M%S)

mkdir -p "${RESULTS_DIR}"

# Benchmark configurations
BATCH_SIZES="1 4 8 16"
PRECISIONS="fp32 fp16 int8"

for model in ${MODELS_DIR}/*.onnx; do
    model_name=$(basename "$model" .onnx)

    for precision in $PRECISIONS; do
        engine="${MODELS_DIR}/${model_name}_${precision}.engine"

        # Build engine if not exists
        if [ ! -f "$engine" ]; then
            echo "Building $precision engine for $model_name..."
            tensorrt-optimize --onnx="$model" --engine="$engine" --precision="$precision"
        fi

        for batch in $BATCH_SIZES; do
            echo "Benchmarking: $model_name, $precision, batch=$batch"

            # Run trtexec benchmark
            /usr/bin/trtexec \
                --loadEngine="$engine" \
                --batch=$batch \
                --iterations=1000 \
                --warmUp=100 \
                --dumpProfile \
                --exportProfile="${RESULTS_DIR}/${model_name}_${precision}_batch${batch}_${TIMESTAMP}.json" \
                > "${RESULTS_DIR}/${model_name}_${precision}_batch${batch}_${TIMESTAMP}.txt"
        done
    done
done

# Generate summary report
python3 << 'EOF'
import json
import os
import glob
import pandas as pd

results = []

for file in glob.glob(f"{RESULTS_DIR}/*_{TIMESTAMP}.json"):
    with open(file) as f:
        data = json.load(f)

    # Extract metrics
    filename = os.path.basename(file).replace('.json', '')
    parts = filename.split('_')

    results.append({
        'Model': parts[0],
        'Precision': parts[1],
        'Batch Size': int(parts[2].replace('batch', '')),
        'Latency (ms)': data.get('averageLatency', 0),
        'Throughput (FPS)': data.get('throughput', 0),
        'GPU Utilization (%)': data.get('gpuUtilization', 0),
    })

# Create DataFrame
df = pd.DataFrame(results)
df = df.sort_values(['Model', 'Precision', 'Batch Size'])

# Save to CSV
csv_file = f"{RESULTS_DIR}/benchmark_summary_{TIMESTAMP}.csv"
df.to_csv(csv_file, index=False)

print(df.to_string())
print(f"\nSummary saved to: {csv_file}")
EOF

echo "Benchmarking complete. Results in ${RESULTS_DIR}/"
```

---

## Best Practices Summary

1. **Use TensorRT**: Always optimize models with TensorRT for Jetson
2. **FP16 by Default**: Use FP16 precision for best performance/accuracy tradeoff
3. **INT8 for Scale**: Use INT8 quantization for maximum throughput
4. **Batch Wisely**: Find optimal batch size for your latency requirements
5. **Stream Everything**: Use CUDA streams for concurrent operations
6. **Custom Kernels**: Write custom CUDA kernels for unique operations
7. **Profile Extensively**: Use Nsight Systems/Compute for profiling
8. **Pipeline Optimization**: Overlap preprocessing, inference, and postprocessing
9. **Memory Management**: Reuse buffers, avoid allocations in hot paths
10. **Benchmark Regularly**: Track performance across model updates

---

## Conclusion

This completes the advanced learning materials for Yocto & Meta-Tegra systems. All six modules provide comprehensive coverage of:

1. Performance Optimization
2. Production Deployment
3. Custom BSP Development
4. Real-Time Systems
5. Debugging Techniques
6. AI/ML Optimization

These materials are designed for experienced developers building production-grade systems on NVIDIA Jetson platforms using Yocto Project.
