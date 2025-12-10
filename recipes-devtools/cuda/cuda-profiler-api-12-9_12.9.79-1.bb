CUDA_PKG = "cuda-profiler-api"

require cuda-shared-binaries-12.9.inc

MAINSUM = "c439f200325a6aab9cd41f5330bce946d858e54d1ea2b442d53d997fb43369ab"
MAINSUM:x86-64 = "2777376d69c44db9e95073fb6991f8fcd770642ce49d90b9168994b9f2ae7664"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
