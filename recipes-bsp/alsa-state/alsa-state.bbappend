FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

PACKAGE_ARCH_tegra = "${MACHINE_ARCH}"

do_install_append_tegra124() {
    rm ${D}${sysconfdir}/asound.conf
}

RCONFDEPS = ""
RCONFDEPS_tegra = "tegra-configs-alsa"
RDEPENDS_${PN} += "${RCONFDEPS}"
