L4T_DEB_COPYRIGHT_MD5 = "b37655968c46b0b74d098dc784bd4263"
DEPENDS = "tegra-libraries-core virtual/egl"

require tegra-debian-libraries-common.inc

MAINSUM = "013bb6e293abda453ff177e591b927269fdce2c2693447157f3ce40d5ce9ffae"
MAINSUM:tegra210 = "e15b36238e2ff67fa615fdbb599203439a481d2ddf95409076d7815303b9c1ce"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvbuf_fdmap.so.1.0.0 \
    tegra/libnvbuf_utils.so.1.0.0 \
"

do_install() {
    install_libraries
    for libname in nvbuf_utils; do
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so.1
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so
    done
}
