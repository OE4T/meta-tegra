DESCRIPTION = "NvSciIpc initialization tool"
L4T_DEB_COPYRIGHT_MD5 = "661953981b9ae6f275ad01c67b2e881e"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-nvsci"

require tegra-debian-libraries-common.inc

MAINSUM = "d8a2781505470bff3842d0ef9cb7993a11e02769aaccd6b2666d5415873afaef"

do_install() {
    install -d ${D}${bindir} ${D}${sysconfdir}
    install -m 0755 ${S}/usr/bin/nvsciipc_init ${D}${bindir}/
    install -m 0644 ${S}/etc/nvsciipc.cfg ${D}${sysconfdir}/
    install -d ${D}${sysconfdir}/sysctl.d
    install -m 0644 ${S}/etc/sysctl.d/60-nvsciipc.conf ${D}${sysconfdir}/sysctl.d/
}

PACKAGES = "${PN}"
FILES:${PN} = "${bindir} ${sysconfdir}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
