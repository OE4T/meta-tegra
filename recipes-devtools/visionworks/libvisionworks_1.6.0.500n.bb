SUMMARY = "NVIDIA VisionWorks target tools"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-repo/copyright;md5=55bbad78645f10682903c530636cf1a9"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/3.2GA/m892ki/JetPackL4T_32_b196/libvisionworks-repo_${PV}_arm64.deb"
SRC_URI[md5sum] = "066cb90ca605f805aa616c5be6b06926"
SRC_URI[sha256sum] = "87c98ba693fc89a564ea1cd202241d55f003e5b62b8c1b47118e4030702197c1"
S = "${WORKDIR}"
B = "${WORKDIR}/build"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"

DEPENDS = "dpkg-native cuda-cudart patchelf-native"

COMPATIBLE_MACHINE = "(tegra186|tegra210)"
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
