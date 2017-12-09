require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

inherit update-alternatives systemd

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 usr/lib
}

do_compile[noexec] = "1"

DRVROOT = "${B}/usr/lib/aarch64-linux-gnu"

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${DRVROOT}/tegra/libdrm.so.2 ${D}${libdir}
    mv ${D}${libdir}/libdrm.so.2 ${D}${libdir}/libdrm-tegra.so.2
    ln -sf libdrm-tegra.so.2 ${D}${libdir}/libdrm-tegra.so
}

# Allow switching between libdrm from freedesktop and libdrm from NVIDIA.
# freedesktop is the default provider.
ALTERNATIVE_${PN} = "libdrm.so.2 libdrm.so.2.4.0"
ALTERNATIVE_LINK_NAME[libdrm.so.2] = "${libdir}/libdrm.so.2"
ALTERNATIVE_LINK_NAME[libdrm.so.2.4.0] = "${libdir}/libdrm.so.2.4.0"
ALTERNATIVE_TARGET = "${libdir}/libdrm-tegra.so.2"
ALTERNATIVE_PRIORITY = "10"

EXCLUDE_FROM_SHLIBS = "1"
INSANE_SKIP_${PN} = "ldflags"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
