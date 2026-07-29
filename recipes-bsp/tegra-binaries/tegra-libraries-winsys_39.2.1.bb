L4T_DEB_COPYRIGHT_MD5 = "34723d6d005ff47007fff977eac035d6"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "77fcb6c2c11ed67403f8e7697aab9d6ef11a2acbde86dd38a062a8945d5d8631"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvwinsys.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
