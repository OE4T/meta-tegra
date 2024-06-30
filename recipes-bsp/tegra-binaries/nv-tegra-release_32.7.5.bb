DESCRIPTION = "Release version information file from L4T"
L4T_DEB_COPYRIGHT_MD5 = "ef1b882a6a8ed90f38e4b0288fcd1525"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "0afb73f595113bf02d2f66924dd351bb665941a569507fd730c66167a4823d94"
MAINSUM:tegra210 = "c2550014e670366c339662203cbc0c5d2afd92b5a6557923844a42526e3aa6ac"

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
