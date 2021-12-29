L4T_DEB_COPYRIGHT_MD5 = "ce4d36df31e6cc73581fd2a25d16834e"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore libx11 libxext freetype fontconfig"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "c873bf1c0605cb98e18ebd5a0d724bdf8a4fbb21b9af5fd0b862e4a44f091e2a"

inherit features_check

REQUIRED_DISTRO_FEATURES = "x11"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libGLX_nvidia.so.0 \
    tegra/libnvidia-glcore.so.${L4T_VERSION} \
"
