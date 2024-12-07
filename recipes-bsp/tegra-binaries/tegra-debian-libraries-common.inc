HOMEPAGE = "https://developer.nvidia.com/embedded-computing"
LICENSE = "Proprietary"

L4T_DEB_COPYRIGHT_MD5 ??= ""
L4T_DEB_TRANSLATED_BPN ?= "${@d.getVar('BPN').replace('tegra-libraries-', 'nvidia-l4t-')}"
LIC_FILES_CHKSUM = "file://usr/share/doc/${L4T_DEB_TRANSLATED_BPN}/copyright;md5=${L4T_DEB_COPYRIGHT_MD5}"
L4T_DEB_IS_COMMON ?= "0"

COMPATIBLE_MACHINE = "(tegra)"

inherit l4t_deb_pkgfeed

def format_deb_uri(d, do_common):
    is_common = bb.utils.to_boolean(d.getVar('L4T_DEB_IS_COMMON'))
    if do_common == is_common:
        return l4t_deb_pkgname(d, d.getVar('L4T_DEB_TRANSLATED_BPN')) + ";subdir=${BP};name=main"
    return ""

SRC_SOC_DEBS = "${@format_deb_uri(d, False)}"
SRC_COMMON_DEBS = "${@format_deb_uri(d, True)}"
SRC_URI[main.sha256sum] = "${MAINSUM}"

PV = "${L4T_VERSION}${@l4t_bsp_debian_version_suffix(d)}"

install_libraries() {
    install -d ${D}${libdir}
    for f in ${TEGRA_LIBRARIES_TO_INSTALL}; do
        fbasename=$(basename "$f")
	install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/$f ${D}${libdir}/$fbasename
    done
}

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install_libraries
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP = "dev-so ldflags"
INSANE_SKIP:append:libc-musl = " file-rdeps"

RDEPENDS:${PN}:append:libc-musl = " gcompat"

PACKAGE_ARCH = "${L4T_BSP_PKGARCH}"
