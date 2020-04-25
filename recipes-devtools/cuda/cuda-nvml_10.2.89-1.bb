CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries-${PV}.inc

SRC_URI[dev.sha256sum] = "c8743e69c84a432c5e6dea6edfcacf1bb6b09b028bee61c8aece7a41d0447265"

ALLOW_EMPTY_${PN} = "1"
FILES_${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/example"

BBCLASSEXTEND = "native nativesdk"
