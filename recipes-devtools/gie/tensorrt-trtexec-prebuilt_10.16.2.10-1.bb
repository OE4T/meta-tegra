DESCRIPTION = "NVIDIA TensorRT Prebuilt Plugins for deep learning"
LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvinfer-bin_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-bin_${PV}+cuda13.2_arm64.deb;name=bin;subdir=tensorrt \
"

BINSHA256SUM = "7a7de013265b50acb1839ec1e42c2e611a323446dd09142deced5b0cea052f56"

SRC_URI[bin.sha256sum] = "${BINSHA256SUM}"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://usr/share/doc/libnvinfer-bin/copyright;md5=85d3e8c5b94689733a8f3c790762d581"

S = "${UNPACKDIR}/tensorrt"

DEPENDS = "cuda-cudart cudnn tensorrt-core tensorrt-plugins libcublas"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/usr/bin/trtexec ${D}${prefix}/src/tensorrt/bin/
}

FILES:${PN} = "${prefix}/src/tensorrt/bin"

PROVIDES = "tensorrt-trtexec"
RPROVIDES:${PN} = "tensorrt-trtexec"
RCONFLICTS:${PN} = "tensorrt-trtexec"
RPROVIDES:${PN}-dev = "tensorrt-trtexec-dev"
RCONFLICTS:${PN}-dev = "tensorrt-trtexec-dev"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
