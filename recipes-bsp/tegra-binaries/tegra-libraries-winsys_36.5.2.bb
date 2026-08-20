L4T_DEB_COPYRIGHT_MD5 = "34723d6d005ff47007fff977eac035d6"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "d2606154c4b74d37ce0eb0d81dbcdad6ce420d0874032a05e00f054a05f8cb50"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvwinsys.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
