SUMMARY = "NVIDIA VisionWorks target tools"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-sfm-repo/copyright;md5=99d8c0c1313afdf990f6407c07a88407"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/003/linux-x64/libvisionworks-sfm-repo_${PV}_armhf_l4t-r21.deb"
SRC_URI[md5sum] = "56d9fe64c36d7917b6d91c1f4f6e457b"
SRC_URI[sha256sum] = "3a8baecd34c92e59747b407751c58d9a3a9f0cb90126f99bd0d88519eb93e462"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"

DEPENDS = "dpkg-native cuda-cudart chrpath-native libvisionworks"

COMPATIBLE_MACHINE = "(tegra124)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

do_compile() {
    dpkg-deb --extract ${S}/var/visionworks-sfm-repo/libvisionworks-sfm_${PV}_armhf.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-sfm-repo/libvisionworks-sfm-dev_${PV}_armhf.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-sfm-repo/libvisionworks-sfm-docs_${PV}_all.deb ${B}
    chrpath -r "${CUDAPATH}/${baselib}" ${B}/usr/lib/libvisionworks_sfm.so.0.86.2
}

do_install() {
    install -d ${D}${prefix} ${D}${libdir} ${D}${datadir}
    cp -R --preserve=mode,timestamps ${B}/usr/include ${D}${prefix}/
    cp -R --preserve=mode,timestamps ${B}/usr/lib/* ${D}${libdir}/
    cp -R --preserve=mode,timestamps ${B}/usr/share/visionworks-sfm ${D}${datadir}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

PACKAGES += "${PN}-samples"
FILES_${PN}-dev += "${libdir}/pkgconfig ${datadir}/visionworks-sfm/cmake"
FILES_${PN}-doc += "${datadir}/visionworks-sfm/docs"
FILES_${PN}-samples += "${datadir}/visionworks-sfm/sources"
RDEPENDS_${PN} = "libstdc++"



