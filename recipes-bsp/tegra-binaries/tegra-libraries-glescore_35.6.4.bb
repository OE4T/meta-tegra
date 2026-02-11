L4T_DEB_COPYRIGHT_MD5 = "8c7016b98a9864afb8cc0a7eb8ba62fa"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "13c839d18e2d5907efd36a1a2d38cdb7144f8337cbd08c7ee56d2e8e8337d206"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra-egl/libGLESv1_CM_nvidia.so.1 \
    tegra-egl/libGLESv2_nvidia.so.2 \
"
