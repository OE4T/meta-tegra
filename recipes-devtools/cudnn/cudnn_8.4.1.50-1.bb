SUMMARY = "NVIDIA CUDA Deep Neural Network library"
HOMEPAGE = "https://developer.nvidia.com/cudnn"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v8.h;endline=48;md5=23ac9d0dd5c70a72b69318b45ca3fee6"

inherit l4t_deb_pkgfeed

L4T_DEB_GROUP = "cudnn"

DEPENDS = "libcublas"

SRC_COMMON_DEBS = "\
    libcudnn8_${PV}+cuda11.4_arm64.deb;name=lib;subdir=cudnn \
    libcudnn8-dev_${PV}+cuda11.4_arm64.deb;name=dev;subdir=cudnn \
"
SRC_URI[lib.sha256sum] = "68a9e1515834a4ad7197cd624d6f9312d5b7ca31ca01b7a39f6aec9712b8c508"
SRC_URI[dev.sha256sum] = "23f3097db03f7bb1d65a17c45fdbe31ce64959d9acb682acc0603e2e75428780"
COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"

def extract_basever(d):
    ver = d.getVar('PV').split('-')[0]
    components = ver.split('.')
    return '%s.%s.%s' % (components[0], components[1], components[2])

def extract_majver(d):
    ver = d.getVar('PV').split('-')[0]
    return ver.split('.')[0]

BASEVER = "${@extract_basever(d)}"
MAJVER = "${@extract_majver(d)}"

S = "${WORKDIR}/cudnn"

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
