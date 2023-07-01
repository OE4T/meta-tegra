DESCRIPTION = "Sound configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "0a282b202b51eab090698fcda604cb76"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "1745df356e76e50c28ce490acb8e8caca756469c3c4fda2e59ea13c110661f33"
MAINSUM:tegra210 = "a2896e4298bb045396fe656df1f7e14002337a21b5652d8c835eebceff0dfd44"

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
