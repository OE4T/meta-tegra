HOMEPAGE = "https://developer.nvidia.com/embedded"

COMPATIBLE_MACHINE = "(tegra)"

L4T_DEB_SOCNAME ?= "t194"
SRC_SOC_DEBS = "nvidia-l4t-graphics-demos_${PV}_arm64.deb;subdir=l4t-graphics-demos"
PV .= "${@l4t_bsp_debian_version_suffix(d)}"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "e86fdd7516f7e173bc37c714523f4b52edde14e76c88ca43bc111d92cffb621b"

S = "${WORKDIR}/l4t-graphics-demos/usr/src/nvidia/graphics_demos"
B = "${S}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
