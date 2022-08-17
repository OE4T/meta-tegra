require cuda-shared-binaries.inc

MAINSUM = "9cfae20c24d580a28ab97cff977e8fe7a0248fd3335272f23391d0a16455e14f"
MAINSUM:x86-64 = "c3583aac799a74c716d76ffacad8064104454681ad912afe25822ed17708a167"
DEVSUM = "71240afeb550733de7292ac582a7ab8a16eef44225120de7cdd80ec3c8a8572f"
DEVSUM:x86-64 = "95295f7f0c00153c3889b0d7055cfef3a314b0611bb833ce635c4d26849a340b"

FILES:${PN}-dev:remove = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc-builtins.so"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc.so"
INSANE_SKIP:${PN} += "dev-so"
BBCLASSEXTEND = "native nativesdk"
