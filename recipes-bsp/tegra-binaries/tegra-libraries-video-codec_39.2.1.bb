L4T_DEB_COPYRIGHT_MD5 = "771e1b620a7c195673a9835204148c36"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-video-codec-openrm"

require tegra-debian-libraries-common.inc

COMPATIBLE_MACHINE = "(tegra264)"

MAINSUM = "a25ce7fcec1904a5b841e262413c5c6384eb82a9f5d9bc70877b677d970551c6"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvidia-encode.so \
    nvidia/libnvidia-opticalflow.so \
"

do_install() {
    install_libraries
    # libnvcuvid.so moved to /opt/nvidia/l4t-gpu-libs/openrm/ in 39.2.1
    install -d ${D}${libdir}
    install -m 0644 ${S}/opt/nvidia/l4t-gpu-libs/openrm/libnvcuvid.so ${D}${libdir}/
    ln -s libnvcuvid.so ${D}${libdir}/libnvcuvid.so.1
    ln -s libnvidia-encode.so ${D}${libdir}/libnvidia-encode.so.1
    ln -s libnvidia-opticalflow.so ${D}${libdir}/libnvidia-opticalflow.so.1
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
