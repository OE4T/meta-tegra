DESCRIPTION = "NVIDIA VisionWorks Plus (SFM) contains platform specific optimized libraries, including SFM, a library of \
               computer vision primitives and algorithms with framework optimizied for NVIDIA platforms built on top of \
               NVIDIA Visionworks and extends its API"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-sfm-repo/copyright;md5=99d8c0c1313afdf990f6407c07a88407"

SRC_URI = "https://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/4.1.1/xddsn.im/JetPackL4T_4.1.1_b57/libvisionworks-sfm-repo_${PV}_arm64.deb"
SRC_URI[md5sum] = "c32a242d215e32950a590ed6d5e449bc"
#SRC_URI[sha256sum] = "3a8baecd34c92e59747b407751c58d9a3a9f0cb90126f99bd0d88519eb93e462"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"

DEPENDS = "dpkg-native cuda-cudart patchelf-native libvisionworks"

COMPATIBLE_MACHINE = "(tegra186|tegra194|tegra210)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

do_compile() {
    dpkg-deb --extract ${S}/var/visionworks-sfm-repo/libvisionworks-sfm_${PV}_arm64.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-sfm-repo/libvisionworks-sfm-dev_${PV}_arm64.deb ${B}
    patchelf --set-rpath "${CUDAPATH}/${baselib}" ${B}/usr/lib/libvisionworks_sfm.so.0.90.3
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
