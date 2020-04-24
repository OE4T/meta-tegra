CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "54d96b9cdba5a53da92b2cdaada27ec7a886b3a6a29e7d33b5fdf429ad788681"

DEPENDS = "cuda-cupti"

BBCLASSEXTEND = "native nativesdk"
