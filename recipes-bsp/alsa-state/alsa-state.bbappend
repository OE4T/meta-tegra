FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

PACKAGE_ARCH_tegra = "${MACHINE_ARCH}"

do_install_append_tegra() {
    rm ${D}${sysconfdir}/asound.conf
    rmdir ${D}${sysconfdir} 2>/dev/null || true
}

RCONFDEPS = ""
RCONFDEPS_tegra = "tegra-configs-alsa"
RDEPENDS_${PN} += "${RCONFDEPS}"
ALLOW_EMPTY_${PN} = "1"
