CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "faaf360708a2cca6b5443b607beb43bc7d9948dd477064d7f2fa3f03b162e7d5"
MAINSUM:x86-64 = "ffdc0872dab2f223b12cda7109a30baf8791234abe35d8ed41644249e6fbf305"
DEVSUM = "b0ef666c34ea95ce0d8e2994e6b00105a47b018618c2a3943e26b841cb49077f"
DEVSUM:x86-64 = "d735b90a2c4a5e63716440e431453162cbbbd6365fccdc93aaa3ba460972cf98"

RDEPENDS:${PN} = "libnvjitlink"
RDEPENDS:${PN}-stubs = "libnvjitlink"
BBCLASSEXTEND = "native nativesdk"
