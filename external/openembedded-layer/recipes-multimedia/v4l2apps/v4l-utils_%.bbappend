FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:tegra = " \
    file://0001-Make-plugin-directory-relative-to-ORIGIN.patch \
    file://0002-Replace-stat-fstat-calls-with-__xstat-__fxstat.patch \
"

EXTRA_OECONF:tegra = " --without-jpeg"
DEPENDS:remove:tegra = "jpeg"

TEGRA_PLUGINS ?= ""
TEGRA_PLUGINS:tegra = "tegra-libraries-libv4l-plugins"
RRECOMMENDS:libv4l += "${TEGRA_PLUGINS}"

inherit container-runtime-csv
CONTAINER_CSV_BASENAME = "libv4l"
CONTAINER_CSV_FILES = "${libdir}/*.so* ${libdir}/libv4l/ov* ${libdir}/libv4l/*.so ${libdir}/libv4l/plugins/*.so"
# These files aren't in nvidia host-files-for-container.d/l4t.csv and conflict with attempts
# to install v4l-utils inside the container (Invalid cross-device link)
CONTAINER_CSV_EXCLUDE_FILES = "${libdir}/libv4l2rds*"
RDEPENDS:libv4l:append:tegra = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', '${CONTAINER_CSV_PKGNAME}', '', d)}"
RDEPENDS:${PN}:remove = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', '${CONTAINER_CSV_PKGNAME}', '', d)}"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
