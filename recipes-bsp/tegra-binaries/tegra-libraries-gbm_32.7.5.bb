L4T_DEB_COPYRIGHT_MD5 = "ef1b882a6a8ed90f38e4b0288fcd1525"
DEPENDS = "libdrm patchelf-native"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

LICENSE += "& BSD-3-Clause"
LIC_FILES_CHKSUM += "file://usr/share/doc/nvidia-tegra/LICENSE.minigbm;md5=72f855f00b364ec8bdc025e1a36b39c3"

MAINSUM = "0afb73f595113bf02d2f66924dd351bb665941a569507fd730c66167a4823d94"
MAINSUM:tegra210 = "c2550014e670366c339662203cbc0c5d2afd92b5a6557923844a42526e3aa6ac"

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
