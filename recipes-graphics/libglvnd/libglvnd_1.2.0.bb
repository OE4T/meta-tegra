DESCRIPTION = "libglvnd is a vendor-neutral dispatch layer for arbitrating OpenGL API calls between multiple vendors."
HOMEPAGE = "https://github.com/NVIDIA/libglvnd"
LICENSE = "MIT & BSD & GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://README.md;beginline=310;md5=f98ec0fbe6c0d2fbbd0298b5d9e664d3"

SRC_URI = "https://github.com/NVIDIA/${BPN}/releases/download/v${PV}/${BP}.tar.gz \
           file://0001-Add-EGL-and-GLES2-extensions-for-Tegra.patch \
           file://0002-Make-Wayland-support-configurable.patch \
           "
SRC_URI[md5sum] = "59068b27ff62bf2ad31a028673ab58da"
SRC_URI[sha256sum] = "2dacbcfa47b7ffb722cbddc0a4f1bc3ecd71d2d7bb461bceb8e396dc6b81dc6d"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"
PROVIDES += "virtual/egl virtual/libgl virtual/libgles1 virtual/libgles2"

DEPENDS = "tegra-mmapi-glheaders"

inherit autotools pkgconfig

PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
"

PACKAGECONFIG[x11] = "--enable-x11 --enable-glx,--disable-x11 --disable-glx,libx11 libxext xorgproto"
PACKAGECONFIG[wayland] = "--enable-wayland,--disable-wayland,wayland"
