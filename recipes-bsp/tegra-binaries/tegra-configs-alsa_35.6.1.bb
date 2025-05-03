DESCRIPTION = "Sound configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "27caeb86bc9bb9f3419a7c87e9a301f6"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"

require tegra-debian-libraries-common.inc

MAINSUM = "d7d3ecf6f394857b1b8ea2e33b3791f6bcfc083811f7756e7a1afade95c76b3b"

SRC_URI += "file://asound.conf.tegra-hda-p3767-p3509"

do_install() {
    if [ -n "${TEGRA_AUDIO_DEVICE}" ]; then
        install -d ${D}${sysconfdir}
	if [ -e "${WORKDIR}/asound.conf.${TEGRA_AUDIO_DEVICE}" ]; then
            install -m 0644 ${WORKDIR}/asound.conf.${TEGRA_AUDIO_DEVICE} ${D}${sysconfdir}/asound.conf
	else
            install -m 0644 ${S}/etc/asound.conf.${TEGRA_AUDIO_DEVICE} ${D}${sysconfdir}/asound.conf
	fi
    fi

    install -d ${D}${datadir}/alsa/cards ${D}${datadir}/alsa/init/postinit
    install -m 0644 ${S}/usr/share/alsa/cards/*.conf ${D}${datadir}/alsa/cards/
    install -m 0644 ${S}/usr/share/alsa/init/postinit/* ${D}${datadir}/alsa/init/postinit/
}

FILES:${PN} = "${sysconfdir} ${datadir}/alsa"
PACKAGE_ARCH = "${MACHINE_ARCH}"
