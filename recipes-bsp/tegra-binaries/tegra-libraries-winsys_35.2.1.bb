L4T_DEB_COPYRIGHT_MD5 = "ce4d36df31e6cc73581fd2a25d16834e"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "aeab37657f0d0b801090adf8e5b17a8b86b86a6848a1e4cc333a7d3572eebab6"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvwinsys.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
