DESCRIPTION = "nvpower tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "d9a8361f8068fc4e3b934118d1de9f3f"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'nvpmodel')};subdir=${BP};name=nvpmodel"
SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'tools')};subdir=${BP};name=tools"

MAINSUM = "5fe480d1937754498299b138cf86b5e5b78d50f4b52c0b3b7f900c50c6de6ad4"
SRC_URI[nvpmodel.sha256sum] = "b5fd90f3bcb52ad0754b228ea51f5a705f4249da668153690d0a58886a1e9319"
SRC_URI[tools.sha256sum] = "0ba6d6f7ef41683b63df0188fb6711f3eab77faf088b626fc3f5c5fabf0ddc2e"

SRC_URI += "file://nvpower.init \
    file://nvpower.service \
    file://0001-Remove-symlink-creation-functions.patch \
"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libjetsonpower.so \
"

do_install() {
    install_libraries
    install -m 0755 -D -t ${D}${libexecdir} ${B}/etc/systemd/nvpower.sh
    install -d ${D}${sysconfdir}/nvpower/libjetsonpower
    install -m 0644 ${B}/etc/nvpower/libjetsonpower/${NVPOWER}.conf ${D}${sysconfdir}/nvpower/libjetsonpower/
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${UNPACKDIR}/nvpower.init ${D}${sysconfdir}/init.d/nvpower
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/nvpower.service ${D}${systemd_system_unitdir}
}

inherit systemd update-rc.d

INITSCRIPT_NAME = "nvpower"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE:${PN} = "nvpower.service"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RDEPENDS:${PN} = "bash tegra-nvpmodel"
PACKAGE_ARCH = "${MACHINE_ARCH}"
