DESCRIPTION = "nvpower tool and configuration files"
L4T_DEB_COPYRIGHT_MD5 = "8dc9729e1dc38aac4adb4bd6f6e3b370"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init"
SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'tools')};subdir=${BP};name=tools"

MAINSUM = "04975607d121dd679a9f026939d5c126dd9e682bbba6b71c01942212ebc2b090"
SRC_URI[init.sha256sum] = "165ca517257cc4ff89a17afe83f6d9e04df8630f55837f0649f792dccaefc156"
SRC_URI[tools.sha256sum] = "864281721f202c9e3ae8c7b66ff469b05ee8abc6d3ae6cb0eaaa8a5e7769398f"

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
