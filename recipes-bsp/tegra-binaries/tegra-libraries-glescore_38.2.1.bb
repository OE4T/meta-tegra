L4T_DEB_COPYRIGHT_MD5 = "8c7016b98a9864afb8cc0a7eb8ba62fa"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "49d5c4d659293d3850f77bae7f5a8888d40ced71abaec585ba40e88ea29fa293"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra-egl/libGLESv1_CM_nvidia.so.1 \
    tegra-egl/libGLESv2_nvidia.so.2 \
"
