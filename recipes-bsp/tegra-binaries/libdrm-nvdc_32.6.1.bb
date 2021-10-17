DESCRIPTION = "NVIDIA DRM compatibility library"

require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DEPENDS = "tegra-libraries-core"

inherit container-runtime-csv

CONTAINER_CSV_FILES = "${libdir}/libdrm_nvdc.so ${libdir}/tegra/libdrm.so.2"

do_configure () {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 usr/lib/aarch64-linux-gnu/tegra/libdrm.so.2
}

do_compile() {
    echo ${libdir}/tegra > tegra.conf
}

do_install() {
    install -d ${D}${libdir}/tegra
    install -m 0644 ${B}/usr/lib/aarch64-linux-gnu/tegra/libdrm.so.2 ${D}${libdir}/tegra/
    ln -sf tegra/libdrm.so.2 ${D}${libdir}/libdrm_nvdc.so
    install -Dpm 644 tegra.conf ${D}${sysconfdir}/ld.so.conf.d/tegra.conf
}

DEBIAN_NOAUTONAME:${PN} = "1"
DEBIAN_NOAUTONAME:${PN}-dev = "1"
DEBIAN_NOAUTONAME:${PN}-dbg = "1"
FILES:${PN} = "${libdir} ${sysconfdir}/ld.so.conf.d"
FILES:${PN}-dev = ""
PRIVATE_LIBS = "libdrm.so.2"
INSANE_SKIP:${PN} = "ldflags dev-so"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
