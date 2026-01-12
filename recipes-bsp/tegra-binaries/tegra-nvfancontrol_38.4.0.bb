DESCRIPTION = "NVIDIA Fan Control"
L4T_DEB_COPYRIGHT_MD5 = "d95be6c3cf958e72e34231acb466ebee"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-nvfancontrol"
DEPENDS = "tegra-nvpower"

require tegra-debian-libraries-common.inc

MAINSUM = "1340fbe1362dfc18073aa2cb97d57689c9ff03f46507f642cbc488117c0cb6bd"

SRC_URI += "\
   file://nvfancontrol.init \
   file://nvfancontrol.service \
"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${B}/usr/sbin/nvfancontrol ${D}${sbindir}/
    install -d ${D}${sysconfdir}/init.d
    install -m 0644 ${B}/etc/nvpower/nvfancontrol/${NVFANCONTROL}.conf ${D}${sysconfdir}/nvfancontrol.conf
    install -m 0755 ${UNPACKDIR}/nvfancontrol.init ${D}${sysconfdir}/init.d/nvfancontrol
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/nvfancontrol.service ${D}${systemd_system_unitdir}/
}

inherit systemd update-rc.d

INITSCRIPT_NAME = "nvfancontrol"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE:${PN} = "nvfancontrol.service"
RDEPENDS:${PN} = "bash tegra-nvpower tegra-nvpmodel"
PACKAGE_ARCH = "${MACHINE_ARCH}"
