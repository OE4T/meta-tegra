HOMEPAGE = "https://developer.nvidia.com/embedded"

COMPATIBLE_MACHINE = "(tegra)"

# The sources for t186 and t210 are identical, so just pick one
L4T_DEB_SOCNAME = "t186"
SRC_SOC_DEBS = "nvidia-l4t-graphics-demos_${PV}-20210519111140:arm64.deb;subdir=l4t-graphics-demos"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "61a9462c499c424f6c37cbe3f09d8ef4a6611d463b0824649507c1318b6c6ad6"

S = "${WORKDIR}/l4t-graphics-demos/usr/src/nvidia/graphics_demos"
B = "${S}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
