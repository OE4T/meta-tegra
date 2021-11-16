DESCRIPTION = "nvpmodel tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "fe65a18c0cbf70d19e3d6202a8c825b6"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-configs_${PV}_arm64.deb;subdir=${BP};name=configs"

L4T_BSP_DEB_VERSION = "${L4T_BSP_DEB_ORIG_VERSION}"
MAINSUM = "5a74992b743b419a0763c96c6840a08792767bd12de6e5ebc384b0cbcf21bd92"
MAINSUM_tegra210 = "801e7f20a5eb2b96b36eff69e592c29cd10f53872b6ebafbc8757b2d2614b502"
CFGSUM = "20c27ceff1d8f75df8715236f5712b5d9dbe6684c6550e777ffc37455a2cd3c5"
CFGSUM_tegra210 = "29fa498e9aeb0341c9baa08c75160a7343f51e1465357f7c7aa5fc84a88a8a14"
SRC_URI[configs.sha256sum] = "${CFGSUM}"

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

FILES_${PN} = "${sbindir}/nvpmodel ${sysconfdir}"
INSANE_SKIP_${PN} = "ldflags"

PACKAGE_ARCH = "${MACHINE_ARCH}"
