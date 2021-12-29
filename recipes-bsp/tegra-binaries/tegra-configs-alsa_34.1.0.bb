DESCRIPTION = "Sound configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "0a282b202b51eab090698fcda604cb76"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "aae9f9cf02fd0a0159f772b3582422164989131617a971df8ebdbff1339c59fd"

do_install() {
    if [ -n "${TEGRA_AUDIO_DEVICE}" ]; then
        install -d ${D}${sysconfdir}
        install -m 0644 ${S}/etc/asound.conf.${TEGRA_AUDIO_DEVICE} ${D}${sysconfdir}/asound.conf
    fi

    install -d ${D}${datadir}/alsa/cards ${D}${datadir}/alsa/init/postinit
    install -m 0644 ${S}/usr/share/alsa/cards/*.conf ${D}${datadir}/alsa/cards/
    install -m 0644 ${S}/usr/share/alsa/init/postinit/* ${D}${datadir}/alsa/init/postinit/
}
do_install:append:tegra210() {
    install -m 0644 ${B}/usr/share/alsa/cards/tegra-hda.conf ${D}${datadir}/alsa/cards/
    install -m 0644 ${B}/usr/share/alsa/cards/tegra-snd-t210r.conf ${D}${datadir}/alsa/cards/
}

FILES:${PN} = "${sysconfdir} ${datadir}/alsa"
PACKAGE_ARCH = "${MACHINE_ARCH}"
