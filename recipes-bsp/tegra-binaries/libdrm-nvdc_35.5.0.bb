DESCRIPTION = "NVIDIA DRM compatibility library"
L4T_DEB_COPYRIGHT_MD5 = "8dc9729e1dc38aac4adb4bd6f6e3b370"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'weston')};subdir=${BP};name=weston"
MAINSUM = "e32ade8d9abebedaffdc922c513152ea9686aa965d9e10514647dfccb17ebdf1"
SRC_URI[weston.sha256sum] = "111fcc07130ed003f3fd3e5133704f7ca1c2d274ce160786e17808241e090f48"

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
DEBIAN_NOAUTONAME:${PN}-lic = "1"
DEBIAN_NOAUTONAME:${PN}-src = "1"
FILES:${PN} = "${libdir} ${sysconfdir}/ld.so.conf.d"
FILES:${PN}-dev = "${includedir}/libdrm/nvidia"
PRIVATE_LIBS = "libdrm.so.2"
