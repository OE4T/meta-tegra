DESCRIPTION = "Miscellaneous tools provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "1c55a704d80b8d8275c122433b1661bf"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"

require tegra-debian-libraries-common.inc

MAINSUM = "987b70e0a4927ec4f9a1b768b117f18d7566676eaec83c6a10452af5110c3084"

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
