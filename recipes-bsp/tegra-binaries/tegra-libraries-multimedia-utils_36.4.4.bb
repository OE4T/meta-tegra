L4T_DEB_COPYRIGHT_MD5 = "3bc757bed87bacc5ca956f246fd49a81"
DEPENDS = "tegra-libraries-core virtual/egl"

require tegra-debian-libraries-common.inc

MAINSUM = "fc10f2033350baef0f00b06f52d119286d0a035a1c0440ee170c0f6371ffd523"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvbuf_fdmap.so.1.0.0 \
    nvidia/libnvbufsurface.so.1.0.0 \
"

do_install() {
    install_libraries
    for libname in nvbufsurface; do
        ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so.1
        ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so
    done
}
