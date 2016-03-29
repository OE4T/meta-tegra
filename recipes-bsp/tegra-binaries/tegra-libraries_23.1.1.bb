require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_HOST = "(arm.*)"
COMPATIBLE_MACHINE = "(jetson-tx1)"

DEPENDS = "mesa"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 usr/lib usr/sbin
}

do_compile[noexec] = "1"

DRVROOT = "${B}/usr/lib/arm-linux-gnueabihf"

do_install() {
    install -d ${D}${libdir}
    for f in ${DRVROOT}/tegra/lib*; do    
        install -m 0644 $f ${D}${libdir}
    done
    for f in ${DRVROOT}/tegra-egl/lib*; do    
        install -m 0644 $f ${D}${libdir}
    done
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so
    ln -sf libGL.so.1 ${D}${libdir}/libGL.so
    ln -sf libEGL.so.1 ${D}${libdir}/libEGL.so
    ln -sf libGLESv1_CM.so.1 ${D}${libdir}/libGLESv1_CM.so
    ln -sf libGLESv2.so.2 ${D}${libdir}/libGLESv2.so
    # nvcamera_daemon only looks in this special subdir, so symlink it
    install -d ${D}${libdir}/arm-linux-gnueabihf
    ln -snf ${libdir} ${D}${libdir}/arm-linux-gnueabihf/tegra-egl
    install -d ${D}${sbindir}
    install -m755 ${B}${sbindir}/nvcamera-daemon ${D}${sbindir}/
}

PACKAGES = "${PN}"
PROVIDES += "virtual/libgl virtual/libgles1 virtual/libgles2 virtual/egl"

FILES_${PN} = "${libdir} ${sbindir}"
RDEPENDS_${PN} = "alsa-lib"

INSANE_SKIP_${PN} = "dev-so textrel ldflags build-deps"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
