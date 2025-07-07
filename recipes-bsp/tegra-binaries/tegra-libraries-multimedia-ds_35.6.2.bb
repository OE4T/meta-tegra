L4T_DEB_COPYRIGHT_MD5 = "c5a9810a8ac2bdcdce4e85013d7044d4"
DEPENDS = "tegra-libraries-multimedia glib-2.0 gstreamer1.0-plugins-base"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

LICENSE += "& MIT & BSD-3-Clause"

MAINSUM = "09f4217a0d6d2a4688453271aa75f292afa79682231f2ae7feb1643115e550b6"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvdsbufferpool.so.1.0.0 \
"

do_install() {
    install_libraries
    for libname in nvdsbufferpool; do
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so.1
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so
    done
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
