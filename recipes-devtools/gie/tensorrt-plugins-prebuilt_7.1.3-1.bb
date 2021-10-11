DESCRIPTION = "NVIDIA TensorRT Prebuilt Plugins for deep learning"
LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed container-runtime-csv

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

PREFIX = "NoDLA-"
PREFIX_tegra194 = "DLA-"

L4T_DEB_GROUP = "tensorrt"

SRC_SOC_DEBS = "\
    libnvonnxparsers7_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvonnxparsers7_${PV}+cuda10.2_arm64.deb;name=onnx;subdir=tensorrt \
    libnvonnxparsers-dev_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvonnxparsers-dev_${PV}+cuda10.2_arm64.deb;name=onnxdev;subdir=tensorrt \
    libnvinfer-plugin7_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvinfer-plugin7_${PV}+cuda10.2_arm64.deb;name=plugin;subdir=tensorrt \
    libnvinfer-plugin-dev_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvinfer-plugin-dev_${PV}+cuda10.2_arm64.deb;name=plugindev;subdir=tensorrt \
    libnvinfer-bin_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvinfer-bin_${PV}+cuda10.2_arm64.deb;name=bin;subdir=tensorrt \
"

ONNXSHA256SUM = "e00ed2bff48de16ca275ce322fbc881b5bbd4a1521fd7f685d56cfb30136039a"
ONNXDEVSHA256SUM = "8c18ffac6b9118491248e14000cb72638268ce4f3698522a478cac6dd4631c34"
PLUGINSHA256SUM = "a57ea8b4757fa4592c6ba1555bc07045909b553a493c0bde8c8b23c74cf082b4"
PLUGINDEVSHA256SUM = "94820301980118e34cc1ab99570d3a7a13b96f4811828908b63bf638b4252edd"
BINSHA256SUM = "94927076974c59ae45b0c56300387816b8fa03d397d1503d8633e7989b4a20f6"

ONNXSHA256SUM_tegra194 = "26109c58e8eab9dc746fb3fc39cc39bf5a8bf2d61a27b5d9e7aa035fc0b97b4c"
ONNXDEVSHA256SUM_tegra194 = "52783ca7245171eb83c623a08851c0cfdc659696272b2edf6dfe3503ae9bc49b"
PLUGINSHA256SUM_tegra194 = "680c7849542dca1cac68fc94f7474f31446cf093b6e6df1d7bab8c24a6122aa3"
PLUGINDEVSHA256SUM_tegra194 = "bac84671b2990b8110d4f2ac69b4a29cf230346db37a902d1f47cf7a9be4601e"
BINSHA256SUM_tegra194 = "56275d8cba17be877af063db49ecb7b6e02bed5f8d387b1182a6a7ec2cabee2a"

SRC_URI[onnx.sha256sum] = "${ONNXSHA256SUM}"
SRC_URI[onnxdev.sha256sum] = "${ONNXDEVSHA256SUM}"
SRC_URI[plugin.sha256sum] = "${PLUGINSHA256SUM}"
SRC_URI[plugindev.sha256sum] = "${PLUGINDEVSHA256SUM}"
SRC_URI[bin.sha256sum] = "${BINSHA256SUM}"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInferPlugin.h;endline=48;md5=59218f2f10ab9e4132dda76c59e80fa1"

S = "${WORKDIR}/tensorrt"

DEPENDS = "cuda-cudart cudnn tegra-libraries tensorrt-core"

CONTAINER_CSV_FILES = "${libdir}/*.so* /usr/src/*"

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
    install -d ${D}${prefix}/src
    cp --preserve=mode,timestamps --recursive ${S}/usr/src/tensorrt ${D}${prefix}/src/
}

FILES_${PN} += "${prefix}/src/tensorrt/bin"

RDEPENDS_${PN} += "tegra-libraries"
PROVIDES = "tensorrt-plugins"
RPROVIDES_${PN} = "tensorrt-plugins"
RCONFLICTS_${PN} = "tensorrt-plugins"
RPROVIDES_${PN}-dev = "tensorrt-plugins-dev"
RCONFLICTS_${PN}-dev = "tensorrt-plugins-dev"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
PACKAGE_ARCH_tegra194 = "${SOC_FAMILY_PKGARCH}"
