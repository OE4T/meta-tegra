L4T_DEB_COPYRIGHT_MD5 = "fab0c15b4bbf7f8d5ac2bd6673d4211c"
DEPENDS = "libdrm"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

LICENSE += "& BSD-3-Clause"
LIC_FILES_CHKSUM += "file://usr/share/doc/nvidia-tegra/LICENSE.minigbm;md5=72f855f00b364ec8bdc025e1a36b39c3"

MAINSUM = "8659b23489309907f3ac1f19b270306be37f6a4cc5ddac8cb35e96f5c3ae13bf"
MAINSUM_tegra210 = "7d816cf7bf7831f3ccde19e2bf6f9d731211b9146765271632fb2ed550522032"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvgbm.so \
"

DEBIAN_NOAUTONAME_${PN} = "1"
DEBIAN_NOAUTONAME_${PN}-dev = "1"
DEBIAN_NOAUTONAME_${PN}-dbg = "1"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RRECOMMENDS_${PN} = "kernel-module-nvgpu"
