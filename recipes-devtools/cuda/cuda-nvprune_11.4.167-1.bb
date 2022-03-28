CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "405e3d8900d658cd6c09f7606dc089631822b0c7b03e8ff956714b396e5cdfad"
MAINSUM:x86-64 = "e03cf55f05c714b0de9eae4ae7597904bcaffb34627429f7ea51b853161ceccb"

BBCLASSEXTEND = "native nativesdk"
