DESCRIPTION = "libglvnd is a vendor-neutral dispatch layer for arbitrating OpenGL API calls between multiple vendors."
HOMEPAGE = "https://github.com/NVIDIA/libglvnd"
LICENSE = "MIT & BSD & GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://README.md;beginline=310;md5=f98ec0fbe6c0d2fbbd0298b5d9e664d3"

SRC_URI = "https://github.com/NVIDIA/${BPN}/releases/download/v${PV}/${BP}.tar.gz \
    file://0001-include-install-GL-headers-when-GL-is-enabled.patch \
    file://0002-Add-a-configure-option-to-disable-glesv1-or-glesv2.patch \
    file://0003-egl-Sync-with-Khronos.patch \
    file://0004-Update-GL-gl.h-to-match-Mesa.patch \
    file://0005-Add-EGL-and-GLES2-extensions-for-Tegra.patch \
    file://0006-Make-Wayland-support-configurable.patch \
    file://0007-Define-EGL_NO_X11-if-X11-support-is-disabled.patch \
    "
SRC_URI[md5sum] = "59068b27ff62bf2ad31a028673ab58da"
SRC_URI[sha256sum] = "2dacbcfa47b7ffb722cbddc0a4f1bc3ecd71d2d7bb461bceb8e396dc6b81dc6d"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"
PROVIDES += "virtual/egl virtual/libgl virtual/libgles1 virtual/libgles2"

DEPENDS = "l4t-nvidia-glheaders"

inherit autotools pkgconfig

PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"

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
