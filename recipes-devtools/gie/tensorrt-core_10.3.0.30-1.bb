DESCRIPTION = "NVIDIA TensorRT Core (GPU Inference Engine) for deep learning"
LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvinfer8_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer8_${PV}+cuda12.5_arm64.deb;name=lib;subdir=tensorrt \
    libnvinfer-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-dev_${PV}+cuda12.5_arm64.deb;name=dev;subdir=tensorrt \
    libnvinfer-headers-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-headers-dev_${PV}+cuda12.5_arm64.deb;name=hdev;subdir=tensorrt \
    libnvinfer-dispatch8_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-dispatch8_${PV}+cuda12.5_arm64.deb;name=disp;subdir=tensorrt \
    libnvinfer-dispatch-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-dispatch-dev_${PV}+cuda12.5_arm64.deb;name=dispdev;subdir=tensorrt \
    libnvparsers8_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvparsers8_${PV}+cuda12.5_arm64.deb;name=nvp;subdir=tensorrt \
    libnvparsers-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvparsers-dev_${PV}+cuda12.5_arm64.deb;name=nvpdev;subdir=tensorrt \
    libnvinfer-lean8_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-lean8_${PV}+cuda12.5_arm64.deb;name=nvl;subdir=tensorrt \
    libnvinfer-lean-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-lean-dev_${PV}+cuda12.5_arm64.deb;name=nvldev;subdir=tensorrt \
"

SRC_URI[lib.sha256sum] = "eea369672f2398a9973fdbed34d8d003b3c8aebca12b764bd10c8ae9242f8544"
SRC_URI[dev.sha256sum] = "b24b0c9d09fda80b2b9d4fdee6f02deb513e3f876f94e4119caa538a68964e15"
SRC_URI[hdev.sha256sum] = "033392f7454439f3f01044753ceb21c835a6a153cbce13325bd1c9582e730d1f"
SRC_URI[disp.sha256sum] = "e8ed32add12a305df9419e4b9c1292b10618d2c2306621d38ae81d7ebe6bc313"
SRC_URI[dispdev.sha256sum] = "fd5b91dacbfb2d074bb554f6d7f796558a19255c25cb15609eb4ea7110a0ffe1"
SRC_URI[nvp.sha256sum] = "97f64f4ecaf57ada8ddf7104d6458a179d1906a852722eddc849853fc7e19d21"
SRC_URI[nvpdev.sha256sum] = "ba07db5637c1b80920a7cc6f6fabd2503014036a3b24b837b393e3c8ac9b5282"
SRC_URI[nvl.sha256sum] = "b6ce879fe2a85cc96bea66235a83bacf8c092afe3b98dd512edb5292dde5a8e1"
SRC_URI[nvldev.sha256sum] = "5e76b7970fbc2132e7b36fc2ca23af4c872e690e84321a4eb17391dc5486f355"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=11;md5=117f6d17a39656035fa9d36b73ca4916"

REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "tegra-libraries-multimedia libcudla tegra-libraries-dla-compiler"

def extract_basever(d):
    ver = d.getVar('PV').split('-')[0]
    components = ver.split('.')
    return '.'.join(components[:3])

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
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_lean.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_static.a ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvparsers_static.a ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_dispatch_static.a ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_lean_static.a ${D}${libdir}

    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvcaffe_parser.so.${MAJVER}
    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvcaffe_parser.so
    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvcaffe_parser.so.${BASEVER}
    ln -s libnvinfer_lean.so.${BASEVER} ${D}${libdir}/libnvinfer_lean.so
    ln -s libnvinfer_lean.so.${BASEVER} ${D}${libdir}/libnvinfer_lean.so.${MAJVER}
    ln -s libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}/libnvinfer_dispatch.so
    ln -s libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}/libnvinfer_dispatch.so.${MAJVER}
    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvparsers.so.${MAJVER}
    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvparsers.so
    ln -s libnvinfer.so.${BASEVER} ${D}${libdir}/libnvinfer.so.${MAJVER}
    ln -s libnvinfer.so.${BASEVER} ${D}${libdir}/libnvinfer.so
    ln -s libnvparsers_static.a ${D}${libdir}/libnvcaffe_parser.a

    install -d ${D}${libdir}/stubs
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/stubs/libcublasLt_static_stub_trt.a ${D}${libdir}/stubs
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/stubs/libcublas_static_stub_trt.a ${D}${libdir}/stubs
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/stubs/libcudnn_static_stub_trt.a ${D}${libdir}/stubs
}

FILES:${PN}-staticdev += "${libdir}/stubs"

INSANE_SKIP:${PN} = "already-stripped"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
