DESCRIPTION = "nvpmodel tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "fb3fdb4531cd26a131ab4f5beaaf2f94"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-nvpmodel"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init"
MAINSUM = "07ce163b59058992f225328cf993e9c97a9806319db6b14e29f5662f3b6a032c"
SRC_URI[init.sha256sum] = "95f5f1c860fed1bf353919eb979b3f67301acdbe5abae8c964edebad405a737e"

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
    install -m 0755 ${B}/opt/nvidia/l4t-gpusetup/gpu_pg_mask ${D}/opt/nvidia/l4t-gpusetup/
}

FILES:${PN} = "${sbindir}/nvpmodel ${sysconfdir}"
FILES:${PN}:append:tegra264 = " /opt/nvidia/l4t-gpusetup"
INSANE_SKIP:${PN} = "ldflags"

PACKAGE_ARCH = "${MACHINE_ARCH}"
