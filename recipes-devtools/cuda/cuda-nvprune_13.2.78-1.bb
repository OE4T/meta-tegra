CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "0c8307645d5b45373f35fb2fb46b631adcf3a712bd49a7fd69d2bbf0157efbc7"
MAINSUM:x86-64 = "4c4550efe10d2c52705b902400d738fb3bf1a29b01a3ff3a71a0ba0a3d019fa2"

BBCLASSEXTEND = "native nativesdk"
