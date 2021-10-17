REPLACE_STUBS = "0"
CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries-${PV}.inc

DEPENDS:tegra = "tegra-libraries-cuda"

L4T_DEB_GROUP = "cuda-cudart"
DEVSUM = "5ea760773de2685acfac3931bcdc2eff3a18eceb28fc86f80061e215e9e81456"
DEVSUM:x86-64 = "e98f46f1a850b0e90399645b79fce38857781bbec0b0db49ae3fcd01b7c8d961"

ALLOW_EMPTY:${PN} = "1"
PRIVATE_LIBS = "libcuda.so.1"

BBCLASSEXTEND = "native nativesdk"
