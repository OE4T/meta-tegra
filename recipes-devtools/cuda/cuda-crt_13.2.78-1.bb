CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "21991c55c73532a50daa878e550093befbc2271386562ee3300b7958b5933bcf"
MAINSUM:x86-64 = "2a5c46bf9c0c25597cb4fdb1f3da3905bf22fefab7d2ba6b8222947f15493399"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
