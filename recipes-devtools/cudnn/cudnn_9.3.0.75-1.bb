SUMMARY = "NVIDIA CUDA Deep Neural Network library"
HOMEPAGE = "https://developer.nvidia.com/cudnn"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v9.h;endline=48;md5=11e690c2afb545fc389ff0637693b996"

inherit l4t_deb_pkgfeed

L4T_DEB_GROUP = "cudnn"

DEPENDS = "libcublas zlib"

SRC_COMMON_DEBS = "\
    libcudnn9-cuda-12_${PV}_arm64.deb;name=lib;subdir=cudnn \
    libcudnn9-static-cuda-12_${PV}_arm64.deb;name=staticlib;subdir=cudnn \
    libcudnn9-dev-cuda-12_${PV}_arm64.deb;name=dev;subdir=cudnn \
"
SRC_URI[lib.sha256sum] = "0f3a869f4b9f5c20912acfb043c911f210304a554de27c71346638ac73c96377"
SRC_URI[staticlib.sha256sum] = "8f951856fedc3b890b56109917e2877413c5c49a2d2cb81a4dd4d30269d2d565"
SRC_URI[dev.sha256sum] = "749c910beca1b32216a0dbb2705aac261c3180437eb69922cb70932c2315ceff"
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
    install -m 0644 ${S}/usr/include/aarch64-linux-gnu/*.h ${D}${includedir}
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
