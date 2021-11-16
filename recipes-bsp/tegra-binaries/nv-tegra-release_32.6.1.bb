DESCRIPTION = "Release version information file from L4T"
L4T_DEB_COPYRIGHT_MD5 = "fab0c15b4bbf7f8d5ac2bd6673d4211c"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

L4T_BSP_DEB_VERSION = "${L4T_BSP_DEB_ORIG_VERSION}"
MAINSUM = "2c87814d6d06344a81baf7709377c5d2b1cf22b999fa136ca20531cf58f315c1"
MAINSUM_tegra210 = "d2d8941982e1b344868b0b2d2a93f6ecf886493722c2620a5864262f5db73363"

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

FILES_${PN} = "${sysconfdir}"
FILES_${PN}-dev = "${datadir}/nv_tegra"
