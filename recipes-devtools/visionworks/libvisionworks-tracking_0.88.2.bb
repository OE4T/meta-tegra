SUMMARY = "NVIDIA VisionWorks target tools"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-tracking-repo/copyright;md5=99d8c0c1313afdf990f6407c07a88407"

inherit nvidia_devnet_downloads

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/libvisionworks-tracking-repo_${PV}_arm64.deb"
SRC_URI[md5sum] = "7630f0309c883cc6d8a1ab5a712938a5"
SRC_URI[sha256sum] = "b1f529cf3e44be81f19544a148cbe80ba8ae91b57b0837bd7c84d4f6b1f0d822"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"

DEPENDS = "dpkg-native cuda-cudart patchelf-native libvisionworks"

COMPATIBLE_MACHINE = "(tegra)"
COMPATIBLE_MACHINE_tegra124 = "(-)"

do_compile() {
    dpkg-deb --extract ${S}/var/visionworks-tracking-repo/libvisionworks-tracking_${PV}_arm64.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-tracking-repo/libvisionworks-tracking-dev_${PV}_arm64.deb ${B}
    patchelf --set-rpath "${CUDAPATH}/${baselib}" ${B}/usr/lib/libvisionworks_tracking.so.${PV}
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
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
