L4T_DEB_COPYRIGHT_MD5 = "34723d6d005ff47007fff977eac035d6"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "e4448e39255b6bac877217f404330e83a6797771c817b5e11a6d48921024b152"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvwinsys.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
