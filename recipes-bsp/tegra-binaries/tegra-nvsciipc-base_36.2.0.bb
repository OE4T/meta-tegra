DESCRIPTION = "NvSciIpc initialization tool"
L4T_DEB_COPYRIGHT_MD5 = "6a451131481f1bf9e113fb684e99849c"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    ${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init \
"

MAINSUM = "aa9ff1565efae4493d107eed1c0dcef1d883f9a39acefdbd6a9623017790b374"
INITSUM = "8a9ae402b74951745f9c4501c51b78998e817cae363240bec22fa9c5068081dd"
SRC_URI[init.sha256sum] = "${INITSUM}"

do_install() {
    install -d ${D}${bindir} ${D}${sysconfdir}
    install -m 0755 ${S}/usr/bin/nvsciipc_init ${D}${bindir}/
    install -m 0644 ${S}/etc/nvsciipc.cfg ${D}${sysconfdir}/
}

PACKAGES = "${PN}"
FILES:${PN} = "${bindir} ${sysconfdir}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
