require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DESCRIPTION = "Miscellaneous configuration files provided by L4T"

inherit systemd

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/config.tbz2
}

do_compile[noexec] = "1"

do_install() {
  install -d ${D}${sysconfdir}/udev/rules.d
  install -m 0644 ${B}/etc/udev/rules.d/99-tegra-mmc-ra.rules ${D}${sysconfdir}/udev/rules.d
 
  # Generic xorg.conf (machine override below sets the one for the jetson, thus we can avoid the udev rule)
  install -d ${D}${sysconfdir}/X11
  install -m 0644 ${B}/etc/X11/xorg.conf ${D}${sysconfdir}/X11/xorg.conf

  # Encoder tuning
  install -m 0644 ${B}/etc/enctune.conf ${D}${sysconfdir}

  # udev rule takes care of choosing the right one, we could probably specify that in the machine configuration file
  install -m 0644 ${B}/etc/asound.conf.* ${D}${sysconfdir}
  install -m 0644 ${B}/etc/udev/rules.d/90-alsa-asound-tegra.rules ${D}${sysconfdir}/udev/rules.d

  # Rule that sets permissions on some of the devices
  install -m 0644 ${B}/etc/udev/rules.d/99-tegra-devices.rules ${D}${sysconfdir}/udev/rules.d

  #Pulseaudio files:
  install -d ${D}${sysconfdir}/pulse
  install -m 0644 ${B}/etc/pulse/default.pa.orig ${D}${sysconfdir}/pulse/default.pa
  # I'm ignoring the daemon.conf since the version of PA probably changed from that of L4T_21.6

  # startup scripts: the config archive provides for both upstart and systemd scripts. In standard L4T upstart is used, whereas here
  # we're working with systemd. They are similar but slightly different (e.g. in the parameters passed to configure the interactive
  # governor, and in the CPU frequency boost on input event). 
  # To keep the system as close to stock L4T I've ported the upstart script to a systemd service.
  # We can instead discard the nvfb script as it just sets up some symlinks for egl libraries etc, which are already solved by these packages
  install -d ${D}${systemd_system_unitdir}
  install -d ${D}${sysconfdir}/systemd
  install -m 0755 ${S}/nvstartup.sh ${D}${sysconfdir}/systemd/
  install -m 0644 ${S}/nvstartup.service ${D}${systemd_system_unitdir}/
}

do_install_append_jetson-tk1() {
  # Jetson tk1 specific files 
  install -m 0644 ${B}/etc/X11/xorg.conf.jetson-tk1 ${D}${sysconfdir}/X11/xorg.conf
  install -m 0644 ${B}/etc/pulse/default.pa.hdmi ${D}${sysconfdir}/pulse/default.pa
}

# ${PN}-all is just a utility package
PACKAGES = "${PN}-all ${PN}-omx-tegra ${PN}-xorg ${PN}-alsa ${PN}-udev ${PN}-nvstartup ${PN}-pulseaudio"
ALLOW_EMPTY_${PN}-all = "1"
RDEPENDS_${PN}-all = "${PN}-omx-tegra ${PN}-xorg ${PN}-alsa ${PN}-udev ${PN}-nvstartup ${PN}-pulseaudio"

FILES_${PN}-omx-tegra = "${sysconfdir}/enctune.conf"
FILES_${PN}-xorg = "${sysconfdir}/X11"
FILES_${PN}-alsa = "${sysconfdir}/asound.conf.* ${sysconfdir}/udev/rules.d/90-alsa-asound-tegra.rules"
FILES_${PN}-udev = "${sysconfdir}/udev/rules.d/99-tegra-devices.rules ${sysconfdir}/udev/rules.d/99-tegra-mmc-ra.rules"
FILES_${PN}-pulseaudio = "${sysconfdir}/pulse"
FILES_${PN}-nvstartup = "${sysconfdir}/systemd/nvstartup.sh ${systemd_system_unitdir}/nvstartup.service"
SYSTEMD_PACKAGES = "${PN}-nvstartup"
SYSTEMD_SERVICE_${PN}-nvstartup = "nvstartup.service"
RDEPENDS_${PN}-alsa = "udev"
RDEPENDS_${PN}-udev = "udev"
RDEPENDS_${PN}-nvstartup = "bash"
