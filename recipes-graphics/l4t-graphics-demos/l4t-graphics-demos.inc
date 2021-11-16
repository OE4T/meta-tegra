HOMEPAGE = "https://developer.nvidia.com/embedded"

COMPATIBLE_MACHINE = "(tegra)"

# The sources for t186 and t210 are identical, so just pick one
L4T_DEB_SOCNAME = "t186"
L4T_BSP_DEB_VERSION = "${L4T_BSP_DEB_DEFAULT_VERSION_T186}"
SRC_SOC_DEBS = "nvidia-l4t-graphics-demos_${PV}_arm64.deb;subdir=l4t-graphics-demos"
PV .= "${@l4t_bsp_debian_version_suffix(d)}"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "3dd87898d09a6fd417bfe8f23a4f559594cf062ae64516f5d82eb7c10adb19a8"

S = "${WORKDIR}/l4t-graphics-demos/usr/src/nvidia/graphics_demos"
B = "${S}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
