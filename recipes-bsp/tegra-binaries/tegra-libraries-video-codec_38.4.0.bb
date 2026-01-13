L4T_DEB_COPYRIGHT_MD5 = "771e1b620a7c195673a9835204148c36"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-video-codec-openrm"

require tegra-debian-libraries-common.inc

MAINSUM = "333cb909011aebec1ab2280428d199afa5ed78860c8ca2f7211a0c8b0b28d553"

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
