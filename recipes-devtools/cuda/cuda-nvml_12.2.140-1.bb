CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries.inc

DEPENDS:tegra = "tegra-libraries-nvml"

L4T_DEB_GROUP = "${BPN}-dev"
DEVSUM = "656284c7c71c43b9d5cab213e01132369c79d32a11d8d9ffc841c0460a833604"
DEVSUM:x86-64 = "6bf315a0a15faa718da18e78910918339e096ff1e68e3d89970f4b3a760b5864"

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/example"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libnvidia-ml.so.1"

BBCLASSEXTEND = "native nativesdk"
