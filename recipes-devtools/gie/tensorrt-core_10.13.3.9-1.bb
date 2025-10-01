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

SRC_URI[lib.sha256sum] = "c153dd67175d12661437d5bc9b573344467bc716c03133bc77ed1befa820eac6"
SRC_URI[dev.sha256sum] = "40b2b769b6f5c77684d5feec2ca01e87cbb3d0e1ff8b41c679e264f7d98b65e8"
SRC_URI[hdev.sha256sum] = "61baa587776b5a074fc4bc794836b32c3fb4d0573ce7610389fc65f58ac0336b"
SRC_URI[disp.sha256sum] = "da615f77ffd29b14652dc62658089b29cc9cde4560af9991d54f12d45a64b0cd"
SRC_URI[dispdev.sha256sum] = "7d7a89a6873dbcb7640e39d0f1819f39d5854e6a81a56d3763efc98a43eff80b"
SRC_URI[nvl.sha256sum] = "85a6ecce9b42d5dac0a9a8c1df2e03e90410b98c6cc99d16aa90ed8fef657ea1"
SRC_URI[nvldev.sha256sum] = "a84eea17806bef0de03c222a8a3952d6aec2e05e162ae5cc9cdeda5b28982997"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=16;md5=57233dd35fa68de1b2e210aced617156"

REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "tegra-libraries-multimedia"
DEPENDS:append:tegra234 = " tegra-libraries-dla-compiler"

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
