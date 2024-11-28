SUMMARY = "OpenMAX IL plugins for GStreamer (Nvidia-specific)"
SECTION = "multimedia"
LICENSE = "LGPL-2.1-only"
DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-nveglgles gstreamer1.0-plugins-tegra-binaryonly"
DEPENDS += "tegra-libraries-omx"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/gstomx1_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.7.6.inc

# Plugin needs a couple of header files that it does not include, but
# they are present in the V4L2 plugin source package, so extract them
# from there.
unpack_tar_in_tar:append() {
    tar -x -j -f ${SRC_ARCHIVE} Linux_for_Tegra/source/public/gst-nvvideo4linux2_src.tbz2 \
	--to-command="tar -C ${S}/omx -x -j --no-same-owner -f- nvbufsurface.h nvbuf_utils.h"
}

SRC_URI += "file://0001-use_lt_sysroot_when_parsing_gstconfig_header.patch \
	    file://0003-add-missing-nviva-lib.patch \
            file://fix-h265enc-compilation-errors.patch \
            file://0001-Elminate-compiled-in-path-for-core-library-lookup.patch \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://omx/gstomx.h;beginline=1;endline=22;md5=3e5e5aa39221b7c190c3b7a0dac609d3"

S = "${WORKDIR}/gstomx1_src/gst-omx1"

CFLAGS += "-DHAVE_NVBUF_UTILS"
LDFLAGS += "-lnvbuf_utils"

inherit autotools pkgconfig gettext container-runtime-csv features_check

REQUIRED_DISTRO_FEATURES = "opengl"

CONTAINER_CSV_LIB_PATH = "/usr/lib/aarch64-linux-gnu/"

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"

do_configure:append() {
    touch ${S}/omx/gstnvivameta_api.h
}

acpaths = "-I ${S}/common/m4 -I ${S}/m4"

EXTRA_OECONF += "--disable-valgrind --with-omx-target=tegra"

FILES:${PN} += "${libdir}/gstreamer-1.0/*.so"
FILES:${PN}-dbg += "${libdir}/gstreamer-1.0/.debug"
FILES:${PN}-dev += "${libdir}/gstreamer-1.0/*.la"
FILES:${PN}-staticdev += "${libdir}/gstreamer-1.0/*.a"
RDEPENDS:${PN} += "tegra-configs-omx-tegra gstreamer1.0-plugins-nveglgles-nveglglessink tegra-libraries-omx"
