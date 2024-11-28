DESCRIPTION = "Sound configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "893ae780971ba0060468740475e46dc3"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "c5d32bc0c84ad79f9266a622aa8c09ec6a230de19a3e35edeb01116cf9506570"
MAINSUM:tegra210 = "256b73c87f325ea94600a94f37a7e8fa1c69b8f7b299a616b3876a61738a3472"

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
