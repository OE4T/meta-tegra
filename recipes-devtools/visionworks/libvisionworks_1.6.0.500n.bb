DESCRIPTION = "NVIDIA VisionWorks Toolkit is a CUDA accelerated software development package for computer vision (CV) and image processing."
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-repo/copyright;md5=55bbad78645f10682903c530636cf1a9"

SRC_URI = "https://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/4.1.1/xddsn.im/JetPackL4T_4.1.1_b57/libvisionworks-repo_${PV}_arm64.deb"
SRC_URI[md5sum] = "73961d2e2b387d2116eb85710d80aeb3"
SRC_URI[sha256sum] = "c5c507716e48fa2e9dc6f7bef63b5a8d2fde8ebe5489c28e4ebcd84100d3297f"
S = "${WORKDIR}"
B = "${WORKDIR}/build"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"

DEPENDS = "dpkg-native cuda-cudart patchelf-native"

COMPATIBLE_MACHINE = "(tegra186|tegra210|tegra194)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

do_compile() {
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks_${PV}_arm64.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks-dev_${PV}_all.deb ${B}
    patchelf --set-rpath "${CUDAPATH}/${baselib}" ${B}/usr/lib/libvisionworks.so
}

do_install() {
    install -d ${D}${prefix} ${D}${libdir} ${D}${datadir}
    cp -R --preserve=mode,timestamps ${B}/usr/include ${D}${prefix}/
    cp -R --preserve=mode,timestamps ${B}/usr/lib/* ${D}${libdir}/
    cp -R --preserve=mode,timestamps ${B}/usr/share/visionworks ${D}${datadir}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

FILES_${PN} = "${libdir}/libvisionworks.so"
FILES_${PN}-dev = "${includedir} ${libdir}/pkgconfig ${datadir}/visionworks"
RDEPENDS_${PN} = "libstdc++"
