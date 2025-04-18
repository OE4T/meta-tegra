DESCRIPTION = "libglvnd is a vendor-neutral dispatch layer for arbitrating OpenGL API calls between multiple vendors."
HOMEPAGE = "https://gitlab.freedesktop.org/glvnd/libglvnd"
LICENSE = "MIT & BSD-1-Clause & BSD-3-Clause & GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://README.md;beginline=323;md5=7ac5f0111f648b92fe5427efeb08e8c4"

SRC_REPO = "gitlab.freedesktop.org/glvnd/libglvnd.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"

# v1.7.0 tag
SRCREV = "faa23f21fc677af5792825dc30cb1ccef4bf33a6"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"
PROVIDES += "libglvnd virtual/egl virtual/libgl virtual/libgles1 virtual/libgles2 virtual/libgles3"

DEPENDS = "l4t-nvidia-glheaders"

inherit meson pkgconfig features_check python3native

S = "${WORKDIR}/git"

PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"

PACKAGECONFIG[x11] = "-Dx11=enabled -Dglx=enabled,-Dx11=disabled -Dglx=disabled,libx11 libxext xorgproto,tegra-libraries-glxcore"

RPROVIDES:${PN} += "libglvnd libegl libgl libgles1 libgles2"
RPROVIDES:${PN}-dev += "libglvnd-dev libegl-dev libgl-dev libgles1-dev libgles2-dev libgles3-dev"
RCONFLICTS:${PN} = "libegl libgl ligbles1 libgles2"
RCONFLICTS:${PN}-dev += "libegl-dev libgl-dev libgles1-dev libgles2-dev libgles3-dev"
RREPLACES:${PN} = "libegl libgl libgles1 ligbles2"
RREPLACES:${PN}-dev += "libegl-dev libgl-dev libgles1-dev libgles2-dev libgles3-dev"
RPROVIDES:${PN}-dbg += "libglvnd-dbg"

RDEPENDS:${PN}:append:tegra = " tegra-libraries-eglcore tegra-libraries-glescore"
RDEPENDS:${PN}-dev:append:tegra = " l4t-nvidia-glheaders-dev"
RRECOMMENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'egl-wayland', '', d)}"
