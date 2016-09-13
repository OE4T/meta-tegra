do_install_append_tegra210() {
	sed -i -re's,^(ARGS=.*)"$,\1 -ignoreABI",' ${D}${sysconfdir}/X11/Xserver
}
