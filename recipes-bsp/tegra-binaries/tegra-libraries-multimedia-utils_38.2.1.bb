L4T_DEB_COPYRIGHT_MD5 = "0600ee93b3515fbce119bdcc40ee64e4"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-nvsci virtual/egl"

require tegra-debian-libraries-common.inc

MAINSUM = "639fa5f0e8d7f706c4a219ba19200c6b9d763c4ea2d31e6f3dad9f75ea437a22"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvbuf_fdmap.so.1.0.0 \
    nvidia/libnvbufsurface.so.1.0.0 \
    nvidia/libnvbufsurface_nvsci.so.1.0.0 \
"

do_install() {
    install_libraries
    for libname in nvbufsurface; do
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so.1
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so
    done
}
