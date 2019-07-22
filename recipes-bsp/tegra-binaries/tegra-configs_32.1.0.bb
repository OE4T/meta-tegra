require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DESCRIPTION = "Miscellaneous configuration files provided by L4T"

inherit systemd

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/config.tbz2
    sed -e'/camera_device_detect/d' ${B}/etc/udev/rules.d/99-tegra-devices.rules
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sysconfdir}/init.d ${D}${systemd_system_unitdir}
    install -m 0755 ${S}/nvstartup.init ${D}${sysconfdir}/init.d/nvstartup
    install -m 0644 ${S}/nvstartup.service ${D}${systemd_system_unitdir}/
    install -d ${D}${sbindir}
    sed -e's,\(sudo bash .*\),: #\1,' -e'/^# Ensure libglx/,$d' ${B}/etc/systemd/nv.sh >${D}${sbindir}/nvstartup
    chmod 0755 ${D}${sbindir}/nvstartup
    install -d ${D}/${sysconfdir}/udev/rules.d
    install -m 0644 ${B}/etc/udev/rules.d/99-tegra-devices.rules ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${B}/etc/udev/rules.d/99-tegra-mmc-ra.rules ${D}${sysconfdir}/udev/rules.d

    install -d ${D}${sysconfdir}/X11

    install -m 0644 ${B}/etc/enctune.conf ${D}${sysconfdir}
}

do_install_append_tegra186() {
    install -m 0644 ${B}/etc/X11/xorg.conf.t186_ref ${D}${sysconfdir}/X11/xorg.conf
}
do_install_append_tegra194() {
    install -m 0644 ${B}/etc/X11/xorg.conf.t194_ref ${D}${sysconfdir}/X11/xorg.conf
}
do_install_append_tegra210() {
    install -m 0644 ${B}/etc/X11/xorg.conf ${D}${sysconfdir}/X11/
}

PACKAGES = "${PN}-udev ${PN}-omx-tegra ${PN}-xorg ${PN}-nvstartup"
FILES_${PN}-udev = "${sysconfdir}/udev/rules.d"
FILES_${PN}-xorg = "${sysconfdir}/X11"
FILES_${PN}-omx-tegra = "${sysconfdir}/enctune.conf"
FILES_${PN}-nvstartup = "${sysconfdir}/init.d/nvstartup ${sbindir}"
SYSTEMD_PACKAGES = "${PN}-nvstartup"
SYSTEMD_SERVICE_${PN}-nvstartup = "nvstartup.service"
RDEPENDS_${PN}-udev = "udev"
RDEPENDS_${PN}-nvstartup = "bash"
