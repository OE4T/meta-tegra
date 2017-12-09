require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DESCRIPTION = "Miscellaneous tools provided by L4T"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nv_tools.tbz2
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${B}/home/nvidia/tegrastats ${D}${sbindir}/
    install -m 0755 ${B}/home/nvidia/jetson_clocks.sh ${D}${sbindir}/
}

do_install_append_tegra186() {
    install -d ${D}${sysconfdir}/nvpmodel
    install -m 0755 ${B}/usr/sbin/nvpmodel ${D}${sbindir}/
    install -m 0644 ${B}/etc/nvpmodel/*.conf ${D}${sysconfdir}/nvpmodel/
    ln -s nvpmodel/nvpmodel_t186.conf ${D}${sysconfdir}/nvpmodel.conf
    install -d ${D}${sysconfdir}/init.d
    install -m 0644 ${S}/nvpmodel.init ${D}${sysconfdir}/init.d/nvpmodel
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/nvpmodel.service ${D}${systemd_system_unitdir}
}

inherit systemd

NVPMODEL = ""
NVPMODEL_tegra186 = "${PN}-nvpmodel"
PACKAGES = "${PN}-tegrastats ${PN}-jetson-clocks ${NVPMODEL}"
FILES_${PN}-tegrastats = "${sbindir}/tegrastats"
INSANE_SKIP_${PN}-tegrastats = "ldflags"
FILES_${PN}-jetson-clocks = "${sbindir}/jetson_clocks.sh"
RDEPENDS_${PN}-jetson-clocks = "bash"
FILES_${PN}-nvpmodel = "${sbindir}/nvpmodel ${sysconfdir}"
INSANE_SKIP_${PN}-nvpmodel = "ldflags"

SYSTEMD_PACKAGES = "${NVPMODEL}"
SYSTEMD_SERVICE_${PN}-nvpmodel = "nvpmodel.service"
