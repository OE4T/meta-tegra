require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DESCRIPTION = "nvpmodel tool and configuration files"

COMPATIBLE_MACHINE = "(tegra)"
COMPATIBLE_MACHINE_tegra124 = "(-)"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nv_tools.tbz2
}

do_compile[noexec] = "1"

# TODO: For TX2i, use:
#   nvpmodel_t186_storm_ucm1, or
#   nvpmodel_t186_storm_ucm2
NVPMODEL = ""
NVPMODEL_tegra186 ?= "nvpmodel_t186"
NVPMODEL_tegra194 ?= "nvpmodel_t194"
NVPMODEL_tegra210 ?= "nvpmodel_t210"
NVPMODEL_jetson-nano ?= "nvpmodel_t210_jetson-nano"

do_install() {
    install -d ${D}${sbindir}
    install -d ${D}${sysconfdir}/nvpmodel
    install -m 0755 ${B}/usr/sbin/nvpmodel ${D}${sbindir}/
    install -m 0644 ${B}/etc/nvpmodel/${NVPMODEL}.conf ${D}${sysconfdir}/nvpmodel.conf
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/nvpmodel.init ${D}${sysconfdir}/init.d/nvpmodel
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/nvpmodel.service ${D}${systemd_system_unitdir}
}

inherit systemd update-rc.d

INITSCRIPT_NAME = "nvpmodel"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE_${PN} = "nvpmodel.service"

FILES_${PN} = "${sbindir}/nvpmodel ${sysconfdir}"
INSANE_SKIP_${PN} = "ldflags"

PACKAGE_ARCH = "${MACHINE_ARCH}"
