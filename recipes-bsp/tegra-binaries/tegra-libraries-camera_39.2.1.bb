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

MAINSUM = "b3a20033dc8131423f7e60b0005c58a03569d23bfa06aa3c4d7065ff208d8662"
GSTSUM = "5101937ba4df642e2a054b5bc87ff6155703fee21ad1c85c134d6cf2b02b6f33"
CORESUM = "277a6fb25f2c06cc2dee6d352bef3abb15929739f396fea1cee70d1dea83008b"
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
    nvidia/libnvcam_fsync.so \
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
    install -d ${D}${sbindir}
    if ${@bb.utils.contains('PACKAGECONFIG', 'x11', 'true', 'false', d)}; then
        install -d ${D}${libdir}/libv4l/plugins/
        install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/nvidia/libv4l2_nvargus.so ${D}${libdir}/libv4l/plugins/
        install -m 0755 ${B}/usr/sbin/nvargus_nvraw ${D}${sbindir}/
    fi
    install -m755 ${B}/usr/sbin/nvargus-daemon ${D}${sbindir}/
    install -m755 ${B}/usr/sbin/nvtunerd ${D}${sbindir}/
    install -d ${D}${libdir}/nvsipl_drv
    install -m 0644 ${S}/usr/lib/nvsipl_drv/libnvsipl_qry_vb1940.so ${D}${libdir}/nvsipl_drv/
    install -m 0644 ${S}/usr/lib/nvsipl_drv/libnvsipl_qry_nova0_hawk.so ${D}${libdir}/nvsipl_drv/
    install -m 0644 ${S}/usr/lib/nvsipl_drv/libnvsipl_qry_smart_imx728.so ${D}${libdir}/nvsipl_drv/
    install -m 0644 ${S}/usr/lib/nvsipl_drv/libnvsipl_qry_smart_imx623.so ${D}${libdir}/nvsipl_drv/
}

PACKAGES =+ "tegra-libraries-argus-daemon-base ${PN}-nvtunerd ${PN}-nvraw ${PN}-sipl-vb1940 ${PN}-sipl-nova0-hawk ${PN}-sipl-imx728 ${PN}-sipl-imx623"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
FILES:${PN} += "${libdir}/libv4l/plugins"
FILES:tegra-libraries-argus-daemon-base = "${sbindir}/nvargus-daemon"
FILES:${PN}-nvtunerd = "${sbindir}/nvtunerd"
FILES:${PN}-nvraw = "${sbindir}/nvargus_nvraw"
FILES:${PN}-sipl-vb1940 = "${libdir}/nvsipl_drv/libnvsipl_qry_vb1940.so"
FILES:${PN}-sipl-nova0-hawk = "${libdir}/nvsipl_drv/libnvsipl_qry_nova0_hawk.so"
FILES:${PN}-sipl-imx728 = "${libdir}/nvsipl_drv/libnvsipl_qry_smart_imx728.so"
FILES:${PN}-sipl-imx623 = "${libdir}/nvsipl_drv/libnvsipl_qry_smart_imx623.so"
RDEPENDS:${PN} = "tegra-argus-daemon"
