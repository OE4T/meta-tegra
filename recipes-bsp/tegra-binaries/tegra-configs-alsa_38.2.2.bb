DESCRIPTION = "Sound configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "1d9a81a4a5115c509c05a2219bd840b9"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"

require tegra-debian-libraries-common.inc

MAINSUM = "316e3e400d8c5c20be0b7d64f752b408e5a9507463a13f7f536587048c113473"

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
