DESCRIPTION = "nvpmodel tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "fb3fdb4531cd26a131ab4f5beaaf2f94"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-nvpmodel"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init"
MAINSUM = "363d95088f788b301a52a93da841e26b685a010d16056bf417c4704c3f265457"
SRC_URI[init.sha256sum] = "e6db7f2002d85300b8cdda62cac0219144997453915efc8aef16a20ca8fa44bc"

DEPENDS = "tegra-libraries-core"

# When left unset, l4t default setting will be used
NVPMODEL_CONFIG_DEFAULT ??= ""

do_install() {
    install -d ${D}${sbindir}
    install -d ${D}${sysconfdir}/nvpmodel
    install -m 0755 ${B}/usr/sbin/nvpmodel ${D}${sbindir}/
    install -m 0644 ${B}/etc/nvpmodel/${NVPMODEL}.conf ${D}${sysconfdir}/nvpmodel.conf
    if [ -n "${NVPMODEL_CONFIG_DEFAULT}" ]; then
        sed -i -e "s/PM_CONFIG DEFAULT=[0-9]\+/PM_CONFIG DEFAULT=${NVPMODEL_CONFIG_DEFAULT}/" ${D}${sysconfdir}/nvpmodel.conf
    fi
}

do_install:append:tegra264() {
    install -d ${D}/opt/nvidia/l4t-gpusetup
    install -m 0755 ${B}/var/lib/l4t-gpu-static-pg/gpu_pg_mask ${D}/opt/nvidia/l4t-gpusetup/
}

FILES:${PN} = "${sbindir}/nvpmodel ${sysconfdir}"
FILES:${PN}:append:tegra264 = " /opt/nvidia/l4t-gpusetup"
INSANE_SKIP:${PN} = "ldflags"

PACKAGE_ARCH = "${MACHINE_ARCH}"
