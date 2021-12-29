DESCRIPTION = "Miscellaneous tools provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "fe65a18c0cbf70d19e3d6202a8c825b6"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

<<<<<<<< HEAD:recipes-bsp/tegra-binaries/tegra-tools_32.7.2.bb
MAINSUM = "f78249b26d93eb1e9851aa4558fe144125139ee165d4dfd9726cd7fe71d0e228"
MAINSUM:tegra210 = "a993495e473e9050f1bacadc5e7713bc652f28257a2acf80389e1d04206a48a7"
========
MAINSUM = "b646c5bdd080221b59bdfcdddd25ac64e736b261e1731234bc9e04d8f915cdcf"
>>>>>>>> 390a51f3 (tegra-binaries: update for 34.1.0):recipes-bsp/tegra-binaries/tegra-tools_34.1.0.bb

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
