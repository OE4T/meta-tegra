SUMMARY = "NVIDIA CUDA Deep Neural Network library"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v7.h;endline=48;md5=b48d68d7e5eb6b858c229fdb89171636"

L4T_URI_BASE = "https://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/4.1.1/xddsn.im/JetPackL4T_4.1.1_b57"
SRC_URI = "\
    ${L4T_URI_BASE}/libcudnn7_${PV}+cuda10.0_arm64.deb;name=lib;unpack=false \
    ${L4T_URI_BASE}/libcudnn7-dev_${PV}+cuda10.0_arm64.deb;name=dev;unpack=false \
    ${L4T_URI_BASE}/libcudnn7-doc_${PV}+cuda10.0_arm64.deb;name=doc;unpack=false \
"
SRC_URI[lib.md5sum] = "9cf73c1e0fbcc42347a26eb8063b11cd"
SRC_URI[lib.sha256sum] = "efe6d731098eb002fae82ff0a2a2f08810fffb21216c9ce507c717656136fa8f"
SRC_URI[dev.md5sum] = "3f7a7ef6422f964f4298d390b82c8985"
SRC_URI[dev.sha256sum] = "bca3cba1d96be869d6d06d1163b37515d8d90e1ff0fea84b01c72b9cb9eee18a"
SRC_URI[doc.md5sum] = "5533177508406c84b543c37d090f9846"
SRC_URI[doc.sha256sum] = "a73e78c0a2b99e91fa64edc22f330d7d7680ec5ac5b6cd0e0d3d369c16209447"
COMPATIBLE_MACHINE = "(tegra186|tegra194)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

PR = "r0"

DEPENDS = "dpkg-native"

def extract_basever(d):
    ver = d.getVar('PV').split('-')[0]
    components = ver.split('.')
    return '%s.%s.%s' % (components[0], components[1], components[2])

BASEVER = "${@extract_basever(d)}"

S = "${WORKDIR}/cudnn"

do_configure() {
    dpkg-deb --extract ${WORKDIR}/libcudnn7_${PV}+cuda10.0_arm64.deb ${S}
    dpkg-deb --extract ${WORKDIR}/libcudnn7-dev_${PV}+cuda10.0_arm64.deb ${S}
    dpkg-deb --extract ${WORKDIR}/libcudnn7-doc_${PV}+cuda10.0_arm64.deb ${S}
}
do_populate_lic[depends] += "${PN}:do_configure"

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir} ${D}${libdir} ${D}${datadir} ${D}${prefix}/src
    install -m 0644 ${S}/usr/include/aarch64-linux-gnu/*.h ${D}${includedir}
    install -m 0644 ${B}/usr/lib/aarch64-linux-gnu/libcudnn.so.${BASEVER} ${D}${libdir}/
    install -m 0644 ${B}/usr/lib/aarch64-linux-gnu/libcudnn_static_v7.a ${D}${libdir}/
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
