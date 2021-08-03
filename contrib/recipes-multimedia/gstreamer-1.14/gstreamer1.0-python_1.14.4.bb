SUMMARY = "Python bindings for GStreamer 1.0"
HOMEPAGE = "http://cgit.freedesktop.org/gstreamer/gst-python/"
SECTION = "multimedia"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=c34deae4e395ca07e725ab0076a5f740"

SRC_URI = "http://gstreamer.freedesktop.org/src/${PNREAL}/${PNREAL}-${PV}.tar.xz"
SRC_URI[md5sum] = "d4c0e3915f547feef49208ee08981e5a"
SRC_URI[sha256sum] = "d0fdb24f93b6d889f309d2f526b8ea9577e0084ff0a62b4623ef1aed52e85a1b"

DEPENDS = "gstreamer1.0 python3-pygobject"
RDEPENDS:${PN} += "gstreamer1.0 python3-pygobject"

PNREAL = "gst-python"

S = "${WORKDIR}/${PNREAL}-${PV}"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST:append = " --enable-introspection --disable-introspection"

inherit autotools pkgconfig distutils3-base upstream-version-is-even gobject-introspection features_check

do_install:append() {
    # gstpythonplugin hardcodes the location of the libpython from the build
    # workspace and then fails at runtime. We can override it using
    # --with-libpython-dir=${libdir}, but it still fails because it looks for a
    # symlinked library ending in .so instead of the actually library with
    # LIBNAME.so.MAJOR.MINOR. Although we could patch the code to use the path
    # we want, it will break again if the library version ever changes. We need
    # to think about the best way of handling this and possibly consult
    # upstream.
    #
    # Note that this particular find line is taken from the Debian packaging for
    # gst-python1.0.
    find "${D}" \
        -name '*.pyc' -o \
        -name '*.pyo' -o \
        -name '*.la' -o \
        -name 'libgstpythonplugin*' \
        -delete
}
