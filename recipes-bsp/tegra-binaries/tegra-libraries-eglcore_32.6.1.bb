DEPENDS = "tegra-libraries-core"

require tegra-libraries-common.inc

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra-egl/libEGL_nvidia.so.0 \
    tegra/libnvidia-eglcore.so.${PV} \
    tegra/libnvidia-glsi.so.${PV} \
    tegra/libnvidia-rmapi-tegra.so.${PV} \
"


do_install() {
    install_libraries
    install -d ${D}${datadir}/glvnd/egl_vendor.d
    install -m644 ${DRVROOT}/tegra-egl/nvidia.json ${D}${datadir}/glvnd/egl_vendor.d/10-nvidia.json
}

pkg_postinst_${PN}() {
    # argus and scf libraries hard-coded to use this path
    install -d $D/usr/lib/aarch64-linux-gnu/tegra-egl
    ln $D${libdir}/libEGL_nvidia.so.0 $D/usr/lib/aarch64-linux-gnu/tegra-egl/
}

FILES_${PN} += "${datadir}/glvnd/egl_vendor.d"
CONTAINER_CSV_FILES += "${datadir}/glvnd/egl_vendor.d/*"
CONTAINER_CSV_EXTRA = "lib, /usr/lib/aarch64-linux-gnu/tegra-egl/libEGL_nvidia.so.0"

