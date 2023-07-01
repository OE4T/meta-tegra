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

MAINSUM = "9c3a7f59f154909387706adc245cdf622fcd770e7c03d850a06723dac1da0f4c"
MAINSUM_tegra210 = "28ca29a1039d70d147a841d091e56ded22a082b3aba849be2e7dc95d5917c853"
GSTSUM = "185186f095899a96b2e10eab0753a34d02ea199302eda0fc57263a5ceb1ad9d7"
GSTSUM_tegra210 = "b1160768c594153149c0b673868b0d1462c38148302d30910fcaf3544799e3fb"
CORESUM = "8659b23489309907f3ac1f19b270306be37f6a4cc5ddac8cb35e96f5c3ae13bf"
CORESUM_tegra210 = "7d816cf7bf7831f3ccde19e2bf6f9d731211b9146765271632fb2ed550522032"
SRC_URI[gstreamer.sha256sum] = "${GSTSUM}"
SRC_URI[core.sha256sum] = "${CORESUM}"

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
	install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/libv4l2_nvargus.so ${D}${libdir}/libv4l/plugins/
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
