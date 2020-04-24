DESCRIPTION = "NVIDIA VisionWorks Toolkit is a CUDA accelerated software development package for computer vision (CV) and image processing."
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-repo/copyright;md5=55bbad78645f10682903c530636cf1a9"

inherit nvidia_devnet_downloads container-runtime-csv

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/libvisionworks-repo_${PV}_arm64.deb"

SRC_URI[sha256sum] = "a57d500397d2a36bfb2eb214e402db334fb2eebd25accbaed38b13361eb64b10"
S = "${WORKDIR}"
B = "${WORKDIR}/build"

DEPENDS = "dpkg-native cuda-cudart"

COMPATIBLE_MACHINE = "tegra"

do_compile() {
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks_${PV}_arm64.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks-dev_${PV}_all.deb ${B}
}

do_install() {
    install -d ${D}${prefix} ${D}${libdir} ${D}${datadir} ${D}${libdir}/pkgconfig
    cp -R --preserve=mode,timestamps ${B}/usr/include ${D}${prefix}/
    cp -R --preserve=mode,timestamps ${B}/usr/lib/pkgconfig/* ${D}${libdir}/pkgconfig
    cp --preserve=mode,timestamps ${B}/usr/lib/libvisionworks.so ${D}${libdir}/
    cp -R --preserve=mode,timestamps ${B}/usr/share/visionworks ${D}${datadir}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

FILES_${PN} = "${libdir}/libvisionworks.so"
FILES_${PN}-dev = "${includedir} ${libdir}/pkgconfig ${datadir}/visionworks"
RDEPENDS_${PN} = "libstdc++"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
