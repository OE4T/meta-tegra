DESCRIPTION = "NVIDIA TensorRT Prebuilt Plugins for deep learning"
LICENSE = "Apache-2.0"

inherit l4t_deb_pkgfeed features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvonnxparsers11_${PV}+cuda13.4_arm64.deb;downloadfilename=libnvonnxparsers11_${PV}+cuda13.4_arm64.deb;name=onnx;subdir=tensorrt \
    libnvonnxparsers-dev_${PV}+cuda13.4_arm64.deb;downloadfilename=libnvonnxparsers-dev_${PV}+cuda13.4_arm64.deb;name=onnxdev;subdir=tensorrt \
    libnvinfer-plugin11_${PV}+cuda13.4_arm64.deb;downloadfilename=libnvinfer-plugin11_${PV}+cuda13.4_arm64.deb;name=plugin;subdir=tensorrt \
    libnvinfer-plugin-dev_${PV}+cuda13.4_arm64.deb;downloadfilename=libnvinfer-plugin-dev_${PV}+cuda13.4_arm64.deb;name=plugindev;subdir=tensorrt \
    libnvinfer-headers-plugin-dev_${PV}+cuda13.4_arm64.deb;downloadfilename=libnvinfer-headers-plugin-dev_${PV}+cuda13.4_arm64.deb;name=hplugindev;subdir=tensorrt \
    libnvinfer-vc-plugin11_${PV}+cuda13.4_arm64.deb;downloadfilename=libnvinfer-vc-plugin11_${PV}+cuda13.4_arm64.deb;name=vc;subdir=tensorrt \
    libnvinfer-vc-plugin-dev_${PV}+cuda13.4_arm64.deb;downloadfilename=libnvinfer-vc-plugin-dev_${PV}+cuda13.4_arm64.deb;name=vcdev;subdir=tensorrt \
"

SRC_URI[onnx.sha256sum] = "dec89bd74309323951d5b64ad46cd1cf8d7c870615f04930f8c27cc1fbe9ffb6"
SRC_URI[onnxdev.sha256sum] = "f91f9b87dd3f45b7b3d12a86373835a08e69cebe01f416a6aacc53ecbf84ea37"
SRC_URI[plugin.sha256sum] = "2c4cbe20662f25e264a245b5574afaa93617e7c1a9f99510381af2706a893df2"
SRC_URI[plugindev.sha256sum] = "708f3317d2a09413aeaebcac6d145ddc52dedcdf936605073689f58dbfccd7ae"
SRC_URI[hplugindev.sha256sum] = "f56964186b9e91af19eaa95099ef1ba6a6676a00097d81c10afadc9a72322723"
SRC_URI[vc.sha256sum] = "d0ebbf49f9983a4bace0655db41fc6fc1c61b75758f14402af660d94bce75a64"
SRC_URI[vcdev.sha256sum] = "487ad37b9e766f8e0e9fa3e0420685431fa66f5d40f205171d14779948b01a87"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInferPlugin.h;endline=16;md5=cac95541e748626c31067a3f6573562f"

require tensorrt-version.inc

S = "${UNPACKDIR}/tensorrt"

DEPENDS = "cuda-cudart tensorrt"

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

CVE_PRODUCT = "nvidia:tensorrt"
CVE_VERSION = "${BASEVER}"
