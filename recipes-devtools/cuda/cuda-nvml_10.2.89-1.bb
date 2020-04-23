require cuda-shared-binaries-${PV}.inc

FILES_${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/example"

BBCLASSEXTEND = "native nativesdk"
