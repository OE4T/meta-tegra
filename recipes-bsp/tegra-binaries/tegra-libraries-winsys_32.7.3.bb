L4T_DEB_COPYRIGHT_MD5 = "ce4d36df31e6cc73581fd2a25d16834e"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "18aa04998115a2d4e3285ddd3f864cf5a5e5e6cd07c9b82f49bf63c347506fcb"
MAINSUM:tegra210 = "6928ebe44d8097622ef24e1c505fe45c69415ee2db8614ecffb11a35f5d0a343"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvwinsys.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
