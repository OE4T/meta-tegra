CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "b24f88e7f8e7caeb6ab11aa9bbd38a09efbd7ccb6278b1e721ded49e8285651d"
MAINSUM:x86-64 = "fc14af5568604708cb908f7fa331b1b4c3fdec907f66837f8842637b535a1a87"

BBCLASSEXTEND = "native nativesdk"
