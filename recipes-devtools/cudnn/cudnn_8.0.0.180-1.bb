SUMMARY = "NVIDIA CUDA Deep Neural Network library"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v8.h;endline=47;md5=55730a4c450a3c34d3734af620a7d6eb"

inherit l4t_deb_pkgfeed container-runtime-csv

SRC_COMMON_DEBS = "\
    libcudnn8_${PV}+cuda10.2_arm64.deb;name=lib;subdir=cudnn \
    libcudnn8-dev_${PV}+cuda10.2_arm64.deb;name=dev;subdir=cudnn \
    libcudnn8-doc_${PV}+cuda10.2_arm64.deb;name=doc;subdir=cudnn \
"
SRC_URI[lib.sha256sum] = "899405cf41d55e11b9000a25ac70c5523307b06eaad90ef603468bc08c7e0ddb"
SRC_URI[dev.sha256sum] = "4b581303d1515d9a204b6639dd0bfee3b27571602f289abe707350abbe3a268d"
SRC_URI[doc.sha256sum] = "dc9c550a0bd3160917f1bf2921f8542a4c8cb52ba8c0ebe789c2076ab8b7f242"
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
    install -d ${D}${includedir} ${D}${libdir} ${D}${datadir} ${D}${prefix}/src
    install -m 0644 ${S}/usr/include/aarch64-linux-gnu/*.h ${D}${includedir}
    for f in ${D}${includedir}/*_v${MAJVER}.h; do
	incname=$(basename $f)
	ln -s ${incname} ${D}${includedir}/$(basename ${incname} _v${MAJVER}.h).h
    done
    for f in ${S}/usr/lib/aarch64-linux-gnu/*.so.${BASEVER}; do
	libname=$(basename $f .so.${BASEVER})
	install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/${libname}.so.${BASEVER} ${D}${libdir}/
	install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/${libname}_static_v${MAJVER}.a ${D}${libdir}/
	ln -s ${libname}.so.${BASEVER} ${D}${libdir}/${libname}.so.${MAJVER}
	ln -s ${libname}.so.${BASEVER} ${D}${libdir}/${libname}.so
	ln -s ${libname}_static_v${MAJVER}.a ${D}${libdir}/${libname}_static.a
    done
    cp --preserve=mode,timestamps --recursive ${S}/usr/share/* ${D}${datadir}/
    rm -rf ${D}${datadir}/lintian
    cp --preserve=mode,timestamps --recursive ${S}/usr/src/* ${D}${prefix}/src/
}

PACKAGES += "${PN}-samples"
FILES_${PN}-samples = "${prefix}/src"
INSANE_SKIP_${PN} = "ldflags"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

RDEPENDS_${PN}-samples = "libcublas libcublas-dev cuda-cudart"
RPROVIDES_${PN}-samples = "${PN}-examples"
INSANE_SKIP_${PN}-samples = "build-deps dev-deps ldflags staticdev"
