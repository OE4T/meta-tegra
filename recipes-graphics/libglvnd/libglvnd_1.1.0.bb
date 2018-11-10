DESCRIPTION = "libglvnd is a vendor-neutral dispatch layer for arbitrating OpenGL API calls between multiple vendors."
HOMEPAGE = "https://github.com/NVIDIA/libglvnd"
LICENSE = "MIT & BSD"
LIC_FILES_CHKSUM = "file://README.md;beginline=309;md5=f98ec0fbe6c0d2fbbd0298b5d9e664d3"

SRC_URI = "https://github.com/NVIDIA/libglvnd/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[md5sum] = "362be291eb0c112b66110bdef6545c1b"
SRC_URI[sha256sum] = "f5a74598e769d55d652c464cb6507437dac5c2d513f16c6ddf3a1bec655a1824"

SRC_URI += "\
    file://0001-build-Find-Python-the-Autotools-way.patch \
    file://0002-EGL-Fix-typo-in-the-makefile.patch \
    file://0003-GLdispatch-Keep-the-mutex-when-calling-the-vendor-s-.patch \
    file://0004-Remove-a-couple-of-duplicate-typedefs.patch \
"

REQUIRED_DISTRO_FEATURES = "x11 opengl"

DEPENDS = "libx11 libxext xorgproto"

inherit autotools pkgconfig

