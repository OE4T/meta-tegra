SUMMARY = "Vision Programming Interface(VPI) is an API for accelerated \
  computer vision and image processing for embedded systems."
HOMEPAGE = "https://developer.nvidia.com/embedded/vpi"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = " \
    file://etc/ld.so.conf.d/vpi1.conf;md5=bfdc8e915558abe115e0fc762a2ff724 \
    file://opt/nvidia/vpi1/include/vpi/VPI.h;endline=48;md5=7c9511c3e53f3d844d189edd66c44682"

inherit l4t_deb_pkgfeed

SRC_COMMON_DEBS = "\
    libnvvpi1_${PV}_arm64.deb;name=lib;subdir=vpi1 \
    vpi1-dev_${PV}_arm64.deb;name=dev;subdir=vpi1 \
"
SRC_URI[lib.sha256sum] = "6c82f73820ab6a7b991cf88e28722a73b65979236a35d5e3bf4567130b283d99"
SRC_URI[dev.sha256sum] = "7863af049be6bf7ef24d68412d8eb1c68f08f5f87059f2e6a3f57308b973a727"

S = "${WORKDIR}/vpi1"
B = "${S}"

DEPENDS = "cuda-cudart cuda-cufft"

COMPATIBLE_MACHINE = "(tegra)"

do_compile() {
    :
}

do_install() {
    install -d ${D}/opt/nvidia/vpi1/lib64 
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/vpi1/lib64/* ${D}/opt/nvidia/vpi1/lib64

    install -d ${D}/${sysconfdir}/ld.so.conf.d/
    cp --preserve=mode,timestamps ${B}/etc/ld.so.conf.d/vpi1.conf ${D}/${sysconfdir}/ld.so.conf.d/

    install -d  ${D}/opt/nvidia/vpi1/include
    cp -R --preserve=mode,timestamps ${B}/opt/nvidia/vpi1/include/* ${D}/opt/nvidia/vpi1/include
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

FILES_${PN} = "/opt/nvidia/vpi1/lib64/ ${sysconfdir}/ld.so.conf.d"
FILES_${PN}-dev += "/opt/nvidia/vpi1/include"
INSANE_SKIP_${PN} = "dev-so"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"

PROVIDES += "libnvvpi1-dev"
