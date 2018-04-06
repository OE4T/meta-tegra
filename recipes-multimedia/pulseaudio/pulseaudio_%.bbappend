do_install_append_tegra() {
    rm ${D}${sysconfdir}/pulse/default.pa
}

RCONFDEPS = ""
RCONFDEPS_tegra = "tegra-configs-pulseaudio"
RDEPENDS_pulseaudio-server += "${RCONFDEPS}"

PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
