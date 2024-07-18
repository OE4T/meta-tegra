SUMMARY = "libv4l2 from v4l-utils, minimally built"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING.libv4l;md5=d749e86a105281d7a44c2328acebc4b0"
PROVIDES = "libv4l v4l-utils"
COMPATIBLE_MACHINE = "(tegra)"

DEPENDS = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'virtual/libx11', '', d)}"

SRC_URI = "http://linuxtv.org/downloads/v4l-utils/v4l-utils-${PV}.tar.bz2 \
           file://0001-Make-plugin-directory-relative-to-ORIGIN.patch \
           file://0002-Replace-stat-fstat-calls-with-__xstat-__fxstat.patch \
           file://0003-Update-conversion-defaults-to-match-NVIDIA-sources.patch \
"
SRC_URI[md5sum] = "18996bd5e9d83d47055c05de376708cd"
SRC_URI[sha256sum] = "6cb60d822eeed20486a03cc23e0fc65956fbc1e85e0c1a7477f68bbd9802880d"

S = "${WORKDIR}/v4l-utils-${PV}"

inherit autotools gettext pkgconfig container-runtime-csv features_check

REQUIRED_DISTRO_FEATURES = "opengl"

EXTRA_OECONF = "--disable-libdvbv5 --disable-v4l-utils --disable-qv4l2 \
                --enable-shared --disable-qvidcap --disable-gconv --disable-bpf \
		--with-udevdir=${nonarch_base_libdir}/udev"

do_install:append:tegra() {
    rm -rf ${D}${libdir}/libv4l/plugins
}

CONTAINER_CSV_BASENAME = "libv4l"
CONTAINER_CSV_LIB_PATH = "/usr/lib/aarch64-linux-gnu/"
CONTAINER_CSV_FILES = "${libdir}/*.so* ${libdir}/libv4l/ov* ${libdir}/libv4l/*.so"

PACKAGES =+ "libv4l libv4l-dev"
RPROVIDES:${PN}-dbg += "libv4l-dbg"
FILES:libv4l += "${libdir}/libv4l*${SOLIBS} ${libdir}/libv4l/*.so ${libdir}/libv4l/plugins/*.so \
                 ${libdir}/libv4l/*-decomp"

FILES:libv4l-dev += "${includedir} ${libdir}/pkgconfig \
                     ${libdir}/libv4l*${SOLIBSDEV} ${libdir}/*.la \
                     ${libdir}/v4l*${SOLIBSDEV} ${libdir}/libv4l/*.la ${libdir}/libv4l/plugins/*.la"
RDEPENDS:libv4l = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', '${CONTAINER_CSV_PKGNAME}', '', d)}"
RDEPENDS:${PN}:remove = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', '${CONTAINER_CSV_PKGNAME}', '', d)}"
RRECOMMENDS:libv4l = "tegra-libraries-multimedia-v4l"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
