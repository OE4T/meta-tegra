DESCRIPTION = "NVIDIA VisionWorks Plus (SFM) contains platform specific optimized libraries, including SFM, a library of \
               computer vision primitives and algorithms with framework optimizied for NVIDIA platforms built on top of \
               NVIDIA Visionworks and extends its API"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-sfm-repo/copyright;md5=99d8c0c1313afdf990f6407c07a88407"

inherit nvidia_devnet_downloads

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/libvisionworks-sfm-repo_${PV}_arm64.deb"
SRC_URI[md5sum] = "647b0ae86a00745fc6d211545a9fcefe"
SRC_URI[sha256sum] = "b98bab05e6bc8d75cffbec477437bdfbb640e59503ff0e15c82a714ee43e70ce"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"

DEPENDS = "dpkg-native cuda-cudart patchelf-native libvisionworks"

COMPATIBLE_MACHINE = "(tegra)"
COMPATIBLE_MACHINE_tegra124 = "(-)"

do_compile() {
    dpkg-deb --extract ${S}/var/visionworks-sfm-repo/libvisionworks-sfm_${PV}_arm64.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-sfm-repo/libvisionworks-sfm-dev_${PV}_arm64.deb ${B}
    patchelf --set-rpath "${CUDAPATH}/${baselib}" ${B}/usr/lib/libvisionworks_sfm.so.${PV}
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
