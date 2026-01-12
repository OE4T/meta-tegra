HOMEPAGE = "https://developer.nvidia.com/embedded"

COMPATIBLE_MACHINE = "(tegra)"

SRC_SOC_DEBS = "${@l4t_deb_pkgname(d, 'graphics-demos')};subdir=l4t-graphics-demos"
PV .= "${@l4t_bsp_debian_version_suffix(d, pkgname='nvidia-l4t-graphics-demos')}"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "3ed19d2cc0c143d7f30250f907ac3dc301b65c623519b58fb00421b039c17fa9"

S = "${UNPACKDIR}/l4t-graphics-demos/usr/src/nvidia/graphics_demos"
B = "${S}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
