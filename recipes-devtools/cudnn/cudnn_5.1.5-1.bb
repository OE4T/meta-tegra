SUMMARY = "NVIDIA CUDA Deep Neural Network library"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://cudnn_v5.h;endline=48;md5=b48d68d7e5eb6b858c229fdb89171636"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/006/linux-x64/cuDNN-v5.1.zip;downloadfilename=${BP}.zip"
SRC_URI[md5sum] = "22421e0e8dae7c176bd84db3590bfb05"
SRC_URI[sha256sum] = "33e0e1ff9037b703965945ba88d05b72d9c592d8fab7293a36c1ae5965309d7c"

COMPATIBLE_MACHINE = "(jetsontx1)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

S = "${WORKDIR}/cuDNN"
B = "${WORKDIR}/build"

DEPENDS = "dpkg-native"

do_configure() {
    dpkg-deb --extract ${S}/libcudnn5_${PV}+cuda8.0_arm64.deb ${B}
    dpkg-deb --extract ${S}/libcudnn5-dev_${PV}+cuda8.0_arm64.deb ${B}
    cp ${B}/usr/include/aarch64-linux-gnu/cudnn_v5.h ${S}/
}
do_populate_lic[depends] += "${PN}:do_configure"

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir}
    cp ${B}/usr/include/aarch64-linux-gnu/*.h ${D}${includedir}
    install -d ${D}${libdir}
    cp -d ${B}/usr/lib/aarch64-linux-gnu/* ${D}${libdir}
    ln -s libcudnn.so.5.1.5 ${D}${libdir}/libcudnn.so
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

RDEPENDS_${PN} = "cuda-cudart"
