L4T_DEB_COPYRIGHT_MD5 = "a8d022028b967269ba2167d08c52e3d5"

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

MAINSUM = "f396b2c7b4a73a64ac5d8e3ca1ce7f501bc9c6bcd3a9052570eb9bae46b16bfc"
MAINSUM:tegra210 = "9608bace9efa0a3cb29c19500a917ff3f0f0fce5451dee3fc9f6bc01d47b41d2"

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

CONTAINER_CSV_LIB_PATH = "/usr/lib/aarch64-linux-gnu/"

CONTAINER_CSV_FILES = "${libdir}/*.so* ${libdir}/gstreamer-1.0/*.so*"

FILES_SOLIBSDEV = ""
FILES:${PN} = "${libdir}"
DEBIAN_NOAUTONAME:${PN} = "1"

RRECOMMENDS:${PN} = "gstreamer1.0-plugins-nvarguscamerasrc gstreamer1.0-plugins-nvv4l2camerasrc"
