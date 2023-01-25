CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries.inc

L4T_DEB_GROUP = "${BPN}-dev"
DEVSUM = "8d66d8b56bdb79d3778f7a9dc20b433ad2aa191d38f663ae973340e1a5b91d3b"
DEVSUM:x86-64 = "ea2efe3a6f938b14892e063c99ef4fa59804f47e60c2fd4f3494c39c549e19ce"

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/example"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libnvidia-ml.so.1"

BBCLASSEXTEND = "native nativesdk"
