DESCRIPTION = "Miscellaneous tools provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "fe65a18c0cbf70d19e3d6202a8c825b6"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "27f9da6ddd55b6d1bfd1fa38cd61cf61d215e61a88f992fb400bec72141668c2"
MAINSUM_tegra210 = "9ad3a1b64b97691670a5eb9fe766fdbb4f5ef1f03bbea27e72978757838af6ea"

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
