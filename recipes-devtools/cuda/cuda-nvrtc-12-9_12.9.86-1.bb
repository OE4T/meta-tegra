CUDA_PKG = "cuda-nvrtc cuda-nvrtc-dev"

require cuda-shared-binaries-12.9.inc

MAINSUM = "57cd5a9792be281992301e186648b925fc29d3e7c1e090cf53ccde15456c5275"
MAINSUM:x86-64 = "4e1cc14cc3578dd69286d9ac6a0623297e854ba09539b1780b6989d2199395af"
DEVSUM = "86a05125718af2613b342f57b353f5bee185bbb7c81afe807d019cb004af87aa"
DEVSUM:x86-64 = "737a64f295cd74d4d3f7df4e195544938c9091c52d6e768a884922fdc1b1bc97"

FILES:${PN}-dev:remove = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc-builtins.so"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc.so"
INSANE_SKIP:${PN} += "dev-so"
BBCLASSEXTEND = "native nativesdk"
