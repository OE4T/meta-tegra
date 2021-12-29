DESCRIPTION = "nvpmodel tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "859d1cdffa6db892caf6acf77a9264ef"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-nvpmodel"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

<<<<<<<< HEAD:recipes-bsp/tegra-binaries/tegra-nvpmodel-base_32.7.2.bb
SRC_SOC_DEBS += "nvidia-l4t-configs_${PV}_arm64.deb;subdir=${BP};name=configs"

MAINSUM = "f78249b26d93eb1e9851aa4558fe144125139ee165d4dfd9726cd7fe71d0e228"
MAINSUM:tegra210 = "a993495e473e9050f1bacadc5e7713bc652f28257a2acf80389e1d04206a48a7"
CFGSUM = "5583b9cae938cba31272d14f7f723c2514f8b64caf85f4d4ecec07525358b540"
CFGSUM:tegra210 = "e81d5c88d86bda285e9ddfee1262024977247a79b11cb4aed84f5a9d0f28d524"
SRC_URI[configs.sha256sum] = "${CFGSUM}"
========
MAINSUM = "9400b6fc4ba36e34dc85bae47fa0cf13acdda8a0d73fe73fd78469c7eacabde6"
>>>>>>>> 390a51f3 (tegra-binaries: update for 34.1.0):recipes-bsp/tegra-binaries/tegra-nvpmodel-base_34.1.0.bb

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
