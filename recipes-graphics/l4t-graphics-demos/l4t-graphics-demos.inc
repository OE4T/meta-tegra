HOMEPAGE = "https://developer.nvidia.com/embedded"

COMPATIBLE_MACHINE = "(tegra)"

# The sources for t186 and t210 are identical, so just pick one
L4T_DEB_SOCNAME = "t186"
L4T_DEB_GROUP = "nvidia-l4t-graphics-demos"
SRC_SOC_DEBS = "nvidia-l4t-graphics-demos_${PV}-20200625213407_arm64.deb;subdir=l4t-graphics-demos"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "b4add3d2a191b056d31527b9a4974446134fcf065f6254286f43ad1bd3b33557"

S = "${WORKDIR}/l4t-graphics-demos/usr/src/nvidia/graphics_demos"
B = "${S}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
