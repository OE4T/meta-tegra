DESCRIPTION = "NVIDIA TensorRT Core (GPU Inference Engine) for deep learning"
LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed container-runtime-csv features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvinfer8_${PV}+cuda11.4_arm64.deb;downloadfilename=libnvinfer8_${PV}+cuda11.4_arm64.deb;name=lib;subdir=tensorrt \
    libnvinfer-dev_${PV}+cuda11.4_arm64.deb;downloadfilename=libnvinfer-dev_${PV}+cuda11.4_arm64.deb;name=dev;subdir=tensorrt \
    libnvparsers8_${PV}+cuda11.4_arm64.deb;downloadfilename=libnvparsers8_${PV}+cuda11.4_arm64.deb;name=nvp;subdir=tensorrt \
    libnvparsers-dev_${PV}+cuda11.4_arm64.deb;downloadfilename=libnvparsers-dev_${PV}+cuda11.4_arm64.deb;name=nvpdev;subdir=tensorrt \
"
LIBSHA256SUM = "fc7410c58c99e01fe5e0449c2856ccf01cefb98565745d1f35f85c386f2d00c6"
DEVSHA256SUM = "4a12c7e5fb64a71b5fd5ad3e3df13b57240fc48d8d65361196779beeb7f119e1"
NVPSHA256SUM = "6a009e670ca4fae9d01649dffed3cfd8e2854959d39f00d4475729b19a27fb80"
NVPDEVSHA256SUM = "9d2d395484b9fe5daf9a1a954f7867a74fe77858fc61b203395c452f3ade5ccd"

SRC_URI[lib.sha256sum] = "${LIBSHA256SUM}"
SRC_URI[dev.sha256sum] = "${DEVSHA256SUM}"
SRC_URI[nvp.sha256sum] = "${NVPSHA256SUM}"
SRC_URI[nvpdev.sha256sum] = "${NVPDEVSHA256SUM}"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=11;md5=f099d2358ef8a23caa6cbf96136cac44"

REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "tegra-libraries-multimedia"

def extract_basever(d):
    ver = d.getVar('PV').split('-')[0]
    components = ver.split('.')
    return '%s.%s.%s' % (components[0], components[1], components[2])

def extract_majver(d):
    ver = d.getVar('PV').split('-')[0]
    return ver.split('.')[0]

BASEVER = "${@extract_basever(d)}"
MAJVER = "${@extract_majver(d)}"

S = "${WORKDIR}/tensorrt"

CONTAINER_CSV_FILES = "${libdir}/*.so*"

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
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvparsers.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_builder_resource.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_static.a ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvparsers_static.a ${D}${libdir}
    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvcaffe_parser.so.${MAJVER}
    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvcaffe_parser.so
    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvcaffe_parser.so.${BASEVER}
    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvparsers.so.${MAJVER}
    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvparsers.so
    ln -s libnvinfer.so.${BASEVER} ${D}${libdir}/libnvinfer.so.${MAJVER}
    ln -s libnvinfer.so.${BASEVER} ${D}${libdir}/libnvinfer.so
    ln -s libnvparsers_static.a ${D}${libdir}/libnvcaffe_parser.a
}

INSANE_SKIP:${PN} = "already-stripped"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
