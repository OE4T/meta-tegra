L4T_DEB_COPYRIGHT_MD5 = "34723d6d005ff47007fff977eac035d6"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "a202ea5f467836eed8466faa7541dec3521c4d1668ca097cea3eee0dfc52cab6"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvwinsys.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
