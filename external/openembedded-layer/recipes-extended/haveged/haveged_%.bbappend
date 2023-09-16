FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
EXTRA_INHERITS = ""
EXTRA_INHERITS:tegra = "update-rc.d ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"
inherit ${EXTRA_INHERITS}

INITSCRIPT_PACKAGES:tegra = "${PN}"
INITSCRIPT_NAME:tegra = "haveged"
INITSCRIPT_PARAMS:${PN}:tegra = "defaults 9"

SYSTEMD_PACKAGES:tegra = "${PN}"
SYSTEMD_SERVICE:${PN}:tegra = "haveged.service"

# Based on files from v1.9.14
# Obsolete with kernels >= v5.6, but tegra platforms still use 4.9
SRC_URI:append:tegra = " \
    file://haveged.service \
    file://haveged.init \
"

do_install:append:tegra() {
    install -d ${D}${INIT_D_DIR}
    sed -e "s,@SBIN_DIR@,${sbindir},g" ${WORKDIR}/haveged.init >${D}${INIT_D_DIR}/haveged
    chmod 755 ${D}${INIT_D_DIR}/haveged
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${WORKDIR}/haveged.service ${D}${systemd_system_unitdir}/haveged.service
	sed -i -e "s,@SBIN_DIR@,${sbindir},g" ${D}${systemd_system_unitdir}/haveged.service
    fi
}

PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
