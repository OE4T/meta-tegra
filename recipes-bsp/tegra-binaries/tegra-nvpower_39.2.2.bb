DESCRIPTION = "nvpower tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "d9a8361f8068fc4e3b934118d1de9f3f"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'nvpmodel')};subdir=${BP};name=nvpmodel"
SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'tools')};subdir=${BP};name=tools"

MAINSUM = "d10d5e95a4970e7667c08fd987e4d37ac6efa1268b6ee07ecf769a537bfa43c3"
SRC_URI[nvpmodel.sha256sum] = "363d95088f788b301a52a93da841e26b685a010d16056bf417c4704c3f265457"
SRC_URI[tools.sha256sum] = "dd2dd0771ce3ca22770d9e913a5c3ae6131ddb76b23f62fd4b3740d82522eb74"

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
    sed -i -e 's,/etc/systemd/nvpmodel\.sh,${libexecdir}/nvpmodel.sh,g' ${D}${libexecdir}/nvpower.sh
    install -d ${D}${sysconfdir}/nvpower/libjetsonpower
    install -m 0644 ${B}/etc/nvpower/libjetsonpower/${NVPOWER}.conf ${D}${sysconfdir}/nvpower/libjetsonpower/
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${UNPACKDIR}/nvpower.init ${D}${sysconfdir}/init.d/nvpower
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/nvpower.service ${D}${systemd_system_unitdir}
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}/pylibjetsonpower
    install -m 0644 ${S}/usr/lib/python3/dist-packages/pylibjetsonpower/__init__.py \
        ${D}${PYTHON_SITEPACKAGES_DIR}/pylibjetsonpower/
}

inherit python3-dir systemd update-rc.d

INITSCRIPT_NAME = "nvpower"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE:${PN} = "nvpower.service"
PACKAGES =+ "${PN}-python"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}/pylibjetsonpower"
RDEPENDS:${PN}-python = "${PN} python3"
RRECOMMENDS:${PN} = "${PN}-python"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RDEPENDS:${PN} = "bash tegra-nvpmodel"
PACKAGE_ARCH = "${MACHINE_ARCH}"
