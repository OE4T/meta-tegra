DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-multimedia tegra-libraries-multimedia-utils"

require tegra-libraries-common.inc

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

do_install() {
    install_libraries
    install -d ${D}${libdir}/libv4l/plugins/ ${D}${libdir}/libv4l/plugins-wrapped/
    for f in ${TEGRA_PLUGINS}; do
        install -m 0644 ${DRVROOT}/tegra/$f ${D}${libdir}/libv4l/plugins/
    done
    install -m 0644 ${DRVROOT}/tegra/libv4l2_nvvidconv.so ${D}${libdir}/libv4l/plugins-wrapped/
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
FILES:${PN} += "${libdir}/libv4l"
RDEPENDS:${PN} = "libv4l2-nvvidconv-wrapper"
