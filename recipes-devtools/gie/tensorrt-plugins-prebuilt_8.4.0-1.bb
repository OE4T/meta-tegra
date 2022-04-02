DESCRIPTION = "NVIDIA TensorRT Prebuilt Plugins for deep learning"
LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed container-runtime-csv features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvonnxparsers8_${PV}+cuda11.4_arm64.deb;downloadfilename=libnvonnxparsers8_${PV}+cuda11.4_arm64.deb;name=onnx;subdir=tensorrt \
    libnvonnxparsers-dev_${PV}+cuda11.4_arm64.deb;downloadfilename=libnvonnxparsers-dev_${PV}+cuda11.4_arm64.deb;name=onnxdev;subdir=tensorrt \
    libnvinfer-plugin8_${PV}+cuda11.4_arm64.deb;downloadfilename=libnvinfer-plugin8_${PV}+cuda11.4_arm64.deb;name=plugin;subdir=tensorrt \
    libnvinfer-plugin-dev_${PV}+cuda11.4_arm64.deb;downloadfilename=libnvinfer-plugin-dev_${PV}+cuda11.4_arm64.deb;name=plugindev;subdir=tensorrt \
"

ONNXSHA256SUM = "419936472e8a9ff680696c9e41480441329e6170443f5740d72438da7a707637"
ONNXDEVSHA256SUM = "0b7f26bb2b3ab000d1a5c715779dd82dd01a738dff9057a8b76183e703c98623"
PLUGINSHA256SUM = "a740a9689ceec2d73de8e9193c96071a1b50d5da4f678f907afd928322ed4c67"
PLUGINDEVSHA256SUM = "d72a876c2c4b37077d2eea5803cdcaa02396b1e9e7b0efbf11a8b215a3706f95"

SRC_URI[onnx.sha256sum] = "${ONNXSHA256SUM}"
SRC_URI[onnxdev.sha256sum] = "${ONNXDEVSHA256SUM}"
SRC_URI[plugin.sha256sum] = "${PLUGINSHA256SUM}"
SRC_URI[plugindev.sha256sum] = "${PLUGINDEVSHA256SUM}"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInferPlugin.h;endline=48;md5=1243a04ad50f54138acd8a623c6d90cf"

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

DEPENDS = "cuda-cudart cudnn tensorrt-core libcublas"

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
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_plugin.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvonnxparser.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_plugin_static.a ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvonnxparser_static.a ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libonnx_proto.a ${D}${libdir}
    ln -s libnvinfer_plugin.so.${BASEVER} ${D}${libdir}/libnvinfer_plugin.so.${MAJVER}
    ln -s libnvinfer_plugin.so.${BASEVER} ${D}${libdir}/libnvinfer_plugin.so
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
