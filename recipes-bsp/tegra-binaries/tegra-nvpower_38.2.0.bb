DESCRIPTION = "nvpower tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "d9a8361f8068fc4e3b934118d1de9f3f"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'nvpmodel')};subdir=${BP};name=nvpmodel"
SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'tools')};subdir=${BP};name=tools"

MAINSUM = "5ce971e279e87f9b8d7a1f6f3e040219b54c4f47c90c295d1a2cba083cbff659"
SRC_URI[nvpmodel.sha256sum] = "f214be70497d83a2fb51a8b6ab408fe6aeeacb74e0ea722d8726000d4ba1dbcc"
SRC_URI[tools.sha256sum] = "2b985d1a7943b92888fcc150b0f5364f56edba0529c05d2e3993cb5793819d66"

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
