SUMMARY = "NVIDIA VisionWorks target tools"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-repo/copyright;md5=49db087d45a7fa00de462381f7bf9fd0"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/013/linux-x64/libvisionworks-repo_${PV}_arm64_l4t-r26.deb"
SRC_URI[md5sum] = "3021b38a3717917811961470fc1ce8ce"
SRC_URI[sha256sum] = "e62bc999c46e1e1981a6432e5edbad3d3ca6f9951e902bddfca77c7318c9b67c"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"

DEPENDS = "dpkg-native cuda-cudart chrpath-native"

COMPATIBLE_MACHINE = "(jetsontx2|jetsontx1)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

do_compile() {
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks_${PV}_arm64.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks-dev_${PV}_all.deb ${B}
    chrpath -r "${CUDAPATH}/${baselib}" ${B}/usr/lib/libvisionworks.so.1.6.0
}

do_install() {
    install -d ${D}${prefix} ${D}${libdir} ${D}${datadir}
    cp -R --preserve=mode,timestamps ${B}/usr/include ${D}${prefix}/
    cp -R --preserve=mode,timestamps ${B}/usr/lib/* ${D}${libdir}/
    cp -R --preserve=mode,timestamps ${B}/usr/share/visionworks ${D}${datadir}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

FILES_${PN}-dev += "${libdir}/pkgconfig ${datadir}/visionworks"
RDEPENDS_${PN} = "libstdc++"
