DESCRIPTION = "Release version information file from L4T"
L4T_DEB_COPYRIGHT_MD5 = "d9a8361f8068fc4e3b934118d1de9f3f"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

MAINSUM = "5c5ae35b29c3f1ac438fb1d4c065d6e5f22fa640cedc35862774cd836f627ded"

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
