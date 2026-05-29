DESCRIPTION = "NVIDIA TensorRT Prebuilt Plugins for deep learning"
LICENSE = "Apache-2.0"

inherit l4t_deb_pkgfeed features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvonnxparsers10_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvonnxparsers10_${PV}+cuda13.2_arm64.deb;name=onnx;subdir=tensorrt \
    libnvonnxparsers-dev_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvonnxparsers-dev_${PV}+cuda13.2_arm64.deb;name=onnxdev;subdir=tensorrt \
    libnvinfer-plugin10_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-plugin10_${PV}+cuda13.2_arm64.deb;name=plugin;subdir=tensorrt \
    libnvinfer-plugin-dev_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-plugin-dev_${PV}+cuda13.2_arm64.deb;name=plugindev;subdir=tensorrt \
    libnvinfer-headers-plugin-dev_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-headers-plugin-dev_${PV}+cuda13.2_arm64.deb;name=hplugindev;subdir=tensorrt \
    libnvinfer-vc-plugin10_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-vc-plugin10_${PV}+cuda13.2_arm64.deb;name=vc;subdir=tensorrt \
    libnvinfer-vc-plugin-dev_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-vc-plugin-dev_${PV}+cuda13.2_arm64.deb;name=vcdev;subdir=tensorrt \
"

SRC_URI[onnx.sha256sum] = "f146cbcc4d5d0d29f2c0123dd6a4b16ed1159eff27d03beeab27bbe9a766bff8"
SRC_URI[onnxdev.sha256sum] = "53bcf84f06c20f2b171379db79389d6680775a94c761b57ea7480134d0c038a7"
SRC_URI[plugin.sha256sum] = "a19ba2f43860e768045947f59a63fecd1464feeba353bfb6a096985b3a634710"
SRC_URI[plugindev.sha256sum] = "4552160fed001a990daceca670a9e562470b794cd7f74b354d016bf4ba4efbf7"
SRC_URI[hplugindev.sha256sum] = "951ebb46c7a33ff71dca064e62fab19c8ebdc4b895aa14027e6907113aa3e928"
SRC_URI[vc.sha256sum] = "d7490d02f1ec286843bf796fbd4d3d464ca963d2cf9ceefca1ae7b2b14a4203b"
SRC_URI[vcdev.sha256sum] = "21b9df45051d1d7b956629b6638ecddb1a91c22c1a96be8222d88243883b336a"

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
