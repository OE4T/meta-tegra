DESCRIPTION = "nvbufsurface API headers from Deepstream SDK sources"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://includes/nvbufsurface.h;endline=9;md5=09eb13a8baa87017b1c492bc44d9f06e"
HOMEPAGE = "https://developer.nvidia.com/deepstream-sdk"

inherit l4t_deb_pkgfeed

L4T_DEB_GROUP = "deepstream-6.0"
SRC_COMMON_DEBS = "deepstream-6.0_${PV}_arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "320927fe83b40bea2da5408f6777985065038c7f1506b2f5d5eaa0bcfc2a1564"

COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"

S = "${WORKDIR}/${BPN}/opt/nvidia/deepstream/deepstream-6.0/sources"
B = "${WORKDIR}/build"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/includes/nvbufsurface.h ${S}/includes/nvbufsurftransform.h ${D}${includedir}/
}

ALLOW_EMPTY:${PN} = "1"
