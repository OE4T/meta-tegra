require cuda-shared-binaries.inc

MAINSUM = "b8189ac22bb781b1dcccc61a902fb623238cd4e65cf456dcd95f8f319eb346be"
MAINSUM:x86-64 = "524fa89fbdbd48549850afb60e9c518f1d5af2ca9c8847d43bc2af0c539cb688"
DEVSUM = "a0a1596466507b8fb5fa0bfad848ec7c601c927d5f51db72a78135979e5c5423"
DEVSUM:x86-64 = "052261d2567c0e03375cc1fc9a5f5cdc436c1fbe63ee081d5a81a9f257ca707e"

FILES:${PN}-dev:remove = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc-builtins.so"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc.so"
INSANE_SKIP:${PN} += "dev-so"
BBCLASSEXTEND = "native nativesdk"
