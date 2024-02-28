L4T_DEB_COPYRIGHT_MD5 = "34723d6d005ff47007fff977eac035d6"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "38043813c3f3a31da46c9ebe05f2267fd10170ab083a11ab2ab5adc3679a69bb"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra-egl/libGLESv1_CM_nvidia.so.1 \
    tegra-egl/libGLESv2_nvidia.so.2 \
"
