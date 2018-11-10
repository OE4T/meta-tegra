require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DESCRIPTION = "Sound configuration files provided by L4T"

inherit systemd

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/config.tbz2 etc usr/share/alsa/cards
}

do_compile[noexec] = "1"

do_install() {
    if [ -n "${TEGRA_AUDIO_DEVICE}" ]; then
        install -d ${D}${sysconfdir}
        install -m 0644 ${B}/etc/asound.conf.${TEGRA_AUDIO_DEVICE} ${D}${sysconfdir}/asound.conf
    fi

    install -d ${D}${datadir}/alsa/cards
}

do_install_append_tegra186() {
    install -m 0644 ${B}/usr/share/alsa/cards/tegra-hda.conf ${D}${datadir}/alsa/cards/
}
do_install_append_tegra194() {
    install -m 0644 ${B}/usr/share/alsa/cards/tegra-hda-galen.conf ${D}${datadir}/alsa/cards/
}

FILES_${PN} = "${sysconfdir} ${datadir}/alsa"
PACKAGE_ARCH = "${MACHINE_ARCH}"
