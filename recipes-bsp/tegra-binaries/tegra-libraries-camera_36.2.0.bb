L4T_DEB_COPYRIGHT_MD5 = "20dd062de73f2212a90b36eaeb843c2c"
DEPENDS = "tegra-libraries-core tegra-libraries-multimedia tegra-libraries-multimedia-utils tegra-libraries-cuda virtual/egl virtual/libgles2 expat"

require tegra-debian-libraries-common.inc

LICENSE += "& BSD-3-Clause"
LIC_FILES_CHKSUM += "\
    file://usr/share/doc/nvidia-tegra/LICENSE.libnvargus;md5=271791ce6ff6f928d44a848145021687 \
    file://usr/share/doc/nvidia-tegra/LICENSE.libnvcam_imageencoder;md5=059e39d33711ff9e6a76760cffcf0811 \
"

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'gstreamer')};subdir=${BP};name=gstreamer"
SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'core')};subdir=${BP};name=core"

MAINSUM = "54f810cd5bf941ee792395208c6814ef871343ca1032a0189cf1b71a8829c43f"
GSTSUM = "9938ac169f177013b1cc062bcd6882c24fbcec60bab8028b72af7985b93d735b"
CORESUM = "846bb4d14c787fb93e0753c70ff059f0c6fde8649b9a33f76342eb49991edb88"
SRC_URI[gstreamer.sha256sum] = "${GSTSUM}"
SRC_URI[core.sha256sum] = "${CORESUM}"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = ",,gtk+3 libx11 glib-2.0 cairo"

TEGRA_LIBRARIES_EXTRACT += "usr/sbin var/nvidia"
TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvargus.so \
    nvidia/libnvargus_socketclient.so \
    nvidia/libnvargus_socketserver.so \
    nvidia/libnvcam_imageencoder.so \
    nvidia/libnvcameratools.so \
    nvidia/libnvcamerautils.so \
    nvidia/libnvcamlog.so \
    nvidia/libnvcamv4l2.so \
    nvidia/libnvcapture.so \
    nvidia/libnveglstreamproducer.so \
    nvidia/libnvfusacapinterface.so \
    nvidia/libnvfusacap.so \
    nvidia/libnvisp.so \
    nvidia/libnvisppg.so \
    nvidia/libnvmedia_isp_ext.so \
    nvidia/libnvfnet.so \
    nvidia/libnvfnetstoredefog.so \
    nvidia/libnvfnetstorehdfx.so \
    nvidia/libnvodm_imager.so \
    nvidia/libnvscf.so \
"

do_install() {
    install_libraries
    install -d ${D}${localstatedir}
    cp -R ${B}/var/nvidia ${D}${localstatedir}/
    if ${@bb.utils.contains('PACKAGECONFIG', 'x11', 'true', 'false', d)}; then
        install -d ${D}${libdir}/libv4l/plugins/
	install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/nvidia/libv4l2_nvargus.so ${D}${libdir}/libv4l/plugins/
    fi
    install -d ${D}${sbindir}
    install -m755 ${B}/usr/sbin/nvargus-daemon ${D}${sbindir}/
    install -m755 ${B}/usr/sbin/nvtunerd ${D}${sbindir}/
}

PACKAGES =+ "tegra-libraries-argus-daemon-base ${PN}-nvtunerd"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
FILES:${PN} += "${libdir}/libv4l/plugins"
FILES:tegra-libraries-argus-daemon-base = "${sbindir}/nvargus-daemon"
FILES:${PN}-nvtunerd = "${sbindir}/nvtunerd"
RDEPENDS:${PN} = "tegra-argus-daemon"
