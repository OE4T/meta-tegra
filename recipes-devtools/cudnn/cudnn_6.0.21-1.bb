SUMMARY = "NVIDIA CUDA Deep Neural Network library"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v6.h;endline=48;md5=b48d68d7e5eb6b858c229fdb89171636"

SRC_URI = "\
    http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/013/linux-x64/libcudnn6_${PV}+cuda8.0_arm64.deb;name=lib;unpack=false \
    http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/013/linux-x64/libcudnn6-dev_${PV}+cuda8.0_arm64.deb;name=dev;unpack=false \
    http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/013/linux-x64/libcudnn6-doc_${PV}+cuda8.0_arm64.deb;name=doc;unpack=false \
"
SRC_URI[lib.md5sum] = "4d96b7b3354b03a49ad1ea0ca281ae08"
SRC_URI[lib.sha256sum] = "3fccf1b58dc9b9efa24d2c97be9f230744b597a6573ec085d75aa543f0e41208"
SRC_URI[dev.md5sum] = "97fe08882a20bf28c8c61d22bb52ad6a"
SRC_URI[dev.sha256sum] = "6a22969346a86cd1a09330d6baa15c071735564cecc5a517d30591d40e4bbe72"
SRC_URI[doc.md5sum] = "d0233c3bba7760130b48a79cec6b5f24"
SRC_URI[doc.sha256sum] = "cb4c30331916da5917dec02d059ab63fe13dcd622b6e6fee2b9687ca8008fd16"

COMPATIBLE_MACHINE = "(tegra186|tegra210)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

DEPENDS = "dpkg-native"

S = "${WORKDIR}/cudnn"

do_configure() {
    dpkg-deb --extract ${WORKDIR}/libcudnn6_${PV}+cuda8.0_arm64.deb ${S}
    dpkg-deb --extract ${WORKDIR}/libcudnn6-dev_${PV}+cuda8.0_arm64.deb ${S}
    dpkg-deb --extract ${WORKDIR}/libcudnn6-doc_${PV}+cuda8.0_arm64.deb ${S}
}
do_populate_lic[depends] += "${PN}:do_configure"

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir} ${D}${libdir} ${D}${datadir} ${D}${prefix}/src
    install -m 0644 ${S}/usr/include/aarch64-linux-gnu/*.h ${D}${includedir}
    install -m 0644 ${B}/usr/lib/aarch64-linux-gnu/libcudnn* ${D}${libdir}/
    ln -s libcudnn.so.6.0.20 ${D}${libdir}/libcudnn.so    
    cp --preserve=mode,timestamps --recursive ${S}/usr/share/* ${D}${datadir}/
    rm -rf ${D}${datadir}/lintian
    cp --preserve=mode,timestamps --recursive ${S}/usr/src/* ${D}${prefix}/src/
}

PACKAGES =+ "${PN}-examples"
FILES_${PN}-examples = "${prefix}/src"
INSANE_SKIP_${PN} = "ldflags"
INSANE_SKIP_${PN}-examples = "ldflags staticdev"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

RDEPENDS_${PN} = "cuda-cudart"
