CUDA_PKG = "cuda-driver-dev"

require cuda-shared-binaries-12.9.inc

DEPENDS:tegra = "tegra-libraries-cuda tegra-cuda-utils"

L4T_DEB_GROUP = "cuda-cudart"
DEVSUM = "0fc12ca4745fe1437beee52f31bcec2b932e34810034b35176dd6e8a2c909d93"
DEVSUM:x86-64 = "5fece9ec181860350dbda0e6368cee79797c6f00f9a44f376d241a1889e5608b"

ALLOW_EMPTY:${PN} = "1"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libcuda.so.1"

BBCLASSEXTEND = "native nativesdk"
