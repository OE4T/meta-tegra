DESCRIPTION = "Miscellaneous tools provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "fe65a18c0cbf70d19e3d6202a8c825b6"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

L4T_BSP_DEB_VERSION = "${L4T_BSP_DEB_ORIG_VERSION}"
MAINSUM = "5a74992b743b419a0763c96c6840a08792767bd12de6e5ebc384b0cbcf21bd92"
MAINSUM_tegra210 = "801e7f20a5eb2b96b36eff69e592c29cd10f53872b6ebafbc8757b2d2614b502"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/usr/bin/tegrastats ${D}${bindir}/
    install -m 0755 ${S}/usr/bin/jetson_clocks ${D}${bindir}/
}

PACKAGES = "${PN}-tegrastats ${PN}-jetson-clocks ${PN}"
ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} = "${PN}-tegrastats ${PN}-jetson-clocks"
FILES_${PN}-tegrastats = "${bindir}/tegrastats"
INSANE_SKIP_${PN}-tegrastats = "ldflags"
FILES_${PN}-jetson-clocks = "${bindir}/jetson_clocks"
RDEPENDS_${PN}-jetson-clocks = "bash"
