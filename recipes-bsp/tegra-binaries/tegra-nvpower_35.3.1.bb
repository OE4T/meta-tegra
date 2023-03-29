DESCRIPTION = "nvpower tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "fab0c15b4bbf7f8d5ac2bd6673d4211c"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init"
SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'tools')};subdir=${BP};name=tools"

MAINSUM = "4ae0a682f763d084c1c2b0c6a214a50ddf1910dcf666199e2f5d42130e789ffd"
SRC_URI[init.sha256sum] = "f968bd70371dd0e8b4a1ab4dac3e1b84552453ab39c9ca6460efd778b2ecf816"
SRC_URI[tools.sha256sum] = "8d223e6e13661b11dfe09c5759b4d97c86417479d08ad0bbfba130ca00272e1a"

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
