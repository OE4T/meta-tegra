HOMEPAGE = "https://developer.nvidia.com/embedded"

COMPATIBLE_MACHINE = "(tegra)"

# The sources for t186 and t210 are identical, so just pick one
L4T_DEB_SOCNAME = "t186"
SRC_SOC_DEBS = "nvidia-l4t-graphics-demos_${PV}-20210115151051_arm64.deb;subdir=l4t-graphics-demos"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "05f03c2a1667d4d5fc86002147309e6d1df9cddd7138ba44bb207142ec5eb094"

S = "${WORKDIR}/l4t-graphics-demos/usr/src/nvidia/graphics_demos"
B = "${S}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
