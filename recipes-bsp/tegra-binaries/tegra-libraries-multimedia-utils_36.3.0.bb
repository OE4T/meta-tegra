L4T_DEB_COPYRIGHT_MD5 = "3bc757bed87bacc5ca956f246fd49a81"
DEPENDS = "tegra-libraries-core virtual/egl"

require tegra-debian-libraries-common.inc

MAINSUM = "746da883697216ad54585799a076ab1be984ec4f835972bfecca7708cd508e6c"

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
