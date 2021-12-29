DESCRIPTION = "NVIDIA prebuilt binary-only GStreamer plugins"
L4T_DEB_COPYRIGHT_MD5 = "e9ada95d2bb512af0bf751941ce6e6ba"

DEPENDS = "\
	glib-2.0 \
	gstreamer1.0-plugins-base \
	tegra-libraries-multimedia tegra-libraries-multimedia-utils \
	${@bb.utils.contains('DISTRO_FEATURES', ['x11', 'alsa'], 'virtual/libx11 alsa-lib', '', d)} \
	libdrm virtual/egl virtual/libgles2 \
"
# XXX-- temporary
DEPENDS += "tegra-libraries-camera tegra-libraries-nvdsseimeta libv4l"
# --XXX

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-gstreamer"

require tegra-debian-libraries-common.inc

LICENSE += "& BSD-3-Clause & LGPL-2.0-or-later & LGPL-2.1-only"
LIC_FILES_CHKSUM += "\
    file://usr/share/doc/nvidia-tegra/LICENSE.gst-nvvideo4linux2;md5=457fb5d7ae2d8cd8cabcc21789a37e5c \
    file://usr/share/doc/nvidia-tegra/LICENSE.libgstnvdrmvideosink;md5=674ef4559ff709167b72104cb9814e93 \
    file://usr/share/doc/nvidia-tegra/LICENSE.libgstnvv4l2camerasrc;md5=75b8238951e471a2ce246b5f2d40b3e3 \
    file://usr/share/doc/nvidia-tegra/LICENSE.libgstnvvideosinks;md5=86ed1f32df3aaa376956e408540c024b \
"

MAINSUM = "c57247c840ef77743d3f3054c4cf7e88d8b3bef91e65919ef7f3005bd3f4bc3a"

decompress_license() {
    gunzip ${S}/usr/share/doc/nvidia-tegra/LICENSE.gst-nvvideo4linux2.gz
}
do_unpack[postfuncs] += "decompress_license"
do_unpack[depends] += "gzip-native:do_populate_sysroot"

TEGRA_LIBRARIES_TO_INSTALL = "\
    libgstnvegl-1.0.so.0 \
    libgstnvexifmeta.so \
    libgstnvivameta.so \
    libnvsample_cudaprocess.so \
"
do_install() {
    install_libraries
    install -d ${D}${libdir}/gstreamer-1.0
    for f in ${S}/usr/lib/aarch64-linux-gnu/gstreamer-1.0/lib*; do
        install -m 0644 $f ${D}${libdir}/gstreamer-1.0/
    done
    # Remove the plugins we build from source
    #rm -f ${D}${libdir}/gstreamer-1.0/libgstnvarguscamerasrc.so*
    #rm -f ${D}${libdir}/gstreamer-1.0/libgstnveglglessink.so*
    #rm -f ${D}${libdir}/gstreamer-1.0/libgstnvjpeg.so*
    #rm -f ${D}${libdir}/gstreamer-1.0/libgstnvv4l2camerasrc.so*
    #rm -f ${D}${libdir}/gstreamer-1.0/libgstnvvideo4linux2.so*
    #rm -f ${D}${libdir}/gstreamer-1.0/libgstnvvideosinks.so*
    #rm -f ${D}${libdir}/gstreamer-1.0/libgstomx.so*
    #rm -f ${D}${libdir}/gstreamer-1.0/libgstnvtee*
    #rm -f ${D}${libdir}/gstreamer-1.0/libgstnvdrmvideo*
    #rm -f ${D}${libdir}/gstreamer-1.0/libgstnvcomposit*
    #rm -f ${D}${libdir}/gstreamer-1.0/libgstnvvidconv*
    # XXX nvgstapps
    install -d ${D}${bindir}
    install -m 0755 ${S}/usr/bin/* ${D}${bindir}/
    # XXX
}

CONTAINER_CSV_FILES = "${libdir}/*.so* ${libdir}/gstreamer-1.0/*.so*"

FILES_SOLIBSDEV = ""
FILES:${PN} = "${libdir}"
DEBIAN_NOAUTONAME:${PN} = "1"

PLUGINS_PACKAGES = " \
    gstreamer1.0-omx-tegra \
    gstreamer1.0-plugins-nvarguscamerasrc \
    gstreamer1.0-plugins-nvcompositor \
    gstreamer1.0-plugins-nvcompositor \
    gstreamer1.0-plugins-nvdrmvideosink \
    gstreamer1.0-plugins-nveglgles \
    gstreamer1.0-plugins-nvjpeg \
    gstreamer1.0-plugins-nvtee \
    gstreamer1.0-plugins-nvv4l2camerasrc \
    gstreamer1.0-plugins-nvvidconv \
    gstreamer1.0-plugins-nvvidconv \
    gstreamer1.0-plugins-nvvideo4linux2 \
    gstreamer1.0-plugins-nvvideosinks \
    gstreamer1.0-plugins-tegra \
"
RPROVIDES:${PN} = "${PLUGINS_PACKAGES}"
RREPLACES:${PN} = "${PLUGINS_PACKAGES}"
RCONFLICTS:${PN} = "${PLUGINS_PACKAGES}"
RRECOMMENDS:${PN} = "gstreamer1.0-plugins-nvarguscamerasrc gstreamer1.0-plugins-nvv4l2camerasrc"

PACKAGES =+ "nvgstapps-prebuilt"
FILES:nvgstapps-prebuilt = "${bindir}"
RPROVIDES:nvgstapps-prebuilt = "nvgstapps"
RREPLACES:nvgstapps-prebuilt = "nvgstapps"
RCONFLICTS:nvgstapps-prebuilt = "nvgstapps"
RDEPENDS:nvgstapps-prebuilt = "bash"
