DESCRIPTION = "nvpmodel tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "5d7528c1fe500782f8b39e724c929984"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-configs_${PV}_arm64.deb;subdir=${BP};name=configs"

MAINSUM = "ffe3f115eae01b2c0a67e56fba12d005ecd7253d5a1e15cebeebb824268788bf"
MAINSUM:tegra210 = "7f0acc45f285b3f7540812016889b7d1775a206396075bf3018a7a49cc8c8150"
CFGSUM = "5d43d1bcd562f2f925bd4dd0e7d437699eff3651a8ef0a6e5570bb5fac2ff07d"
CFGSUM:tegra210 = "e84a6843cace8dcc5c51bc5ce76c1fba20fda4a6648ea7eb85e605ad6c102688"
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
