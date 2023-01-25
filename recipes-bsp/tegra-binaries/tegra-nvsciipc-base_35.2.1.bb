DESCRIPTION = "NvSciIpc initialization tool"
L4T_DEB_COPYRIGHT_MD5 = "fe65a18c0cbf70d19e3d6202a8c825b6"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    ${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init \
"

MAINSUM = "342d62df7bba36332fea289584b4153ccf8ad7d5bec17afd5fef0958b31e822f"
INITSUM = "36b53071e126e9ddf6017e745f47165727ff1c7eb1eedea53a0aec41363dc044"
SRC_URI[init.sha256sum] = "${INITSUM}"

do_install() {
    install -d ${D}${bindir} ${D}${sysconfdir}
    install -m 0755 ${S}/usr/bin/nvsciipc_init ${D}${bindir}/
    install -m 0644 ${S}/etc/nvsciipc.cfg ${D}${sysconfdir}/
}

PACKAGES = "${PN}"
FILES:${PN} = "${bindir} ${sysconfdir}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
