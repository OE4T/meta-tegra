require cuda-shared-binaries.inc

MAINSUM = "b9d65746b57a14c32d88a3a0c290c41c27adc93f6b29293c91d1236b35da2196"
MAINSUM:x86-64 = "7f04d10b6d2b358f4aa8b4c38363951ece1cad7868fdbe941f97e0e61965ead5"
DEVSUM = "8c7ecd4fa8d5a709a8bb2cfdb21ea57068a97f38ac22b0f5c13bb1a1de601886"
DEVSUM:x86-64 = "8616488c3abeda1bb6cf008fad9b93af281c0f89a76dc183e40f48681bf9c782"

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/Readme.txt \
                    ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/include ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/sample"

BBCLASSEXTEND = "native nativesdk"
