CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "c9f5d0e87bb74fd9ab1c8062b77b4af648f6aad6b70a0ddd056d60e8b0b30088"
MAINSUM:x86-64 = "5aa1242ece5f03cc9bb9640bab4f1991036eada6c97ce74e39b1a8d1d8f9f6f1"

BBCLASSEXTEND = "native nativesdk"
