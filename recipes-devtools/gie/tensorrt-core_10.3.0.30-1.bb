DESCRIPTION = "NVIDIA TensorRT Core (GPU Inference Engine) for deep learning"
LICENSE = "Apache-2.0"

inherit l4t_deb_pkgfeed features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvinfer10_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer10_${PV}+cuda12.5_arm64.deb;name=lib;subdir=tensorrt \
    libnvinfer-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-dev_${PV}+cuda12.5_arm64.deb;name=dev;subdir=tensorrt \
    libnvinfer-headers-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-headers-dev_${PV}+cuda12.5_arm64.deb;name=hdev;subdir=tensorrt \
    libnvinfer-dispatch10_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-dispatch10_${PV}+cuda12.5_arm64.deb;name=disp;subdir=tensorrt \
    libnvinfer-dispatch-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-dispatch-dev_${PV}+cuda12.5_arm64.deb;name=dispdev;subdir=tensorrt \
    libnvinfer-lean10_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-lean10_${PV}+cuda12.5_arm64.deb;name=nvl;subdir=tensorrt \
    libnvinfer-lean-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-lean-dev_${PV}+cuda12.5_arm64.deb;name=nvldev;subdir=tensorrt \
"

SRC_URI[lib.sha256sum] = "fabaad3ac8a50cf25da83a25938844808be4a3a43c2e76ea0b70b9cd2c6234bc"
SRC_URI[dev.sha256sum] = "b24b0c9d09fda80b2b9d4fdee6f02deb513e3f876f94e4119caa538a68964e15"
SRC_URI[hdev.sha256sum] = "40a4fa566218f71176144a0eafa5aef8cf0af7ee9211b20487f1970d5344bbb8"
SRC_URI[disp.sha256sum] = "0a39d0e37017d677ee05e122ae3bad500e7d819c4c11ea3c92197dcc62e322bc"
SRC_URI[dispdev.sha256sum] = "a87c324e438bcc669c86c7e959ba9fb6827cd0d3122a1d730b2224f7ca82c91a"
SRC_URI[nvl.sha256sum] = "27e2d97fd79f1a0a6f864d6a2ec234be72c173f48dbfa8788b38e89df49261de"
SRC_URI[nvldev.sha256sum] = "3c8bba2c5a5fdac9e57ac13490ca42eb79ae9caf8ca1a07bd94a9f7ed10d370b"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=16;md5=cac95541e748626c31067a3f6573562f"

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
