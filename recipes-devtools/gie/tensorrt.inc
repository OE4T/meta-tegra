DESCRIPTION = "NVIDIA TensorRT Core (GPU Inference Engine) for deep learning"
LICENSE = "Apache-2.0"

inherit l4t_deb_pkgfeed features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvinfer-headers-dev_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-headers-dev_${PV}+cuda13.2_arm64.deb;name=hdev;subdir=tensorrt \
"

SRC_URI[hdev.sha256sum] = "6208dbba5dc7fd04ca1bca71a3dcaed6642cb08765edbfcc10996b52844b2ec9"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=16;md5=caedbfdd8e95ff43ea31d4b19e6f95a3"

REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "tegra-libraries-multimedia"

require tensorrt-version.inc

S = "${UNPACKDIR}/tensorrt"

do_configure() {
    :
}

do_compile() {
    :
}

INSANE_SKIP:${PN} = "already-stripped"

CVE_PRODUCT = "nvidia:tensorrt"
CVE_VERSION = "${BASEVER}"
