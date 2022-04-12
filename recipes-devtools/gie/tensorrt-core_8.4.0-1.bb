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
LIBSHA256SUM = "528e3a668962ab26d78047e401d5da7810b6d09cc8e14ad64420cf0acf77a6ea"
DEVSHA256SUM = "a5da680fa77135982db8d9261e9deca10fa032266a51a711df355c85c73ffbdb"
NVPSHA256SUM = "ceff97a7b37a478c38ff74c59bb32ec1e49b106c2e7c3bef8d80103f04cfd145"
NVPDEVSHA256SUM = "f3281b12a1be13a600b15fe07700ba85c79254a82b0926ed9a72d6a290d905a7"

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
