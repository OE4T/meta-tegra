L4T_DEB_COPYRIGHT_MD5 = "0600ee93b3515fbce119bdcc40ee64e4"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-nvsci virtual/egl"

require tegra-debian-libraries-common.inc

MAINSUM = "81f41749acb7680bddfb59546631c82a13fc74c439097b24b31fbc6b4d5c8a3a"

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
