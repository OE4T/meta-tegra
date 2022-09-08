DESCRIPTION = "nvpower tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "fab0c15b4bbf7f8d5ac2bd6673d4211c"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init"
SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'tools')};subdir=${BP};name=tools"

MAINSUM = "26463537a9d0b0438c89cbdabc3242d8e73410f023ed1d11ceb40a18e49b4fd7"
SRC_URI[init.sha256sum] = "9a7b2a92b8900af7d47a6d1f9e2c3c00e6fe78184c0bd02a8edfa72dcd2fa74b"
SRC_URI[tools.sha256sum] = "8e4d140cdcd3349e93cc28d87e0e0c61dd091f9535f1e022812ecb0fe5e00bb2"

SRC_URI += "\
    file://0001-Drop-bc-usage-and-remove-symlink-creation-functions.patch \
    file://nvpower.init \
    file://nvpower.service \
"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libjetsonpower.so \
"

do_install() {
    install_libraries
    install -d ${D}${libexecdir}
    install -d ${D}${sysconfdir}/nvpower/libjetsonpower
    install -m 0755 ${B}/etc/systemd/nvpower.sh ${D}${libexecdir}/
    install -m 0644 ${B}/etc/nvpower/libjetsonpower/${NVPOWER}.conf ${D}${sysconfdir}/nvpower/libjetsonpower/
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/nvpower.init ${D}${sysconfdir}/init.d/nvpower
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/nvpower.service ${D}${systemd_system_unitdir}
}

inherit systemd update-rc.d

INITSCRIPT_NAME = "nvpower"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE:${PN} = "nvpower.service"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RDEPENDS:${PN} = "bash tegra-nvpmodel"
PACKAGE_ARCH = "${MACHINE_ARCH}"
