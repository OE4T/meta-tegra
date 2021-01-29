DESCRIPTION = "nvbufsurface API headers from Deepstream-5.0 sources"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://includes/nvbufsurface.h;endline=9;md5=a08740d8d989aeb6ee322d7b636ca67c"
HOMEPAGE = "https://developer.nvidia.com/deepstream-sdk"

inherit l4t_deb_pkgfeed

L4T_DEB_GROUP = "deepstream-5.0"
SRC_COMMON_DEBS = "deepstream-5.0_${PV}_arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "a7a7015515883ac88c7587c7a2acfcf78510e539b84b702afd05f4f330faa55e"

COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"

S = "${WORKDIR}/${BPN}/opt/nvidia/deepstream/deepstream-5.0/sources"
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

ALLOW_EMPTY_${PN} = "1"
