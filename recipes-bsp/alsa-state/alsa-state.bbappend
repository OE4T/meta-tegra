PACKAGE_ARCH_tegra = "${MACHINE_ARCH}"

do_install_append_tegra() {
    rm ${D}${sysconfdir}/asound.conf
    rmdir ${D}${sysconfdir} 2>/dev/null || true
}

RDEPENDS_${PN}_append_tegra = " tegra-configs-alsa"
ALLOW_EMPTY_${PN}_tegra = "1"
