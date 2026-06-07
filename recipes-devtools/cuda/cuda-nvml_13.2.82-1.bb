CUDA_PKG = "${BPN}-dev"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

DEPENDS:tegra = "tegra-libraries-nvml"

L4T_DEB_GROUP = "${BPN}-dev"
DEVSUM = "cf823b38ce5ad0720fa2a29940fc95c0dc4afb9c4fc969ea3830058c30a4463f"
DEVSUM:x86-64 = "864ec1309ef2aa1385dcadf4f1a8f1dad4235b3b69cde13367a80cf302dcc0b8"

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/example"
FILES:${PN}-doc += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/doc"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libnvidia-ml.so.1"
INSANE_SKIP:${PN}-stubs += "staticdev"

BBCLASSEXTEND = "native nativesdk"
