CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "fe39e03cf8ef59ca1dc5cc88f08c1e615776b0f9a574a770f85863bc6f34a68e"
MAINSUM:x86-64 = "f8f00cde29fd51691454807ab6018afd3e1995b689d00b109c89d7f49e68fa36"

BBCLASSEXTEND = "native nativesdk"
