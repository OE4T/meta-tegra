L4T_DEB_COPYRIGHT_MD5 = "ce4d36df31e6cc73581fd2a25d16834e"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "327062086957dca7cc1c73208202654bc303946f1f70125b4e8491a5bba11ff9"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra-egl/libEGL_nvidia.so.0 \
    tegra/libnvidia-eglcore.so.${L4T_VERSION} \
    tegra/libnvidia-glsi.so.${L4T_VERSION} \
    tegra/libnvidia-glvkspirv.so.${L4T_VERSION} \
    tegra/libnvidia-rmapi-tegra.so.${L4T_VERSION} \
"

do_install() {
    install_libraries
    install -d ${D}${datadir}/glvnd/egl_vendor.d
    install -m644 ${S}/usr/lib/aarch64-linux-gnu/tegra-egl/nvidia.json ${D}${datadir}/glvnd/egl_vendor.d/10-nvidia.json
}

pkg_postinst:${PN}() {
    # argus and scf libraries hard-coded to use this path
    install -d $D/usr/lib/aarch64-linux-gnu/tegra-egl
    ln $D${libdir}/libEGL_nvidia.so.0 $D/usr/lib/aarch64-linux-gnu/tegra-egl/
}

FILES:${PN} += "${datadir}/glvnd/egl_vendor.d"
