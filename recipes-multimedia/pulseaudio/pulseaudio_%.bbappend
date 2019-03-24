EXTRA_OECONF_append_tegra124 = " --disable-memfd"

do_install_append_tegra124() {
    rm ${D}${sysconfdir}/pulse/default.pa
}

RCONFDEPS = ""
RCONFDEPS_tegra124 = "tegra-configs-pulseaudio"
RDEPENDS_pulseaudio-server += "${RCONFDEPS}"

PACKAGE_ARCH_tegra124 = "${SOC_FAMILY_PKGARCH}"
