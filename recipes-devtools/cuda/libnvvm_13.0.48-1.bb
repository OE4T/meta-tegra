CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "ee1ebce1ffe6a0f15bd86576f6d2acf10032484f9469eada6ff5e6c9050b6ec6"
MAINSUM:x86-64 = "ce319e81bfb19abd2a9c2ed1fa2d683e4a4936b598617c1460ca3463a24855c4"

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} += "dev-so"

BBCLASSEXTEND = "native nativesdk"
