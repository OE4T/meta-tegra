L4T_DEB_COPYRIGHT_MD5 = "34723d6d005ff47007fff977eac035d6"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "163716edf02fb477162cb07152860e19bac32f5e3172162edc5f65645ad55c16"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra-egl/libEGL_nvidia.so.0 \
    nvidia/libnvidia-eglcore.so.${L4T_LIB_VERSION} \
    nvidia/libnvidia-glsi.so.${L4T_LIB_VERSION} \
    nvidia/libnvidia-glvkspirv.so.${L4T_LIB_VERSION} \
    nvidia/libnvidia-rtcore.so.${L4T_LIB_VERSION} \
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
