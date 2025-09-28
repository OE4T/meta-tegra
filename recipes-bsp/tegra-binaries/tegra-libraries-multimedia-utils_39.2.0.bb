L4T_DEB_COPYRIGHT_MD5 = "0600ee93b3515fbce119bdcc40ee64e4"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-nvsci virtual/egl"

require tegra-debian-libraries-common.inc

MAINSUM = "2725cd6665934f34fb3a0f0c0f4c21d9fd9d95bf43b6471171f6a1eb6dabe454"

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
