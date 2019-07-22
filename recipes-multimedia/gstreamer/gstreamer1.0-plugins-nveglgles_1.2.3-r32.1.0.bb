require recipes-multimedia/gstreamer/gstreamer1.0-plugins.inc

LICENSE = "GPLv2+ & LGPLv2+ & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=73a5855a8119deb017f5f13cf327095d \
                    file://COPYING.LIB;md5=21682e4e8fea52413fd26c60acb907e5 \
                    file://ext/eglgles/gstegladaptation.c;beginline=9;endline=25;md5=51eafe984c428127773b6a95eb959d0b"

TEGRA_SRC_SUBARCHIVE = "public_sources/gstegl_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.1.0.inc

SRC_URI += "file://0001-introspection-pkgconfig.patch \
	    file://0002-fix-libtool-references.patch \
	    file://0003-fix-pkg-config-path-in-makefiles.patch \
	    file://0004-make-x11-optional.patch \
	    file://fix-missing-gstegljitter.patch \
	    file://make-wayland-configurable.patch \
"

DEPENDS += "gstreamer1.0-plugins-base virtual/egl virtual/libgles2"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11 wayland', d)}"
PACKAGECONFIG[x11] = "--with-x11 --with-egl-window-system=x11,--without-x11 --with-egl-window-system=auto,libx11 libxext"
PACKAGECONFIG[wayland] = "--with-wayland,--without-wayland,wayland,mesa"

S = "${WORKDIR}/gstegl_src/gst-egl"

inherit gettext gobject-introspection

do_configure_append() {
    rm -f ${S}/po/POTFILES.in
    echo "" > ${S}/po/POTFILES.in
}

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/gst-libs/gst/egl/.libs"
}

do_install_append() {
    sed -i -e's,${STAGING_INCDIR},${includedir},g' ${D}${libdir}/pkgconfig/gstreamer-egl-1.0.pc
}
