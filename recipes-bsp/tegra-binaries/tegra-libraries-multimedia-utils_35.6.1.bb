L4T_DEB_COPYRIGHT_MD5 = "3bc757bed87bacc5ca956f246fd49a81"
DEPENDS = "tegra-libraries-core virtual/egl"

require tegra-debian-libraries-common.inc

MAINSUM = "9daa410b81a89580e1cd4abe425e85016ea7fc08f12fe8de01890b3372c0544a"

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
