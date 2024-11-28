L4T_DEB_COPYRIGHT_MD5 = "c1b806ee919776b86256593381a7399e"
DEPENDS = "tegra-libraries-core tegra-libraries-multimedia tegra-libraries-multimedia-utils tegra-libraries-cuda virtual/egl virtual/libgles2 expat"

require tegra-debian-libraries-common.inc

LICENSE += "& BSD-3-Clause"
LIC_FILES_CHKSUM += "\
    file://usr/share/doc/nvidia-tegra/LICENSE.libnvargus;md5=271791ce6ff6f928d44a848145021687 \
    file://usr/share/doc/nvidia-tegra/LICENSE.libnvcam_imageencoder;md5=059e39d33711ff9e6a76760cffcf0811 \
"

SRC_SOC_DEBS += "nvidia-l4t-gstreamer_${PV}_arm64.deb;subdir=${BP};name=gstreamer"
SRC_SOC_DEBS += "nvidia-l4t-core_${PV}_arm64.deb;subdir=${BP};name=core"

MAINSUM = "0043810a8994ba8171677d45e709443d02a487191bd61c363bf8396c8f9224d2"
MAINSUM:tegra210 = "cba6d67cbfc95765459489290e948b6d86fde65d045b8b58eca9ef01906faf3b"
GSTSUM = "f396b2c7b4a73a64ac5d8e3ca1ce7f501bc9c6bcd3a9052570eb9bae46b16bfc"
GSTSUM:tegra210 = "9608bace9efa0a3cb29c19500a917ff3f0f0fce5451dee3fc9f6bc01d47b41d2"
CORESUM = "7b81a016d6a0f283553b01516fa562c8c99c95bb87d88b680dcbc113e8bfa938"
CORESUM:tegra210 = "61ada8308bd38fe6fe1793b658c83c3ef3dcc057a0d9e080dc0336e61be683c2"
SRC_URI[gstreamer.sha256sum] = "${GSTSUM}"
SRC_URI[core.sha256sum] = "${CORESUM}"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

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
SOC_SPECIFIC_LIBS:tegra210 = ""


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
    install -m755 ${B}/usr/sbin/nvargus_nvraw ${D}${sbindir}/
    install -m755 ${B}/usr/sbin/nvtunerd ${D}${sbindir}/
}

PACKAGES =+ "tegra-libraries-argus-daemon-base ${PN}-nvtunerd ${PN}-nvargus-nvraw"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
FILES:${PN} += "${libdir}/libv4l/plugins"
FILES:tegra-libraries-argus-daemon-base = "${sbindir}/nvargus-daemon"
FILES:${PN}-nvargus-nvraw = "${sbindir}/nvargus_nvraw"
FILES:${PN}-nvtunerd = "${sbindir}/nvtunerd"
RDEPENDS:${PN} = "tegra-argus-daemon"

CONTAINER_CSV_FILES:append = "${@bb.utils.contains('PACKAGECONFIG', 'x11', ' ${libdir}/libv4l/plugins/*', '', d)}"
