CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "ac9bd1be3cc437838f18747159be70b3ba85b69b6b93577ca3fe03e9e6132ad2"
MAINSUM:x86-64 = "581cc6c25121538fba179609cd1a05d6c1affaba76894f4db0114cb1880c14c4"

BBCLASSEXTEND = "native nativesdk"
