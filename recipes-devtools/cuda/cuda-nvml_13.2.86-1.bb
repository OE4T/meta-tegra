CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries.inc

DEPENDS:tegra = "tegra-libraries-nvml"

L4T_DEB_GROUP = "${BPN}-dev"
DEVSUM = "b86034691b18e5f395695aba0cde2a0f8915a7e9428da5ceb1ff1701b87e4842"
DEVSUM:x86-64 = "f1ce2d83496efb3a6824c17b636fb5149148dec39b36c197e212336c5963012b"

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/example"
FILES:${PN}-doc += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/doc"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libnvidia-ml.so.1"
INSANE_SKIP:${PN}-stubs += "staticdev"

BBCLASSEXTEND = "native nativesdk"
