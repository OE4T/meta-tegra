DESCRIPTION = "nvpmodel tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "d3b815fd718b1544bc231398ead64c3f"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-nvpmodel"

require tegra-debian-libraries-common.inc

MAINSUM = "f647e39e947c9666b9506116ceba7229f0d08c386d192b9e31092b4d4cce5806"

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

FILES:${PN} = "${sbindir}/nvpmodel ${sysconfdir}"
INSANE_SKIP:${PN} = "ldflags"

PACKAGE_ARCH = "${MACHINE_ARCH}"
