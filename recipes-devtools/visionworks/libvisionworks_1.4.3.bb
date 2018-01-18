SUMMARY = "NVIDIA VisionWorks target tools"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/libvisionworks-repo/copyright;md5=49db087d45a7fa00de462381f7bf9fd0"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/003/linux-x64/libvisionworks-repo_${PV}_armhf_l4t-r21.deb"
SRC_URI[md5sum] = "78e8229020b57cd3e0b7b77df38cb8f6"
SRC_URI[sha256sum] = "8166c9fe9e9a5d8dd24eb66070237c442569abffffc6363e6a3da9814e278706"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"

DEPENDS = "dpkg-native cuda-cudart chrpath-native"

COMPATIBLE_MACHINE = "(tegra124)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

do_compile() {
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks_${PV}_armhf.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks-samples_${PV}_armhf.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks-dev_${PV}_all.deb ${B}
    dpkg-deb --extract ${S}/var/visionworks-repo/libvisionworks-docs_${PV}_all.deb ${B}    
    chrpath -r "${CUDAPATH}/${baselib}" ${B}/usr/lib/libvisionworks.so.1.4.3
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

PACKAGES += "${PN}-samples"
INSANE_SKIP_${PN}-samples = "staticdev"

FILES_${PN}-dev += "${libdir}/pkgconfig ${datadir}/visionworks/cmake ${datadir}/visionworks/samples"
FILES_${PN}-doc += "${datadir}/visionworks/docs"
FILES_${PN}-samples += "${datadir}/visionworks/sources"
RDEPENDS_${PN} = "libstdc++"



