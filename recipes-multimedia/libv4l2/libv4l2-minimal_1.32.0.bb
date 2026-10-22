SUMMARY = "libv4l2 from v4l-utils, minimally built"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING.libv4l;md5=88b8889c2c4329d4cf18ce5895e64c16"
PROVIDES = "libv4l v4l-utils"
COMPATIBLE_MACHINE = "(tegra)"

DEPENDS = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'virtual/libx11', '', d)}"
LDFLAGS:append = " -pthread"
# v4l2 explicitly sets _FILE_OFFSET_BITS=32 to get access to
# both 32 and 64 bit file APIs.  But it does not handle the time side?
# Needs further investigation
GLIBC_64BIT_TIME_FLAGS = ""

SRC_URI = "git://git.linuxtv.org/v4l-utils.git;protocol=https;branch=master;tag=v4l-utils-${PV} \
          file://0001-Make-plugin-directory-relative-to-ORIGIN.patch \
          file://0003-Update-conversion-defaults-to-match-NVIDIA-sources.patch \
"
SRCREV = "5a666c7ce89c00d66aa8e53c8f098a0c6c401f91"

inherit meson gettext pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "opengl"

EXTRA_OEMESON = "-Dlibdvbv5=disabled -Dv4l-utils=false -Dqv4l2=disabled -Dqvidcap=disabled -Dgconv=disabled -Djpeg=disabled \
                 -Dudevdir=${base_libdir}/udev -Dv4l2-compliance-32=false -Dv4l2-ctl-32=false"
CFLAGS:append:libc-glibc = " -DHAVE_RTLD_DI_ORIGIN"

do_install:append() {
    rm -rf ${D}${libdir}/libv4l/plugins
}

PACKAGES =+ "libv4l libv4l-dev"
RPROVIDES:${PN}-dbg += "libv4l-dbg"
FILES:libv4l += "${libdir}/libv4l*${SOLIBS} ${libdir}/libv4l/*.so ${libdir}/libv4l/plugins/*.so \
                 ${libdir}/libv4l/*-decomp"

FILES:libv4l-dev += "${includedir} ${libdir}/pkgconfig \
                     ${libdir}/libv4l*${SOLIBSDEV} ${libdir}/*.la \
                     ${libdir}/v4l*${SOLIBSDEV} ${libdir}/libv4l/*.la ${libdir}/libv4l/plugins/*.la"
RRECOMMENDS:libv4l = "tegra-libraries-multimedia-v4l"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"

CVE_PRODUCT = "v4l-utils"
