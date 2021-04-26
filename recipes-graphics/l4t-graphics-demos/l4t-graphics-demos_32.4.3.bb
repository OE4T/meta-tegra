require l4t-graphics-demos.inc

LICENSE = "MIT & Proprietary"
LIC_FILES_CHKSUM = "file://README;endline=21;md5=9344f9b3e882bebae9422f515711d756 \
                    file://gears-cube/Makefile;endline=8;md5=a2d67caf4241d62192371ef03b193fea"

REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "libglvnd libdrm"

inherit pkgconfig features_check

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11 wayland', d)}"
PACKAGECONFIG[x11] = ",,libx11"
PACKAGECONFIG[wayland] = ",,libxkbcommon wayland libffi"

CONFIGURESTAMPFILE = "${WORKDIR}/configure.sstate"

do_configure() {
    if [ -n "${CONFIGURESTAMPFILE}" ]; then
        if [ -e "${CONFIGURESTAMPFILE}" -a "`cat ${CONFIGURESTAMPFILE}`" != "${BB_TASKHASH}" ]; then
            rm -rf ${S}/x11 ${S}/wayland ${S}/egldevice
        fi
        echo "${BB_TASKHASH}" > "${CONFIGURESTAMPFILE}"
    fi
}

do_compile() {
    for winsys in egldevice ${PACKAGECONFIG}; do
        cflags="-I${S}/nvgldemo -I${S}/nvtexfont -I${S}/gears-lib"
	ldflags="-ldl"
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
	        cflags="$cflags -DEGL_NO_X11 -DWAYLAND `pkg-config --cflags xkbcommon wayland-client wayland-egl libffi`"
		ldflags="$ldflags `pkg-config --libs xkbcommon wayland-client wayland-egl libffi`"
		;;
	esac
	for demo in bubble ctree eglstreamcube gears-lib gears-basic gears-cube; do
            oe_runmake -C $demo NV_WINSYS=$winsys CC="${CC}" CXX="${CXX}" LD="${CC}" AR="${AR}" NV_PLATFORM_LDFLAGS="${LDFLAGS}" NV_PLATFORM_OPT="${CFLAGS}" NV_PLATFORM_SDK_LIB="" NV_PLATFORM_SDK_INC="$cflags" NV_PLATFORM_WINSYS_LIBS="$ldflags"
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
    done
}

PACKAGES =+ "${PN}-x11 ${PN}-wayland ${PN}-egldevice"
FILES_${PN}-x11 = "${bindir}/${BPN}/x11"
FILES_${PN}-wayland = "${bindir}/${BPN}/wayland"
FILES_${PN}-egldevice = "${bindir}/${BPN}/egldevice"
ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN}-egldevice = "libdrm"
RDEPENDS_${PN} = "${PN}-egldevice ${@' '.join(['${PN}-%s' % p for p in d.getVar('PACKAGECONFIG').split()])}"
