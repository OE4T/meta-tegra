CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "882c05ec6d681980c1a6cc5afacd4c2a58e6d819cb1aa043ae16bb74f97a70f8"
MAINSUM:x86-64 = "16d1ce512b030ada31a5e1ef7324fa5b01758f3249e582e85727decaba8bd255"

DEPENDS = "cuda-cupti"
DEPENDS:append:tegra = " tegra-libraries-cuda"

BBCLASSEXTEND = "native nativesdk"
