DESCRIPTION = "L4T graphics demo programs"
require l4t-graphics-demos.inc

LICENSE = "MIT & Proprietary"
LIC_FILES_CHKSUM = "file://README;endline=21;md5=17bf753e98379a9888c5bd3f81da8d44 \
                    file://gears-cube/Makefile;endline=8;md5=a2d67caf4241d62192371ef03b193fea"

SRC_URI += "\
    file://0001-Fix-stdbool.h-inclusion-check.patch \
    file://0002-weston-dmabuf-formats-cross-build-fixes.patch \
"

REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "libglvnd libdrm libdrm-nvdc"

inherit pkgconfig features_check

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11 wayland', d)}"
PACKAGECONFIG[x11] = ",,libx11 virtual/libgbm"
PACKAGECONFIG[wayland] = ",,libxkbcommon wayland wayland-native weston libffi virtual/libgbm tegra-drm-headers tegra-mmapi tegra-libraries-multimedia-utils"

CONFIGURESTAMPFILE = "${WORKDIR}/configure.sstate"

PARALLEL_MAKE = ""

do_configure() {
    if [ -n "${CONFIGURESTAMPFILE}" ]; then
        if [ -e "${CONFIGURESTAMPFILE}" ]; then
            if [ "`cat ${CONFIGURESTAMPFILE}`" != "${BB_TASKHASH}" ]; then
                rm -rf ${S}/x11 ${S}/wayland ${S}/egldevice
            fi
            echo "${BB_TASKHASH}" > "${CONFIGURESTAMPFILE}"
        fi
    fi
}

do_compile() {
    for winsys in egldevice ${PACKAGECONFIG}; do
        cflags="-isystem${S}/include -I${S}/nvgldemo -I${S}/nvtexfont -I${S}/gears-lib -I=${includedir}/libdrm/nvidia"
	ldflags="-ldl"
	extra=
        case $winsys in
	    egldevice)
	        cflags="$cflags -DEGL_NO_X11 `pkg-config --cflags libdrm`"
		ldflags="$ldflags `pkg-config --libs libdrm`"
		;;
            x11)
	        cflags="$cflags -DX11 `pkg-config --cflags x11`"
                ldflags="$ldflags `pkg-config --libs x11`"
		;;
	    wayland)
	        cflags="$cflags -DEGL_NO_X11 -DWAYLAND `pkg-config --cflags xkbcommon wayland-client wayland-egl libffi libdrm`"
		ldflags="$ldflags -lnvbufsurface `pkg-config --libs xkbcommon wayland-client wayland-egl libffi`"
		extra=weston-dmabuf-formats
		;;
	esac
	for demo in bubble ctree eglstreamcube gears-lib gears-basic gears-cube $extra; do
            oe_runmake -C $demo $mflags NV_WINSYS=$winsys CC="${CC}" CXX="${CXX}" LD="${CC}" AR="${AR}" NV_PLATFORM_LDFLAGS="${LDFLAGS}" NV_PLATFORM_OPT="${CFLAGS}" NV_PLATFORM_SDK_LIB="" NV_PLATFORM_SDK_INC="$cflags" NV_PLATFORM_WINSYS_LIBS="$ldflags"
        done
    done
}

do_install() {
    install -d ${D}${bindir}/${BPN}
    for winsys in egldevice ${PACKAGECONFIG}; do
        install -d ${D}${bindir}/${BPN}/$winsys
        for demo in bubble ctree eglstreamcube; do
	    install -m 0755 ${B}/$demo/$winsys/$demo ${D}${bindir}/${BPN}/$winsys/
        done
	install -m 0755 ${B}/gears-basic/$winsys/gears ${D}${bindir}/${BPN}/$winsys/
	install -m 0755 ${B}/gears-cube/$winsys/gearscube ${D}${bindir}/${BPN}/$winsys/
	if [ "$winsys" = "wayland" ]; then
	    install -m 0755 ${B}/weston-dmabuf-formats/$winsys/weston-dmabuf-formats ${D}${bindir}/${BPN}/$winsys/
	fi
    done
}

PACKAGES =+ "${PN}-x11 ${PN}-wayland ${PN}-egldevice"
FILES:${PN}-x11 = "${bindir}/${BPN}/x11"
FILES:${PN}-wayland = "${bindir}/${BPN}/wayland"
FILES:${PN}-egldevice = "${bindir}/${BPN}/egldevice"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN}-egldevice = "libdrm"
RDEPENDS:${PN} = "${PN}-egldevice ${@' '.join(['${PN}-%s' % p for p in d.getVar('PACKAGECONFIG').split()])}"
