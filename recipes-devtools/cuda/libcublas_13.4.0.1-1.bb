CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "f263c44a55f8628f072bf6d8bf5e7bfafa12f3ff72d68e8c9531c86889f6647d"
MAINSUM:x86-64 = "c1ced84210922716f029fda6b47b9c84124bb216f79722d9198689d3d1a89d96"
DEVSUM = "837509d3f2a36ce0f8de34272bce5c79b14c4e6e11b2432cdfd6cb6a92e156cd"
DEVSUM:x86-64 = "20c67cfe92e56eb4689197c3008290b955c653801faa037333aef147b8d284c2"

EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS:${PN}-stubs = "libcublas.so.13 libcublasLt.so.13 libnvblas.so.13"

BBCLASSEXTEND = "native nativesdk"
