CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries.inc

L4T_DEB_GROUP = "${BPN}-dev"
DEVSUM = "a7b39adbea14e5801da89616408c66a07d5cb8396e28b757ef68afd7dbfc7486"
DEVSUM:x86-64 = "fdf44b776046816e898d56c4c120a41968b158e402503ebe04a9302f37481312"

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/example"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libnvidia-ml.so.1"

BBCLASSEXTEND = "native nativesdk"
