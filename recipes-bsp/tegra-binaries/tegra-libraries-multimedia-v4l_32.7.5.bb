L4T_DEB_COPYRIGHT_MD5 = "da66dd592b6aab6a884628599ea927fe"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-multimedia tegra-libraries-multimedia-utils"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

MAINSUM = "00aee7780face8a801d1bd612eaef2beaa01b72738acdbc3a83a3dd050830ced"
MAINSUM:tegra210 = "faf67ea3283106cbe825078e6d22c5d75ebae2a28d5c480071ffcd000ca8042e"

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
