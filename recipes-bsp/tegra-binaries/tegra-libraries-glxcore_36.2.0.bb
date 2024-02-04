L4T_DEB_COPYRIGHT_MD5 = "34723d6d005ff47007fff977eac035d6"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore libx11 libxext freetype fontconfig"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "163716edf02fb477162cb07152860e19bac32f5e3172162edc5f65645ad55c16"

inherit features_check

REQUIRED_DISTRO_FEATURES = "x11"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libGLX_nvidia.so.0 \
    nvidia/libnvidia-glcore.so.${L4T_LIB_VERSION} \
"

RDEPENDS:${PN} = "libxcb-glx"
