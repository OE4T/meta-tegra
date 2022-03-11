DESCRIPTION = "Sound configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "0a282b202b51eab090698fcda604cb76"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "0167fba98b85dbf7e51571bc23f28b8f79377877ed98bb30097dc02a11ddb85e"
MAINSUM:tegra210 = "b930532c06af7e6dec08ab77af422dc8040b9e71d8bfa3c6cec2159cb4e309d1"

do_install() {
    if [ -n "${TEGRA_AUDIO_DEVICE}" ]; then
        install -d ${D}${sysconfdir}
        install -m 0644 ${S}/etc/asound.conf.${TEGRA_AUDIO_DEVICE} ${D}${sysconfdir}/asound.conf
    fi

    install -d ${D}${datadir}/alsa/cards
}

do_install:append:tegra186() {
    install -m 0644 ${S}/usr/share/alsa/cards/tegra-hda.conf ${D}${datadir}/alsa/cards/
    install -m 0644 ${S}/usr/share/alsa/cards/tegra-snd-t186r.conf ${D}${datadir}/alsa/cards/
}
do_install:append:tegra194() {
    install -m 0644 ${S}/usr/share/alsa/cards/tegra-hda-galen.conf ${D}${datadir}/alsa/cards/
    install -m 0644 ${S}/usr/share/alsa/cards/tegra-hda-xnx.conf ${D}${datadir}/alsa/cards/
    install -m 0644 ${S}/usr/share/alsa/cards/jetson-xaviernx.conf ${D}${datadir}/alsa/cards/
    install -m 0644 ${S}/usr/share/alsa/cards/tegra-snd-t19x-.conf ${D}${datadir}/alsa/cards/
}
do_install:append:tegra210() {
    install -m 0644 ${B}/usr/share/alsa/cards/tegra-hda.conf ${D}${datadir}/alsa/cards/
    install -m 0644 ${B}/usr/share/alsa/cards/tegra-snd-t210r.conf ${D}${datadir}/alsa/cards/
}

FILES:${PN} = "${sysconfdir} ${datadir}/alsa"
PACKAGE_ARCH = "${MACHINE_ARCH}"
