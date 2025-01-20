L4T_DEB_COPYRIGHT_MD5 = "d3617777039321a257aef01439341b02"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "ab19b524f942b35d21cc7893cf5b8a5d0a4007614e88c27dd85e2668a537cc5c"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra-egl/libGLESv1_CM_nvidia.so.1 \
    tegra-egl/libGLESv2_nvidia.so.2 \
"
