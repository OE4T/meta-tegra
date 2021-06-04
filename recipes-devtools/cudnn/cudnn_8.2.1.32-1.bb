SUMMARY = "NVIDIA CUDA Deep Neural Network library"
HOMEPAGE = "https://developer.nvidia.com/cudnn"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v8.h;endline=48;md5=59218f2f10ab9e4132dda76c59e80fa1"

inherit l4t_deb_pkgfeed container-runtime-csv

L4T_DEB_GROUP = "cudnn"

SRC_COMMON_DEBS = "\
    libcudnn8_${PV}+cuda10.2_arm64.deb;name=lib;subdir=cudnn \
    libcudnn8-dev_${PV}+cuda10.2_arm64.deb;name=dev;subdir=cudnn \
    libcudnn8-samples_${PV}+cuda10.2_arm64.deb;name=samples;subdir=cudnn \
"
SRC_URI[lib.sha256sum] = "4c1619640e5411fb53e87828c62ff429daa608c8f02efb96460b43f743d64bb8"
SRC_URI[dev.sha256sum] = "adf7873edbde7fe293f672ebc65fcec299642950797d18b1c3a89855bb23904e"
SRC_URI[samples.sha256sum] = "52347d4fb10ef6db4897dd06407b41e46260bbd3c969d0886a739be7aa5f4e95"
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
	ln -s ${libname}.so.${BASEVER} ${D}${libdir}/${libname}.so.${MAJVER}
	ln -s ${libname}.so.${BASEVER} ${D}${libdir}/${libname}.so
    done
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libcudnn_static_v${MAJVER}.a ${D}${libdir}/
    ln -s libcudnn_static_v${MAJVER}.a ${D}${libdir}/libcudnn_static.a
    cp --preserve=mode,timestamps --recursive ${S}/usr/share/* ${D}${datadir}/
    rm -rf ${D}${datadir}/lintian
    cp --preserve=mode,timestamps --recursive ${S}/usr/src/* ${D}${prefix}/src/
}

PACKAGES += "${PN}-samples"
FILES:${PN}-samples = "${prefix}/src"
INSANE_SKIP:${PN} = "ldflags"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

RDEPENDS:${PN}-samples = "libcublas libcublas-dev cuda-cudart"
RPROVIDES:${PN}-samples = "${PN}-examples"
INSANE_SKIP:${PN}-samples = "build-deps dev-deps ldflags staticdev"
