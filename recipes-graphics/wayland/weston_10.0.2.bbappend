FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:tegra = " file://0001-Drop-DRM-version-check-in-meson.build.patch \
                         file://0002-gl-renderer-Add-EGL-client-support-for-EGLStream-fra.patch \
                         file://0003-compositor-Process-stream-attach-requests-with-wl_eg.patch \
                         file://0004-Fix-dmabuf-explicit-synchronization-for-tegra.patch \
"

DEPENDS:append:tegra = " egl-wayland"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"

RDEPENDS:${PN}:append:tegra = " egl-wayland egl-gbm"

# wlshell is used by nvidia gstreamer plugins
EXTRA_OEMESON:append:tegra = " -Ddeprecated-wl-shell=true"

RRECOMMENDS:${PN}:append:tegra = " kernel-module-tegra-udrm kernel-module-nvidia-drm"
RRECOMMENDS:${PN}:append:tegra234 = " nvidia-drm-loadconf"
