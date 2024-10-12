DESCRIPTION = "NVIDIA TensorRT Prebuilt Plugins for deep learning"
LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvonnxparsers8_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvonnxparsers8_${PV}+cuda12.5_arm64.deb;name=onnx;subdir=tensorrt \
    libnvonnxparsers-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvonnxparsers-dev_${PV}+cuda12.5_arm64.deb;name=onnxdev;subdir=tensorrt \
    libnvinfer-plugin8_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-plugin8_${PV}+cuda12.5_arm64.deb;name=plugin;subdir=tensorrt \
    libnvinfer-plugin-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-plugin-dev_${PV}+cuda12.5_arm64.deb;name=plugindev;subdir=tensorrt \
    libnvinfer-headers-plugin-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-headers-plugin-dev_${PV}+cuda12.5_arm64.deb;name=hplugindev;subdir=tensorrt \
    libnvinfer-vc-plugin8_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-vc-plugin8_${PV}+cuda12.5_arm64.deb;name=vc;subdir=tensorrt \
    libnvinfer-vc-plugin-dev_${PV}+cuda12.5_arm64.deb;downloadfilename=libnvinfer-vc-plugin-dev_${PV}+cuda12.5_arm64.deb;name=vcdev;subdir=tensorrt \
"

SRC_URI[onnx.sha256sum] = "993a8c0290044ff82849b8799119da81af1c5b43b63d5aa090def3b0baf2ee1a"
SRC_URI[onnxdev.sha256sum] = "58fa4ad302c46948457d545c1ac93d8d6c509c3466bd903a88934d6af9a11672"
SRC_URI[plugin.sha256sum] = "b36da0b64794ae1d8a64aaff0d61a588aba1461bcb0b3e87b0d9672076255e5d"
SRC_URI[plugindev.sha256sum] = "32033dd29d0b04e2c3fe5e65dfbda2f3b61bb3869410e61f80e358603137f824"
SRC_URI[hplugindev.sha256sum] = "ce43de06fdcaa65bb11378df10973bf726aa1c83f0ee53709d53b5b8c5a2d09f"
SRC_URI[vc.sha256sum] = "f82f768196ed7d13de2241b1c370048fac9506a93fee9affbaaeab7c9f0d76e4"
SRC_URI[vcdev.sha256sum] = "316b7645afcaf638f3c6a03375b9fb4b71fbb5154c02d2c57843c1af67670aaf"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInferPlugin.h;endline=11;md5=117f6d17a39656035fa9d36b73ca4916"

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
