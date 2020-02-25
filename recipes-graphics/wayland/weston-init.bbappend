FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI_append_tegra = " file://weston-tegra-overrides.conf"

do_install_append_tegra() {
    install -d ${D}${sysconfdir}/systemd/system/weston@.service.d
    install -m 0644 ${WORKDIR}/weston-tegra-overrides.conf ${D}${sysconfdir}/systemd/system/weston@.service.d/
    if [ "${VIRTUAL-RUNTIME_init_manager}" = "systemd" ]; then
        install -d ${D}${sysconfdir}/systemd/system
	ln -s /dev/null ${D}${sysconfdir}/systemd/system/weston.service
    fi
}

PACKAGE_ARCH_tegra = "${MACHINE_ARCH}"
