CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "4b641739ec5f47a54a7833a89aa15575a8a73260847d07ab751686e494c4f1bc"
MAINSUM:x86-64 = "d0b1dcc36af225edba2fb2a2890ce9c18c2153ac9f1478d498857309a13d6b34"
DEVSUM = "bcffd6bcd6919e08606e0a21cdd5d5805dab693d2988f77270af40c856b729af"
DEVSUM:x86-64 = "498a9730a199a81042c4b95d955de0d6f98e559fd796882d6f7ac11f2cc038be"

RDEPENDS:${PN} = "libcublas libcusparse libnvjitlink"
BBCLASSEXTEND = "native nativesdk"
