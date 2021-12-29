L4T_DEB_COPYRIGHT_MD5 = "ce4d36df31e6cc73581fd2a25d16834e"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore libx11 libxext freetype fontconfig"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

<<<<<<<< HEAD:recipes-bsp/tegra-binaries/tegra-libraries-glxcore_32.7.2.bb
MAINSUM = "97fd11faa79ab16f5525492628108a68a58a5c1c5127ba58509784d58b90729c"
MAINSUM:tegra210 = "29fa00ca54f7776503a608989ae7baa2459cc1a1d52e075819a0a07761192c25"
========
MAINSUM = "c873bf1c0605cb98e18ebd5a0d724bdf8a4fbb21b9af5fd0b862e4a44f091e2a"
>>>>>>>> 390a51f3 (tegra-binaries: update for 34.1.0):recipes-bsp/tegra-binaries/tegra-libraries-glxcore_34.1.0.bb

inherit features_check

REQUIRED_DISTRO_FEATURES = "x11"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libGLX_nvidia.so.0 \
    tegra/libnvidia-glcore.so.${L4T_VERSION} \
"
