SUMMARY = "NVIDIA CUDA Deep Neural Network library"
HOMEPAGE = "https://developer.nvidia.com/cudnn"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v9.h;endline=48;md5=11e690c2afb545fc389ff0637693b996"

inherit l4t_deb_pkgfeed

L4T_DEB_GROUP = "cudnn"

DEPENDS = "libcublas zlib"

SRC_COMMON_DEBS = "\
    libcudnn9-cuda-13_${PV}_arm64.deb;name=lib;subdir=cudnn \
    libcudnn9-static-cuda-13_${PV}_arm64.deb;name=staticlib;subdir=cudnn \
    libcudnn9-dev-cuda-13_${PV}_arm64.deb;name=dev;subdir=cudnn \
    libcudnn9-headers-cuda-13_${PV}_arm64.deb;name=hdr;subdir=cudnn \
"

SRC_URI[lib.sha256sum] = "04a793a6e206b153578851f25d7dcb923a17d19e166fb1acb4e4b2f47e7a6516"
SRC_URI[staticlib.sha256sum] = "f928d0f40c641c251e6f0397d007fa39d82653da04b65efef9df0a5c45b5f945"
SRC_URI[dev.sha256sum] = "854b889fee8135209acdb076c5feffd8170e9d6c330e5f6d3c19005d7a555917"
SRC_URI[hdr.sha256sum] = "4f8f12616437d16629769ca899524dfa3057d40c6044c2e8b44078d37be7e467"
COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"

def extract_basever(d):
    ver = d.getVar('PV').split('-')[0]
    components = ver.split('.')
    return '.'.join(components[:3])

def extract_majver(d):
    ver = d.getVar('PV').split('-')[0]
    return ver.split('.')[0]

BASEVER = "${@extract_basever(d)}"
MAJVER = "${@extract_majver(d)}"

S = "${UNPACKDIR}/cudnn"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir} ${D}${libdir} ${D}${datadir}
    install -m 0644 ${S}/usr/include/aarch64-linux-gnu/*_v${MAJVER}.h ${D}${includedir}
    for f in ${D}${includedir}/*_v${MAJVER}.h; do
	incname=$(basename $f)
	ln -s ${incname} ${D}${includedir}/$(basename ${incname} _v${MAJVER}.h).h
    done
    for f in ${S}/usr/lib/aarch64-linux-gnu/*.so.${BASEVER}; do
	libname=$(basename $f .so.${BASEVER})
	install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/${libname}.so.${BASEVER} ${D}${libdir}/
	ln -s ${libname}.so.${BASEVER} ${D}${libdir}/${libname}.so.${MAJVER}
	ln -s ${libname}.so.${MAJVER} ${D}${libdir}/${libname}.so
	if [ "${libname}" != "libcudnn" ]; then
	    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/${libname}_static.a ${D}${libdir}/
	    ln -s ${libname}_static.a ${D}${libdir}/${libname}_static_v${MAJVER}.a
	fi
    done
    cp --preserve=mode,timestamps --recursive ${S}/usr/share/* ${D}${datadir}/
    rm -rf ${D}${datadir}/lintian
}

INSANE_SKIP:${PN} = "ldflags"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
