DESCRIPTION = "libglvnd is a vendor-neutral dispatch layer for arbitrating OpenGL API calls between multiple vendors."
HOMEPAGE = "https://github.com/NVIDIA/libglvnd"
LICENSE = "MIT & BSD"
LIC_FILES_CHKSUM = "file://README.md;beginline=309;md5=f98ec0fbe6c0d2fbbd0298b5d9e664d3"

SRC_URI = "https://github.com/NVIDIA/libglvnd/archive/v${PV}.tar.gz;downloadfilename=${BP}.tar.gz"
SRC_URI[md5sum] = "390f7934a22a17c9542621b727fc5908"
SRC_URI[sha256sum] = "baca5e1a78b96a112650cdc597be3f856d4754eb73a7bf3f6629e78a7e9f2b5a"

COMPATIBLE_MACHINE = "(tegra)"
COMPATIBLE_MACHINE_tegra124 = "(-)"

REQUIRED_DISTRO_FEATURES = "x11 opengl"

DEPENDS = "libx11 libxext xorgproto"

inherit autotools pkgconfig

PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
