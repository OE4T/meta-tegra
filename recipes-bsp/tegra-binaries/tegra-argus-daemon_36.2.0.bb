DESCRIPTION = "nvargusdaemon initscript/service"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://nvargus-daemon.init \
    file://nvargus-daemon.service \
"

INHIBIT_DEFAULT_DEPS = "1"
COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m755 ${S}/nvargus-daemon.init ${D}${sysconfdir}/init.d/nvargus-daemon
    install -d ${D}${systemd_system_unitdir}
    install -m644 ${S}/nvargus-daemon.service ${D}${systemd_system_unitdir}
}

inherit systemd update-rc.d features_check

REQUIRED_DISTRO_FEATURES = "opengl"

INITSCRIPT_NAME = "nvargus-daemon"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE:${PN} = "nvargus-daemon.service"
RDEPENDS:${PN} = "tegra-libraries-argus-daemon-base"
RRECOMMENDS:${PN} = "\
    kernel-module-nv-ar0234 \
    kernel-module-nv-imx185 \
    kernel-module-nv-imx185-v1 \
    kernel-module-nv-imx219 \
    kernel-module-nv-imx268 \
    kernel-module-nv-imx274 \
    kernel-module-nv-imx318 \
    kernel-module-nv-imx477 \
    kernel-module-nv-lc898212 \
    kernel-module-nv-ov10823 \
    kernel-module-nv-ov23850 \
    kernel-module-nv-ov5693 \
    kernel-module-nv-ov9281 \
"
