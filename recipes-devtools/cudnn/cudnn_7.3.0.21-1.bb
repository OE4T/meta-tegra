SUMMARY = "NVIDIA CUDA Deep Neural Network library"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v7.h;endline=48;md5=b48d68d7e5eb6b858c229fdb89171636"

L4T_URI_BASE = "https://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t_prerel/4.0/m39xww/beta40/JetPackL4T_40_b190"
SRC_URI = "\
    ${L4T_URI_BASE}/libcudnn7_${PV}+cuda10.0_arm64.deb;name=lib;unpack=false \
    ${L4T_URI_BASE}/libcudnn7-dev_${PV}+cuda10.0_arm64.deb;name=dev;unpack=false \
    ${L4T_URI_BASE}/libcudnn7-doc_${PV}+cuda10.0_arm64.deb;name=doc;unpack=false \
"
SRC_URI[lib.md5sum] = "3d56f469ab924c4daf9b7a8920da557a"
SRC_URI[lib.sha256sum] = "54ddf59ece7ea76dbca4d101102709aac4373dfd817abd0f458865b985a7ca56"
SRC_URI[dev.md5sum] = "995e8aece254735f71243a9e704e9248"
SRC_URI[dev.sha256sum] = "4fa5151c75bb924f4066fb9c079951af95c82c12d49981e070aacee2abe89a53"
SRC_URI[doc.md5sum] = "7cebda83bfe17e145191d1056c17f13e"
SRC_URI[doc.sha256sum] = "0c775c04400d574c12742f03139dced74a06295a06b0538f856fb5b3fadedd2c"

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
