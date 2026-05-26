CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "21991c55c73532a50daa878e550093befbc2271386562ee3300b7958b5933bcf"
MAINSUM:x86-64 = "b42bb4f2a42ef4bc2be66118c57cab60a71a4ce452d08e440ca195e58080ebd3"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
