HOMEPAGE = "https://developer.nvidia.com/embedded"

COMPATIBLE_MACHINE = "(tegra)"

# The sources for t186 and t210 are identical, so just pick one
L4T_DEB_SOCNAME = "t186"
L4T_DEB_GROUP = "nvidia-l4t-graphics-demos"
SRC_SOC_DEBS = "nvidia-l4t-graphics-demos_${PV}-20201016123640_arm64.deb;subdir=l4t-graphics-demos"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "6e1a18cf34cc033ac566b2d59f25c09390cd59e6f75c9752fd456fe9b67f7cff"

S = "${WORKDIR}/l4t-graphics-demos/usr/src/nvidia/graphics_demos"
B = "${S}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
