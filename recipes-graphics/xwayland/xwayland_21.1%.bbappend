FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
SRC_URI_append_tegra = "\
    file://0001-xwayland-Move-dmabuf-interface-to-common-glamor-code.patch \
    file://0002-xwayland-move-formats-and-modifiers-functions-to-com.patch \
    file://0003-xwayland-Add-check_flip-glamor-backend-function.patch \
    file://0004-xwayland-implement-pixmap_from_buffers-for-the-eglst.patch \
"

EXTRA_OEMESON_append_tegra = " -Dglx=false -Dxwayland_eglstream=true"
DEPENDS_append_tegra = " egl-wayland libxshmfence mesa"
PACKAGE_ARCH_tegra = "${TEGRA_PKGARCH}"

RDEPENDS_${PN}_append_tegra = " egl-wayland xkbcomp"
