HOMEPAGE = "https://developer.nvidia.com/embedded"

COMPATIBLE_MACHINE = "(tegra)"

L4T_DEB_SOCNAME = "t194"
SRC_SOC_DEBS = "nvidia-l4t-graphics-demos_${PV}_arm64.deb;subdir=l4t-graphics-demos"
PV .= "${@l4t_bsp_debian_version_suffix(d)}"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "8a1114776fcbe25c9acf1e5690e322b5123101b60eb9c3a09c592146068768fd"

S = "${WORKDIR}/l4t-graphics-demos/usr/src/nvidia/graphics_demos"
B = "${S}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
