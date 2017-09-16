SUMMARY = "NVIDIA VisionWorks target tools"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-repo/copyright;md5=49db087d45a7fa00de462381f7bf9fd0"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/006/linux-x64/libvisionworks-repo_${PV}_arm64_l4t-r24.deb"
SRC_URI[md5sum] = "4da187505f27799a2a1d9f27e44d440b"
SRC_URI[sha256sum] = "5f9bd4836a71d848e79be02a5e10a405209b66c8c2ee242357454a34ab3a358d"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

CUDAPATH ?= "/usr/local/cuda-8.0"

DEPENDS = "dpkg-native cuda-cudart chrpath-native"

COMPATIBLE_MACHINE = "(jetsontx1)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

do_compile() {
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks_${PV}_arm64.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks-dev_${PV}_all.deb ${B}
    cd ${B}/usr
    if [ "${baselib}" != "lib" ]; then
        mv ./lib ./${baselib}
    fi
    chrpath -r "${CUDAPATH}/${baselib}" ./${baselib}/libvisionworks.so.1.5.3
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
