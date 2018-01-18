do_install_append_tegra186() {
    rm ${D}${sysconfdir}/pulse/default.pa
}
do_install_append_tegra210() {
    rm ${D}${sysconfdir}/pulse/default.pa
}
do_install_append_tegra124() {
    rm ${D}${sysconfdir}/pulse/default.pa
}

RCONFDEPS = ""
RCONFDEPS_tegra186 = "tegra-configs-pulseaudio"
RCONFDEPS_tegra210 = "tegra-configs-pulseaudio"
RCONFDEPS_tegra124 = "tegra-configs-pulseaudio"
RDEPENDS_${PN} += "${RCONFDEPS}"

PACKAGE_ARCH_tegra186 = "${SOC_FAMILY_PKGARCH}"
PACKAGE_ARCH_tegra210 = "${SOC_FAMILY_PKGARCH}"
PACKAGE_ARCH_tegra124 = "${SOC_FAMILY_PKGARCH}"
