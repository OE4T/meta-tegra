DESCRIPTION = "nvpower tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "4316d40a8ea9e946e76430d2d02b5848"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init"
SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'tools')};subdir=${BP};name=tools"

MAINSUM = "4630328fe8baa73352bbd5e7d6acc0e8cb7d7026f172cb68d13bd7d54063feb0"
SRC_URI[init.sha256sum] = "8a9ae402b74951745f9c4501c51b78998e817cae363240bec22fa9c5068081dd"
SRC_URI[tools.sha256sum] = "aa9ff1565efae4493d107eed1c0dcef1d883f9a39acefdbd6a9623017790b374"

SRC_URI += "\
    file://0001-Drop-bc-usage-and-remove-symlink-creation-functions.patch \
    file://nvpower.init \
    file://nvpower.service \
"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libjetsonpower.so \
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
