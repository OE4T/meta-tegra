L4T_DEB_COPYRIGHT_MD5 = "fd75d85e3194706b8dca7c74e3f615d0"
DEPENDS = "tegra-libraries-core tegra-libraries-multimedia tegra-libraries-multimedia-utils tegra-libraries-cuda virtual/egl virtual/libgles2 expat"

require tegra-debian-libraries-common.inc

LICENSE += "& BSD-3-Clause"
LIC_FILES_CHKSUM += "\
    file://usr/share/doc/nvidia-tegra/LICENSE.libnvargus;md5=271791ce6ff6f928d44a848145021687 \
    file://usr/share/doc/nvidia-tegra/LICENSE.libnvcam_imageencoder;md5=059e39d33711ff9e6a76760cffcf0811 \
"

SRC_SOC_DEBS += "nvidia-l4t-gstreamer_${PV}_arm64.deb;subdir=${BP};name=gstreamer"
SRC_SOC_DEBS += "nvidia-l4t-core_${PV}_arm64.deb;subdir=${BP};name=core"

<<<<<<<< HEAD:recipes-bsp/tegra-binaries/tegra-libraries-camera_32.7.2.bb
MAINSUM = "8c0303c1439f91ef5fbebee35816c62a2967266772703c4bd113c735711ca266"
MAINSUM:tegra210 = "0da28adf6cf5800209c7056fe2e40a7aeac700eb78804b61b93a811fa4edbfa0"
GSTSUM = "38644cad136670d9a70d1988de438600603f0f7e463fff9c5a66b0777aab09c6"
GSTSUM:tegra210 = "9817b22c796ef6de131f8aa17398978b4c50b7da424bf2419d08fce6ed1576c7"
CORESUM = "41845dade9d3e1cd67be7875e0634167de414678f088d4c6342ccd696894e63e"
CORESUM:tegra210 = "553a56f565e0ac9659a6633c3fe07afc3e68fad4451bcec9b651a929c0e986c5"
========
MAINSUM = "35e7bb39da59b9c9526f0ae9f539c8aaee8a823f54b4ab8b3a808edbee7df3ed"
GSTSUM = "c57247c840ef77743d3f3054c4cf7e88d8b3bef91e65919ef7f3005bd3f4bc3a"
CORESUM = "da997f39c1e66d5d8ceeca6e4ee33a3e9951237c1238ea51df25c7d8e10c65a7"
>>>>>>>> 390a51f3 (tegra-binaries: update for 34.1.0):recipes-bsp/tegra-binaries/tegra-libraries-camera_34.1.0.bb
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
    tegra/libnveglstream_camconsumer.so \
    tegra/libnveglstreamproducer.so \
    tegra/libnvfusacapinterface.so \
    tegra/libnvfusacap.so \
    tegra/libnvisppg.so \
    tegra/libnvisp.so \
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

CONTAINER_CSV_FILES:append = "${@bb.utils.contains('PACKAGECONFIG', 'x11', ' ${libdir}/libv4l/plugins/*', '', d)}"
