DESCRIPTION = "NVIDIA nvsiplcamera GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause AND LicenseRef-Proprietary"
LIC_FILES_CHKSUM = "file://gstnvsipl.h;endline=29;md5=a94fb30f9bc256e10bfed0615bba9756 \
                    file://README.txt;endline=32;md5=06e37147d5873f2584371719d9887f89 \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/gst-nvsiplcamera_src.tbz2"
TEGRA_SRC_SUBARCHIVE_OPTS = "--exclude=3rdpartyheaders.tbz2"

require recipes-bsp/tegra-sources/tegra-sources-39.2.0.inc

COMPATIBLE_MACHINE = "(tegra)"

SRC_URI += "\
    file://0001-Build-fixups.patch \
"

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base virtual/egl tegra-libraries-camera tegra-libraries-multimedia-ds tegra-libraries-multimedia-utils tegra-mmapi jetson-sipl-api"

S = "${UNPACKDIR}/gst-nvsiplcamera"

inherit pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "opengl"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
