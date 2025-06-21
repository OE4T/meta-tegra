DESCRIPTION = "NVIDIA custom gstreamer events helper library"
SECTION = "multimedia"
LICENSE = "MIT & Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE.libgstnvcustomhelper;md5=9e0fe9cd844e2cba9b43e7a16ad5d431 \
                    file://README;endline=11;md5=8a55074f13f4cdb3c9966343177e1f9e \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/libgstnvcustomhelper_src.tbz2"

require recipes-bsp/tegra-sources/tegra-sources-36.4.3.inc

SRC_URI += " file://0001-Makefile-fixups-for-OE-builds.patch"

DEPENDS = "gstreamer1.0"

S = "${UNPACKDIR}/gst-nvcustomhelper"
B = "${WORKDIR}/build"

inherit pkgconfig

EXTRA_OEMAKE = "-C ${S} -f Makefile.public OUT_DIR=${B}"

do_install() {
	oe_runmake install DESTDIR="${D}"
	install -d ${D}${includedir}
	install -m0644 ${S}/gst-nvcustomevent.h ${D}${includedir}/
}
RPROVIDES:${PN} += "libgstnvcustomhelper.so()(64bit)"

FILES:${PN} = "${libdir}/libgstnvcustomhelper.so*"
FILES_SOLIBSDEV = ""
INSANE_SKIP:${PN} = "dev-so"
