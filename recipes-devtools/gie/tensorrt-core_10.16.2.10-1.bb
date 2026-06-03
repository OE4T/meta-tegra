DESCRIPTION = "NVIDIA TensorRT Core (GPU Inference Engine) for deep learning"
LICENSE = "Apache-2.0"

inherit l4t_deb_pkgfeed features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvinfer10_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer10_${PV}+cuda13.2_arm64.deb;name=lib;subdir=tensorrt \
    libnvinfer-dev_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-dev_${PV}+cuda13.2_arm64.deb;name=dev;subdir=tensorrt \
    libnvinfer-headers-dev_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-headers-dev_${PV}+cuda13.2_arm64.deb;name=hdev;subdir=tensorrt \
    libnvinfer-dispatch10_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-dispatch10_${PV}+cuda13.2_arm64.deb;name=disp;subdir=tensorrt \
    libnvinfer-dispatch-dev_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-dispatch-dev_${PV}+cuda13.2_arm64.deb;name=dispdev;subdir=tensorrt \
    libnvinfer-lean10_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-lean10_${PV}+cuda13.2_arm64.deb;name=nvl;subdir=tensorrt \
    libnvinfer-lean-dev_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-lean-dev_${PV}+cuda13.2_arm64.deb;name=nvldev;subdir=tensorrt \
"

SRC_URI[lib.sha256sum] = "d02111e3600d91bef407fe537a4179445cdc73fb259bf1f83dd256804de1897e"
SRC_URI[dev.sha256sum] = "d090713a4690eb7f20612008180b749c9b8e76de2fffc46ca8ef00c2fbe812e0"
SRC_URI[hdev.sha256sum] = "6208dbba5dc7fd04ca1bca71a3dcaed6642cb08765edbfcc10996b52844b2ec9"
SRC_URI[disp.sha256sum] = "c5812d5f1fa4036a3c2710c53722ed96e87885a4f615f528cc45a1e00b7c28fc"
SRC_URI[dispdev.sha256sum] = "2411eec858d7cac59e37433a449f19663b61e4a26c6c11868a769e34817f8c8b"
SRC_URI[nvl.sha256sum] = "a0ed45dd7fda97fd7ee67af5ee4ed5868cd12161618b1786b12122b73f34ad36"
SRC_URI[nvldev.sha256sum] = "dbeaed0a6a6779940ed0ad1423018593a71367ca005d25c13069c0393adaf1f8"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=16;md5=caedbfdd8e95ff43ea31d4b19e6f95a3"

REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "tegra-libraries-multimedia"

def extract_basever(d):
    ver = d.getVar('PV').split('-')[0]
    components = ver.split('.')
    return '.'.join(components[:3])

def extract_majver(d):
    ver = d.getVar('PV').split('-')[0]
    return ver.split('.')[0]

BASEVER = "${@extract_basever(d)}"
MAJVER = "${@extract_majver(d)}"

S = "${UNPACKDIR}/tensorrt"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/usr/include/aarch64-linux-gnu/*.h ${D}${includedir}
    install -d ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_builder_resource_*.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_lean.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_static.a ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_dispatch_static.a ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_lean_static.a ${D}${libdir}

    ln -s libnvinfer_lean.so.${BASEVER} ${D}${libdir}/libnvinfer_lean.so
    ln -s libnvinfer_lean.so.${BASEVER} ${D}${libdir}/libnvinfer_lean.so.${MAJVER}
    ln -s libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}/libnvinfer_dispatch.so
    ln -s libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}/libnvinfer_dispatch.so.${MAJVER}
    ln -s libnvinfer.so.${BASEVER} ${D}${libdir}/libnvinfer.so.${MAJVER}
    ln -s libnvinfer.so.${BASEVER} ${D}${libdir}/libnvinfer.so
}

INSANE_SKIP:${PN} = "already-stripped"

PACKAGES =+ "${PN}-dispatch-dev ${PN}-dispatch ${PN}-lean-dev ${PN}-lean"
FILES:${PN}-dispatch = "${libdir}/libnvinfer_dispatch${SOLIBS}"
FILES:${PN}-dispatch-dev = "${libdir}/libnvinfer_dispatch${SOLIBSDEV}"
RDEPENDS:${PN}-dispatch-dev = "${PN}-dispatch"
FILES:${PN}-lean = "${libdir}/libnvinfer_lean${SOLIBS}"
FILES:${PN}-lean-dev = "${libdir}/libnvinfer_lean${SOLIBSDEV}"
RDEPENDS:${PN}-lean-dev = "${PN}-lean"
RDEPENDS:${PN}-dev += "${PN}-dispatch-dev ${PN}-lean-dev"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
