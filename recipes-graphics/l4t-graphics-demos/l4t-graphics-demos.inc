HOMEPAGE = "https://developer.nvidia.com/embedded"

COMPATIBLE_MACHINE = "(tegra)"

# The sources for t186 and t210 are identical, so just pick one
L4T_DEB_SOCNAME = "t186"
L4T_BSP_DEB_VERSION = "${L4T_BSP_DEB_DEFAULT_VERSION_T186}"
SRC_SOC_DEBS = "nvidia-l4t-graphics-demos_${PV}_arm64.deb;subdir=l4t-graphics-demos"
PV .= "${@l4t_bsp_debian_version_suffix(d)}"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "93fa437637b9be2c68275323b25e1507f902318970b72b8f0a1f28c4c77e008c"

S = "${WORKDIR}/l4t-graphics-demos/usr/src/nvidia/graphics_demos"
B = "${S}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
