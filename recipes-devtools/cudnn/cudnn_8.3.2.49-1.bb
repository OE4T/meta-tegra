SUMMARY = "NVIDIA CUDA Deep Neural Network library"
HOMEPAGE = "https://developer.nvidia.com/cudnn"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v8.h;endline=48;md5=59218f2f10ab9e4132dda76c59e80fa1"

inherit l4t_deb_pkgfeed container-runtime-csv

L4T_DEB_GROUP = "cudnn"

DEPENDS = "libcublas"

SRC_COMMON_DEBS = "\
    libcudnn8_${PV}+cuda11.4_arm64.deb;name=lib;subdir=cudnn \
    libcudnn8-dev_${PV}+cuda11.4_arm64.deb;name=dev;subdir=cudnn \
"
SRC_URI[lib.sha256sum] = "025d1351143e6ebbf290f4fb787028a16a0f0dd1221e673cf718c7f18327a7f7"
SRC_URI[dev.sha256sum] = "ac2866f7d14dd7aea376ad5204229805c274f65e3fdcf430609e552adc47c247"
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
