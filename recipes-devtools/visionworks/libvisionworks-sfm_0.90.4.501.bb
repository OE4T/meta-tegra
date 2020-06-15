DESCRIPTION = "NVIDIA VisionWorks Plus (SFM) contains platform specific optimized libraries, including SFM, a library of \
               computer vision primitives and algorithms with framework optimizied for NVIDIA platforms built on top of \
               NVIDIA Visionworks and extends its API"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-sfm/copyright;md5=99d8c0c1313afdf990f6407c07a88407"

inherit l4t_deb_pkgfeed container-runtime-csv

SRC_COMMON_DEBS = "\
    libvisionworks-sfm_${PV}_arm64.deb;subdir=${BPN};name=lib \
    libvisionworks-sfm-dev_${PV}_arm64.deb;subdir=${BPN};name=dev \
"

SRC_URI[lib.sha256sum] = "f0aa8309f87791c8766b5ac86d4f218692d91479d4109b90081579b0e635e9d3"
SRC_URI[dev.sha256sum] = "3f7bb0b4130f21f7142b2560acc0357221646839629b90428f099c47a53d4ecc"

S = "${WORKDIR}/${BPN}"
B = "${S}"

DEPENDS = "cuda-cudart libvisionworks"

COMPATIBLE_MACHINE = "(tegra)"

do_compile() {
    :
}

do_install() {
    install -d ${D}${prefix} ${D}${libdir} ${D}${datadir}
    cp -R --preserve=mode,timestamps ${B}/usr/include ${D}${prefix}/
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/usr/lib/* ${D}${libdir}/
    cp -R --preserve=mode,timestamps ${B}/usr/share/visionworks-sfm ${D}${datadir}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

PACKAGES += "${PN}-samples"
FILES_${PN}-dev += "${datadir}/visionworks-sfm/cmake"
FILES_${PN}-doc += "${datadir}/visionworks-sfm/docs"
FILES_${PN}-samples += "${datadir}/visionworks-sfm/sources"
RDEPENDS_${PN} = "libstdc++"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
