DESCRIPTION = "Sound configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "27caeb86bc9bb9f3419a7c87e9a301f6"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"

require tegra-debian-libraries-common.inc

MAINSUM = "165ca517257cc4ff89a17afe83f6d9e04df8630f55837f0649f792dccaefc156"

SRC_URI += "file://asound.conf.tegra-hda-p3767-p3509"

do_install() {
    if [ -n "${TEGRA_AUDIO_DEVICE}" ]; then
        install -d ${D}${sysconfdir}
        if [ -e "${UNPACKDIR}/asound.conf.${TEGRA_AUDIO_DEVICE}" ]; then
            install -m 0644 ${UNPACKDIR}/asound.conf.${TEGRA_AUDIO_DEVICE} ${D}${sysconfdir}/asound.conf
        else
            install -m 0644 ${S}/etc/asound.conf.${TEGRA_AUDIO_DEVICE} ${D}${sysconfdir}/asound.conf
            if [ "${TEGRA_AUDIO_DEVICE}" = "tegra-hda-jetson-agx" ]; then
                sed -i -e's!HDA,8!HDA,3!' ${D}${sysconfdir}/asound.conf
            fi
        fi
    fi

    install -d ${D}${datadir}/alsa/cards ${D}${datadir}/alsa/init/postinit
    install -m 0644 ${S}/usr/share/alsa/cards/*.conf ${D}${datadir}/alsa/cards/
    install -m 0644 ${S}/usr/share/alsa/init/postinit/* ${D}${datadir}/alsa/init/postinit/
}

FILES:${PN} = "${sysconfdir} ${datadir}/alsa"
PACKAGE_ARCH = "${MACHINE_ARCH}"
