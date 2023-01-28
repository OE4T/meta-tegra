DESCRIPTION = "nvpower tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "fab0c15b4bbf7f8d5ac2bd6673d4211c"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init"
SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'tools')};subdir=${BP};name=tools"

MAINSUM = "1f859d400c4d4bc0a41119bee18074d48d8fa3396a5adf521b0182b08be2d5ab"
SRC_URI[init.sha256sum] = "36b53071e126e9ddf6017e745f47165727ff1c7eb1eedea53a0aec41363dc044"
SRC_URI[tools.sha256sum] = "342d62df7bba36332fea289584b4153ccf8ad7d5bec17afd5fef0958b31e822f"

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
