FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI_append_tegra = " file://weston-tegra-overrides.conf"

do_install_append_tegra() {
    install -d ${D}${sysconfdir}/systemd/system/weston.service.d
    install -m 0644 ${WORKDIR}/weston-tegra-overrides.conf ${D}${sysconfdir}/systemd/system/weston.service.d/
}

PACKAGE_ARCH_tegra = "${TEGRA_PKGARCH}"
