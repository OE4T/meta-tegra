L4T_DEB_COPYRIGHT_MD5 = "3d9212d4d5911fa3200298cd55ed6621"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-multimedia tegra-libraries-multimedia-utils"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

MAINSUM = "55da515d66f472a2e07d8ce6e05a3c44d2ddd1d579aeb44e5a9469598de7188f"
MAINSUM_tegra210 = "ce237b46aa279914442d7120d4f2c9fd72affe54640ef26b878213b890fb3e6c"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libtegrav4l2.so \
    ${SOC_SPECIFIC_LIBS} \
"
SOC_SPECIFIC_LIBS = "tegra/libnvcuvidv4l2.so"
SOC_SPECIFIC_LIBS_tegra210 = ""

TEGRA_PLUGINS = "\
    libv4l2_nvvideocodec.so \
    ${SOC_SPECIFIC_PLUGINS} \
"
SOC_SPECIFIC_PLUGINS = "libv4l2_nvcuvidvideocodec.so"
SOC_SPECIFIC_PLUGINS_tegra210 = ""

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
FILES_${PN} += "${libdir}/libv4l"
RDEPENDS_${PN} = "libv4l2-nvvidconv-wrapper"

# This package includes files that were moved from tegra-libraries and tegra-libraries-libv4l-plugins so we need to mark
# them as conflicts to have a successful apt upgrade.
RREPLACES_${PN} = "tegra-libraries tegra-libraries-libv4l-plugins"
RCONFLICTS_${PN} = "tegra-libraries tegra-libraries-libv4l-plugins"
