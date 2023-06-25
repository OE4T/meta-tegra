CUDA_PKG = "cuda-nvml-dev"

require cuda-shared-binaries-11.8.inc

L4T_DEB_GROUP = "cuda-nvml-dev"
DEVSUM = "dc2386fbea53b4712a12d940bff2b3d8c59546492de3d4b163aea628c062c834"
DEVSUM:x86-64 = "66bee76aec1d59aad169526d7ac6323304baf4195a23730822f4776996cf1408"

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/example"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libnvidia-ml.so.1"

BBCLASSEXTEND = "native nativesdk"
