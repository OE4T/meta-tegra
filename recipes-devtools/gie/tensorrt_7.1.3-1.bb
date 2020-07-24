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
    libnvinfer-bin_${PV}+cuda10.2_arm64.deb;downloadfilename=${PREFIX}libnvinfer-bin_${PV}+cuda10.2_arm64.deb;name=bin;subdir=tensorrt \
"

LIBSHA256SUM = "88636112a84fde159cce5b6eed11907bc98ddef02e88310c788a5462b88293be"
DEVSHA256SUM = "436e0832560e1c5b2a5377c1f9679ae1bf60649e5c0537a81c77baed53066633"
SAMPSHA256SUM = "3a5162474cce7e191d78703b196bc8bdd5a6908d7537200194f1e80d72cf45a0"
NVPSHA256SUM = "5477aeb1898d55182de02c3861f3576338ab01f2b71c776cb3fff5707e8e7758"
NVPDEVSHA256SUM = "0554c20ce85cfa0675f77df2539c34cecdeed9cf0402989c3404ab6e158f39aa"
ONNXSHA256SUM = "e00ed2bff48de16ca275ce322fbc881b5bbd4a1521fd7f685d56cfb30136039a"
ONNXDEVSHA256SUM = "8c18ffac6b9118491248e14000cb72638268ce4f3698522a478cac6dd4631c34"
PLUGINSHA256SUM = "a57ea8b4757fa4592c6ba1555bc07045909b553a493c0bde8c8b23c74cf082b4"
PLUGINDEVSHA256SUM = "94820301980118e34cc1ab99570d3a7a13b96f4811828908b63bf638b4252edd"
BINSHA256SUM = "94927076974c59ae45b0c56300387816b8fa03d397d1503d8633e7989b4a20f6"

LIBSHA256SUM_tegra194 = "6f6b0e27339ff352f975c65842f4b315db8b341ae5c7d92545c2fe8b3a727ce3"
DEVSHA256SUM_tegra194 = "b494da5c43360bd20b832854c766b4d4e3cfecdfbd73bef8d96c4ce9ae7f022c"
SAMPSHA256SUM_tegra194 = "87d32bb231c19717c4690e05c770eb51d3d5ad4bea8fbd0ee46ba4d1a386ad9f"
NVPSHA256SUM_tegra194 = "65db2ae0f5d7dbb452306823ac4b6ea27386a5650db337c7fa50969f3d9d3cea"
NVPDEVSHA256SUM_tegra194 = "dc48c0dc44ff69246f3062a480e9d5ac8f72f5bc798bdfad401ebbcb9912e475"
ONNXSHA256SUM_tegra194 = "26109c58e8eab9dc746fb3fc39cc39bf5a8bf2d61a27b5d9e7aa035fc0b97b4c"
ONNXDEVSHA256SUM_tegra194 = "52783ca7245171eb83c623a08851c0cfdc659696272b2edf6dfe3503ae9bc49b"
PLUGINSHA256SUM_tegra194 = "680c7849542dca1cac68fc94f7474f31446cf093b6e6df1d7bab8c24a6122aa3"
PLUGINDEVSHA256SUM_tegra194 = "bac84671b2990b8110d4f2ac69b4a29cf230346db37a902d1f47cf7a9be4601e"
BINSHA256SUM_tegra194 = "56275d8cba17be877af063db49ecb7b6e02bed5f8d387b1182a6a7ec2cabee2a"

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
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
PACKAGE_ARCH_tegra194 = "${SOC_FAMILY_PKGARCH}"
