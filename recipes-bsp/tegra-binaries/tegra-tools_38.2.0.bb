DESCRIPTION = "Miscellaneous tools provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "8dd8762d7a7fea51677fa5d99d4653e2"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"

require tegra-debian-libraries-common.inc

MAINSUM = "2b985d1a7943b92888fcc150b0f5364f56edba0529c05d2e3993cb5793819d66"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/usr/bin/tegrastats ${D}${bindir}/
    install -m 0755 ${S}/usr/bin/jetson_clocks ${D}${bindir}/
}

PACKAGES = "${PN}-tegrastats ${PN}-jetson-clocks ${PN}"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "${PN}-tegrastats ${PN}-jetson-clocks"
FILES:${PN}-tegrastats = "${bindir}/tegrastats"
INSANE_SKIP:${PN}-tegrastats = "ldflags"
FILES:${PN}-jetson-clocks = "${bindir}/jetson_clocks"
RDEPENDS:${PN}-jetson-clocks = "bash"
