SUMMARY = "OpenMAX IL plugins for GStreamer (Nvidia-specific)"
SECTION = "multimedia"
LICENSE = "LGPLv2.1"
LICENSE_FLAGS = "commercial"
HOMEPAGE = "http://www.gstreamer.net/"
DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad gstreamer1.0-plugins-nveglgles libdrm"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r23_Release_v1.0/source/gstomx1_src.tbz2 \
	   file://0001-use_lt_sysroot_when_parsing_gstconfig_header.patch \
	   file://0002-add-missing-h265-support.patch \
"

SRC_URI[md5sum] = "7521a0b5db182cffd7ac0be17a347806"
SRC_URI[sha256sum] = "38b01308350bfa8d79c46ece7cd3339b21b75509ed17836c90838eaac96bda55"

LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://omx/gstomx.h;beginline=1;endline=22;md5=e9a396be2d7b4026e48886c39b5fe35d"

S = "${WORKDIR}/gstomx1_src/gst-omx1"

inherit autotools pkgconfig gettext

do_configure_append() {
    sed -i -e's,/usr/lib/arm-linux-gnueabihf/tegra/,${libdir}/,g' ${S}/gstomx_config.c
}

acpaths = "-I ${S}/common/m4 -I ${S}/m4"

EXTRA_OECONF += "--disable-valgrind --with-omx-target=tegra"

FILES_${PN} += "${libdir}/gstreamer-1.0/*.so"
FILES_${PN}-dbg += "${libdir}/gstreamer-1.0/.debug"
FILES_${PN}-dev += "${libdir}/gstreamer-1.0/*.la"
FILES_${PN}-staticdev += "${libdir}/gstreamer-1.0/*.a"
