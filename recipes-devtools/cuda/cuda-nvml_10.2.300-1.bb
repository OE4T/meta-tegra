CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries-${PV}.inc

L4T_DEB_GROUP = "${BPN}-dev"
DEVSUM = "70620354d37385ff1d34ac2d4f713ca3419d413103f90e385adb0e7112bec501"
DEVSUM:x86-64 = "e4c243f22c45ecb664b1593abe0240697769f6a017fcd564406bc218d36e04ae"

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/nvml/example"

BBCLASSEXTEND = "native nativesdk"
