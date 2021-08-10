CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "fe29d0a639620c161a732cdff520670142e986d795bb3f7afd7c06e41dca340e"
MAINSUM:x86-64 = "06d1a867618b3a9367bb052af33ec265521ffe99c4eb71fcdaa101e1f1247de0"

BBCLASSEXTEND = "native nativesdk"
