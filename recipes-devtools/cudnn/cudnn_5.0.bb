SUMMARY = "NVIDIA CUDA Deep Neural Network library"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://cuda/include/cudnn.h;endline=48;md5=b48d68d7e5eb6b858c229fdb89171636"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/003/linux-x64/cudnn-7.0-linux-aarch64-v${PV}-ga.tgz"
SRC_URI[md5sum] = "2c9779c4b6af12c3d21f325a354f75c8"
SRC_URI[sha256sum] = "6254d5de712dbcbe3e5d27bc91a86827f03d9114d8457516ffcffd77cedb9f5b"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir}
    cp ${S}/cuda/include/*.h ${D}${includedir}
    install -d ${D}${libdir}
    cp -d ${S}/cuda/lib64/* ${D}${libdir}
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

RDEPENDS_${PN} = "cuda-cudart"
