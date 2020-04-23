SUMMARY = "NVIDIA CUDA Deep Neural Network library"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v8.h;endline=47;md5=55730a4c450a3c34d3734af620a7d6eb"

inherit nvidia_devnet_downloads container-runtime-csv

SRC_URI = "\
    ${NVIDIA_DEVNET_MIRROR}/libcudnn8_${PV}+cuda10.2_arm64.deb;name=lib;subdir=cudnn \
    ${NVIDIA_DEVNET_MIRROR}/libcudnn8-dev_${PV}+cuda10.2_arm64.deb;name=dev;subdir=cudnn \
    ${NVIDIA_DEVNET_MIRROR}/libcudnn8-doc_${PV}+cuda10.2_arm64.deb;name=doc;subdir=cudnn \
"
SRC_URI[lib.sha256sum] = "b94c2f1b4c0bca009d579ccc6c4bab44617e9abdb88b85e6ca16afe82e4e02ad"
SRC_URI[dev.sha256sum] = "1fbe66f176084d28e8bbf9fd8f1301be2fae57d1af6a823f4961a38e26a188c2"
SRC_URI[doc.sha256sum] = "3a420a98261728306386521de6772b3bc4ed53a7fe6073659a429e94d0d57f49"
COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"

def extract_basever(d):
    ver = d.getVar('PV').split('-')[0]
    components = ver.split('.')
    return '%s.%s.%s' % (components[0], components[1], components[2])

BASEVER = "${@extract_basever(d)}"

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
    ln -s cudnn_v7.h ${D}${includedir}/cudnn.h
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libcudnn.so.${BASEVER} ${D}${libdir}/
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libcudnn_static_v8.a ${D}${libdir}/
    ln -s libcudnn.so.${BASEVER} ${D}${libdir}/libcudnn.so.8
    ln -s libcudnn.so.${BASEVER} ${D}${libdir}/libcudnn.so
    ln -s libcudnn_static_v7.a ${D}${libdir}/libcudnn_static.a
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
