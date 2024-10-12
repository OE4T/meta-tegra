SUMMARY = "NVIDIA EGL/GLES GStreamer plugin"
SECTION = "multimedia"
LICENSE = "LGPL-2.0-or-later & MIT"
LIC_FILES_CHKSUM = "file://gst-libs/gst/egl/LICENSE.libgstnvegl-1.0;md5=de0f9dfa389a77a904a5a2919a9e6b08 \
                    file://LICENSE.libgstnveglglessink;md5=5cf2b0235eb3cb8f4073a66ecb29212a"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/gstegl_src.tbz2"

require recipes-bsp/tegra-sources/tegra-sources-36.4.0.inc

SRC_URI += "\
    file://0001-Makefile-fixes-for-OE-builds.patch;patchdir=.. \
    file://0002-Fix-builds-without-x11.patch;patchdir=.. \
    file://0003-Fix-builds-without-wayland.patch;patchdir=.. \
    file://0004-Fix-builds-without-wayland-IVI-extensions.patch;patchdir=.. \
    file://0005-Convert-from-wl-shell-to-xdg-shell-for-wayland.patch;patchdir=.. \
"

DEPENDS = "tegra-mmapi gstreamer1.0 glib-2.0-native gstreamer1.0-plugins-base virtual/egl virtual/libgles2 cuda-cudart cuda-driver"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11 wayland', d)}"
PACKAGECONFIG[x11] = "USE_X11=yes,,libx11"
PACKAGECONFIG[wayland] = "USE_WAYLAND=yes,,wayland wayland-protocols wayland-native"

EXTRA_OEMAKE = "CUDA_VER=${CUDA_VERSION} ${PACKAGECONFIG_CONFARGS}"

CFLAGS += "-Wno-error=int-conversion"

S = "${WORKDIR}/gstegl_src/gst-egl"

inherit pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "opengl"

do_install() {
    oe_runmake install DESTDIR="${D}"
}

FILES:${PN} = "${libdir}/gstreamer-1.0"
