DESCRIPTION = "libglvnd is a vendor-neutral dispatch layer for arbitrating OpenGL API calls between multiple vendors."
HOMEPAGE = "https://gitlab.freedesktop.org/glvnd/libglvnd"
LICENSE = "MIT & BSD & GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://README.md;beginline=323;md5=f98ec0fbe6c0d2fbbd0298b5d9e664d3"

SRC_REPO = "gitlab.freedesktop.org/glvnd/libglvnd.git;protocol=https"
SRC_URI = "git://${SRC_REPO} \
    file://0001-Add-EGL-and-GLES2-extensions-for-Tegra.patch \
    file://0002-Make-Wayland-support-configurable.patch \
    "
SRCREV = "1c32de07074fee8edcb274899948b6551081ed54"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"
PROVIDES += "virtual/egl virtual/libgl virtual/libgles1 virtual/libgles2"

DEPENDS = "l4t-nvidia-glheaders"

inherit autotools pkgconfig features_check python3native

S = "${WORKDIR}/git"

PACKAGE_ARCH_tegra = "${TEGRA_PKGARCH}"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
"

PACKAGECONFIG[x11] = "--enable-x11 --enable-glx,--disable-x11 --disable-glx,libx11 libxext xorgproto"
PACKAGECONFIG[wayland] = "--enable-wayland,--disable-wayland,wayland"

do_install_append() {

    ## no-X11 hack included from mesa:
    #because we cannot rely on the fact that all apps will use pkgconfig,
    #make eglplatform.h independent of EGL_NO_X11
    # If necessary, fix up pkgconfig anyhow, for the benefit of SDK users
    if ${@bb.utils.contains('PACKAGECONFIG', 'x11', 'false', 'true', d)}; then
        sed -i -e 's!^#elif \(defined(__unix__) && defined(EGL_NO_X11)\)$!#elif  1 /* \1 */!' ${D}${includedir}/EGL/eglplatform.h
        sed -i -e 's|^Cflags: .*|& -DEGL_NO_X11|g' ${D}${libdir}/pkgconfig/libglvnd.pc ${D}${libdir}/pkgconfig/egl.pc
    fi
}

RPROVIDES_${PN} += "libegl libgl libgles1 libgles2"
RPROVIDES_${PN}-dev += "libegl-dev libgl-dev libgles1-dev libgles2-dev"
RCONFLICTS_${PN} = "libegl libgl ligbles1 libgles2"
RCONFLICTS_${PN}-dev += "libegl-dev libgl-dev libgles1-dev libgles2-dev"
RREPLACES_${PN} = "libegl libgl libgles1 ligbles2"
RREPLACESS_${PN}-dev += "libegl-dev libgl-dev libgles1-dev libgles2-dev"

RRECOMMENDS_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'egl-wayland', '', d)}"
