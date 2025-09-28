CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "2cd1cc94faa6ae1dd68914470dc8a4abc4c78717088cb8737c946959195b99c4"
MAINSUM:x86-64 = "176eda71755951e2dccb68907b135d380cab42c81f50e3599cb127b1515fe307"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
