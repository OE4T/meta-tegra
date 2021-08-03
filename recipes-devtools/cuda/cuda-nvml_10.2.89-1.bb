CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries-${PV}.inc

DEVSUM = "c8743e69c84a432c5e6dea6edfcacf1bb6b09b028bee61c8aece7a41d0447265"
DEVSUM:x86-64 = "fe21076a38bd066b1c7d0c9bc0124e9dfc816b84f40e1fe1d20156c6bb5869fc"

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/example"

BBCLASSEXTEND = "native nativesdk"
