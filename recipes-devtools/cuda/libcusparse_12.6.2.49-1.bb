CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "a9c53a7adf395a2887f238d719eac8493437258249d9a1e5f759d407aeee32ab"
MAINSUM:x86-64 = "71f0569a17f162185dab8d483d7a5c63695a4b93d47517ad707bc454d894d136"
DEVSUM = "2c40668993b893c15ce86c3035fd88e110d1eb42f9e14da2c40d390de158db72"
DEVSUM:x86-64 = "ac5e0d0ed3c0f6b0d3851ee5dddd9e00bb1fb641cabeb5a3afb34f60c5c6137e"

RDEPENDS:${PN} = "libnvjitlink"
RDEPENDS:${PN}-stubs = "libnvjitlink"
BBCLASSEXTEND = "native nativesdk"
