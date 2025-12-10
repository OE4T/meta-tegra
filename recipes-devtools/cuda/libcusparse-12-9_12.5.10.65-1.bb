CUDA_PKG = "libcusparse libcusparse-dev"

require cuda-shared-binaries-12.9.inc

MAINSUM = "99d3bfa60b34a1f4eefe2d3b04e9f8002bb917d81e3ffafc1a14a5c6c8b5f3e6"
MAINSUM:x86-64 = "7cb624b1a5d572f9085fdb468b099e4933758684d413d2326fd2cbb3d3419b60"
DEVSUM = "6f5bf8b10ca19f8a3ab09da46e241290a0f7a043b27f7c1b171ed5b1cc6cc445"
DEVSUM:x86-64 = "0e206426ca80de4f7fe17491d1f57ace661a42192d5345ee95beafa6d429d96b"

RDEPENDS:${PN} = "libnvjitlink-12-9"
RDEPENDS:${PN}-stubs = "libnvjitlink-12-9"
BBCLASSEXTEND = "native nativesdk"