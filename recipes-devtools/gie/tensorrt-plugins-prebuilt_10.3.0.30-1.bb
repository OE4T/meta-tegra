DESCRIPTION = "NVIDIA TensorRT Prebuilt Plugins for deep learning"
LICENSE = "Apache-2.0"

inherit l4t_deb_pkgfeed features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvonnxparsers10_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvonnxparsers10_${PV}+cuda12.5_arm64.deb;name=onnx;subdir=tensorrt \
    libnvonnxparsers-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvonnxparsers-dev_${PV}+cuda12.5_arm64.deb;name=onnxdev;subdir=tensorrt \
    libnvinfer-plugin10_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-plugin10_${PV}+cuda12.5_arm64.deb;name=plugin;subdir=tensorrt \
    libnvinfer-plugin-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-plugin-dev_${PV}+cuda12.5_arm64.deb;name=plugindev;subdir=tensorrt \
    libnvinfer-headers-plugin-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-headers-plugin-dev_${PV}+cuda12.5_arm64.deb;name=hplugindev;subdir=tensorrt \
    libnvinfer-vc-plugin10_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-vc-plugin10_${PV}+cuda12.5_arm64.deb;name=vc;subdir=tensorrt \
    libnvinfer-vc-plugin-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-vc-plugin-dev_${PV}+cuda12.5_arm64.deb;name=vcdev;subdir=tensorrt \
"

SRC_URI[onnx.sha256sum] = "042e21f28a271934d3c497504b41533e25f044893474e7567e3042e803384950"
SRC_URI[onnxdev.sha256sum] = "8a6675f3d444dead0e0430fb4e7e6b546af9d9a72414ee8964b18edf1fb28756"
SRC_URI[plugin.sha256sum] = "709a8b3111e4d5966abaa806de3282c59586e175f3db9898f3f2a3bf93a5886b"
SRC_URI[plugindev.sha256sum] = "d61e98f8f477143a5a211bc01b3f240875700af591cc117b77af44f18433a941"
SRC_URI[hplugindev.sha256sum] = "ce43de06fdcaa65bb11378df10973bf726aa1c83f0ee53709d53b5b8c5a2d09f"
SRC_URI[vc.sha256sum] = "318238a61b8a434ed54d148c05b31bebca0ad072657721a596190177fc1306d5"
SRC_URI[vcdev.sha256sum] = "86bc05dc389a94146c698e175505c7ca6b45d54d87f08865caf39fa92d0771c9"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInferPlugin.h;endline=16;md5=cac95541e748626c31067a3f6573562f"

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

DEPENDS = "cuda-cudart cudnn tensorrt-core libcublas"

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
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_plugin.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvonnxparser.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_vc_plugin.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_plugin_static.a ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvonnxparser_static.a ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_vc_plugin_static.a ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libonnx_proto.a ${D}${libdir}

    ln -s libnvinfer_plugin.so.${BASEVER} ${D}${libdir}/libnvinfer_plugin.so.${MAJVER}
    ln -s libnvinfer_plugin.so.${BASEVER} ${D}${libdir}/libnvinfer_plugin.so
    ln -s libnvinfer_vc_plugin.so.${BASEVER} ${D}${libdir}/libnvinfer_vc_plugin.so.${MAJVER}
    ln -s libnvinfer_vc_plugin.so.${BASEVER} ${D}${libdir}/libnvinfer_vc_plugin.so
    ln -s libnvonnxparser.so.${BASEVER} ${D}${libdir}/libnvonnxparser.so.${MAJVER}
    ln -s libnvonnxparser.so.${MAJVER} ${D}${libdir}/libnvonnxparser.so
}

PROVIDES = "tensorrt-plugins"
RPROVIDES:${PN} = "tensorrt-plugins"
RCONFLICTS:${PN} = "tensorrt-plugins"
RPROVIDES:${PN}-dev = "tensorrt-plugins-dev"
RCONFLICTS:${PN}-dev = "tensorrt-plugins-dev"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
