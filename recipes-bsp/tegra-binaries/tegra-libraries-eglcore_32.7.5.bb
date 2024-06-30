L4T_DEB_COPYRIGHT_MD5 = "03753bf7be89a121c8d3fd11c4267db9"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "31b5c0de9fc673a540e6c331ae18cb981fc6de1145f4be76419bed0f9de1959b"
MAINSUM:tegra210 = "13fa8733d146999ac1a6e2c37c70c2f4b64988c5bcd5a664f2fda4bb4a04680b"

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
CONTAINER_CSV_FILES += "${datadir}/glvnd/egl_vendor.d/*"
CONTAINER_CSV_EXTRA = "lib, /usr/lib/aarch64-linux-gnu/tegra-egl/libEGL_nvidia.so.0"
