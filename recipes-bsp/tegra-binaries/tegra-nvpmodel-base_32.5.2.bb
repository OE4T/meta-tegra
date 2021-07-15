require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DESCRIPTION = "nvpmodel tool and configuration files"

# When left unset, l4t default setting will be used
NVPMODEL_CONFIG_DEFAULT ??= ""

COMPATIBLE_MACHINE = "(tegra)"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nv_tools.tbz2
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sbindir}
    install -d ${D}${sysconfdir}/nvpmodel
    install -m 0755 ${B}/usr/sbin/nvpmodel ${D}${sbindir}/
    install -m 0644 ${B}/etc/nvpmodel/${NVPMODEL}.conf ${D}${sysconfdir}/nvpmodel.conf
    if [ -n "${NVPMODEL_CONFIG_DEFAULT}" ]; then
        sed -i -e "s/PM_CONFIG DEFAULT=[0-9]\+/PM_CONFIG DEFAULT=${NVPMODEL_CONFIG_DEFAULT}/" ${D}${sysconfdir}/nvpmodel.conf
    fi
}

FILES_${PN} = "${sbindir}/nvpmodel ${sysconfdir}"
INSANE_SKIP_${PN} = "ldflags"

PACKAGE_ARCH = "${MACHINE_ARCH}"
