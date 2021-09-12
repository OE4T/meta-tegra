HOMEPAGE = "https://developer.nvidia.com/embedded"

COMPATIBLE_MACHINE = "(tegra)"

# The sources for t186 and t210 are identical, so just pick one
L4T_DEB_SOCNAME = "t186"
SRC_SOC_DEBS = "nvidia-l4t-graphics-demos_${PV}-20210726122859_arm64.deb;subdir=l4t-graphics-demos"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "ed0316d19fc551419790e5d2e2c0522246d7b320538dab3ca5695c0d915294cb"

S = "${WORKDIR}/l4t-graphics-demos/usr/src/nvidia/graphics_demos"
B = "${S}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
