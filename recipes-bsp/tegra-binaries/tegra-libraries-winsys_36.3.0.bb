L4T_DEB_COPYRIGHT_MD5 = "34723d6d005ff47007fff977eac035d6"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "dceb18fadb29d6c95597bc7f0c0ad0c0ce9ae95ad38a0dbfbfb6cbc96e3bfc03"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvwinsys.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
