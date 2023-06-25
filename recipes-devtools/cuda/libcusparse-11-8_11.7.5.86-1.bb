CUDA_PKG = "libcusparse libcusparse-dev"

require cuda-shared-binaries-11.8.inc

MAINSUM = "a65c1e4a3c3826c1b402b47e67e040d60a2351bebd2bf347a51694246bffe975"
MAINSUM:x86-64 = "6252b196b6ce67c63579e0f0fcfcf6e8059ed13c797c3e2d739297807a9c6bf1"
DEVSUM = "643d6696186be3d2de6c56da2fde998096d946d03cff9f28725a0aff64df087d"
DEVSUM:x86-64 = "3a61b6d3094cda2842cea5af6317531f39ec588b5d11c6a5d880def6f5f2e08d"

BBCLASSEXTEND = "native nativesdk"
