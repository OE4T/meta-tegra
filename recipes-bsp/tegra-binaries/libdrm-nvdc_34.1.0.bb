DESCRIPTION = "NVIDIA DRM compatibility library"
L4T_DEB_COPYRIGHT_MD5 = "fab0c15b4bbf7f8d5ac2bd6673d4211c"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

<<<<<<<< HEAD:recipes-bsp/tegra-binaries/libdrm-nvdc_32.7.2.bb
MAINSUM = "41845dade9d3e1cd67be7875e0634167de414678f088d4c6342ccd696894e63e"
MAINSUM:tegra210 = "553a56f565e0ac9659a6633c3fe07afc3e68fad4451bcec9b651a929c0e986c5"
========
SRC_SOC_DEBS += "nvidia-l4t-weston_${PV}_arm64.deb;subdir=${BP};name=weston"
MAINSUM = "da997f39c1e66d5d8ceeca6e4ee33a3e9951237c1238ea51df25c7d8e10c65a7"
SRC_URI[weston.sha256sum] = "d2470e149573158c1230ce89693e2efb397f228df754eee77fc72410a6102aad"
>>>>>>>> 390a51f3 (tegra-binaries: update for 34.1.0):recipes-bsp/tegra-binaries/libdrm-nvdc_34.1.0.bb

do_compile() {
    echo "${libdir}/tegra" > ${B}/tegra.conf
}

do_install() {
    install -d ${D}${libdir}/tegra
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/libdrm.so.2 ${D}${libdir}/tegra/
    ln -sf tegra/libdrm.so.2 ${D}${libdir}/libdrm_nvdc.so
    install -Dpm 644 ${B}/tegra.conf ${D}${sysconfdir}/ld.so.conf.d/tegra.conf
}

CONTAINER_CSV_FILES = "${libdir}/libdrm_nvdc.so ${libdir}/tegra/libdrm.so.2"

DEBIAN_NOAUTONAME:${PN} = "1"
DEBIAN_NOAUTONAME:${PN}-dev = "1"
DEBIAN_NOAUTONAME:${PN}-dbg = "1"
FILES:${PN} = "${libdir} ${sysconfdir}/ld.so.conf.d"
FILES:${PN}-dev = "${includedir}/libdrm/nvidia"
PRIVATE_LIBS = "libdrm.so.2"
