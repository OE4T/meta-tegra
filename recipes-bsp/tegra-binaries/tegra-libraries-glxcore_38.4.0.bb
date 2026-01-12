L4T_DEB_COPYRIGHT_MD5 = "8c7016b98a9864afb8cc0a7eb8ba62fa"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore libx11 libxext freetype fontconfig"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "32225585e2bf526328f303cb51174d264a4a4093936c9d01a6fc5dad04d5617a"

inherit features_check

REQUIRED_DISTRO_FEATURES = "x11"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libGLX_nvidia.so.0 \
    nvidia/libnvidia-glcore.so.${L4T_LIB_VERSION} \
"

RDEPENDS:${PN} = "libxcb-glx"
