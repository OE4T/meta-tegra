DESCRIPTION = "NvSciIpc initialization tool"
L4T_DEB_COPYRIGHT_MD5 = "1c55a704d80b8d8275c122433b1661bf"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    ${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init \
"

MAINSUM = "5e861d7dea1dca2709461e4831946968fe526f31eb87fa205a7b073d2a151ac3"
INITSUM = "3a396551d88ebebd70b1eae57ed74837e82485c7897ab1c233735f3ea63e8215"
SRC_URI[init.sha256sum] = "${INITSUM}"

do_install() {
    install -d ${D}${bindir} ${D}${sysconfdir}
    install -m 0755 ${S}/usr/bin/nvsciipc_init ${D}${bindir}/
    install -m 0644 ${S}/etc/nvsciipc.cfg ${D}${sysconfdir}/
}

PACKAGES = "${PN}"
FILES:${PN} = "${bindir} ${sysconfdir}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
