DESCRIPTION = "libglvnd is a vendor-neutral dispatch layer for arbitrating OpenGL API calls between multiple vendors."
HOMEPAGE = "https://github.com/NVIDIA/libglvnd"
LICENSE = "MIT & BSD"
LIC_FILES_CHKSUM = "file://README.md;beginline=309;md5=f98ec0fbe6c0d2fbbd0298b5d9e664d3"

SRC_URI = "git://github.com/NVIDIA/libglvnd"
# tag v1.1.1
SRCREV = "bc9990b7bede44899627354d0c356f48950f4ad1"

COMPATIBLE_MACHINE = "(tegra)"
COMPATIBLE_MACHINE_tegra124 = "(-)"

REQUIRED_DISTRO_FEATURES = "x11 opengl"

DEPENDS = "libx11 libxext xorgproto"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
