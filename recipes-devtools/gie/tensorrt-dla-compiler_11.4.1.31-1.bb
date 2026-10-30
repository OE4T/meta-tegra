SUMMARY = "NVIDIA TensorRT DLA compiler runtime library"
HOMEPAGE = "https://developer.nvidia.com/tensorrt"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://usr/share/doc/libnvinfer-dla-compiler11/copyright;md5=85d3e8c5b94689733a8f3c790762d581"

inherit l4t_deb_pkgfeed

L4T_DEB_GROUP = "tensorrt"
SRC_COMMON_DEBS = "libnvinfer-dla-compiler11_${PV}+cuda13.4_arm64.deb;name=main;subdir=${BPN}"
SRC_URI[main.sha256sum] = "323ca45da41be05639e27683acdab8db7979a5f8897b956055bd631dd2612ecc"

S = "${UNPACKDIR}/${BPN}"
B = "${S}"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/nvidia/libnvdla_compiler.so ${D}${libdir}/
}

COMPATIBLE_MACHINE = "(tegra234)"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RDEPENDS:${PN} = "tegra-libraries-core"

# The prebuilt compiler embeds its producer's /home/nvidia source paths.
OEQA_BUILDPATHS_SKIP += "/home/nvidia"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
