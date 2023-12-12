require cuda-shared-binaries.inc

MAINSUM = "2fc6fd7b2067b8317487f4451e5918462821c74fb1592e32ddcf932a8863e36c"
MAINSUM:x86-64 = "172184b8f50d015680e1d107b697874dfdd70a1586a5e8744032a8f8a3e0cb39"
DEVSUM = "c35bfa8c1b2c48817f9392c25119cbd8140a60f4e3c71b06d5f132eb114708c7"
DEVSUM:x86-64 = "f693156ca9fedcffc28fc885177742d9256fffdf98a55537837daf33f50bcb8a"

FILES:${PN}-dev:remove = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc-builtins.so"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc.so"
INSANE_SKIP:${PN} += "dev-so"
BBCLASSEXTEND = "native nativesdk"
