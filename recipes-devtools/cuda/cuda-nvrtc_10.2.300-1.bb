require cuda-shared-binaries-${PV}.inc

MAINSUM = "7118079394a22c6cedba1152f0a78ef7bb10ac26c1984ab8c4dcf82fe9f4e20c"
MAINSUM:x86-64 = "f97e6811e99459a9ab300c6c7b6fb228666a87ba46cfdc24ec8871a77801619e"
DEVSUM = "bdce95a7a30daf7d1a52a32385c1ee67a8f742126a2f24d82a7b7dd35d012f3f"
DEVSUM:x86-64 = "6eb41ff9b5a62f073e29fc7698d0097be0a3b464a850ad6950dd108a133b130d"

FILES:${PN}-dev:remove = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc-builtins.so"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc.so"
INSANE_SKIP:${PN} += "dev-so"
BBCLASSEXTEND = "native nativesdk"
