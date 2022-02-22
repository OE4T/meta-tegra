DESCRIPTION = "libglvnd is a vendor-neutral dispatch layer for arbitrating OpenGL API calls between multiple vendors."
HOMEPAGE = "https://gitlab.freedesktop.org/glvnd/libglvnd"
LICENSE = "MIT & BSD-1-Clause & BSD-3-Clause & GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://README.md;beginline=323;md5=7ac5f0111f648b92fe5427efeb08e8c4"

SRC_REPO = "gitlab.freedesktop.org/glvnd/libglvnd.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"

# v1.4.0 tag
SRCREV = "8f3c5b17a21e2222ab3e5fd38870b915815aca49"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"
PROVIDES += "virtual/egl virtual/libgl virtual/libgles1 virtual/libgles2 virtual/libgles3"

DEPENDS = "l4t-nvidia-glheaders"

inherit meson pkgconfig features_check python3native

S = "${WORKDIR}/git"

PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"

PACKAGECONFIG[x11] = "-Dx11=enabled -Dglx=enabled,-Dx11=disabled -Dglx=disabled,libx11 libxext xorgproto,tegra-libraries-glxcore"

RPROVIDES:${PN} += "libegl libgl libgles1 libgles2"
RPROVIDES:${PN}-dev += "libegl-dev libgl-dev libgles1-dev libgles2-dev libgles3-dev"
RCONFLICTS:${PN} = "libegl libgl ligbles1 libgles2"
RCONFLICTS:${PN}-dev += "libegl-dev libgl-dev libgles1-dev libgles2-dev libgles3-dev"
RREPLACES:${PN} = "libegl libgl libgles1 ligbles2"
RREPLACESS_${PN}-dev += "libegl-dev libgl-dev libgles1-dev libgles2-dev libgles3-dev"

RDEPENDS:${PN}:append:tegra = " tegra-libraries-eglcore tegra-libraries-glescore"
RDEPENDS:${PN}-dev:append:tegra = " l4t-nvidia-glheaders-dev"
RRECOMMENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'egl-wayland', '', d)}"
