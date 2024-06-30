L4T_DEB_COPYRIGHT_MD5 = "b37655968c46b0b74d098dc784bd4263"
DEPENDS = "tegra-libraries-core virtual/egl"

require tegra-debian-libraries-common.inc

MAINSUM = "c4a81e9432e2579133860909923762ec0a898930086ad9203f914fe154b15db6"
MAINSUM:tegra210 = "4dd03e4a1777316a8d83029a6515a4829764135a2b5ece1b8552d530fb9d6f9f"

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
