DESCRIPTION = "NVIDIA accelerated JPEG GStreamer plugin"
SECTION = "multimedia"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/gstjpeg_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.7.6.inc

SRC_URI += "file://use-nvjpeg-for-plugin-name.patch"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base libjpeg-turbo tegra-libraries-multimedia tegra-libraries-multimedia-utils"

S = "${WORKDIR}/gstjpeg_src/gst-jpeg/gst-jpeg-1.0"
CFLAGS += "-I${WORKDIR}/gstjpeg_src/nv_headers -DUSE_TARGET_TEGRA"
EXTRA_OECONF = "--disable-examples"
EXTRA_OEMAKE = "JPEG_LIBS=-lnvjpeg"

inherit autotools gtk-doc gettext pkgconfig container-runtime-csv features_check

REQUIRED_DISTRO_FEATURES = "opengl"

CONTAINER_CSV_LIB_PATH = "/usr/lib/aarch64-linux-gnu/"

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"

PACKAGES_DYNAMIC = "^${PN}-.*"
require recipes-multimedia/gstreamer/gstreamer1.0-plugins-packaging.inc
