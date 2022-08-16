DESCRIPTION = "Sound configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "0a282b202b51eab090698fcda604cb76"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "b066cd3e878bf79aa7b1076d28654449d0584d0d43ab4f24da34b734a5359292"

do_install() {
    if [ -n "${TEGRA_AUDIO_DEVICE}" ]; then
        install -d ${D}${sysconfdir}
        install -m 0644 ${S}/etc/asound.conf.${TEGRA_AUDIO_DEVICE} ${D}${sysconfdir}/asound.conf
    fi

    install -d ${D}${datadir}/alsa/cards ${D}${datadir}/alsa/init/postinit
    install -m 0644 ${S}/usr/share/alsa/cards/*.conf ${D}${datadir}/alsa/cards/
    install -m 0644 ${S}/usr/share/alsa/init/postinit/* ${D}${datadir}/alsa/init/postinit/
}

FILES:${PN} = "${sysconfdir} ${datadir}/alsa"
PACKAGE_ARCH = "${MACHINE_ARCH}"
