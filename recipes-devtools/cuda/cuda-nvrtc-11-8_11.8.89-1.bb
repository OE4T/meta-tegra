CUDA_PKG = "cuda-nvrtc cuda-nvrtc-dev"

require cuda-shared-binaries-11.8.inc

MAINSUM = "dfaf79fc04ea7acfd47ce9ba1f8cd69e29b82910d8dbdb407825d79f7de70aa2"
MAINSUM:x86-64 = "6f083fcd63b7f8ef7478bfb7b8aba938d8ca0b5684d610df1243302b12a54f06"
DEVSUM = "ca313cddd4af5b0e104accf8667575cb76e18eac87482c25b497b9ed75f86a59"
DEVSUM:x86-64 = "ebf060e5a84fa0c19f7ab444e6be904b5e38c976baef6b1cf6c7887a2543b171"

FILES:${PN}-dev:remove = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc-builtins.so"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc.so"
INSANE_SKIP:${PN} += "dev-so"
BBCLASSEXTEND = "native nativesdk"
