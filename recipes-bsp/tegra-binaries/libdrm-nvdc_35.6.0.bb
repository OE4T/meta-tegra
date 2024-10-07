DESCRIPTION = "NVIDIA DRM compatibility library"
L4T_DEB_COPYRIGHT_MD5 = "8dc9729e1dc38aac4adb4bd6f6e3b370"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'weston')};subdir=${BP};name=weston"
MAINSUM = "7ea038eeb71cad7cdd63a21382e9d9e756792d7800e412d0a1ab0d544a9b7856"
SRC_URI[weston.sha256sum] = "11ecb4b51091f4a8f5815b562c65dc0efde4eab1f5923fe215d64ae0777642a6"

do_compile() {
    echo "${libdir}/tegra" > ${B}/tegra.conf
}

do_install() {
    install -d ${D}${libdir}/tegra
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/libdrm.so.2 ${D}${libdir}/tegra/
    ln -sf tegra/libdrm.so.2 ${D}${libdir}/libdrm_nvdc.so
    install -Dpm 644 ${B}/tegra.conf ${D}${sysconfdir}/ld.so.conf.d/tegra.conf
}

DEBIAN_NOAUTONAME:${PN} = "1"
DEBIAN_NOAUTONAME:${PN}-dev = "1"
DEBIAN_NOAUTONAME:${PN}-dbg = "1"
FILES:${PN} = "${libdir} ${sysconfdir}/ld.so.conf.d"
FILES:${PN}-dev = "${includedir}/libdrm/nvidia"
PRIVATE_LIBS = "libdrm.so.2"
