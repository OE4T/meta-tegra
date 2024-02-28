DESCRIPTION = "Release version information file from L4T"
L4T_DEB_COPYRIGHT_MD5 = "4316d40a8ea9e946e76430d2d02b5848"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

MAINSUM = "e32ade8d9abebedaffdc922c513152ea9686aa965d9e10514647dfccb17ebdf1"

INHIBIT_DEFAULT_DEPS = "1"

B = "${WORKDIR}/build"

do_compile() {
	head -n 1 ${S}/etc/nv_tegra_release > ${B}/nv_tegra_release
}

do_install() {
    install -d ${D}${sysconfdir} ${D}${datadir}/nv_tegra
    install -m 0644 ${B}/nv_tegra_release ${D}${sysconfdir}
    # stash a copy under /usr/share for use by tools, since /etc doesn't get into build sysroots
    install -m 0644 ${B}/nv_tegra_release ${D}${datadir}/nv_tegra
}

FILES:${PN} = "${sysconfdir}"
FILES:${PN}-dev = "${datadir}/nv_tegra"
