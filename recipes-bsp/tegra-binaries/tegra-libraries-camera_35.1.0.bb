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

MAINSUM = "9e9fcad6c4e994e7be195e921cd81fa28d49a5643a11e14ffe0957d906678175"
GSTSUM = "3153152d43218172d808077022909d6968358d367908658ae88a667702b8914c"
CORESUM = "bc911dbcd46503fcbea42425d7b92e316ccbc6ffc15d5a4c2a41c214c97db1a7"
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
