L4T_DEB_COPYRIGHT_MD5 = "0600ee93b3515fbce119bdcc40ee64e4"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-nvsci virtual/egl"

require tegra-debian-libraries-common.inc

MAINSUM = "4968389a42b2a9b099d6cc9708d230c80a479fcd9823cd505e9d562eb0fb04ba"

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
