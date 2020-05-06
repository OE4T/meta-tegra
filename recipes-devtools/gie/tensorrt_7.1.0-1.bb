DESCRIPTION = "NVIDIA TensorRT (GPU Inference Engine) for deep learning"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed container-runtime-csv

PREFIX = "NoDLA-"
PREFIX_tegra194 = "DLA-"

SRC_SOC_DEBS = "\
    libnvinfer7_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvinfer7_${PV}+cuda10.2_arm64.deb;name=lib;subdir=tensorrt \
    libnvinfer-dev_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvinfer-dev_${PV}+cuda10.2_arm64.deb;name=dev;subdir=tensorrt \
    libnvinfer-samples_${PV}+cuda10.2_all.deb;downloadfilename=${PREFIX}libnvinfer-samples_${PV}+cuda10.2_all.deb;name=samples;subdir=tensorrt \
    libnvparsers7_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvparsers7_${PV}+cuda10.2_arm64.deb;name=nvp;subdir=tensorrt \
    libnvparsers-dev_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvparsers-dev_${PV}+cuda10.2_arm64.deb;name=nvpdev;subdir=tensorrt \
    libnvonnxparsers7_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvonnxparsers7_${PV}+cuda10.2_arm64.deb;name=onnx;subdir=tensorrt \
    libnvonnxparsers-dev_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvonnxparsers-dev_${PV}+cuda10.2_arm64.deb;name=onnxdev;subdir=tensorrt \
    libnvinfer-plugin7_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvinfer-plugin7_${PV}+cuda10.2_arm64.deb;name=plugin;subdir=tensorrt \
    libnvinfer-plugin-dev_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvinfer-plugin-dev_${PV}+cuda10.2_arm64.deb;name=plugindev;subdir=tensorrt \
    libnvinfer-bin_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvinfer-bin_${PN}+cuda10.2_arm64.deb;name=bin;subdir=tensorrt \
"

LIBSHA256SUM = "1017dd614035f3a6e0d0582e994e8e0c7d45e474b1f76920aab739320deff052"
DEVSHA256SUM = "2210f0442642cc1a3799840991513a5ce0e9939b1b6231414c09cb7548b1a3ff"
SAMPSHA256SUM = "7a42428fdba65a4998ce347f124487ed0ebb48fd816cab1bd07c43cf84ba5843"
NVPSHA256SUM = "69094cf95367f992d39d6eea66dfb95513d9b7c768c525a3d7c5909596268562"
NVPDEVSHA256SUM = "58610f54e547aa56eb8945e0fe814536f0b5f9837113372e29b529fe028fdb40"
ONNXSHA256SUM = "a1bb1f4c6d8acf38a153781b18609b603c984e53fdaf55d23c15d03809341972"
ONNXDEVSHA256SUM = "cdd3fc5ac80ebf1a3fb9b33e0d852e28bd316f93bf00151e73393cb2da01a24e"
PLUGINSHA256SUM = "76a927d0ba90253e7c6013d980248de33d6c5ddfb81190e145bae6cdce43f8c4"
PLUGINDEVSHA256SUM = "3860dc02e0177ba029608d040bd97e67e2f1e282a60064361632dec8286d7e58"
BINSHA256SUM = "6cc7e52b327d80c5e6438a69f81a9d53ac290b6224df855a6ce74abff4e6c80e"

LIBSHA256SUM_tegra194 = "4de8ac65bd22a431d1419884ab85b6f80d283a56c9771f71775126bb97375fcb"
DEVSHA256SUM_tegra194 = "64fcd6113b5ab1b828ace5dd69d0a7c5ccde3cbc8a01fe2d0973a67a79c97596"
SAMPSHA256SUM_tegra194 = "a9cddb7967018fe85c97c4b9c1642b58809ea55aef7388036491d7b003c50a8a"
NVPSHA256SUM_tegra194 = "f0ededa77e1eadda5f01875f320a75accbe5c6cebea02a06adb3900b620929d0"
NVPDEVSHA256SUM_tegra194 = "1651ec4c6ac075953eb35db34129f9c0eac354ef3938e761c2736f589d6d641b"
ONNXSHA256SUM_tegra194 = "bfefcd92b774ccf2f90dfb69ba29df69abbc595850b559dd8c6299ad60cb11b9"
ONNXDEVSHA256SUM_tegra194 = "6498b8f6881e4e98ba0d2e843a10d522b570da0c0b724ed1540f329239753480"
PLUGINSHA256SUM_tegra194 = "3dfc5ce48d9d5bde3719e22951bd0b1e7eee63fcb6ad06154baf29667f11f536"
PLUGINDEVSHA256SUM_tegra194 = "f33f4120644abeaf2ab5cb942e97dc9b9373bace1ea34c28a2d2cb7b1b681f79"
BINSHA256SUM_tegra194 = "f532c4b23dbca1478f6c23561913fb3f57c07564f58493dd8ad38eb34836ffd9"

SRC_URI[lib.sha256sum] = "${LIBSHA256SUM}"
SRC_URI[dev.sha256sum] = "${DEVSHA256SUM}"
SRC_URI[samples.sha256sum] = "${SAMPSHA256SUM}"
SRC_URI[nvp.sha256sum] = "${NVPSHA256SUM}"
SRC_URI[nvpdev.sha256sum] = "${NVPDEVSHA256SUM}"
SRC_URI[onnx.sha256sum] = "${ONNXSHA256SUM}"
SRC_URI[onnxdev.sha256sum] = "${ONNXDEVSHA256SUM}"
SRC_URI[plugin.sha256sum] = "${PLUGINSHA256SUM}"
SRC_URI[plugindev.sha256sum] = "${PLUGINDEVSHA256SUM}"
SRC_URI[bin.sha256sum] = "${BINSHA256SUM}"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=48;md5=59218f2f10ab9e4132dda76c59e80fa1"

S = "${WORKDIR}/tensorrt"

CONTAINER_CSV_FILES = "${libdir}/*.so* /usr/src/*"

do_configure() {
    :
}

do_compile() {
    find ${S}/usr/src/tensorrt -name '*.py' | xargs sed -i -r -e 's,^(\s*)print "(.*)$,\1print("\2),'
}

do_install() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/usr/include/aarch64-linux-gnu/*.h ${D}${includedir}
    install -d ${D}${libdir}
    tar -C ${S}/usr/lib/aarch64-linux-gnu -cf- . | tar -C ${D}${libdir}/ --no-same-owner -xf-
    install -d ${D}${prefix}/src
    cp --preserve=mode,timestamps --recursive ${S}/usr/src/tensorrt ${D}${prefix}/src/
}
PACKAGES += "${PN}-samples"
FILES_${PN} += "${prefix}/src/tensorrt/bin"
FILES_${PN}-samples = "${prefix}/src"

RDEPENDS_${PN} += "libstdc++ cudnn libcublas cuda-cudart cuda-nvrtc cuda-nvtx tegra-libraries libglvnd"
RDEPENDS_${PN}-samples += "tegra-libraries bash python3 libglvnd cudnn cuda-cudart libcublas"
RPROVIDES_${PN}-samples = "${PN}-examples"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
INSANE_SKIP_${PN} = "textrel ldflags"
INSANE_SKIP_${PN}-samples = "ldflags"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
