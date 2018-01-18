require recipes-multimedia/gstreamer/gstreamer1.0-plugins.inc

LICENSE = "GPLv2+ & LGPLv2+ & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=73a5855a8119deb017f5f13cf327095d \
                    file://COPYING.LIB;md5=21682e4e8fea52413fd26c60acb907e5 \
                    file://ext/eglgles/gstegladaptation.c;beginline=9;endline=25;md5=51eafe984c428127773b6a95eb959d0b"

TEGRA_SRC_SUBARCHIVE = "gstegl_src.tbz2"
TEGRA_DST = "${WORKDIR}"
S = "${WORKDIR}/gstegl_src/gst-egl/"
require recipes-bsp/tegra-sources/tegra-sources-21.6.0.inc

SRC_URI += "file://0001-introspection-pkgconfig.patch \
            file://0002-fix-libtool-references.patch \
            file://0003-fix-pkg-config-path-in-makefiles.patch \
            file://0004-make-x11-optional.patch \
"

DEPENDS += "gstreamer1.0-plugins-base virtual/egl virtual/libgles2 gobject-introspection"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG[x11] = "--with-x11 --with-egl-window-system=x11,--without-x11 --with-egl-window-system=auto,libx11 libxext"

inherit gettext

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

COMPATIBLE_MACHINE = "(tegra124)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

