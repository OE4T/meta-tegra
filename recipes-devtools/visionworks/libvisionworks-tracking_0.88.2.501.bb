SUMMARY = "NVIDIA VisionWorks target tools"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-tracking/copyright;md5=99d8c0c1313afdf990f6407c07a88407"

inherit l4t_deb_pkgfeed container-runtime-csv

SRC_COMMON_DEBS = "\
    libvisionworks-tracking_${PV}_arm64.deb;subdir=${BPN};name=lib \
    libvisionworks-tracking-dev_${PV}_arm64.deb;subdir=${BPN};name=dev \
"

SRC_URI[lib.sha256sum] = "2d4c9149f40948810a3731f17240167cb21e81aff720cc7454dc358b17ba5b98"
SRC_URI[dev.sha256sum] = "55b4cd272ed5cb299d708e7fda9569f01e5c6e5ccc760005c20f456e5b6b6282"

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
    cp -R --preserve=mode,timestamps ${B}/usr/share/visionworks-tracking ${D}${datadir}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

PACKAGES += "${PN}-samples"
FILES_${PN}-dev += "${datadir}/visionworks-tracking/cmake"
FILES_${PN}-doc += "${datadir}/visionworks-tracking/docs"
FILES_${PN}-samples += "${datadir}/visionworks-tracking/sources"
RDEPENDS_${PN} = "libstdc++"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
