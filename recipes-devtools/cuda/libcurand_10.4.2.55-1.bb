CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "e043b54f8303b93c2962d5c4fbfb4810e1e9fddf0036bfbaed8cc58959b69716"
MAINSUM:x86-64 = "36d13293e04b3e3622560f9f622bed50783cb66275af2ada38e53d4db92a151b"
DEVSUM = "129b5ba9535699b0da1de74ea0c1cdc4e905ce6f3119df6dd01e36490da9666e"
DEVSUM:x86-64 = "c5a1aa5e4cf19cf452f12d0affb808c1a7a7b283264d79b6beee0593cd9ff1d5"

BBCLASSEXTEND = "native nativesdk"
