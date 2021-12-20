DESCRIPTION = "libglvnd is a vendor-neutral dispatch layer for arbitrating OpenGL API calls between multiple vendors."
HOMEPAGE = "https://gitlab.freedesktop.org/glvnd/libglvnd"
LICENSE = "MIT & BSD & GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://README.md;beginline=323;md5=f98ec0fbe6c0d2fbbd0298b5d9e664d3"

SRC_REPO = "gitlab.freedesktop.org/glvnd/libglvnd.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH} \
    file://0001-Add-EGL-and-GLES2-extensions-for-Tegra.patch \
    file://0002-Make-Wayland-support-configurable.patch \
    file://0003-Fix-tests-meson.build-syntax-error.patch \
    "
SRCREV = "dc084876b411366c3785b7584c216356169bb45e"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"
PROVIDES += "virtual/egl virtual/libgl virtual/libgles1 virtual/libgles2 virtual/libgles3"

DEPENDS = "l4t-nvidia-glheaders"

inherit meson pkgconfig features_check python3native

S = "${WORKDIR}/git"

PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
"

PACKAGECONFIG[x11] = "-Dx11=enabled -Dglx=enabled,-Dx11=disabled -Dglx=disabled,libx11 libxext xorgproto,tegra-libraries-glxcore"
PACKAGECONFIG[wayland] = "-Dwayland=enabled,-Dwayland=disabled,wayland"

do_install:append() {

    ## no-X11 hack included from mesa:
    #because we cannot rely on the fact that all apps will use pkgconfig,
    #make eglplatform.h independent of EGL_NO_X11
    # If necessary, fix up pkgconfig anyhow, for the benefit of SDK users
    if ${@bb.utils.contains('PACKAGECONFIG', 'x11', 'false', 'true', d)}; then
        sed -i -e 's!^#elif \(defined(__unix__) && defined(EGL_NO_X11)\)$!#elif  1 /* \1 */!' ${D}${includedir}/EGL/eglplatform.h
        sed -i -e 's|^Cflags: .*|& -DEGL_NO_X11|g' ${D}${libdir}/pkgconfig/libglvnd.pc ${D}${libdir}/pkgconfig/egl.pc
    fi
}

RPROVIDES:${PN} += "libegl libgl libgles1 libgles2"
RPROVIDES:${PN}-dev += "libegl-dev libgl-dev libgles1-dev libgles2-dev libgles3-dev"
RCONFLICTS:${PN} = "libegl libgl ligbles1 libgles2"
RCONFLICTS:${PN}-dev += "libegl-dev libgl-dev libgles1-dev libgles2-dev libgles3-dev"
RREPLACES:${PN} = "libegl libgl libgles1 ligbles2"
RREPLACESS_${PN}-dev += "libegl-dev libgl-dev libgles1-dev libgles2-dev libgles3-dev"

RDEPENDS:${PN}:append:tegra = " tegra-libraries-eglcore tegra-libraries-glescore"
RRECOMMENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'egl-wayland', '', d)}"
