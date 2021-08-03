do_install:tegra() {
    if egrep -q '^([23]\.|4\.([0-9]|1[0-3])\.)' ${STAGING_KERNEL_BUILDDIR}/kernel-abiversion; then
	install -d ${D}${bindir}
	install -m 0755 ${S}/tools/spi/spidev_test ${S}/tools/spi/spidev_fdx ${D}${bindir}/
    else
        oe_runmake DESTDIR=${D} install
    fi
}

RRECOMMENDS:${PN}:append:tegra = " kernel-module-spidev"
