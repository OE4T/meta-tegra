L4T_DEB_COPYRIGHT_MD5 = "d3617777039321a257aef01439341b02"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore libx11 libxext freetype fontconfig"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "12440f0eff87ff22906726fdad61d87a2681bf8ace9d824b8b208e0ee8dfdbcc"

inherit features_check

REQUIRED_DISTRO_FEATURES = "x11"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libGLX_nvidia.so.0 \
    nvidia/libnvidia-glcore.so.${L4T_LIB_VERSION} \
"

RDEPENDS:${PN} = "libxcb-glx"
