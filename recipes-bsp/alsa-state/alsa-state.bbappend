PACKAGE_ARCH:tegra = "${MACHINE_ARCH}"

do_install:append:tegra() {
    rm ${D}${sysconfdir}/asound.conf
    rmdir ${D}${sysconfdir} 2>/dev/null || true
}

RDEPENDS:${PN}:append:tegra = " tegra-configs-alsa nvidia-kernel-oot-alsa"
ALLOW_EMPTY:${PN}:tegra = "1"
