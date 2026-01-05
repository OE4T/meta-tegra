DESCRIPTION = "Miscellaneous tools provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "1c55a704d80b8d8275c122433b1661bf"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"

require tegra-debian-libraries-common.inc

MAINSUM = "864281721f202c9e3ae8c7b66ff469b05ee8abc6d3ae6cb0eaaa8a5e7769398f"

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
