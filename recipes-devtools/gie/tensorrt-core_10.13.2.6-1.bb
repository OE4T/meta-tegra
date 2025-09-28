DESCRIPTION = "NVIDIA TensorRT Core (GPU Inference Engine) for deep learning"
LICENSE = "Apache-2.0"

inherit l4t_deb_pkgfeed features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvinfer10_${PV}+cuda13.0_arm64.deb;downloadfilename=libnvinfer10_${PV}+cuda13.0_arm64.deb;name=lib;subdir=tensorrt \
    libnvinfer-dev_${PV}+cuda13.0_arm64.deb;downloadfilename=libnvinfer-dev_${PV}+cuda13.0_arm64.deb;name=dev;subdir=tensorrt \
    libnvinfer-headers-dev_${PV}+cuda13.0_arm64.deb;downloadfilename=libnvinfer-headers-dev_${PV}+cuda13.0_arm64.deb;name=hdev;subdir=tensorrt \
    libnvinfer-dispatch10_${PV}+cuda13.0_arm64.deb;downloadfilename=libnvinfer-dispatch10_${PV}+cuda13.0_arm64.deb;name=disp;subdir=tensorrt \
    libnvinfer-dispatch-dev_${PV}+cuda13.0_arm64.deb;downloadfilename=libnvinfer-dispatch-dev_${PV}+cuda13.0_arm64.deb;name=dispdev;subdir=tensorrt \
    libnvinfer-lean10_${PV}+cuda13.0_arm64.deb;downloadfilename=libnvinfer-lean10_${PV}+cuda13.0_arm64.deb;name=nvl;subdir=tensorrt \
    libnvinfer-lean-dev_${PV}+cuda13.0_arm64.deb;downloadfilename=libnvinfer-lean-dev_${PV}+cuda13.0_arm64.deb;name=nvldev;subdir=tensorrt \
"

SRC_URI[lib.sha256sum] = "9ee71924fe29c00929eb9cdafaf0354658ea2733959f574a1a70355765e26900"
SRC_URI[dev.sha256sum] = "30f989a8cb99c0a1ff7d25c9514ba96445be6f32489e520a54ec1b0a899c9d8f"
SRC_URI[hdev.sha256sum] = "faf32d2a0533a4c96c7f7f1d06905abf44a1cebd2e60aec201f21bb38f521f29"
SRC_URI[disp.sha256sum] = "4c95231e0e2c50eb62db053855e7561852aaf7d91200e26c3465efdf0f7bad5b"
SRC_URI[dispdev.sha256sum] = "f56c82f7f861718b83caeee5d9cd526a5f0488126d833e8d31cb6a8addd70b25"
SRC_URI[nvl.sha256sum] = "9cd7d049ddcb309fa2f932c276b1e699afddfa8a5443ccc13c9c7747accaa7b8"
SRC_URI[nvldev.sha256sum] = "411861e0703860828dabd059328be281ece6b45c173f090004285df172a27916"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=16;md5=57233dd35fa68de1b2e210aced617156"

REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "tegra-libraries-multimedia tegra-libraries-dla-compiler"

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
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_builder_resource.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_lean.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_static.a ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_dispatch_static.a ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_lean_static.a ${D}${libdir}

    ln -s libnvinfer_lean.so.${BASEVER} ${D}${libdir}/libnvinfer_lean.so
    ln -s libnvinfer_lean.so.${BASEVER} ${D}${libdir}/libnvinfer_lean.so.${MAJVER}
    ln -s libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}/libnvinfer_dispatch.so
    ln -s libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}/libnvinfer_dispatch.so.${MAJVER}
    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvparsers.so.${MAJVER}
    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvparsers.so
    ln -s libnvinfer.so.${BASEVER} ${D}${libdir}/libnvinfer.so.${MAJVER}
    ln -s libnvinfer.so.${BASEVER} ${D}${libdir}/libnvinfer.so
}

INSANE_SKIP:${PN} = "already-stripped"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
