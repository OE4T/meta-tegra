CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "faaf360708a2cca6b5443b607beb43bc7d9948dd477064d7f2fa3f03b162e7d5"
MAINSUM:x86-64 = "82109c5ec046b12696cf7b19ba2476254bdb572d6f207b5e97b842f095829d5a"
DEVSUM = "b0ef666c34ea95ce0d8e2994e6b00105a47b018618c2a3943e26b841cb49077f"
DEVSUM:x86-64 = "40e07132736ed78e78ce7d2705a6c6393c7743c47cde22e5d1566368ac6ec7c3"

RDEPENDS:${PN} = "libnvjitlink"
RDEPENDS:${PN}-stubs = "libnvjitlink"
BBCLASSEXTEND = "native nativesdk"
