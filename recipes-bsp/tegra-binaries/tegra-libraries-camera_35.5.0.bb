L4T_DEB_COPYRIGHT_MD5 = "0fe01f1aa1cd50ac8907271b9fb59bd2"
DEPENDS = "tegra-libraries-core tegra-libraries-multimedia tegra-libraries-multimedia-utils tegra-libraries-cuda virtual/egl virtual/libgles2 expat"

require tegra-debian-libraries-common.inc

LICENSE += "& BSD-3-Clause"
LIC_FILES_CHKSUM += "\
    file://usr/share/doc/nvidia-tegra/LICENSE.libnvargus;md5=271791ce6ff6f928d44a848145021687 \
    file://usr/share/doc/nvidia-tegra/LICENSE.libnvcam_imageencoder;md5=059e39d33711ff9e6a76760cffcf0811 \
"

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'gstreamer')};subdir=${BP};name=gstreamer"
SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'core')};subdir=${BP};name=core"

MAINSUM = "7aebbee250f68886d6fe6959c3c0c9efa52c24c1c76178f9f6a74dc53c180faa"
GSTSUM = "1bca7270934650a3884b934c4bdb5695730a6de8e10cf961968012af13eccb03"
CORESUM = "e32ade8d9abebedaffdc922c513152ea9686aa965d9e10514647dfccb17ebdf1"
SRC_URI[gstreamer.sha256sum] = "${GSTSUM}"
SRC_URI[core.sha256sum] = "${CORESUM}"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = ",,gtk+3 libx11 glib-2.0 cairo"

TEGRA_LIBRARIES_EXTRACT += "usr/sbin var/nvidia"
TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvargus.so \
    tegra/libnvargus_socketclient.so \
    tegra/libnvargus_socketserver.so \
    tegra/libnvcam_imageencoder.so \
    tegra/libnvcameratools.so \
    tegra/libnvcamerautils.so \
    tegra/libnvcamlog.so \
    tegra/libnvcamv4l2.so \
    tegra/libnvcapture.so \
    tegra/libnveglstreamproducer.so \
    tegra/libnvfusacapinterface.so \
    tegra/libnvfusacap.so \
    tegra/libnvisp.so \
    tegra/libnvisppg.so \
    tegra/libnvmedia_isp_ext.so \
    tegra/libnvfnet.so \
    tegra/libnvfnetstoredefog.so \
    tegra/libnvfnetstorehdfx.so \
    tegra/libnvodm_imager.so \
    tegra/libnvscf.so \
"

do_install() {
    install_libraries
    install -d ${D}${localstatedir}
    cp -R ${B}/var/nvidia ${D}${localstatedir}/
    if ${@bb.utils.contains('PACKAGECONFIG', 'x11', 'true', 'false', d)}; then
        install -d ${D}${libdir}/libv4l/plugins/
	install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/libv4l2_nvargus.so ${D}${libdir}/libv4l/plugins/
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
