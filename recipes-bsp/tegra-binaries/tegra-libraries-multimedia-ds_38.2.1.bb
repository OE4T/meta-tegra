L4T_DEB_COPYRIGHT_MD5 = "c5a9810a8ac2bdcdce4e85013d7044d4"
DEPENDS = "tegra-libraries-multimedia glib-2.0 gstreamer1.0-plugins-base"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

LICENSE += "& MIT & BSD-3-Clause"

MAINSUM = "992338e6a0955500206b0be07d4ac5e51f1ff7b6f78ebf2ce44f42ae8d449f87"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvdsbufferpool.so.1.0.0 \
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
