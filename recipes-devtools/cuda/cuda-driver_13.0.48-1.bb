CUDA_PKG = "${BPN}-dev"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

DEPENDS:tegra = "tegra-libraries-cuda tegra-cuda-utils"

L4T_DEB_GROUP = "cuda-cudart"
DEVSUM = "73a305ac2aa16d53cda27feee38f05941ca573332995687105f519190f4350d2"
DEVSUM:x86-64 = "6644cbf207e520117cf37c7b2667b5a04e0c36a0bcc41074fde0fee21f812360"

ALLOW_EMPTY:${PN} = "1"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libcuda.so.1"

BBCLASSEXTEND = "native nativesdk"
