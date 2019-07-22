require tegra-shared-libraries.inc

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 usr/lib/xorg ${DRV_EXTRAS}
}

do_compile[noexec] = "1"

DRVROOT = "${B}/usr/lib/xorg/modules"
GLXEXT ??= "libglx.so"

do_install() {
    install -d ${D}${libdir}/xorg/modules/drivers
    install -m 0644 ${DRVROOT}/drivers/nvidia_drv.so ${D}${libdir}/xorg/modules/drivers/
    install -d ${D}${libdir}/xorg/modules/extensions
    install -m 0644 ${DRVROOT}/extensions/${GLXEXT} ${D}${libdir}/xorg/modules/extensions/
    if [ -f ${S}/xorg.conf ]; then
        install -d ${D}${datadir}/X11/xorg.conf.d
        install -m 0644 ${S}/xorg.conf ${D}${datadir}/X11/xorg.conf.d/00-tegra-xorg.conf
    fi
}

PACKAGES = "${PN}"
RDEPENDS_${PN} = "tegra-libraries"
FILES_${PN} = "${libdir} ${datadir}"
RPROVIDES_${PN} += "xserver-xorg-extension-glx"
RCONFLICTS_${PN} = "xserver-xorg-extension-glx"

INSANE_SKIP_${PN} = "dev-so textrel ldflags xorg-driver-abi"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
