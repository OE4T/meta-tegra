# Tutorial 09: AI Inference on Jetson
## Deploying Deep Learning Models with TensorRT

---

## Learning Objectives

After completing this tutorial, you will be able to:
- Set up TensorRT and CUDA on Jetson
- Convert models to TensorRT engines
- Optimize models for Jetson hardware
- Create real-time inference pipelines
- Integrate cameras with AI models
- Use DeepStream for video analytics
- Measure and optimize inference performance

---

## Prerequisites Checklist

- [ ] Completed Tutorial 01-08 (through camera integration)
- [ ] Understanding of deep learning basics
- [ ] Python with numpy installed
- [ ] Camera working on Jetson
- [ ] At least 16GB storage for models
- [ ] CUDA and TensorRT libraries available

---

## Estimated Duration

**Total Time**: 6-8 hours
- CUDA/TensorRT setup: 1 hour
- Model conversion: 1.5 hours
- Python inference: 1.5 hours
- C++ inference: 1.5 hours
- Pipeline integration: 1.5-2 hours
- Optimization: 1-2 hours

---

## Step-by-Step Instructions

### Step 1: Verify CUDA and TensorRT Installation

Check that AI libraries are available:

```bash
# On Jetson device

# Check CUDA version
nvcc --version

# Expected output:
# Cuda compilation tools, release 11.4, V11.4.315

# Check TensorRT
dpkg -l | grep -i tensorrt

# Check cuDNN
dpkg -l | grep -i cudnn

# Verify GPU is accessible
nvidia-smi

# Expected output showing GPU utilization

# Test CUDA device query
cat > /tmp/cuda-test.py << 'EOF'
import ctypes

# Try to load CUDA library
try:
    cuda = ctypes.CDLL('libcuda.so.1')
    print("✓ CUDA library loaded")

    # Get CUDA device count
    count = ctypes.c_int()
    cuda.cuInit(0)
    cuda.cuDeviceGetCount(ctypes.byref(count))
    print(f"✓ Found {count.value} CUDA device(s)")
except Exception as e:
    print(f"✗ Error: {e}")
EOF

python3 /tmp/cuda-test.py
```

### Step 2: Install AI Framework Dependencies

Add necessary Python packages to your image:

```bash
# On host development machine
cd ~/yocto-jetson/meta-custom

# Create AI packages recipe
cat > recipes-devtools/python/python3-ai-packages_1.0.bb << 'EOF'
SUMMARY = "Python AI/ML packages for Jetson"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    python3-numpy \
    python3-pillow \
    python3-opencv \
    python3-pycuda \
    python3-tensorrt \
"

# Note: Some packages need to be built from source or use NVIDIA's repositories
EOF

# Add to image
cd ~/yocto-jetson/builds/jetson-orin-agx
echo 'IMAGE_INSTALL:append = " \
    cuda-libraries \
    cudnn \
    tensorrt \
    python3-numpy \
    python3-opencv \
"' >> conf/local.conf
```

### Step 3: Download and Prepare a Model

Get a pre-trained model for testing:

```bash
# On Jetson, create workspace
mkdir -p ~/ai-models
cd ~/ai-models

# Download ResNet-50 ONNX model
wget https://github.com/onnx/models/raw/main/vision/classification/resnet/model/resnet50-v2-7.onnx

# Download ImageNet labels
wget https://raw.githubusercontent.com/anishathalye/imagenet-simple-labels/master/imagenet-simple-labels.json

# Verify download
ls -lh *.onnx
```

### Step 4: Convert Model to TensorRT

Create TensorRT engine from ONNX:

```bash
# Convert ONNX to TensorRT engine
cat > ~/ai-models/convert_to_tensorrt.py << 'EOF'
#!/usr/bin/env python3
"""
Convert ONNX model to TensorRT engine
"""

import tensorrt as trt
import sys

def build_engine(onnx_path, engine_path, precision='fp16'):
    """Build TensorRT engine from ONNX model"""

    # Create logger
    logger = trt.Logger(trt.Logger.INFO)

    # Create builder
    builder = trt.Builder(logger)

    # Create network
    network_flags = 1 << int(trt.NetworkDefinitionCreationFlag.EXPLICIT_BATCH)
    network = builder.create_network(network_flags)

    # Create ONNX parser
    parser = trt.OnnxParser(network, logger)

    # Parse ONNX model
    print(f"Loading ONNX model: {onnx_path}")
    with open(onnx_path, 'rb') as f:
        if not parser.parse(f.read()):
            print("ERROR: Failed to parse ONNX model")
            for error in range(parser.num_errors):
                print(parser.get_error(error))
            return None

    print(f"✓ ONNX model parsed successfully")
    print(f"  Network inputs: {network.num_inputs}")
    print(f"  Network outputs: {network.num_outputs}")

    # Create builder config
    config = builder.create_builder_config()

    # Set maximum workspace size (1GB)
    config.set_memory_pool_limit(trt.MemoryPoolType.WORKSPACE, 1 << 30)

    # Set precision
    if precision == 'fp16':
        if builder.platform_has_fast_fp16:
            config.set_flag(trt.BuilderFlag.FP16)
            print("✓ FP16 mode enabled")
        else:
            print("⚠ FP16 not supported, using FP32")
    elif precision == 'int8':
        if builder.platform_has_fast_int8:
            config.set_flag(trt.BuilderFlag.INT8)
            print("✓ INT8 mode enabled")
            # Note: INT8 requires calibration data (not shown here)
        else:
            print("⚠ INT8 not supported, using FP32")

    # Build engine
    print("Building TensorRT engine...")
    print("This may take several minutes...")
    serialized_engine = builder.build_serialized_network(network, config)

    if serialized_engine is None:
        print("ERROR: Failed to build engine")
        return None

    # Save engine
    print(f"Saving engine to: {engine_path}")
    with open(engine_path, 'wb') as f:
        f.write(serialized_engine)

    print("✓ TensorRT engine created successfully")

    # Print engine info
    runtime = trt.Runtime(logger)
    engine = runtime.deserialize_cuda_engine(serialized_engine)

    print(f"\nEngine Information:")
    print(f"  Max batch size: {engine.max_batch_size}")
    print(f"  Device memory size: {engine.device_memory_size / 1024 / 1024:.2f} MB")
    print(f"  Number of bindings: {engine.num_bindings}")

    for i in range(engine.num_bindings):
        name = engine.get_binding_name(i)
        shape = engine.get_binding_shape(i)
        dtype = engine.get_binding_dtype(i)
        print(f"  Binding {i}: {name} | Shape: {shape} | Type: {dtype}")

    return engine_path

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python3 convert_to_tensorrt.py <input.onnx> <output.trt> [fp16|fp32|int8]")
        sys.exit(1)

    onnx_path = sys.argv[1]
    engine_path = sys.argv[2]
    precision = sys.argv[3] if len(sys.argv) > 3 else 'fp16'

    result = build_engine(onnx_path, engine_path, precision)

    if result:
        print("\n✓ Conversion complete!")
        sys.exit(0)
    else:
        print("\n✗ Conversion failed")
        sys.exit(1)
EOF

chmod +x ~/ai-models/convert_to_tensorrt.py

# Run conversion
python3 ~/ai-models/convert_to_tensorrt.py \
    resnet50-v2-7.onnx \
    resnet50-v2-7.trt \
    fp16
```

### Step 5: Create Python Inference Application

Build a real-time inference application:

```bash
cat > ~/ai-models/inference_app.py << 'EOF'
#!/usr/bin/env python3
"""
Real-time image classification using TensorRT
"""

import numpy as np
import cv2
import tensorrt as trt
import pycuda.driver as cuda
import pycuda.autoinit
import json
import time

class TensorRTInference:
    def __init__(self, engine_path, labels_path):
        """Initialize TensorRT inference engine"""

        # Load engine
        self.logger = trt.Logger(trt.Logger.WARNING)
        with open(engine_path, 'rb') as f:
            self.runtime = trt.Runtime(self.logger)
            self.engine = self.runtime.deserialize_cuda_engine(f.read())

        self.context = self.engine.create_execution_context()

        # Load labels
        with open(labels_path, 'r') as f:
            self.labels = json.load(f)

        # Allocate buffers
        self.allocate_buffers()

        print(f"✓ TensorRT engine loaded from {engine_path}")
        print(f"✓ Labels loaded: {len(self.labels)} classes")

    def allocate_buffers(self):
        """Allocate GPU and CPU buffers"""

        self.inputs = []
        self.outputs = []
        self.bindings = []
        self.stream = cuda.Stream()

        for binding in self.engine:
            size = trt.volume(self.engine.get_binding_shape(binding))
            dtype = trt.nptype(self.engine.get_binding_dtype(binding))

            # Allocate host and device buffers
            host_mem = cuda.pagelocked_empty(size, dtype)
            device_mem = cuda.mem_alloc(host_mem.nbytes)

            # Append to lists
            self.bindings.append(int(device_mem))

            if self.engine.binding_is_input(binding):
                self.inputs.append({
                    'host': host_mem,
                    'device': device_mem
                })
            else:
                self.outputs.append({
                    'host': host_mem,
                    'device': device_mem
                })

    def preprocess(self, image):
        """Preprocess image for ResNet-50"""

        # Resize to 224x224
        image = cv2.resize(image, (224, 224))

        # Convert BGR to RGB
        image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

        # Normalize (ImageNet normalization)
        mean = np.array([0.485, 0.456, 0.406])
        std = np.array([0.229, 0.224, 0.225])

        image = image.astype(np.float32) / 255.0
        image = (image - mean) / std

        # HWC to CHW
        image = image.transpose(2, 0, 1)

        # Add batch dimension
        image = np.expand_dims(image, axis=0)

        return image.astype(np.float32)

    def infer(self, image):
        """Run inference on image"""

        # Preprocess
        input_data = self.preprocess(image)

        # Copy input to GPU
        np.copyto(self.inputs[0]['host'], input_data.ravel())
        cuda.memcpy_htod_async(
            self.inputs[0]['device'],
            self.inputs[0]['host'],
            self.stream
        )

        # Run inference
        self.context.execute_async_v2(
            bindings=self.bindings,
            stream_handle=self.stream.handle
        )

        # Copy output from GPU
        cuda.memcpy_dtoh_async(
            self.outputs[0]['host'],
            self.outputs[0]['device'],
            self.stream
        )

        # Synchronize
        self.stream.synchronize()

        # Get results
        output = self.outputs[0]['host']

        # Get top-5 predictions
        top5_idx = np.argsort(output)[-5:][::-1]
        results = []

        for idx in top5_idx:
            results.append({
                'class': self.labels[idx],
                'confidence': float(output[idx])
            })

        return results

    def cleanup(self):
        """Free GPU memory"""
        for item in self.inputs + self.outputs:
            item['device'].free()

def main():
    # Initialize inference engine
    engine = TensorRTInference(
        'resnet50-v2-7.trt',
        'imagenet-simple-labels.json'
    )

    # Open camera
    cap = cv2.VideoCapture(0)
    if not cap.isOpened():
        print("Error: Could not open camera")
        return

    print("Camera opened. Press 'q' to quit.")

    # FPS calculation
    fps_start_time = time.time()
    fps_frame_count = 0
    fps = 0.0

    while True:
        ret, frame = cap.read()
        if not ret:
            break

        # Run inference
        start_time = time.time()
        results = engine.infer(frame)
        inference_time = (time.time() - start_time) * 1000

        # Update FPS
        fps_frame_count += 1
        if fps_frame_count >= 30:
            fps = fps_frame_count / (time.time() - fps_start_time)
            fps_start_time = time.time()
            fps_frame_count = 0

        # Display results
        y_offset = 30
        cv2.putText(frame, f"FPS: {fps:.1f}", (10, y_offset),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 0), 2)
        y_offset += 25

        cv2.putText(frame, f"Inference: {inference_time:.1f}ms", (10, y_offset),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 0), 2)
        y_offset += 25

        # Display top prediction
        top_result = results[0]
        text = f"{top_result['class']}: {top_result['confidence']:.2f}"
        cv2.putText(frame, text, (10, y_offset),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 255, 0), 2)

        # Show frame
        cv2.imshow('TensorRT Inference', frame)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()
    engine.cleanup()

if __name__ == "__main__":
    main()
EOF

chmod +x ~/ai-models/inference_app.py

# Run the application
python3 ~/ai-models/inference_app.py
```

### Step 6: Create C++ Inference Application

For maximum performance, use C++:

```bash
cat > ~/ai-models/tensorrt_inference.cpp << 'EOF'
/*
 * tensorrt_inference.cpp - High-performance TensorRT inference in C++
 */

#include <NvInfer.h>
#include <NvOnnxParser.h>
#include <cuda_runtime_api.h>
#include <opencv2/opencv.hpp>
#include <fstream>
#include <iostream>
#include <memory>
#include <vector>

using namespace nvinfer1;

class Logger : public ILogger {
    void log(Severity severity, const char* msg) noexcept override {
        if (severity <= Severity::kWARNING)
            std::cout << msg << std::endl;
    }
} gLogger;

class TensorRTInference {
private:
    std::unique_ptr<IRuntime> runtime;
    std::unique_ptr<ICudaEngine> engine;
    std::unique_ptr<IExecutionContext> context;

    void* buffers[2];  // Input and output buffers
    cudaStream_t stream;

    int inputIndex;
    int outputIndex;
    size_t inputSize;
    size_t outputSize;

public:
    TensorRTInference(const std::string& enginePath) {
        // Load engine
        std::ifstream file(enginePath, std::ios::binary);
        if (!file.good()) {
            throw std::runtime_error("Failed to open engine file");
        }

        file.seekg(0, std::ios::end);
        size_t size = file.tellg();
        file.seekg(0, std::ios::beg);

        std::vector<char> engineData(size);
        file.read(engineData.data(), size);
        file.close();

        runtime.reset(createInferRuntime(gLogger));
        engine.reset(runtime->deserializeCudaEngine(
            engineData.data(), size));
        context.reset(engine->createExecutionContext());

        // Get binding indices
        inputIndex = engine->getBindingIndex("input");
        outputIndex = engine->getBindingIndex("output");

        // Allocate GPU memory
        Dims inputDims = engine->getBindingDimensions(inputIndex);
        Dims outputDims = engine->getBindingDimensions(outputIndex);

        inputSize = 1;
        for (int i = 0; i < inputDims.nbDims; i++)
            inputSize *= inputDims.d[i];

        outputSize = 1;
        for (int i = 0; i < outputDims.nbDims; i++)
            outputSize *= outputDims.d[i];

        cudaMalloc(&buffers[inputIndex], inputSize * sizeof(float));
        cudaMalloc(&buffers[outputIndex], outputSize * sizeof(float));

        cudaStreamCreate(&stream);

        std::cout << "✓ TensorRT engine loaded" << std::endl;
    }

    ~TensorRTInference() {
        cudaFree(buffers[inputIndex]);
        cudaFree(buffers[outputIndex]);
        cudaStreamDestroy(stream);
    }

    void infer(const cv2::Mat& image, std::vector<float>& output) {
        // Preprocess image
        cv::Mat preprocessed;
        cv::resize(image, preprocessed, cv::Size(224, 224));
        cv::cvtColor(preprocessed, preprocessed, cv::COLOR_BGR2RGB);

        preprocessed.convertTo(preprocessed, CV_32FC3, 1.0/255.0);

        // Normalize
        float mean[] = {0.485, 0.456, 0.406};
        float std[] = {0.229, 0.224, 0.225};

        std::vector<float> inputData(3 * 224 * 224);
        for (int c = 0; c < 3; c++) {
            for (int i = 0; i < 224 * 224; i++) {
                int row = i / 224;
                int col = i % 224;
                float pixel = preprocessed.at<cv::Vec3f>(row, col)[c];
                inputData[c * 224 * 224 + i] = (pixel - mean[c]) / std[c];
            }
        }

        // Copy to GPU
        cudaMemcpyAsync(buffers[inputIndex], inputData.data(),
                        inputSize * sizeof(float),
                        cudaMemcpyHostToDevice, stream);

        // Run inference
        context->enqueueV2(buffers, stream, nullptr);

        // Copy output from GPU
        output.resize(outputSize);
        cudaMemcpyAsync(output.data(), buffers[outputIndex],
                        outputSize * sizeof(float),
                        cudaMemcpyDeviceToHost, stream);

        cudaStreamSynchronize(stream);
    }
};

int main() {
    try {
        // Initialize TensorRT
        TensorRTInference engine("resnet50-v2-7.trt");

        // Open camera
        cv::VideoCapture cap(0);
        if (!cap.isOpened()) {
            std::cerr << "Failed to open camera" << std::endl;
            return 1;
        }

        cv::Mat frame;
        std::vector<float> output;

        while (true) {
            cap >> frame;
            if (frame.empty())
                break;

            auto start = std::chrono::high_resolution_clock::now();

            // Run inference
            engine.infer(frame, output);

            auto end = std::chrono::high_resolution_clock::now();
            auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(
                end - start).count();

            // Display results
            std::string text = "Inference: " + std::to_string(duration) + "ms";
            cv::putText(frame, text, cv::Point(10, 30),
                        cv::FONT_HERSHEY_SIMPLEX, 0.6,
                        cv::Scalar(0, 255, 0), 2);

            cv::imshow("TensorRT Inference", frame);

            if (cv::waitKey(1) == 'q')
                break;
        }

    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
        return 1;
    }

    return 0;
}
EOF

# Create CMakeLists.txt
cat > ~/ai-models/CMakeLists.txt << 'EOF'
cmake_minimum_required(VERSION 3.10)
project(tensorrt_inference)

set(CMAKE_CXX_STANDARD 14)

find_package(CUDA REQUIRED)
find_package(OpenCV REQUIRED)

include_directories(
    /usr/include/aarch64-linux-gnu
    /usr/local/cuda/include
    ${OpenCV_INCLUDE_DIRS}
)

link_directories(
    /usr/lib/aarch64-linux-gnu
    /usr/local/cuda/lib64
)

add_executable(tensorrt_inference tensorrt_inference.cpp)

target_link_libraries(tensorrt_inference
    ${OpenCV_LIBS}
    nvinfer
    nvonnxparser
    cudart
)
EOF

# Build
mkdir -p ~/ai-models/build
cd ~/ai-models/build
cmake ..
make
```

### Step 7: Create GStreamer DeepStream Pipeline

Use NVIDIA DeepStream for video analytics:

```bash
# Create DeepStream config
cat > ~/ai-models/deepstream_config.txt << 'EOF'
[application]
enable-perf-measurement=1
perf-measurement-interval-sec=5

[source0]
enable=1
type=3  # V4L2
camera-width=1920
camera-height=1080
camera-fps-n=30
camera-fps-d=1

[sink0]
enable=1
type=2  # EGL overlay
sync=0

[primary-gie]
enable=1
model-engine-file=resnet50-v2-7.trt
batch-size=1
interval=0
gie-unique-id=1
network-type=1  # Classifier
output-blob-names=output

[tests]
file-loop=0
EOF

# Run DeepStream pipeline
deepstream-app -c deepstream_config.txt
```

### Step 8: Optimize Performance

Benchmark and optimize:

```bash
cat > ~/ai-models/benchmark.py << 'EOF'
#!/usr/bin/env python3
"""Benchmark TensorRT inference performance"""

import tensorrt as trt
import pycuda.driver as cuda
import pycuda.autoinit
import numpy as np
import time

def benchmark_engine(engine_path, iterations=100):
    """Benchmark TensorRT engine"""

    # Load engine
    logger = trt.Logger(trt.Logger.WARNING)
    with open(engine_path, 'rb') as f:
        runtime = trt.Runtime(logger)
        engine = runtime.deserialize_cuda_engine(f.read())

    context = engine.create_execution_context()

    # Allocate buffers
    input_shape = engine.get_binding_shape(0)
    output_shape = engine.get_binding_shape(1)

    input_size = trt.volume(input_shape)
    output_size = trt.volume(output_shape)

    h_input = cuda.pagelocked_empty(input_size, np.float32)
    h_output = cuda.pagelocked_empty(output_size, np.float32)
    d_input = cuda.mem_alloc(h_input.nbytes)
    d_output = cuda.mem_alloc(h_output.nbytes)

    stream = cuda.Stream()
    bindings = [int(d_input), int(d_output)]

    # Warm up
    for _ in range(10):
        cuda.memcpy_htod_async(d_input, h_input, stream)
        context.execute_async_v2(bindings=bindings,
                                 stream_handle=stream.handle)
        cuda.memcpy_dtoh_async(h_output, d_output, stream)
        stream.synchronize()

    # Benchmark
    print(f"Running {iterations} iterations...")
    timings = []

    for i in range(iterations):
        start = time.time()

        cuda.memcpy_htod_async(d_input, h_input, stream)
        context.execute_async_v2(bindings=bindings,
                                 stream_handle=stream.handle)
        cuda.memcpy_dtoh_async(h_output, d_output, stream)
        stream.synchronize()

        timings.append((time.time() - start) * 1000)  # ms

    # Statistics
    timings = np.array(timings)
    print(f"\nResults:")
    print(f"  Mean:   {np.mean(timings):.2f} ms")
    print(f"  Median: {np.median(timings):.2f} ms")
    print(f"  Min:    {np.min(timings):.2f} ms")
    print(f"  Max:    {np.max(timings):.2f} ms")
    print(f"  Std:    {np.std(timings):.2f} ms")
    print(f"  FPS:    {1000/np.mean(timings):.1f}")

    # Cleanup
    d_input.free()
    d_output.free()

if __name__ == "__main__":
    import sys
    if len(sys.argv) < 2:
        print("Usage: python3 benchmark.py <engine.trt>")
        sys.exit(1)

    benchmark_engine(sys.argv[1])
EOF

chmod +x ~/ai-models/benchmark.py
python3 ~/ai-models/benchmark.py resnet50-v2-7.trt
```

### Step 9: Multi-Model Pipeline

Create a pipeline with multiple models:

```bash
cat > ~/ai-models/multi_model_pipeline.py << 'EOF'
#!/usr/bin/env python3
"""
Multi-model pipeline: Object Detection + Classification
"""

import cv2
import numpy as np
from inference_app import TensorRTInference

class DetectionClassificationPipeline:
    def __init__(self):
        # Load detection model (e.g., YOLOv5)
        self.detector = TensorRTInference(
            'yolov5s.trt',
            'coco_labels.json'
        )

        # Load classification model
        self.classifier = TensorRTInference(
            'resnet50-v2-7.trt',
            'imagenet-simple-labels.json'
        )

    def process_frame(self, frame):
        """Process frame with detection then classification"""

        # Step 1: Detect objects
        detections = self.detector.infer(frame)

        results = []

        # Step 2: Classify each detected object
        for det in detections:
            x1, y1, x2, y2 = det['bbox']

            # Crop object
            obj_crop = frame[y1:y2, x1:x2]

            # Classify
            classification = self.classifier.infer(obj_crop)

            results.append({
                'bbox': det['bbox'],
                'detection_class': det['class'],
                'classification': classification[0]
            })

        return results

    def run_camera(self):
        """Run pipeline on camera stream"""

        cap = cv2.VideoCapture(0)

        while True:
            ret, frame = cap.read()
            if not ret:
                break

            results = self.process_frame(frame)

            # Visualize results
            for res in results:
                x1, y1, x2, y2 = res['bbox']
                cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)

                label = f"{res['classification']['class']}: " \
                        f"{res['classification']['confidence']:.2f}"
                cv2.putText(frame, label, (x1, y1-10),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.5,
                            (0, 255, 0), 2)

            cv2.imshow('Multi-Model Pipeline', frame)

            if cv2.waitKey(1) & 0xFF == ord('q'):
                break

        cap.release()
        cv2.destroyAllWindows()

if __name__ == "__main__":
    pipeline = DetectionClassificationPipeline()
    pipeline.run_camera()
EOF
```

### Step 10: Package AI Application with Yocto

Create a recipe for the inference application:

```bash
cd ~/yocto-jetson/meta-custom

cat > recipes-apps/ai-inference/ai-inference_1.0.bb << 'EOF'
SUMMARY = "AI inference application for Jetson"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "cuda tensorrt opencv"
RDEPENDS:${PN} = "python3-numpy python3-pycuda python3-tensorrt python3-opencv"

SRC_URI = "\
    file://inference_app.py \
    file://convert_to_tensorrt.py \
    file://benchmark.py \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 inference_app.py ${D}${bindir}/ai-inference
    install -m 0755 convert_to_tensorrt.py ${D}${bindir}/tensorrt-convert
    install -m 0755 benchmark.py ${D}${bindir}/tensorrt-benchmark

    install -d ${D}${datadir}/ai-models
}

FILES:${PN} = "\
    ${bindir}/* \
    ${datadir}/ai-models \
"
EOF

# Add to image
cd ~/yocto-jetson/builds/jetson-orin-agx
echo 'IMAGE_INSTALL:append = " ai-inference"' >> conf/local.conf
```

---

## Troubleshooting Common Issues

### Issue 1: TensorRT Build Fails

**Symptoms**: Engine conversion errors

**Solutions**:
```bash
# Enable verbose logging
TRT_LOGGER_LEVEL=VERBOSE python3 convert_to_tensorrt.py ...

# Check ONNX model validity
python3 -c "import onnx; onnx.checker.check_model('model.onnx')"

# Reduce workspace size if OOM
config.set_memory_pool_limit(trt.MemoryPoolType.WORKSPACE, 1 << 28)  # 256MB

# Try FP32 if FP16 fails
python3 convert_to_tensorrt.py model.onnx model.trt fp32
```

### Issue 2: Poor Inference Performance

**Symptoms**: Low FPS, high latency

**Solutions**:
```bash
# Use dynamic shapes for variable input sizes
# Use DLA (Deep Learning Accelerator) if available
config.default_device_type = trt.DeviceType.DLA
config.DLA_core = 0

# Enable async execution
# Use FP16 or INT8 for faster inference
# Profile with NVIDIA Nsight Systems

nsys profile --stats=true python3 inference_app.py
```

### Issue 3: Out of Memory Errors

**Symptoms**: CUDA out of memory

**Solutions**:
```bash
# Reduce batch size
# Free unused tensors
import gc
gc.collect()

# Monitor GPU memory
nvidia-smi dmon

# Use smaller models or quantization
```

---

## Verification Checklist

- [ ] CUDA and TensorRT libraries installed
- [ ] Can convert ONNX to TensorRT
- [ ] Python inference works
- [ ] Inference latency < 50ms
- [ ] Can run on camera stream in real-time
- [ ] FPS > 20 for live inference
- [ ] Multi-model pipeline functional
- [ ] Application packaged with Yocto

---

## Next Steps

### Immediate Practice
1. Deploy custom trained model
2. Implement object tracking
3. Add model ensemble

### Proceed to Next Tutorial
**Tutorial 10: OTA Update System** - Deploy AI updates remotely

### Advanced Topics
- INT8 quantization with calibration
- Custom TensorRT plugins
- Multi-stream inference
- Model optimization techniques

---

**Congratulations!** You can now deploy AI models on Jetson with TensorRT, create real-time inference pipelines, and optimize for maximum performance.

---

*Tutorial created by the Yocto & Meta-Tegra Multi-Agent Learning System*
*Last updated: 2025-01-15*
