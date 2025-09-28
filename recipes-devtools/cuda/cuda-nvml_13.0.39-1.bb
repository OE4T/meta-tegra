CUDA_PKG = "${BPN}-dev"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

DEPENDS:tegra = "tegra-libraries-nvml"

L4T_DEB_GROUP = "${BPN}-dev"
DEVSUM = "b99fb5614d36031a6ed7f64912c68d21212794ab7aea4a6c1e084e000b9ad512"
DEVSUM:x86-64 = "896243f51bdbb2e9fbee937964e8f236da16460bda0a53148d7368b9c3cf4136"

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/example"
FILES:${PN}-doc += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/doc"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libnvidia-ml.so.1"
INSANE_SKIP:${PN}-stubs += "staticdev"

BBCLASSEXTEND = "native nativesdk"
