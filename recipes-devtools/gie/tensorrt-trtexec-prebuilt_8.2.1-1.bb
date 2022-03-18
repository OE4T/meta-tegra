DESCRIPTION = "NVIDIA TensorRT Prebuilt Plugins for deep learning"
LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed container-runtime-csv features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvinfer-bin_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvinfer-bin_${PV}+cuda10.2_arm64.deb;name=bin;subdir=tensorrt \
"

BINSHA256SUM = "738904f63c16498da9e9789dd4c99a9be926a2809c30cb45c2a97877b6e3c3d7"

SRC_URI[bin.sha256sum] = "${BINSHA256SUM}"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://usr/share/doc/libnvinfer-bin/copyright;md5=713c2de2adb0f371a903b9fe20431bab"

S = "${WORKDIR}/tensorrt"

DEPENDS = "cuda-cudart cudnn tensorrt-core tensorrt-plugins libcublas"

CONTAINER_CSV_FILES = "/usr/src/tensorrt/bin"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${prefix}/src
    cp --preserve=mode,timestamps --recursive ${S}/usr/src/tensorrt ${D}${prefix}/src/
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
