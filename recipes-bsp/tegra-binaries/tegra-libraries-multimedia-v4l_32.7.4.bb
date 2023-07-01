L4T_DEB_COPYRIGHT_MD5 = "3d9212d4d5911fa3200298cd55ed6621"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-multimedia tegra-libraries-multimedia-utils"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

MAINSUM = "563784b9084fb4cecf36021bb8b6a4da014d27c8d4605ed830bd0fdcbbe16f09"
MAINSUM:tegra210 = "3c897968e5874d48d9abc2beb99c02f75863f3a21ab0b17eaaa0d574a2e9890a"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libtegrav4l2.so \
    ${SOC_SPECIFIC_LIBS} \
"
SOC_SPECIFIC_LIBS = "tegra/libnvcuvidv4l2.so"
SOC_SPECIFIC_LIBS:tegra210 = ""

TEGRA_PLUGINS = "\
    libv4l2_nvvideocodec.so \
    ${SOC_SPECIFIC_PLUGINS} \
"
SOC_SPECIFIC_PLUGINS = "libv4l2_nvcuvidvideocodec.so"
SOC_SPECIFIC_PLUGINS:tegra210 = ""

CONTAINER_CSV_FILES += " \
    ${libdir}/libv4l/plugins-wrapped/*.so \
    ${libdir}/libv4l/plugins/*.so \
"

do_install() {
    install_libraries
    install -d ${D}${libdir}/libv4l/plugins/ ${D}${libdir}/libv4l/plugins-wrapped/
    for f in ${TEGRA_PLUGINS}; do
        install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/$f ${D}${libdir}/libv4l/plugins/
    done
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/libv4l2_nvvidconv.so ${D}${libdir}/libv4l/plugins-wrapped/
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
FILES:${PN} += "${libdir}/libv4l"
RDEPENDS:${PN} = "libv4l2-nvvidconv-wrapper"
