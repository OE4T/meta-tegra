DESCRIPTION = "NVIDIA DRM compatibility library"

require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

do_configure () {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 usr/lib/aarch64-linux-gnu/tegra/libdrm.so.2
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${libdir}/tegra
    install -m 0644 ${B}/usr/lib/aarch64-linux-gnu/tegra/libdrm.so.2 ${D}${libdir}/tegra/
    ln -sf tegra/libdrm.so.2 ${D}${libdir}/libdrm_nvdc.so
}

FILES_${PN} = "${libdir}/tegra"
PRIVATE_LIBS = "libdrm.so.2"
RDEPENDS_${PN} = "tegra-libraries"
INSANE_SKIP_${PN} = "ldflags"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
