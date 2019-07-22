SUMMARY = "NVIDIA CUDA Deep Neural Network library"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v7.h;endline=48;md5=b48d68d7e5eb6b858c229fdb89171636"

inherit nvidia_devnet_downloads

SRC_URI = "\
    ${NVIDIA_DEVNET_MIRROR}/libcudnn7_${PV}+cuda10.0_arm64.deb;name=lib;subdir=cudnn \
    ${NVIDIA_DEVNET_MIRROR}/libcudnn7-dev_${PV}+cuda10.0_arm64.deb;name=dev;subdir=cudnn \
    ${NVIDIA_DEVNET_MIRROR}/libcudnn7-doc_${PV}+cuda10.0_arm64.deb;name=doc;subdir=cudnn \
"
SRC_URI[lib.md5sum] = "92867c0a495f84ec11d108f84b776620"
SRC_URI[lib.sha256sum] = "9abacab3ac2cb4a72c1048e4b1b7db7a7d8c10ff82874ab694f2303e7d79f923"
SRC_URI[dev.md5sum] = "dd0fbfa225b2374b946febc98e2cdec4"
SRC_URI[dev.sha256sum] = "8abab3f7d01221da3a3bb5f4d0c5205354f3612d624b5ca5eeed04884719ad5d"
SRC_URI[doc.md5sum] = "9478c16ceeaaca937d4d26b982e48bd1"
SRC_URI[doc.sha256sum] = "340f58a0c92ef6f8ba59553d2b0e7e049d80486b4d216cea43b5486a080fa194"
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

PACKAGES =+ "${PN}-samples"
FILES_${PN}-samples = "${prefix}/src"
INSANE_SKIP_${PN} = "ldflags"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

RDEPENDS_${PN}-samples = "cuda-cublas-dev cuda-cudart"
RPROVIDES_${PN}-samples = "${PN}-examples"
INSANE_SKIP_${PN}-samples = "build-deps dev-deps ldflags staticdev"
