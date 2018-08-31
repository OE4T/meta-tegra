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

  # Rule that sets permissions on some of the devices
  install -m 0644 ${B}/etc/udev/rules.d/99-tegra-devices.rules ${D}${sysconfdir}/udev/rules.d

  #Pulseaudio files:
  install -d ${D}${sysconfdir}/pulse
  install -m 0644 ${B}/etc/pulse/default.pa.orig ${D}${sysconfdir}/pulse/default.pa
  # I'm ignoring the daemon.conf since the version of PA probably changed from that of L4T_21.7

  # startup scripts: the config archive provides for both upstart and systemd scripts. In standard L4T upstart is used, whereas here
  # we're working with systemd or sysvinit. They are similar but slightly different (e.g. in the parameters passed to configure the interactive
  # governor, and in the CPU frequency boost on input event). 
  # To keep the system as close to stock L4T I've ported the upstart script.
  # We can instead discard the nvfb script as it just sets up some symlinks for egl libraries etc, which are already solved by these packages
  install -d ${D}${sbindir} ${D}${sysconfdir}/init.d ${D}${systemd_system_unitdir}
  install -m 0755 ${S}/nvstartup.sh ${D}${sbindir}/nvstartup
  install -m 0644 ${S}/nvstartup.init ${D}${sysconfdir}/init.d/nvstartup
  install -m 0644 ${S}/nvstartup.service ${D}${systemd_system_unitdir}/
}

do_install_append_jetson-tk1() {
  # Jetson tk1 specific files 
  install -m 0644 ${B}/etc/X11/xorg.conf.jetson-tk1 ${D}${sysconfdir}/X11/xorg.conf
  install -m 0644 ${B}/etc/pulse/default.pa.hdmi ${D}${sysconfdir}/pulse/default.pa
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
# ${PN}-all is just a utility package
PACKAGES = "${PN}-all ${PN}-omx-tegra ${PN}-xorg ${PN}-udev ${PN}-nvstartup ${PN}-pulseaudio"
ALLOW_EMPTY_${PN}-all = "1"
RDEPENDS_${PN}-all = "${PN}-omx-tegra ${PN}-xorg ${PN}-udev ${PN}-nvstartup ${PN}-pulseaudio"

FILES_${PN}-omx-tegra = "${sysconfdir}/enctune.conf"
FILES_${PN}-xorg = "${sysconfdir}/X11"
FILES_${PN}-udev = "${sysconfdir}/udev/rules.d/99-tegra-devices.rules ${sysconfdir}/udev/rules.d/99-tegra-mmc-ra.rules"
FILES_${PN}-pulseaudio = "${sysconfdir}/pulse"
FILES_${PN}-nvstartup = "${sysconfdir}/init.d/nvstartup ${sbindir}"
SYSTEMD_PACKAGES = "${PN}-nvstartup"
SYSTEMD_SERVICE_${PN}-nvstartup = "nvstartup.service"
RDEPENDS_${PN}-udev = "udev"
RDEPENDS_${PN}-nvstartup = "bash"
