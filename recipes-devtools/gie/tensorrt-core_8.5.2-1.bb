DESCRIPTION = "NVIDIA TensorRT Core (GPU Inference Engine) for deep learning"
LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvinfer8_${PV}+cuda11.4_arm64.deb;downloadfilename=libnvinfer8_${PV}+cuda11.4_arm64.deb;name=lib;subdir=tensorrt \
    libnvinfer-dev_${PV}+cuda11.4_arm64.deb;downloadfilename=libnvinfer-dev_${PV}+cuda11.4_arm64.deb;name=dev;subdir=tensorrt \
    libnvparsers8_${PV}+cuda11.4_arm64.deb;downloadfilename=libnvparsers8_${PV}+cuda11.4_arm64.deb;name=nvp;subdir=tensorrt \
    libnvparsers-dev_${PV}+cuda11.4_arm64.deb;downloadfilename=libnvparsers-dev_${PV}+cuda11.4_arm64.deb;name=nvpdev;subdir=tensorrt \
"
LIBSHA256SUM = "55012013528106fca0fb0fd3dbf73a54b8e441567fe7144d1523e5609f036c31"
DEVSHA256SUM = "14ebffd144def765fdb3560c342984a9266761f3dbe7b3f8034f0fae7908360b"
NVPSHA256SUM = "5ef03cac6744145037eb6ea00b63eaf4d99a04c41800fc5d76252d55bcc09801"
NVPDEVSHA256SUM = "8d19d9cab1460564437c1d0e9b8a4f9a5ae93d949935a6cbbed0df6b6dafcd29"

SRC_URI[lib.sha256sum] = "${LIBSHA256SUM}"
SRC_URI[dev.sha256sum] = "${DEVSHA256SUM}"
SRC_URI[nvp.sha256sum] = "${NVPSHA256SUM}"
SRC_URI[nvpdev.sha256sum] = "${NVPDEVSHA256SUM}"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=11;md5=ddd2b55ba0ff98b2a932ed025ffe3f25"

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
