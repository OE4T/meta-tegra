SUMMARY = "NVIDIA CUDA Deep Neural Network library"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v7.h;endline=48;md5=b48d68d7e5eb6b858c229fdb89171636"

inherit nvidia_devnet_downloads container-runtime-csv

SRC_URI = "\
    ${NVIDIA_DEVNET_MIRROR}/libcudnn7_${PV}+cuda10.0_arm64.deb;name=lib;subdir=cudnn \
    ${NVIDIA_DEVNET_MIRROR}/libcudnn7-dev_${PV}+cuda10.0_arm64.deb;name=dev;subdir=cudnn \
    ${NVIDIA_DEVNET_MIRROR}/libcudnn7-doc_${PV}+cuda10.0_arm64.deb;name=doc;subdir=cudnn \
"
SRC_URI[lib.md5sum] = "9f30aa86e505a3b83b127ed7a51309a1"
SRC_URI[lib.sha256sum] = "92186b906591f741becba7f7f0acdf99209fcf1f49dbc5e2e51148050749d9e9"
SRC_URI[dev.md5sum] = "a010637c80859b2143ef24461ee2ef97"
SRC_URI[dev.sha256sum] = "dc3f17b3d3a3db983389784b0902d32651f0b7d1b35a903385d2ccdba421f59a"
SRC_URI[doc.md5sum] = "f9e43d15ff69d65a85d2aade71a43870"
SRC_URI[doc.sha256sum] = "5df29804608ddc34d90d116e99b752bd3a2fb569c4c3fa36964018b5ae0b18f8"
COMPATIBLE_MACHINE = "(tegra)"
COMPATIBLE_MACHINE_tegra124 = "(-)"
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
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libcudnn.so.${BASEVER} ${D}${libdir}/
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libcudnn_static_v7.a ${D}${libdir}/
    ln -s libcudnn.so.${BASEVER} ${D}${libdir}/libcudnn.so.7
    ln -s libcudnn.so.${BASEVER} ${D}${libdir}/libcudnn.so
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
