SUMMARY = "NVIDIA CUDA Deep Neural Network library"
LICENSE = "Proprietary"
HOMEPAGE = "https://developer.nvidia.com/cudnn"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v7.h;endline=48;md5=b48d68d7e5eb6b858c229fdb89171636"

inherit nvidia_devnet_downloads container-runtime-csv

SRC_URI = "\
    ${NVIDIA_DEVNET_MIRROR}/libcudnn7_${PV}+cuda10.0_arm64.deb;name=lib;subdir=cudnn \
    ${NVIDIA_DEVNET_MIRROR}/libcudnn7-dev_${PV}+cuda10.0_arm64.deb;name=dev;subdir=cudnn \
    ${NVIDIA_DEVNET_MIRROR}/libcudnn7-doc_${PV}+cuda10.0_arm64.deb;name=doc;subdir=cudnn \
"
SRC_URI[lib.md5sum] = "e37d987c29bf9ef8ede082c19df12ad8"
SRC_URI[lib.sha256sum] = "40ddff085ce25aa7468c2eec5d808b22bdd57546cb7bbe1707932e928afb44e9"
SRC_URI[dev.md5sum] = "9aee6d3179cd6ae7cd0cb126f1781c94"
SRC_URI[dev.sha256sum] = "96dca964b29c3ce5412158ad99b53f480fe8285747ebc82b13abc9570636e368"
SRC_URI[doc.md5sum] = "4019391d5f31fabdfeca8487e4161bd3"
SRC_URI[doc.sha256sum] = "f1e45aace86823cded6572ad14b13ea403785ffa5392adfb0d5fc4163d5660a9"
COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

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
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libcudnn_static_v7.a ${D}${libdir}/
    ln -s libcudnn.so.${BASEVER} ${D}${libdir}/libcudnn.so.7
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

RDEPENDS_${PN}-samples = "cuda-cublas cuda-cublas-dev cuda-cudart"
RPROVIDES_${PN}-samples = "${PN}-examples"
INSANE_SKIP_${PN}-samples = "build-deps dev-deps ldflags staticdev"
