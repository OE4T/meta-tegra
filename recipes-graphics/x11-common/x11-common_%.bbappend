do_install_append_jetson-tx1() {
	sed -i -re's,^(ARGS=.*)"$,\1 -ignoreABI",' ${D}${sysconfdir}/X11/Xserver
}
