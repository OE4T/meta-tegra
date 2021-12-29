L4T_DEB_COPYRIGHT_MD5 = "9b04019d6032ae79ef58a07a2f55c2b7"
DEPENDS = "tegra-libraries-core virtual/egl"

require tegra-debian-libraries-common.inc

MAINSUM = "005ecf7af17a676191282c564b7e7fcacf507db9e9991600a331dd96b6fb1ce8"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvbuf_fdmap.so.1.0.0 \
    tegra/libnvbuf_utils.so.1.0.0 \
    tegra/libnvbufsurface.so.1.0.0 \
"

do_install() {
    install_libraries
    for libname in nvbuf_utils; do
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so.1
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so
    done
}
