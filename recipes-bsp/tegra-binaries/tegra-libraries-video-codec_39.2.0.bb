L4T_DEB_COPYRIGHT_MD5 = "771e1b620a7c195673a9835204148c36"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-video-codec-openrm"

require tegra-debian-libraries-common.inc

COMPATIBLE_MACHINE = "(tegra264)"

MAINSUM = "fe02b584ce2ec0a0336daeaf491af3453a552729943303724734c68a720fbcff"

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
