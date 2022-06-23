require cuda-shared-binaries.inc

COMPATIBLE_HOST:x86-64 = "(-)"
MAINSUM = "b9d65746b57a14c32d88a3a0c290c41c27adc93f6b29293c91d1236b35da2196"
DEVSUM = "8c7ecd4fa8d5a709a8bb2cfdb21ea57068a97f38ac22b0f5c13bb1a1de601886"

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/Readme.txt \
                    ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/include ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/sample"

BBCLASSEXTEND = "native nativesdk"
