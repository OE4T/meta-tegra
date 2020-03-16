FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append_tegra = " file://0001-Make-plugin-directory-relative-to-ORIGIN.patch"

EXTRA_OECONF_tegra = " --without-jpeg"
DEPENDS_remove_tegra = "jpeg"

TEGRA_PLUGINS ?= ""
TEGRA_PLUGINS_tegra = "tegra-libraries-libv4l-plugins"
RRECOMMENDS_libv4l += "${TEGRA_PLUGINS}"

inherit container-runtime-csv
CONTAINER_CSV_BASENAME = "libv4l"
CONTAINER_CSV_FILES = "${libdir}/*.so* ${libdir}/libv4l/ov* ${libdir}/libv4l/*.so ${libdir}/libv4l/plugins/*.so"
RDEPENDS_libv4l_append_tegra = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', '${CONTAINER_CSV_PKGNAME}', '', d)}"
RDEPENDS_${PN}_remove = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', '${CONTAINER_CSV_PKGNAME}', '', d)}"
PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
