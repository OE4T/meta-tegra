DESCRIPTION = "NVIDIA DRM compatibility library"
L4T_DEB_COPYRIGHT_MD5 = "d9a8361f8068fc4e3b934118d1de9f3f"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'weston')};subdir=${BP};name=weston"
MAINSUM = "5c5ae35b29c3f1ac438fb1d4c065d6e5f22fa640cedc35862774cd836f627ded"
SRC_URI[weston.sha256sum] = "260cadeaef6bea6afa1c9076fddd8a6e2233f3bbcd5cd24c9f5c29853323c7b2"

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
