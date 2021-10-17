DEPENDS = "tegra-libraries-core tegra-libraries-multimedia tegra-libraries-multimedia-utils tegra-libraries-cuda virtual/egl virtual/libgles2 expat"

require tegra-libraries-common.inc

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = ",,gtk+3 libx11 glib-2.0 cairo"

TEGRA_LIBRARIES_EXTRACT += "usr/sbin var/nvidia"
TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvapputil.so \
    tegra/libnvargus.so \
    tegra/libnvargus_socketclient.so \
    tegra/libnvargus_socketserver.so \
    tegra/libnvcam_imageencoder.so \
    tegra/libnvcameratools.so \
    tegra/libnvcamerautils.so \
    tegra/libnvcamlog.so \
    tegra/libnvcamv4l2.so \
    tegra/libnveglstream_camconsumer.so \
    tegra/libnveglstreamproducer.so \
    tegra/libnvfnet.so \
    tegra/libnvfnetstoredefog.so \
    tegra/libnvfnetstorehdfx.so \
    tegra/libnvodm_imager.so \
    tegra/libnvscf.so \
    ${SOC_SPECIFIC_LIBS} \
"

SOC_SPECIFIC_LIBS = "tegra/libnvcapture.so"
SOC_SPECIFIC_LIBS_tegra210 = ""


do_install() {
    install_libraries
    install -d ${D}${localstatedir}
    cp -R ${B}/var/nvidia ${D}${localstatedir}/
    if ${@bb.utils.contains('PACKAGECONFIG', 'x11', 'true', 'false', d)}; then
        install -d ${D}${libdir}/libv4l/plugins/
	install -m 0644 ${DRVROOT}/tegra/libv4l2_nvargus.so ${D}${libdir}/libv4l/plugins/
    fi
    install -d ${D}${sbindir}
    install -m755 ${B}/usr/sbin/nvargus-daemon ${D}${sbindir}/
    install -m755 ${B}/usr/sbin/nvtunerd ${D}${sbindir}/
}

PACKAGES =+ "tegra-libraries-argus-daemon-base ${PN}-nvtunerd"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
FILES_${PN} += "${libdir}/libv4l/plugins"
FILES_tegra-libraries-argus-daemon-base = "${sbindir}/nvargus-daemon"
FILES_${PN}-nvtunerd = "${sbindir}/nvtunerd"
RDEPENDS_${PN} = "tegra-argus-daemon"

CONTAINER_CSV_FILES_append = "${@bb.utils.contains('PACKAGECONFIG', 'x11', ' ${libdir}/libv4l/plugins/*', '', d)}"
