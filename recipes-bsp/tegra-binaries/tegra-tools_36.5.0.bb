DESCRIPTION = "Miscellaneous tools provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "8dd8762d7a7fea51677fa5d99d4653e2"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"

require tegra-debian-libraries-common.inc

MAINSUM = "3441c25d17b1513d68cb21faa25a1e40dc4811b590c3c806e61a78d6d029ffe3"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/usr/bin/tegrastats ${D}${bindir}/
    install -m 0755 ${S}/usr/bin/jetson_clocks ${D}${bindir}/
    install -m 0755 -D -t ${D}${sbindir} ${S}/usr/sbin/nv_get_dram_info ${S}/usr/sbin/nv_fuse_read.sh
}

PACKAGES = "${PN}-tegrastats ${PN}-jetson-clocks ${PN}-fuse-read ${PN}-dram-info ${PN}"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "${PN}-tegrastats ${PN}-jetson-clocks ${PN}-fuse-read ${PN}-dram-info"
FILES:${PN}-tegrastats = "${bindir}/tegrastats"
INSANE_SKIP:${PN}-tegrastats = "ldflags"
FILES:${PN}-jetson-clocks = "${bindir}/jetson_clocks"
RDEPENDS:${PN}-jetson-clocks = "bash"
FILES:${PN}-fuse-read = "${sbindir}/nv_fuse_read.sh"
RDEPENDS:${PN}-fuse-read = "bash xxd coreutils"
FILES:${PN}-dram-info = "${sbindir}/nv_get_dram_info"
INSANE_SKIP:${PN}-dram-info = "ldflags"
