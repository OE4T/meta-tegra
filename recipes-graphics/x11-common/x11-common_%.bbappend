do_install_append_tegra210() {
	sed -i -re's,^(ARGS=.*)"$,\1 -ignoreABI",' ${D}${sysconfdir}/X11/Xserver
}
PACKAGE_ARCH_tegra210 = "${SOC_FAMILY_PKGARCH}"
