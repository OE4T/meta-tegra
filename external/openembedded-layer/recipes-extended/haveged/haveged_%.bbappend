FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}

SYSTEMD_PACKAGES:tegra = "${PN}"
SYSTEMD_SERVICE:${PN}:tegra = "haveged.service"

# Based on init.d/service.redhat from 1.9.14
# https://raw.githubusercontent.com/jirka-h/haveged/v1.9.14/init.d/service.redhat
SRC_URI:append:tegra = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://haveged.service', '', d)}"

do_install:append:tegra() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/haveged.service ${D}${systemd_system_unitdir}/haveged.service
        sed -i -e "s,@SBIN_DIR@,${sbindir},g" ${D}${systemd_system_unitdir}/haveged.service
    fi
}

PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
