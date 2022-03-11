FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:tegra = " file://0001-Drop-DRM-version-check-in-meson.build.patch \
                         file://0002-gl-renderer-Add-EGL-client-support-for-EGLStream-fra.patch \
                         file://0003-compositor-Process-stream-attach-requests-with-wl_eg.patch \
"

DEPENDS:append:tegra = " egl-wayland"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"

RDEPENDS:${PN}:append:tegra = " egl-wayland egl-gbm tegra-udrm-probeconf"
