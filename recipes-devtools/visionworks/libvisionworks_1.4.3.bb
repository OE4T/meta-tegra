SUMMARY = "NVIDIA VisionWorks target tools"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-repo/copyright;md5=49db087d45a7fa00de462381f7bf9fd0"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/003/linux-x64/libvisionworks-repo_${PV}_arm64_l4t-r24.deb"
SRC_URI[md5sum] = "bcabb0e7e269a00ade2e46a55d821cc2"
SRC_URI[sha256sum] = "acef8608fbd7d35dc296369a31391481c1c4ebd3d2a09ad04c74f09df9df4042"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

CUDAPATH ?= "/usr/local/cuda-7.0"

DEPENDS = "dpkg-native cuda-cudart chrpath-native"

do_compile() {
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks_${PV}_arm64.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks-dev_${PV}_all.deb ${B}
    cd ${B}/usr
    if [ "${baselib}" != "lib" ]; then
        mv ./lib ./${baselib}
    fi
    chrpath -r "${CUDAPATH}/${baselib}" ./${baselib}/libvisionworks.so.1.4.3
}

do_install() {
    install -d ${D}${prefix}
    cp -R --preserve=mode,timestamps ${B}/usr/* ${D}${prefix}/
    rm -rf ${D}${datadir}
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

FILES_${PN}-dev += "${libdir}/pkgconfig"
RDEPENDS_${PN} = "libstdc++"
