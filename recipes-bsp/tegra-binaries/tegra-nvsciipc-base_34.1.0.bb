DESCRIPTION = "NvSciIpc initialization tool"
L4T_DEB_COPYRIGHT_MD5 = "fe65a18c0cbf70d19e3d6202a8c825b6"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    nvidia-l4t-init_${PV}_arm64.deb;subdir=${BP};name=init \
"

MAINSUM = "b646c5bdd080221b59bdfcdddd25ac64e736b261e1731234bc9e04d8f915cdcf"
INITSUM = "aae9f9cf02fd0a0159f772b3582422164989131617a971df8ebdbff1339c59fd"
SRC_URI[init.sha256sum] = "${INITSUM}"

do_install() {
    install -d ${D}${bindir} ${D}${sysconfdir}
    install -m 0755 ${S}/usr/bin/nvsciipc_init ${D}${bindir}/
    install -m 0644 ${S}/etc/nvsciipc.cfg ${D}${sysconfdir}/
}

PACKAGES = "${PN}"
FILES:${PN} = "${bindir} ${sysconfdir}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
