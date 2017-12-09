require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DESCRIPTION = "Miscellaneous configuration files provided by L4T"

inherit systemd

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/config.tbz2
    if [ ! -f ${B}/usr/sbin/camera_device_detect -a -f ${B}/etc/udev/rules.d/99-tegra-devices.rules ]; then
        sed -e'/camera_device_detect/d' ${B}/etc/udev/rules.d/99-tegra-devices.rules
    fi
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sysconfdir}/init.d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/nvstartup.init ${D}${sysconfdir}/init.d/nvstartup
    install -m 0644 ${S}/nvstartup.service ${D}${systemd_system_unitdir}/
    install -d ${D}${sbindir}
    sed -e's,\(sudo bash .*\),: #\1,' -e'/^# Ensure libglx/,$d' ${B}/etc/systemd/nv.sh >${D}${sbindir}/nvstartup
    chmod 0755 ${D}${sbindir}/nvstartup
    install -d ${D}/${sysconfdir}/udev/rules.d
    install -m 0644 ${B}/etc/udev/rules.d/99-tegra-devices.rules ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${B}/etc/udev/rules.d/99-tegra-mmc-ra.rules ${D}${sysconfdir}/udev/rules.d
    if [ -f ${B}/usr/sbin/camera_device_detect ]; then
        install -d ${D}${sbindir}
        install -m 0755 ${B}/usr/sbin/camera_device_detect ${D}${sbindir}/
    fi

    install -d ${D}${sysconfdir}/X11
    install -m 0644 ${B}/etc/X11/xorg.conf.* ${D}${sysconfdir}/X11/xorg.conf

    install -m 0644 ${B}/etc/enctune.conf ${D}${sysconfdir}

    install -d ${D}${datadir}/alsa/cards
    install -m 0644 ${B}/usr/share/alsa/cards/tegra-hda.conf ${D}${datadir}/alsa/cards/

    install -d ${D}${sysconfdir}/pulse
    install -m 0644 ${B}/etc/pulse/default.pa.hdmi ${D}${sysconfdir}/pulse/default.pa    
}

PACKAGES = "${PN}-udev ${PN}-omx-tegra ${PN}-xorg ${PN}-alsa ${PN}-pulseaudio ${PN}-nvstartup"
FILES_${PN}-udev = "${sysconfdir}/udev/rules.d ${sbindir}/camera_device_detect"
FILES_${PN}-xorg = "${sysconfdir}/X11"
FILES_${PN}-alsa = "${datadir}/alsa"
FILES_${PN}-omx-tegra = "${sysconfdir}/enctune.conf"
FILES_${PN}-pulseaudio = "${sysconfdir}/pulse"
FILES_${PN}-nvstartup = "${sysconfdir}/init.d/nvstartup ${sbindir}"
SYSTEMD_PACKAGES = "${PN}-nvstartup"
SYSTEMD_SERVICE_${PN}-nvstartup = "nvstartup.service"
RDEPENDS_${PN}-udev = "udev"
RDEPENDS_${PN}-nvstartup = "bash"
