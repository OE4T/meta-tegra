CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries.inc

DEPENDS:tegra = "tegra-libraries-nvml"

L4T_DEB_GROUP = "${BPN}-dev"
DEVSUM = "2a1050aff1a8297dc1bab6cdce2c35daf47eb6e77412d544b35e809e75f2746b"
DEVSUM:x86-64 = "812a1261a29df7e231a14764c94a87400c141eecc0a92dd062001e0d8c6e2504"

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/example"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libnvidia-ml.so.1"
INSANE_SKIP:${PN}-stubs += "staticdev"

BBCLASSEXTEND = "native nativesdk"
