DESCRIPTION = "NVIDIA VisionWorks Plus (SFM) contains platform specific optimized libraries, including SFM, a library of \
               computer vision primitives and algorithms with framework optimizied for NVIDIA platforms built on top of \
               NVIDIA Visionworks and extends its API"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-sfm-repo/copyright;md5=99d8c0c1313afdf990f6407c07a88407"

inherit nvidia_devnet_downloads container-runtime-csv

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/libvisionworks-sfm-repo_${PV}_arm64.deb"
SRC_URI[sha256sum] = "a833fa14db9f1126873066bca99b16565509be4da303ce5a2414e2bb0b8ee4b3"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

DEPENDS = "dpkg-native cuda-cudart libvisionworks"

COMPATIBLE_MACHINE = "(tegra)"

do_compile() {
    dpkg-deb --extract ${S}/var/visionworks-sfm-repo/libvisionworks-sfm_${PV}_arm64.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-sfm-repo/libvisionworks-sfm-dev_${PV}_arm64.deb ${B}
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
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
