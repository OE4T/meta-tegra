HOMEPAGE = "https://developer.nvidia.com/embedded"

COMPATIBLE_MACHINE = "(tegra)"

SRC_SOC_DEBS = "${@l4t_deb_pkgname(d, 'graphics-demos')};subdir=l4t-graphics-demos"
PV .= "${@l4t_bsp_debian_version_suffix(d, pkgname='nvidia-l4t-graphics-demos')}"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "fc950d056f07127cd800c9a191b9e45df437464277bdb2b132b23f25c8ccff70"

S = "${UNPACKDIR}/l4t-graphics-demos/usr/src/nvidia/graphics_demos"
B = "${S}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
