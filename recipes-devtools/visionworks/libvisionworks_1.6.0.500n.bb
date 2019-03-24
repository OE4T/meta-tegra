DESCRIPTION = "NVIDIA VisionWorks Toolkit is a CUDA accelerated software development package for computer vision (CV) and image processing."
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-repo/copyright;md5=55bbad78645f10682903c530636cf1a9"

inherit nvidia_devnet_downloads

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/libvisionworks-repo_${PV}_arm64.deb"

SRC_URI[md5sum] = "e70d49ff115bc5782a3d07b572b5e3c0"
SRC_URI[sha256sum] = "fb46f48965e78e031ba8e987ba0421c03dd5e2428572e5681f1e31c74aabff22"
S = "${WORKDIR}"
B = "${WORKDIR}/build"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"

DEPENDS = "dpkg-native cuda-cudart patchelf-native"

COMPATIBLE_MACHINE = "tegra"
COMPATIBLE_MACHINE_tegra124 = "(-)"

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
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
