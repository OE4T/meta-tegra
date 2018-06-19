require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc

INHIBIT_DEFAULT_DEPS = "1"

do_configure() {
	tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 etc/nv_tegra_release
}

do_compile() {
	rm -f ${B}/nv_tegra_release
	head -n 1 ${B}/etc/nv_tegra_release > ${B}/nv_tegra_release
}

do_install() {
    install -d ${D}${sysconfdir} ${D}${datadir}/nv_tegra
    install -m 0644 ${B}/nv_tegra_release ${D}${sysconfdir}
    # stash a copy under /usr/share for use by tools, since /etc doesn't get into build sysroots
    install -m 0644 ${B}/nv_tegra_release ${D}${datadir}/nv_tegra
}

FILES_${PN} = "${sysconfdir}"
FILES_${PN}-dev = "${datadir}/nv_tegra"
