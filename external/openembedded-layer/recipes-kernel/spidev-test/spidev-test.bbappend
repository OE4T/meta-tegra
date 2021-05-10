def no_install_tools(d):
    kver = d.getVar('KERNEL_VERSION').split('.')
    return "true" if int(kver[0]) < 4 or (int(kver[0]) == 4 and int(kver[1]) < 14) else "false"

NEED_OLD_INSTALL = "${@no_install_tools(d)}"

do_install_tegra() {
    if ${NEED_OLD_INSTALL}; then
	install -d ${D}${bindir}
	install -m 0755 ${S}/tools/spi/spidev_test ${S}/tools/spi/spidev_fdx ${D}${bindir}/
    else
        oe_runmake DESTDIR=${D} install
    fi
}

RRECOMMENDS_${PN}_append_tegra = " kernel-module-spidev"
