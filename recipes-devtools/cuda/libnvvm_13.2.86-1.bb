CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "9fee5e5f5e56390c068afd00921ab38f3fae404acfb69f69b21703fa644b1280"
MAINSUM:x86-64 = "389972c24e8edca34310223338bf63a3e8fc2b8af5113df88b99cb7c47ae3c9a"

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} += "dev-so"

BBCLASSEXTEND = "native nativesdk"
