#!/usr/bin/env python3
"""
TensorRT Inference Demo for NVIDIA Jetson

This script demonstrates:
- TensorRT model loading and optimization
- Image preprocessing for inference
- GPU-accelerated inference
- Post-processing and visualization
- Performance benchmarking

Supports:
- Classification models (ResNet, MobileNet, etc.)
- Detection models (SSD, YOLO, etc.)
- Custom ONNX models

Requirements:
- Python 3.6+
- TensorRT 8.0+
- PyCUDA
- NumPy
- OpenCV (cv2)
- Pillow (PIL)

Usage:
    python3 inference_demo.py --model <model> --input <image> [options]

Examples:
    python3 inference_demo.py --model resnet50.engine --input cat.jpg
    python3 inference_demo.py --model ssd_mobilenet.engine --input street.jpg --task detection
    python3 inference_demo.py --model custom.onnx --input image.png --build

Author: Meta-Tegra Learning System
License: MIT
"""

import os
import sys
import argparse
import time
import numpy as np
from PIL import Image
import cv2

try:
    import tensorrt as trt
    import pycuda.driver as cuda
    import pycuda.autoinit
except ImportError as e:
    print(f"Error importing required library: {e}")
    print("Please install: sudo apt-get install python3-libnvinfer python3-libnvinfer-dev")
    print("And: pip3 install pycuda")
    sys.exit(1)

# TensorRT logger
TRT_LOGGER = trt.Logger(trt.Logger.WARNING)


class TensorRTInference:
    """
    TensorRT inference engine wrapper
    """
    def __init__(self, engine_path):
        """
        Initialize TensorRT inference engine

        Args:
            engine_path: Path to TensorRT engine file
        """
        self.engine_path = engine_path
        self.engine = None
        self.context = None
        self.inputs = []
        self.outputs = []
        self.bindings = []
        self.stream = None

        self.load_engine()

    def load_engine(self):
        """Load TensorRT engine from file"""
        print(f"Loading TensorRT engine: {self.engine_path}")

        with open(self.engine_path, 'rb') as f:
            runtime = trt.Runtime(TRT_LOGGER)
            self.engine = runtime.deserialize_cuda_engine(f.read())

        if self.engine is None:
            raise RuntimeError("Failed to load TensorRT engine")

        self.context = self.engine.create_execution_context()

        # Allocate buffers
        self._allocate_buffers()

        print(f"Engine loaded successfully")
        print(f"Number of bindings: {self.engine.num_bindings}")

    def _allocate_buffers(self):
        """Allocate GPU memory for inputs and outputs"""
        self.inputs = []
        self.outputs = []
        self.bindings = []

        for binding in self.engine:
            size = trt.volume(self.engine.get_binding_shape(binding))
            dtype = trt.nptype(self.engine.get_binding_dtype(binding))

            # Allocate host and device buffers
            host_mem = cuda.pagelocked_empty(size, dtype)
            device_mem = cuda.mem_alloc(host_mem.nbytes)

            # Append to the appropriate list
            self.bindings.append(int(device_mem))

            if self.engine.binding_is_input(binding):
                self.inputs.append({
                    'host': host_mem,
                    'device': device_mem,
                    'binding': binding,
                    'shape': self.engine.get_binding_shape(binding),
                    'dtype': dtype
                })
            else:
                self.outputs.append({
                    'host': host_mem,
                    'device': device_mem,
                    'binding': binding,
                    'shape': self.engine.get_binding_shape(binding),
                    'dtype': dtype
                })

        # Create CUDA stream
        self.stream = cuda.Stream()

        print(f"Allocated {len(self.inputs)} input(s) and {len(self.outputs)} output(s)")

    def infer(self, input_data):
        """
        Run inference

        Args:
            input_data: Input data as numpy array or dict of arrays

        Returns:
            Dictionary of output arrays
        """
        # Copy input data to host buffer
        if isinstance(input_data, dict):
            for name, data in input_data.items():
                for inp in self.inputs:
                    if inp['binding'] == name:
                        np.copyto(inp['host'], data.ravel())
        else:
            np.copyto(self.inputs[0]['host'], input_data.ravel())

        # Transfer input data to GPU
        for inp in self.inputs:
            cuda.memcpy_htod_async(inp['device'], inp['host'], self.stream)

        # Run inference
        self.context.execute_async_v2(
            bindings=self.bindings,
            stream_handle=self.stream.handle
        )

        # Transfer predictions back from GPU
        for out in self.outputs:
            cuda.memcpy_dtoh_async(out['host'], out['device'], self.stream)

        # Synchronize stream
        self.stream.synchronize()

        # Return outputs
        outputs = {}
        for out in self.outputs:
            outputs[out['binding']] = out['host'].reshape(out['shape'])

        return outputs

    def get_input_shape(self):
        """Get expected input shape"""
        return self.inputs[0]['shape']

    def __del__(self):
        """Cleanup"""
        if self.stream:
            self.stream.synchronize()


def build_engine_from_onnx(onnx_path, engine_path, precision='fp16'):
    """
    Build TensorRT engine from ONNX model

    Args:
        onnx_path: Path to ONNX model
        engine_path: Path to save TensorRT engine
        precision: Precision mode ('fp32', 'fp16', 'int8')
    """
    print(f"Building TensorRT engine from ONNX: {onnx_path}")

    builder = trt.Builder(TRT_LOGGER)
    network = builder.create_network(
        1 << int(trt.NetworkDefinitionCreationFlag.EXPLICIT_BATCH)
    )
    parser = trt.OnnxParser(network, TRT_LOGGER)

    # Parse ONNX model
    with open(onnx_path, 'rb') as f:
        if not parser.parse(f.read()):
            print("ERROR: Failed to parse ONNX model")
            for error in range(parser.num_errors):
                print(parser.get_error(error))
            return False

    # Configure builder
    config = builder.create_builder_config()
    config.max_workspace_size = 1 << 30  # 1GB

    if precision == 'fp16' and builder.platform_has_fast_fp16:
        config.set_flag(trt.BuilderFlag.FP16)
        print("FP16 mode enabled")
    elif precision == 'int8' and builder.platform_has_fast_int8:
        config.set_flag(trt.BuilderFlag.INT8)
        print("INT8 mode enabled (requires calibration)")
    else:
        print("FP32 mode enabled")

    # Build engine
    print("Building engine... (this may take a while)")
    engine = builder.build_engine(network, config)

    if engine is None:
        print("ERROR: Failed to build engine")
        return False

    # Serialize engine
    with open(engine_path, 'wb') as f:
        f.write(engine.serialize())

    print(f"Engine saved to: {engine_path}")
    return True


def preprocess_image(image_path, input_shape):
    """
    Preprocess image for inference

    Args:
        image_path: Path to image file
        input_shape: Expected input shape (C, H, W) or (N, C, H, W)

    Returns:
        Preprocessed image as numpy array
    """
    # Load image
    image = Image.open(image_path)

    # Get target size from input shape
    if len(input_shape) == 4:
        _, channels, height, width = input_shape
    else:
        channels, height, width = input_shape

    # Resize image
    image = image.resize((width, height), Image.BILINEAR)

    # Convert to array
    img_array = np.array(image, dtype=np.float32)

    # Normalize (ImageNet mean/std)
    mean = np.array([0.485, 0.456, 0.406]) * 255
    std = np.array([0.229, 0.224, 0.225]) * 255
    img_array = (img_array - mean) / std

    # Transpose to CHW format
    if len(img_array.shape) == 3:
        img_array = np.transpose(img_array, (2, 0, 1))

    # Add batch dimension
    img_array = np.expand_dims(img_array, axis=0)

    return img_array.astype(np.float32)


def postprocess_classification(outputs, top_k=5):
    """
    Post-process classification results

    Args:
        outputs: Model outputs
        top_k: Number of top predictions to return

    Returns:
        List of (class_id, confidence) tuples
    """
    # Get output tensor (assuming single output)
    output = list(outputs.values())[0]

    # Apply softmax
    exp_output = np.exp(output - np.max(output))
    probabilities = exp_output / np.sum(exp_output)

    # Get top-k predictions
    top_indices = np.argsort(probabilities[0])[-top_k:][::-1]

    results = []
    for idx in top_indices:
        results.append((idx, probabilities[0][idx]))

    return results


def postprocess_detection(outputs, confidence_threshold=0.5):
    """
    Post-process detection results

    Args:
        outputs: Model outputs
        confidence_threshold: Minimum confidence threshold

    Returns:
        List of detections
    """
    # This is a simplified example
    # Actual implementation depends on model output format
    detections = []

    # Extract detection results
    # Format may vary: [batch, num_detections, (x1, y1, x2, y2, conf, class)]
    output = list(outputs.values())[0]

    for detection in output[0]:
        confidence = detection[4]
        if confidence >= confidence_threshold:
            detections.append({
                'bbox': detection[:4],
                'confidence': confidence,
                'class': int(detection[5]) if len(detection) > 5 else 0
            })

    return detections


def visualize_results(image_path, results, task='classification'):
    """
    Visualize inference results

    Args:
        image_path: Path to original image
        results: Inference results
        task: Task type ('classification' or 'detection')
    """
    image = cv2.imread(image_path)

    if task == 'classification':
        # Display top predictions
        y_offset = 30
        for class_id, confidence in results:
            text = f"Class {class_id}: {confidence:.3f}"
            cv2.putText(image, text, (10, y_offset),
                       cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)
            y_offset += 30

    elif task == 'detection':
        # Draw bounding boxes
        for det in results:
            x1, y1, x2, y2 = det['bbox']
            confidence = det['confidence']
            class_id = det.get('class', 0)

            # Convert normalized coordinates to pixel coordinates
            h, w = image.shape[:2]
            x1, y1, x2, y2 = int(x1*w), int(y1*h), int(x2*w), int(y2*h)

            # Draw bbox
            cv2.rectangle(image, (x1, y1), (x2, y2), (0, 255, 0), 2)

            # Draw label
            label = f"Class {class_id}: {confidence:.2f}"
            cv2.putText(image, label, (x1, y1-10),
                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)

    # Save result
    output_path = 'output_' + os.path.basename(image_path)
    cv2.imwrite(output_path, image)
    print(f"Result saved to: {output_path}")

    # Optionally display (if display available)
    # cv2.imshow('Results', image)
    # cv2.waitKey(0)


def benchmark_inference(inference_engine, input_data, iterations=100):
    """
    Benchmark inference performance

    Args:
        inference_engine: TensorRT inference engine
        input_data: Input data
        iterations: Number of iterations

    Returns:
        Average inference time in milliseconds
    """
    print(f"\nRunning benchmark ({iterations} iterations)...")

    # Warmup
    for _ in range(10):
        inference_engine.infer(input_data)

    # Benchmark
    start_time = time.time()
    for _ in range(iterations):
        inference_engine.infer(input_data)
    end_time = time.time()

    avg_time = (end_time - start_time) / iterations * 1000  # ms
    fps = 1000 / avg_time

    print(f"Average inference time: {avg_time:.2f} ms")
    print(f"Throughput: {fps:.2f} FPS")

    return avg_time


def main():
    parser = argparse.ArgumentParser(description='TensorRT Inference Demo')
    parser.add_argument('--model', type=str, required=True,
                       help='Path to TensorRT engine or ONNX model')
    parser.add_argument('--input', type=str, required=True,
                       help='Path to input image')
    parser.add_argument('--task', type=str, default='classification',
                       choices=['classification', 'detection'],
                       help='Inference task type')
    parser.add_argument('--build', action='store_true',
                       help='Build engine from ONNX model')
    parser.add_argument('--precision', type=str, default='fp16',
                       choices=['fp32', 'fp16', 'int8'],
                       help='Precision mode when building engine')
    parser.add_argument('--benchmark', type=int, default=0,
                       help='Run benchmark with N iterations')
    parser.add_argument('--visualize', action='store_true',
                       help='Visualize results')

    args = parser.parse_args()

    # Build engine if needed
    if args.build:
        if not args.model.endswith('.onnx'):
            print("Error: --build requires ONNX model")
            return 1

        engine_path = args.model.replace('.onnx', '.engine')
        if not build_engine_from_onnx(args.model, engine_path, args.precision):
            return 1
        args.model = engine_path

    # Check if engine exists
    if not os.path.exists(args.model):
        print(f"Error: Model file not found: {args.model}")
        return 1

    # Load inference engine
    inference = TensorRTInference(args.model)

    # Get input shape
    input_shape = inference.get_input_shape()
    print(f"Input shape: {input_shape}")

    # Preprocess image
    print(f"Preprocessing image: {args.input}")
    input_data = preprocess_image(args.input, input_shape)

    # Run inference
    print("Running inference...")
    start_time = time.time()
    outputs = inference.infer(input_data)
    inference_time = (time.time() - start_time) * 1000

    print(f"Inference time: {inference_time:.2f} ms")

    # Post-process results
    if args.task == 'classification':
        results = postprocess_classification(outputs)
        print("\nTop-5 predictions:")
        for class_id, confidence in results:
            print(f"  Class {class_id}: {confidence:.4f}")
    else:
        results = postprocess_detection(outputs)
        print(f"\nDetected {len(results)} objects")
        for i, det in enumerate(results):
            print(f"  Object {i}: Class {det['class']}, "
                  f"Confidence {det['confidence']:.4f}")

    # Visualize if requested
    if args.visualize:
        visualize_results(args.input, results, args.task)

    # Benchmark if requested
    if args.benchmark > 0:
        benchmark_inference(inference, input_data, args.benchmark)

    return 0


if __name__ == '__main__':
    sys.exit(main())


"""
Usage Examples:
===============

1. Run classification inference:
   python3 inference_demo.py --model resnet50.engine --input cat.jpg --visualize

2. Build engine from ONNX and run inference:
   python3 inference_demo.py --model mobilenet.onnx --input image.jpg --build --precision fp16

3. Run detection inference:
   python3 inference_demo.py --model ssd.engine --input street.jpg --task detection --visualize

4. Benchmark performance:
   python3 inference_demo.py --model model.engine --input test.jpg --benchmark 100

Installation:
=============

1. Install TensorRT:
   sudo apt-get install python3-libnvinfer python3-libnvinfer-dev

2. Install PyCUDA:
   pip3 install pycuda

3. Install dependencies:
   pip3 install numpy opencv-python pillow

Model Conversion:
=================

1. PyTorch to ONNX:
   import torch
   model = torch.load('model.pth')
   dummy_input = torch.randn(1, 3, 224, 224)
   torch.onnx.export(model, dummy_input, 'model.onnx')

2. TensorFlow to ONNX:
   python -m tf2onnx.convert --saved-model model/ --output model.onnx

3. ONNX to TensorRT:
   Use --build flag in this script or:
   trtexec --onnx=model.onnx --saveEngine=model.engine --fp16

Performance Tips:
=================

1. Use FP16 precision for better performance on Jetson
2. Enable DLA (Deep Learning Accelerator) if available
3. Increase max workspace size for complex models
4. Use dynamic shapes for variable input sizes
5. Batch multiple inferences when possible
6. Keep model on GPU (avoid CPU-GPU transfers)

Troubleshooting:
================

- CUDA out of memory: Reduce batch size or max workspace
- Slow inference: Check if using GPU (cuda.Device(0))
- Model not loading: Verify TensorRT version compatibility
- Import errors: Install python3-libnvinfer-dev
"""
