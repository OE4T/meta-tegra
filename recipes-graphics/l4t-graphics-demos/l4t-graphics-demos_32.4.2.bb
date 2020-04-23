require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc

SRC_PKG_PATH  := "${S}/nv_tegra"
SRC_PKG_PATH[vardepvalue] = ""
S = "${WORKDIR}/graphics_demos"
B = "${S}"

LICENSE = "MIT & Proprietary"
LIC_FILES_CHKSUM = "file://README;endline=21;md5=21846d899fa13550ba5f4090f7408e21 \
                    file://gears-cube/Makefile;endline=8;md5=a2d67caf4241d62192371ef03b193fea"

do_unpack_from_tarfile() {
    tar -C ${WORKDIR} -x --strip-components=3 --exclude "*/include/*" -f ${SRC_PKG_PATH}/graphics_demos.tbz2
}
do_unpack_from_tarfile[dirs] = "${WORKDIR}"
do_unpack_from_tarfile[cleandirs] = "${S}"
do_unpack_from_tarfile[depends] += "tegra-binaries:do_preconfigure"

addtask unpack_from_tarfile before do_configure do_populate_lic

REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "libglvnd libdrm"

inherit pkgconfig

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
	        cflags="$cflags `pkg-config --cflags libdrm`"
		ldflags="$ldflags `pkg-config --libs libdrm`"
		;;
            x11)
	        cflags="$cflags `pkg-config --cflags x11`"
                ldflags="$ldflags `pkg-config --libs x11`"
		;;
	    wayland)
	        cflags="$cflags `pkg-config --cflags xkbcommon wayland-client wayland-egl libffi`"
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
RDEPENDS_${PN} = "${PN}-egldevice ${@' '.join(['${PN}-%s' % p for p in d.getVar('PACKAGECONFIG').split()])}"
