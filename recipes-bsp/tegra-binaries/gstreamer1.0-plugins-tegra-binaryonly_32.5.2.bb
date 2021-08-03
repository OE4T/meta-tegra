require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

inherit container-runtime-csv

CONTAINER_CSV_FILES = "${libdir}/*.so* ${libdir}/gstreamer-1.0/*.so*"

DEPENDS = "\
	gstreamer1.0-plugins-base \
	${@bb.utils.contains('DISTRO_FEATURES', ['x11', 'alsa'], 'virtual/libx11 alsa-lib', '', d)} \
"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nv_sample_apps/nvgstapps.tbz2
}

do_compile[noexec] = "1"

LIBROOT = "${B}/usr/lib/aarch64-linux-gnu"

do_install() {
    install -d ${D}${libdir}/gstreamer-1.0
    install -m 0644 ${LIBROOT}/libgstnvegl-1.0.so.0 ${D}${libdir}
    install -m 0644 ${LIBROOT}/libgstnvivameta.so ${D}${libdir}
    install -m 0644 ${LIBROOT}/libgstnvexifmeta.so ${D}${libdir}
    install -m 0644 ${LIBROOT}/libnvsample_cudaprocess.so ${D}${libdir}
    for f in ${LIBROOT}/gstreamer-1.0/lib*; do
        install -m 0644 $f ${D}${libdir}/gstreamer-1.0/
    done
    rm -f ${D}${libdir}/gstreamer-1.0/libgstnvarguscamerasrc.so*
    rm -f ${D}${libdir}/gstreamer-1.0/libgstnveglglessink.so*
    rm -f ${D}${libdir}/gstreamer-1.0/libgstnvjpeg.so*
    rm -f ${D}${libdir}/gstreamer-1.0/libgstnvv4l2camerasrc.so*
    rm -f ${D}${libdir}/gstreamer-1.0/libgstnvvideo4linux2.so*
    rm -f ${D}${libdir}/gstreamer-1.0/libgstnvvideosinks.so*
    rm -f ${D}${libdir}/gstreamer-1.0/libgstomx.so*
    rm -f ${D}${libdir}/gstreamer-1.0/libgstnvtee*
    rm -f ${D}${libdir}/gstreamer-1.0/libgstnvdrmvideo*
    rm -f ${D}${libdir}/gstreamer-1.0/libgstnvcomposit*
    rm -f ${D}${libdir}/gstreamer-1.0/libgstnvvidconv*
}

FILES_SOLIBSDEV = ""
FILES:${PN} = "${libdir}"
DEBIAN_NOAUTONAME:${PN} = "1"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN}-nvcompositor = "dev-so ldflags build-deps"
INSANE_SKIP:${PN} = "dev-so ldflags build-deps"
RDEPENDS:${PN} = "gstreamer1.0 libgstvideo-1.0 glib-2.0 libegl tegra-libraries libdrm"
RRECOMMENDS:${PN} = "gstreamer1.0-plugins-nvarguscamerasrc gstreamer1.0-plugins-nvv4l2camerasrc"
