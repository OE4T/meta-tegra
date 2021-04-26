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
}

FILES_SOLIBSDEV = ""
PACKAGES =+ "${PN}-nvcompositor"
FILES_${PN}-nvcompositor = "${libdir}/gstreamer-1.0/libgstnvcompositor.so"
FILES_${PN} = "${libdir}"
DEBIAN_NOAUTONAME_${PN} = "1"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP_${PN}-nvcompositor = "dev-so ldflags build-deps"
INSANE_SKIP_${PN} = "dev-so ldflags build-deps"
RDEPENDS_${PN} = "gstreamer1.0 libgstvideo-1.0 glib-2.0 libegl tegra-libraries libdrm"
RRECOMMENDS_${PN} = "gstreamer1.0-plugins-nvarguscamerasrc gstreamer1.0-plugins-nvv4l2camerasrc"
RDEPENDS_${PN}-nvcompositor = "gstreamer1.0 libgstbadbase-1.0 libgstbadvideo-1.0 libgstvideo-1.0 glib-2.0 tegra-libraries"
