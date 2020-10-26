DESCRIPTION = "NVIDIA nvarguscamerasrc GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://nvbuf_utils.h;endline=9;md5=afc209f3955d083a93f5009bc9a65a22 \
                    file://README.txt;endline=25;md5=364434949752edc42711344c8401d55b \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/gst-nvarguscamera_src.tbz2"
TEGRA_SRC_SUBARCHIVE_OPTS = "--exclude=3rdpartyheaders.tbz2"

require recipes-bsp/tegra-sources/tegra-sources-32.4.4.inc

SRC_URI += "\
    file://0001-Build-fixups.patch \
    file://0002-Remove-dependencies-on-tegra-mmapi-sample-code.patch \
"

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base virtual/egl tegra-libraries tegra-mmapi"

S = "${WORKDIR}/gst-nvarguscamera"

inherit pkgconfig container-runtime-csv

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES_${PN} = "${libdir}/gstreamer-1.0"
