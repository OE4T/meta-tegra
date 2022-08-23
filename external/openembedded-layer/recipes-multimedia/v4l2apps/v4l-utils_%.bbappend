FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:tegra = " \
    file://0001-Make-plugin-directory-relative-to-ORIGIN.patch \
    file://0002-Replace-stat-fstat-calls-with-__xstat-__fxstat.patch \
    file://0003-Update-conversion-defaults-to-match-NVIDIA-sources.patch \
"

FCINHERIT = ""
FCINHERIT:tegra = "tegra_opengl_required"
inherit ${FCINHERIT}

EXTRA_OECONF:tegra = " --without-jpeg"
DEPENDS:remove:tegra = "jpeg"

do_install:append:tegra() {
    rm -rf ${D}${libdir}/libv4l/plugins
}

FILES_libv4l:remove:tegra = "${libdir}/libv4l/plugins/*.so"

TEGRA_PLUGINS ?= ""
TEGRA_PLUGINS:tegra = "tegra-libraries-multimedia-v4l"
RRECOMMENDS:libv4l += "${TEGRA_PLUGINS}"

PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
