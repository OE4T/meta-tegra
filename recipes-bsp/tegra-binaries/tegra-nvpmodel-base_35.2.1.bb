DESCRIPTION = "nvpmodel tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "859d1cdffa6db892caf6acf77a9264ef"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-nvpmodel"

require tegra-debian-libraries-common.inc

MAINSUM = "61524668ef47922322d16ffda6eae5dadcdef7792c422da83fd050130a4e35cb"

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
