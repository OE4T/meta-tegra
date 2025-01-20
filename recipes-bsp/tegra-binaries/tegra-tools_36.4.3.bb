DESCRIPTION = "Miscellaneous tools provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "1c55a704d80b8d8275c122433b1661bf"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"

require tegra-debian-libraries-common.inc

MAINSUM = "5e861d7dea1dca2709461e4831946968fe526f31eb87fa205a7b073d2a151ac3"

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
