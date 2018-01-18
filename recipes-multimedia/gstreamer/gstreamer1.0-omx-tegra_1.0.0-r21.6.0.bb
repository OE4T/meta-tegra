SUMMARY = "OpenMAX IL plugins for GStreamer (Nvidia-specific)"
SECTION = "multimedia"
LICENSE = "LGPLv2.1"
LICENSE_FLAGS = "commercial"
HOMEPAGE = "http://www.gstreamer.net/"
DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad gstreamer1.0-plugins-nveglgles libdrm"

require recipes-bsp/tegra-sources/tegra-sources-21.6.0.inc
TEGRA_SRC_SUBARCHIVE = "gstomx1_src.tbz2"

SRC_URI += "file://0001-use_lt_sysroot_when_parsing_gstconfig_header.patch \
"

S = "${WORKDIR}/gstomx1_src/gst-omx1"

LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://omx/gstomx.h;beginline=1;endline=22;md5=e9a396be2d7b4026e48886c39b5fe35d"

inherit autotools pkgconfig gettext

do_configure_append() {
    sed -i -e's,/usr/lib/.*/tegra/,${libdir}/,g' ${S}/gstomx_config.c
#    touch ${S}/omx/gstnvivameta_api.h
}

acpaths = "-I ${S}/common/m4 -I ${S}/m4"

EXTRA_OECONF += "--disable-valgrind --with-omx-target=tegra"

FILES_${PN} += "${libdir}/gstreamer-1.0/*.so"
FILES_${PN}-dbg += "${libdir}/gstreamer-1.0/.debug"
FILES_${PN}-dev += "${libdir}/gstreamer-1.0/*.la"
FILES_${PN}-staticdev += "${libdir}/gstreamer-1.0/*.a"
RDEPENDS_${PN} += "tegra-configs-omx-tegra gstreamer1.0-plugins-nveglgles-nveglglessink"

COMPATIBLE_MACHINE = "(tegra124)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
