FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:tegra = " \
    file://0001-Make-plugin-directory-relative-to-ORIGIN.patch \
    file://0002-Replace-stat-fstat-calls-with-__xstat-__fxstat.patch \
    file://0003-Update-conversion-defaults-to-match-NVIDIA-sources.patch \
"

FCINHERIT = ""
FCINHERIT:tegra = "tegra_opengl_required"
inherit container-runtime-csv ${FCINHERIT}

EXTRA_OECONF:tegra = " --without-jpeg"
DEPENDS:remove:tegra = "jpeg"

do_install:append:tegra() {
    rm -rf ${D}${libdir}/libv4l/plugins
}

FILES_libv4l:remove:tegra = "${libdir}/libv4l/plugins/*.so"

TEGRA_PLUGINS ?= ""
TEGRA_PLUGINS:tegra = "tegra-libraries-multimedia-v4l"
RRECOMMENDS:libv4l += "${TEGRA_PLUGINS}"

CONTAINER_CSV_BASENAME = "libv4l"
CONTAINER_CSV_LIB_PATH = "/usr/lib/aarch64-linux-gnu/"
CONTAINER_CSV_FILES = "${libdir}/*.so* ${libdir}/libv4l/ov* ${libdir}/libv4l/*.so"
# These files aren't in nvidia host-files-for-container.d/l4t.csv and conflict with attempts
# to install v4l-utils inside the container (Invalid cross-device link)
CONTAINER_CSV_EXCLUDE_FILES = "${libdir}/libv4l2rds*"
RDEPENDS:libv4l:append:tegra = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', '${CONTAINER_CSV_PKGNAME}', '', d)}"
RDEPENDS:${PN}:remove = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', '${CONTAINER_CSV_PKGNAME}', '', d)}"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
