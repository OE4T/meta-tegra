SUMMARY = "NVIDIA EGL/GLES GStreamer plugin"
SECTION = "multimedia"
LICENSE = "GPL-2.0-or-later & LGPL-2.0-or-later & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=73a5855a8119deb017f5f13cf327095d \
                    file://COPYING.LIB;md5=21682e4e8fea52413fd26c60acb907e5 \
                    file://ext/eglgles/gstegladaptation.c;beginline=9;endline=25;md5=51eafe984c428127773b6a95eb959d0b"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/gstegl_src.tbz2"

require recipes-bsp/tegra-sources/tegra-sources-34.1.0.inc

SRC_URI += " file://0001-Makefile-fixes-for-OE-builds.patch"

DEPENDS = "tegra-mmapi gstreamer1.0 glib-2.0-native gstreamer1.0-plugins-base virtual/egl virtual/libgles2 cuda-cudart cuda-driver"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11 wayland', d)}"
PACKAGECONFIG[x11] = "USE_X11=yes,,libx11"
PACKAGECONFIG[wayland] = "USE_WAYLAND=yes,,wayland"

EXTRA_OEMAKE = "CUDA_VER=${CUDA_VERSION} ${PACKAGECONFIG_CONFARGS}"

S = "${WORKDIR}/gstegl_src/gst-egl"

inherit pkgconfig container-runtime-csv features_check

REQUIRED_DISTRO_FEATURES = "opengl"

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"
