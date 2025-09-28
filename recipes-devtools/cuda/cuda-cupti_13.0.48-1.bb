CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "a390c229852ea31c61771f82f29963e6d4580d74c865a74ba3fecf53d3959886"
MAINSUM:x86-64 = "f75e9d5af41072e27ecfa607717ba34d90e3cba3f49be7efcf9428c4161fe89e"
DEVSUM = "af8ad5f0c08a5505b7ffc7a81621524b4519b8c81da75180e1f464b68236e75c"
DEVSUM:x86-64 = "4c691055e536b2c2206ecef63efba1000fa3977f6fec598bc74b662c11068fa1"

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI"
RDEPENDS:${PN}-dev += "make perl perl-module-getopt-long perl-module-posix perl-module-cwd"

BBCLASSEXTEND = "native nativesdk"
