SUMMARY = "NVIDIA VisionWorks target tools"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-tracking-repo/copyright;md5=99d8c0c1313afdf990f6407c07a88407"

inherit nvidia_devnet_downloads container-runtime-csv

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/libvisionworks-tracking-repo_${PV}_arm64.deb"
SRC_URI[sha256sum] = "bbdf32fd6df40610390880eb6c87ba6a06aeb95465ec955d8d2994106b336887"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

DEPENDS = "dpkg-native cuda-cudart libvisionworks"

COMPATIBLE_MACHINE = "(tegra)"

do_compile() {
    dpkg-deb --extract ${S}/var/visionworks-tracking-repo/libvisionworks-tracking_${PV}_arm64.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-tracking-repo/libvisionworks-tracking-dev_${PV}_arm64.deb ${B}
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
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
