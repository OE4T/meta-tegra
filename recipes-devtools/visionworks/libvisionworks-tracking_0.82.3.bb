SUMMARY = "NVIDIA VisionWorks target tools"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-tracking-repo/copyright;md5=99d8c0c1313afdf990f6407c07a88407"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/003/linux-x64/libvisionworks-tracking-repo_${PV}_armhf_l4t-r21.deb"
SRC_URI[md5sum] = "46aa86a6e84e2499533765427b7180f9"
SRC_URI[sha256sum] = "2dc63dd79b0c859a6b3526c13f6666fbf5d5d93ac129ef70ce26a4e88cd5f4e4"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"

DEPENDS = "dpkg-native cuda-cudart chrpath-native libvisionworks"

COMPATIBLE_MACHINE = "(tegra124)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

do_compile() {
    dpkg-deb --extract ${S}/var/visionworks-tracking-repo/libvisionworks-tracking_${PV}_armhf.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-tracking-repo/libvisionworks-tracking-dev_${PV}_armhf.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-tracking-repo/libvisionworks-tracking-docs_${PV}_all.deb ${B}
    chrpath -r "${CUDAPATH}/${baselib}" ${B}/usr/lib/libvisionworks_tracking.so.0.82.3
}

do_install() {
    install -d ${D}${prefix} ${D}${libdir} ${D}${datadir}
    cp -R --preserve=mode,timestamps ${B}/usr/include ${D}${prefix}/
    cp -R --preserve=mode,timestamps ${B}/usr/lib/* ${D}${libdir}/
    cp -R --preserve=mode,timestamps ${B}/usr/share/visionworks-tracking ${D}${datadir}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

PACKAGES += "${PN}-samples"
FILES_${PN}-dev += "${libdir}/pkgconfig ${datadir}/visionworks-tracking/cmake"
FILES_${PN}-doc += "${datadir}/visionworks-tracking/docs"
FILES_${PN}-samples += "${datadir}/visionworks-tracking/sources"
RDEPENDS_${PN} = "libstdc++"



