L4T_DEB_COPYRIGHT_MD5 = "0600ee93b3515fbce119bdcc40ee64e4"
DEPENDS = "tegra-libraries-core virtual/egl"

require tegra-debian-libraries-common.inc

MAINSUM = "8a056c9b16f2498f2b180aa78b419004c221a1b25fd774fdd1c4de1be6a794e1"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvbuf_fdmap.so.1.0.0 \
    tegra/libnvbufsurface.so.1.0.0 \
"

do_install() {
    install_libraries
    for libname in nvbufsurface; do
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so.1
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so
    done
}
