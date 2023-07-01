L4T_DEB_COPYRIGHT_MD5 = "e9ada95d2bb512af0bf751941ce6e6ba"

DEPENDS = "\
	glib-2.0 \
	gstreamer1.0-plugins-base \
	tegra-libraries-multimedia tegra-libraries-multimedia-utils \
	${@bb.utils.contains('DISTRO_FEATURES', ['x11', 'alsa'], 'virtual/libx11 alsa-lib', '', d)} \
	libdrm virtual/egl virtual/libgles2 \
"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-gstreamer"

require tegra-debian-libraries-common.inc

LICENSE += "& MIT"
LIC_FILES_CHKSUM += "file://usr/share/doc/nvidia-tegra/LICENSE.gstvideocuda;md5=f37b39e990d2d2fa02cb5c87368357f4"

MAINSUM = "185186f095899a96b2e10eab0753a34d02ea199302eda0fc57263a5ceb1ad9d7"
MAINSUM_tegra210 = "b1160768c594153149c0b673868b0d1462c38148302d30910fcaf3544799e3fb"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

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

CONTAINER_CSV_FILES = "${libdir}/*.so* ${libdir}/gstreamer-1.0/*.so*"

FILES_SOLIBSDEV = ""
FILES_${PN} = "${libdir}"
DEBIAN_NOAUTONAME_${PN} = "1"

RRECOMMENDS_${PN} = "gstreamer1.0-plugins-nvarguscamerasrc gstreamer1.0-plugins-nvv4l2camerasrc"
