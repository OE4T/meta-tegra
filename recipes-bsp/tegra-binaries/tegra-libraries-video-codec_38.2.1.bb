L4T_DEB_COPYRIGHT_MD5 = "771e1b620a7c195673a9835204148c36"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-video-codec-openrm"

require tegra-debian-libraries-common.inc

MAINSUM = "652192212d67ccd0b468e47777262dafd768a53e4dc985ee84cdfa07ed9c7e0e"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvcuvid.so \
    nvidia/libnvidia-encode.so \
    nvidia/libnvidia-opticalflow.so \
"

do_install() {
    install_libraries
    ln -s libnvcuvid.so ${D}${libdir}/libnvcuvid.so.1
    ln -s libnvidia-encode.so ${D}${libdir}/libnvidia-encode.so.1
    ln -s libnvidia-opticalflow.so ${D}${libdir}/libnvidia-opticalflow.so.1
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
