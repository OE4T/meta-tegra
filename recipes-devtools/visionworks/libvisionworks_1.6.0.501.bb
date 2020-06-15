DESCRIPTION = "NVIDIA VisionWorks Toolkit is a CUDA accelerated software development package for computer vision (CV) and image processing."
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/${BPN}/copyright;md5=55bbad78645f10682903c530636cf1a9"

inherit l4t_deb_pkgfeed container-runtime-csv

SRC_COMMON_DEBS = "\
    libvisionworks_${PV}_arm64.deb;subdir=${BPN};name=lib \
    libvisionworks-dev_${PV}_all.deb;subdir=${BPN};name=dev \
"

SRC_URI[lib.sha256sum] = "cf9257e362764b236e6e63bb1b1dc033657cf6b38cdfe65f547e93fc2aa15d71"
SRC_URI[dev.sha256sum] = "f6bf7959ced8220852c74f10461c492a609199e9865a4b8f5a1323a15356b40b"

S = "${WORKDIR}/${BPN}"
B = "${S}"

DEPENDS = "cuda-cudart"

COMPATIBLE_MACHINE = "tegra"

do_compile() {
    :
}

do_install() {
    install -d ${D}${prefix} ${D}${libdir} ${D}${datadir} ${D}${libdir}/pkgconfig
    cp -R --preserve=mode,timestamps ${B}/usr/include ${D}${prefix}/
    cp -R --preserve=mode,timestamps ${B}/usr/lib/pkgconfig/* ${D}${libdir}/pkgconfig
    cp --preserve=mode,timestamps,links --no-dereference ${B}/usr/lib/libvisionworks.so* ${D}${libdir}/
    cp -R --preserve=mode,timestamps ${B}/usr/share/visionworks ${D}${datadir}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

FILES_${PN}-dev += "${datadir}/visionworks"
RDEPENDS_${PN} = "libstdc++"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
