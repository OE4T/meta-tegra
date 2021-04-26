CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "e92834f576241295c74393b478cb7121d9e20cb3454aed26c464c3523eaeadde"

ALLOW_EMPTY_${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
