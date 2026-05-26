CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "f263c44a55f8628f072bf6d8bf5e7bfafa12f3ff72d68e8c9531c86889f6647d"
MAINSUM:x86-64 = "5f541ceb389071f0ed22c397a7d76f5379a46365db35f49fcea0161228548cf7"
DEVSUM = "837509d3f2a36ce0f8de34272bce5c79b14c4e6e11b2432cdfd6cb6a92e156cd"
DEVSUM:x86-64 = "6704fee6c78fbf70eb5dafb5565a1c10f57a831d6f3e37c423d2f045607a5ac8"

EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS:${PN}-stubs = "libcublas.so.13 libcublasLt.so.13 libnvblas.so.13"

BBCLASSEXTEND = "native nativesdk"
