CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "54d96b9cdba5a53da92b2cdaada27ec7a886b3a6a29e7d33b5fdf429ad788681"
MAINSUM_x86-64 = "1250be701216367d55476fbb1842389ffe2b58f0dce045151ffa9e17fe61d195"

DEPENDS = "cuda-cupti"

BBCLASSEXTEND = "native nativesdk"
