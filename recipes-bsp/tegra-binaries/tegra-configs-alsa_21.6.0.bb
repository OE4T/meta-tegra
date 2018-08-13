require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DESCRIPTION = "Sound configuration files provided by L4T"

inherit systemd

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/config.tbz2 etc
}

do_compile[noexec] = "1"

do_install() {
    if [ -n "${TEGRA_AUDIO_DEVICE}" ]; then
        install -d ${D}${sysconfdir}
        install -m 0644 ${B}/etc/asound.conf.${TEGRA_AUDIO_DEVICE} ${D}${sysconfdir}/asound.conf
    fi
}

ALLOW_EMPTY_${PN} = "1"

FILES_${PN} = "${sysconfdir} ${datadir}/alsa"
PACKAGE_ARCH = "${MACHINE_ARCH}"
