FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
SRC_URI_append_tegra = " file://wired.network"

do_install_append_tegra() {
	install -D -m0644 ${WORKDIR}/wired.network ${D}${systemd_unitdir}/network/80-wired.network
}

FILES_${PN} += "${systemd_unitdir}/network"
