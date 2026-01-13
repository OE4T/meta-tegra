L4T_DEB_COPYRIGHT_MD5 = "131ace355007ed982c3ed68c6abadcf9"
DEPENDS = "tegra-libraries-core tegra-libraries-multimedia tegra-libraries-multimedia-utils tegra-libraries-cuda virtual/egl virtual/libgles2 expat"

require tegra-debian-libraries-common.inc

LICENSE += "& BSD-3-Clause"
LIC_FILES_CHKSUM += "\
    file://usr/share/doc/nvidia-l4t-camera/LICENSE.libnvargus;md5=271791ce6ff6f928d44a848145021687 \
    file://usr/share/doc/nvidia-l4t-camera/LICENSE.libnvcam_imageencoder;md5=059e39d33711ff9e6a76760cffcf0811 \
"

SRC_COMMON_DEBS += "${@l4t_deb_pkgname(d, 'gstreamer')};subdir=${BP};name=gstreamer"
SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'core')};subdir=${BP};name=core"

MAINSUM = "85e51596d428196415bf248340aabf53a00d5abf92c3bd3def070b452aeda346"
GSTSUM = "ccd25a20c89d115af04ae0fab5b7fb427fcea33e19649f62ef15dbbc8897f52f"
CORESUM = "5fe480d1937754498299b138cf86b5e5b78d50f4b52c0b3b7f900c50c6de6ad4"
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
    nvidia/libnvfusacapext.so \
    nvidia/libnvisp.so \
    nvidia/libnvisppg.so \
    nvidia/libnvmedia_isp_ext.so \
    nvidia/libnvfnet.so \
    nvidia/libnvfnetstoredefog.so \
    nvidia/libnvfnetstorehdfx.so \
    nvidia/libnvodm_imager.so \
    nvidia/libnvscf.so \
    nvidia/libnvcamerahal.so \
    nvidia/libnvsipl.so \
    nvidia/libnvsipl_control.so \
    nvidia/libnvsipl_devblk.so \
    nvidia/libnvsipl_devblk_cdi.so \
    nvidia/libnvsipl_devblk_crypto.so \
    nvidia/libnvsipl_devblk_ddi.so \
    nvidia/libnvsipl_pipeline.so \
    nvidia/libnvsipl_query.so \
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
