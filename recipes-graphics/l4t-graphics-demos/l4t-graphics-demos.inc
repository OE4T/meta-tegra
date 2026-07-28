HOMEPAGE = "https://developer.nvidia.com/embedded"

COMPATIBLE_MACHINE = "(tegra)"

SRC_SOC_DEBS = "${@l4t_deb_pkgname(d, 'graphics-demos')};subdir=l4t-graphics-demos"
PV .= "${@l4t_bsp_debian_version_suffix(d, pkgname='nvidia-l4t-graphics-demos')}"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "13e903f5c9f5e0fc5030b158fd2cd01a9dd381a0ec79aa8a5ec919d7972f5127"

S = "${UNPACKDIR}/l4t-graphics-demos/usr/src/nvidia/graphics_demos"
B = "${S}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
