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

SRC_URI[onnx.sha256sum] = "64c34bd51d057c048baa7c055ab676e20bb30100f3d63d3eb0ef3032d6686d4f"
SRC_URI[onnxdev.sha256sum] = "c6345331c14119f0bd236b74c82dc967fdd6f9a55f489f626423b3d9268cdb55"
SRC_URI[plugin.sha256sum] = "0af6f955b17da27fc94284cdd39a67b6702452bc8bc8587f9f6a63be58ca1f71"
SRC_URI[plugindev.sha256sum] = "23a3e2f949ddbc266c5945b257f7be276171b6f19ec3d67d76857e890d36f42d"
SRC_URI[hplugindev.sha256sum] = "8e66fb552c748f7c890cf69eb7596d4a42ab48f916dfbc2036987a79aab43cd5"
SRC_URI[vc.sha256sum] = "732f161bbebe8b2d1567f2f3cb8e26de38ef469ab08bbfdad2edffa460696e19"
SRC_URI[vcdev.sha256sum] = "c39aa8d25d8be49e9cd17bc96f2dc45efa2bc8c8606cec0f33cc2ddede402944"

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
