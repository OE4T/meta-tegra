CUDA_PKG = "cuda-cccl"
L4T_DEB_GROUP = "cuda-thrust"

require cuda-shared-binaries.inc

MAINSUM = "1306a4ca4f83024bd3d02f24a2c911f69dd02c2ff6b6ef386a527e5bd717ff01"
MAINSUM:x86-64 = "11e2b7e5a9ce2006e2be47084de365fc79b441c53c6fae371b4b7a8b09696224"

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
