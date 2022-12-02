DESCRIPTION = "Sound configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "0a282b202b51eab090698fcda604cb76"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "aabb2c405d303a166e31946a51ac39eaf96293a86b40e33a02d9c067ebc3508e"
MAINSUM:tegra210 = "af336fe5295030664eef19497fe42279e6f41f2a34b989b0a54266967e4b2f36"

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
