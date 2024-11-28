DESCRIPTION = "Release version information file from L4T"
L4T_DEB_COPYRIGHT_MD5 = "ef1b882a6a8ed90f38e4b0288fcd1525"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "7b81a016d6a0f283553b01516fa562c8c99c95bb87d88b680dcbc113e8bfa938"
MAINSUM:tegra210 = "61ada8308bd38fe6fe1793b658c83c3ef3dcc057a0d9e080dc0336e61be683c2"

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
