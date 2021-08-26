FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append_tegra = " file://0001-Make-plugin-directory-relative-to-ORIGIN.patch"
SRC_URI_append_tegra = " file://0002-Update-conversion-defaults-to-match-NVIDIA-sources.patch"

EXTRA_OECONF_tegra = " --without-jpeg"
DEPENDS_remove_tegra = "jpeg"

do_install_append_tegra() {
    rm -rf ${D}${libdir}/libv4l/plugins
}

FILES_libv4l_remove_tegra = "${libdir}/libv4l/plugins/*.so"

TEGRA_PLUGINS ?= ""
TEGRA_PLUGINS_tegra = "tegra-libraries-libv4l-plugins"
RRECOMMENDS_libv4l += "${TEGRA_PLUGINS}"

inherit container-runtime-csv
CONTAINER_CSV_BASENAME = "libv4l"
CONTAINER_CSV_FILES = "${libdir}/*.so* ${libdir}/libv4l/ov* ${libdir}/libv4l/*.so"
# These files aren't in nvidia host-files-for-container.d/l4t.csv and conflict with attempts
# to install v4l-utils inside the container (Invalid cross-device link)
CONTAINER_CSV_EXCLUDE_FILES = "${libdir}/libv4l2rds*"
RDEPENDS_libv4l_append_tegra = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', '${CONTAINER_CSV_PKGNAME}', '', d)}"
RDEPENDS_${PN}_remove = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', '${CONTAINER_CSV_PKGNAME}', '', d)}"
PACKAGE_ARCH_tegra = "${TEGRA_PKGARCH}"
