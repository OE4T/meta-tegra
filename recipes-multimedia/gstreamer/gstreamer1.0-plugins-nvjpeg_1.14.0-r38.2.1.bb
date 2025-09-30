DESCRIPTION = "NVIDIA accelerated JPEG GStreamer plugin"
SECTION = "multimedia"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://gst-jpeg/gst-jpeg-1.0/COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/gstjpeg_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-38.2.1.inc

COMPATIBLE_MACHINE = "(tegra)"

SRC_URI += "file://0001-Update-makefile.am-for-OE-builds.patch"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base tegra-libraries-multimedia tegra-libraries-multimedia-utils tegra-mmapi"

S = "${UNPACKDIR}/gstjpeg_src"
AUTOTOOLS_SCRIPT_PATH = "${S}/gst-jpeg/gst-jpeg-1.0"
CFLAGS += "-I${S}/nv_headers -DUSE_TARGET_TEGRA"
EXTRA_OECONF = "--disable-examples"

inherit autotools gtk-doc gettext pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGES_DYNAMIC = "^${PN}-.*"
require recipes-multimedia/gstreamer/gstreamer1.0-plugins-packaging.inc

COMPATIBLE_MACHINE = "(tegra)"
