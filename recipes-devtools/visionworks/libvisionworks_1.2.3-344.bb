SUMMARY = "NVIDIA VisionWorks target tools"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-repo/copyright;md5=99d8c0c1313afdf990f6407c07a88407"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/002/linux-x64/libvisionworks-repo_${PV}_armhf_l4t_r23.deb"
SRC_URI[md5sum] = "3422db0714523b86b53bd88edc92c21f"
SRC_URI[sha256sum] = "aca0c685476058ca219a1f75dfd87a852d97cef29b7da238fd07c6721bf06bb2"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

DEPENDS = "dpkg-native cuda-cudart"

do_compile() {
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks_${PV}_armhf.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks-dev_${PV}_armhf.deb ${B}
}

do_install() {
    install -d ${D}${prefix}
    cp -R --preserve=mode,timestamps ${B}/usr/* ${D}${prefix}/
    rm -rf ${D}${datadir}
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
COMPATIBLE_HOST = "(arm.*)"

FILES_${PN}-dev += "${libdir}/pkgconfig"
RDEPENDS_${PN} = "libstdc++"
