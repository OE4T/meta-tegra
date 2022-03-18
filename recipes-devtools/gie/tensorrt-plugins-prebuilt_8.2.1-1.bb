DESCRIPTION = "NVIDIA TensorRT Prebuilt Plugins for deep learning"
LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed container-runtime-csv features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvonnxparsers8_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvonnxparsers8_${PV}+cuda10.2_arm64.deb;name=onnx;subdir=tensorrt \
    libnvonnxparsers-dev_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvonnxparsers-dev_${PV}+cuda10.2_arm64.deb;name=onnxdev;subdir=tensorrt \
    libnvinfer-plugin8_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvinfer-plugin8_${PV}+cuda10.2_arm64.deb;name=plugin;subdir=tensorrt \
    libnvinfer-plugin-dev_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvinfer-plugin-dev_${PV}+cuda10.2_arm64.deb;name=plugindev;subdir=tensorrt \
"

ONNXSHA256SUM = "d7c27711b73e4cc3febb21969d21a84cd92e21a7863da75d0a72b50ea2c12833"
ONNXDEVSHA256SUM = "e50047d41ceb8341bdaa67f9df3e365257d80b520ba68af340b18ec8a238366b"
PLUGINSHA256SUM = "ea0bcf03218d1c22fdb563f3db45e00829234712f05ce71866c9486bc1dcadaf"
PLUGINDEVSHA256SUM = "e357606f5884608cbb07a0dfe64c48490746e90e6eaf91bbed2324623e368b7c"
BINSHA256SUM = "738904f63c16498da9e9789dd4c99a9be926a2809c30cb45c2a97877b6e3c3d7"

SRC_URI[onnx.sha256sum] = "${ONNXSHA256SUM}"
SRC_URI[onnxdev.sha256sum] = "${ONNXDEVSHA256SUM}"
SRC_URI[plugin.sha256sum] = "${PLUGINSHA256SUM}"
SRC_URI[plugindev.sha256sum] = "${PLUGINDEVSHA256SUM}"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInferPlugin.h;endline=48;md5=3d6981c1227c404d42d710f96a875a1b"

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
    cp --preserve=mode,timestamps,links --no-dereference ${S}/usr/lib/aarch64-linux-gnu/*.so* ${D}${libdir}
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
