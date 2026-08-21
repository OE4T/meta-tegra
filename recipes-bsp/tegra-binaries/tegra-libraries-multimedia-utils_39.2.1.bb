L4T_DEB_COPYRIGHT_MD5 = "0600ee93b3515fbce119bdcc40ee64e4"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-nvsci virtual/egl"

require tegra-debian-libraries-common.inc

MAINSUM = "71bad758d12a486ac55f5c5cc12b1f908b88111b7695837ae3b76c682e97222d"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvbuf_fdmap.so.1.0.0 \
    nvidia/libnvbufsurface.so.1.0.0 \
    nvidia/libnvbufsurface_nvsci.so.1.0.0 \
"

do_install() {
    install_libraries
    ln -sf libnvbufsurface.so.1.0.0 ${D}${libdir}/libnvbufsurface.so
    ln -sf libnvbufsurface_nvsci.so.1.0.0 ${D}${libdir}/libnvbufsurface_nvsci.so
}
