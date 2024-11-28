DESCRIPTION = "nvpmodel tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "5d7528c1fe500782f8b39e724c929984"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-configs_${PV}_arm64.deb;subdir=${BP};name=configs"

MAINSUM = "feb52e12ac3755e1fab266417a85271f00e9d21133b7c14d11227a22a5726174"
MAINSUM:tegra210 = "09652eeccdfe1829f78ac33401199ccd536507bbcca64eeb9bdbb958df2b5371"
CFGSUM = "9ffadef17218b911825190228ccf932b7f66f00249b4e92cb710ad89d3026cef"
CFGSUM:tegra210 = "735527299540a9f7d910d7b15b3e31bb8db0480c7693327e90b656ed757dcd66"
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

FILES:${PN} = "${sbindir}/nvpmodel ${sysconfdir}"
INSANE_SKIP:${PN} = "ldflags"

PACKAGE_ARCH = "${MACHINE_ARCH}"
