DESCRIPTION = "NVIDIA TensorRT Prebuilt Plugins for deep learning"
LICENSE = "Apache-2.0"

inherit l4t_deb_pkgfeed features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvonnxparsers10_${PV}+cuda13.0_arm64.deb;downloadfilename=libnvonnxparsers10_${PV}+cuda13.0_arm64.deb;name=onnx;subdir=tensorrt \
    libnvonnxparsers-dev_${PV}+cuda13.0_arm64.deb;downloadfilename=libnvonnxparsers-dev_${PV}+cuda13.0_arm64.deb;name=onnxdev;subdir=tensorrt \
    libnvinfer-plugin10_${PV}+cuda13.0_arm64.deb;downloadfilename=libnvinfer-plugin10_${PV}+cuda13.0_arm64.deb;name=plugin;subdir=tensorrt \
    libnvinfer-plugin-dev_${PV}+cuda13.0_arm64.deb;downloadfilename=libnvinfer-plugin-dev_${PV}+cuda13.0_arm64.deb;name=plugindev;subdir=tensorrt \
    libnvinfer-headers-plugin-dev_${PV}+cuda13.0_arm64.deb;downloadfilename=libnvinfer-headers-plugin-dev_${PV}+cuda13.0_arm64.deb;name=hplugindev;subdir=tensorrt \
    libnvinfer-vc-plugin10_${PV}+cuda13.0_arm64.deb;downloadfilename=libnvinfer-vc-plugin10_${PV}+cuda13.0_arm64.deb;name=vc;subdir=tensorrt \
    libnvinfer-vc-plugin-dev_${PV}+cuda13.0_arm64.deb;downloadfilename=libnvinfer-vc-plugin-dev_${PV}+cuda13.0_arm64.deb;name=vcdev;subdir=tensorrt \
"

SRC_URI[onnx.sha256sum] = "0128fecb00bb7d7f59d2816b803f19653f921729ba06eb61da2cb777a95050ae"
SRC_URI[onnxdev.sha256sum] = "c68edca25b9480b8515e0747962f510ac81f95e60ef762c3319da637250b7733"
SRC_URI[plugin.sha256sum] = "4673987380a8e0117076c37b80189b7cec1145f46561e19c268479864ca55738"
SRC_URI[plugindev.sha256sum] = "692ae002e3a9ba1c4d3b90c89d7d2f83bbc958322c1977da9f4489d69264dde1"
SRC_URI[hplugindev.sha256sum] = "e15f944f2fc86c0dfcb8811cedd1ef063f5f101a48e42b0c768f743bffb4f915"
SRC_URI[vc.sha256sum] = "7dac97d1596e40217134b7328570d053a2f26ee0a3b0518837348129c30ae750"
SRC_URI[vcdev.sha256sum] = "d840c502211925604e3de5898fe429bad85117345175494a7d44f881455e2787"

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

S = "${UNPACKDIR}/tensorrt"

DEPENDS = "cuda-cudart tensorrt-core"

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

RDEPENDS:${PN} += "cudnn libcublas"
PROVIDES = "tensorrt-plugins"
RPROVIDES:${PN} = "tensorrt-plugins"
RCONFLICTS:${PN} = "tensorrt-plugins"
RPROVIDES:${PN}-dev = "tensorrt-plugins-dev"
RCONFLICTS:${PN}-dev = "tensorrt-plugins-dev"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
