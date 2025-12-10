CUDA_PKG = "cuda-nvml-dev"

require cuda-shared-binaries-12.9.inc

DEPENDS:tegra = "tegra-libraries-nvml"

L4T_DEB_GROUP = "cuda-nvml-dev"
DEVSUM = "ef72570ae398ffdc0cdc4d051e0e41aa58c2b34db351166be0269b2d9e1efb6c"
DEVSUM:x86-64 = "b26e97a445f6a0f587b8b6e2118907d09db1e77ecb5095b921555aa4ea95c585"

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/example"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libnvidia-ml.so.1"
INSANE_SKIP:${PN}-stubs += "staticdev"

BBCLASSEXTEND = "native nativesdk"
