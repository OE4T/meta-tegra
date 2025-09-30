DESCRIPTION = "nvpower tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "d9a8361f8068fc4e3b934118d1de9f3f"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'nvpmodel')};subdir=${BP};name=nvpmodel"
SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'tools')};subdir=${BP};name=tools"

MAINSUM = "5637db582894922614fa6fc92f81e6c0161e4ebc0920540133b68361c48cbdfe"
SRC_URI[nvpmodel.sha256sum] = "0cb5ad56c58a47e82f645714eddd61c2d1077c7ce8716ff48ac348d98d1915b8"
SRC_URI[tools.sha256sum] = "b2673758e16a86922f1da219631a55290476ebf6f833e5c2487ca6caa07575eb"

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
