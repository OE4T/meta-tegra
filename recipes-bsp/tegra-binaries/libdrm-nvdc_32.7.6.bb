DESCRIPTION = "NVIDIA DRM compatibility library"
L4T_DEB_COPYRIGHT_MD5 = "ef1b882a6a8ed90f38e4b0288fcd1525"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

MAINSUM = "7b81a016d6a0f283553b01516fa562c8c99c95bb87d88b680dcbc113e8bfa938"
MAINSUM:tegra210 = "61ada8308bd38fe6fe1793b658c83c3ef3dcc057a0d9e080dc0336e61be683c2"

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
FILES:${PN}-dev = ""
PRIVATE_LIBS = "libdrm.so.2"
