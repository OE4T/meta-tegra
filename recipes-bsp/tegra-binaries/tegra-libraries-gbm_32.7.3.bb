L4T_DEB_COPYRIGHT_MD5 = "fab0c15b4bbf7f8d5ac2bd6673d4211c"
DEPENDS = "libdrm patchelf-native"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

LICENSE += "& BSD-3-Clause"
LIC_FILES_CHKSUM += "file://usr/share/doc/nvidia-tegra/LICENSE.minigbm;md5=72f855f00b364ec8bdc025e1a36b39c3"

MAINSUM = "b69e15c45a24066eaebc112f39c6876ad0ab72c22e73df7ede37e40c33b1e0d8"
MAINSUM:tegra210 = "30cfc4d9c38731165a36c85bee0a46643a7eae798903781e2cc47d02606aa79a"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvgbm.so \
"

do_install:append() {
    patchelf --set-soname libnvgbm.so.1 ${D}${libdir}/libnvgbm.so
}

DEBIAN_NOAUTONAME:${PN} = "1"
DEBIAN_NOAUTONAME:${PN}-dev = "1"
DEBIAN_NOAUTONAME:${PN}-dbg = "1"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RRECOMMENDS:${PN} = "kernel-module-nvgpu"
