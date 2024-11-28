L4T_DEB_COPYRIGHT_MD5 = "03753bf7be89a121c8d3fd11c4267db9"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "1fa3a9d3af25bdb0c9c000419b19eb74996c768b15d11c8fff9a6165c8632b24"
MAINSUM:tegra210 = "44df4b465417916ac1bdb1c801892c7af948114cadf5a3a1eafb780984600a94"

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
