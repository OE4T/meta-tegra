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
LIBSHA256SUM = "1e1fb1f03c4c05ceb2cd1d33fdff15a11090812b5f51d0b028ad7d16058c6741"
DEVSHA256SUM = "d1aadb326ce73f7b0c739e3eb2351ccdd358b066fbd3ed7d0eff65b2e362b6db"
NVPSHA256SUM = "0412a6326abcf9a6389bfd933333724675867803b1efe3e4cdc20be9443a4a0d"
NVPDEVSHA256SUM = "b06a5adab609b2975e56bef10d93690e9ee34939f86686af1b3e992b51512fe7"

SRC_URI[lib.sha256sum] = "${LIBSHA256SUM}"
SRC_URI[dev.sha256sum] = "${DEVSHA256SUM}"
SRC_URI[nvp.sha256sum] = "${NVPSHA256SUM}"
SRC_URI[nvpdev.sha256sum] = "${NVPDEVSHA256SUM}"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=48;md5=1243a04ad50f54138acd8a623c6d90cf"

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
