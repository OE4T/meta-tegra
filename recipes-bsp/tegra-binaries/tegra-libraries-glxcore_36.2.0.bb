L4T_DEB_COPYRIGHT_MD5 = "34723d6d005ff47007fff977eac035d6"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore libx11 libxext freetype fontconfig"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "6679762d5405c2f026810de6076b9d7bf87e05bc184d30358358f45090e3ae14"

inherit features_check

REQUIRED_DISTRO_FEATURES = "x11"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libGLX_nvidia.so.0 \
    nvidia/libnvidia-glcore.so.${L4T_LIB_VERSION} \
"

RDEPENDS:${PN} = "libxcb-glx"
