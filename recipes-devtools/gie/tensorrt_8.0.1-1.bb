DESCRIPTION = "NVIDIA TensorRT (GPU Inference Engine) for deep learning"
LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed container-runtime-csv

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvinfer8_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvinfer8_${PV}+cuda10.2_arm64.deb;name=lib;subdir=tensorrt \
    libnvinfer-dev_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvinfer-dev_${PV}+cuda10.2_arm64.deb;name=dev;subdir=tensorrt \
    libnvinfer-samples_${PV}+cuda10.2_all.deb;downloadfilename=libnvinfer-samples_${PV}+cuda10.2_all.deb;name=samples;subdir=tensorrt \
    libnvparsers8_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvparsers8_${PV}+cuda10.2_arm64.deb;name=nvp;subdir=tensorrt \
    libnvparsers-dev_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvparsers-dev_${PV}+cuda10.2_arm64.deb;name=nvpdev;subdir=tensorrt \
    libnvonnxparsers8_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvonnxparsers8_${PV}+cuda10.2_arm64.deb;name=onnx;subdir=tensorrt \
    libnvonnxparsers-dev_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvonnxparsers-dev_${PV}+cuda10.2_arm64.deb;name=onnxdev;subdir=tensorrt \
    libnvinfer-plugin8_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvinfer-plugin8_${PV}+cuda10.2_arm64.deb;name=plugin;subdir=tensorrt \
    libnvinfer-plugin-dev_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvinfer-plugin-dev_${PV}+cuda10.2_arm64.deb;name=plugindev;subdir=tensorrt \
    libnvinfer-bin_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvinfer-bin_${PV}+cuda10.2_arm64.deb;name=bin;subdir=tensorrt \
"

LIBSHA256SUM = "305c4482a315ceb59e514823b359fdeebfbdd5fa2124e277dd176589e2f49aea"
DEVSHA256SUM = "f750c910a23107715dc2510d360725e46e9072079caacf3cec4255dd38bee849"
SAMPSHA256SUM = "6eadd053f8e840f17ee8ea5cde98b2b170b91a763cc5cf0bda5a8c59a4d5390d"
NVPSHA256SUM = "34040352c9f44611928a7d6aa6a7f885b6506a7b3310a8b9fc0782a9ba42037a"
NVPDEVSHA256SUM = "b09864c351aebf2200fb98f48dc68b4a75260bbcd01423bbf1633acdc115b9be"
ONNXSHA256SUM = "8d4b0722515d91592e73dca2c43b798430bef4633b34d912324b53b63acf41ae"
ONNXDEVSHA256SUM = "6f477ab54c4fd646ab9f65baed0157dca7ca29de6bc7f992f5285d8baa30b5eb"
PLUGINSHA256SUM = "71435b08b97346e2b0f568332c3440b8f6c00b5198f83ab5935f161aae39f8d8"
PLUGINDEVSHA256SUM = "4dabae4f5ea8f3eb54dbd36cf3dde3d038fae2a857529b869844b05cec77092a"
BINSHA256SUM = "f4a98ac9086b4a195bcab26aca176a9db6b5a196ff42d3dfdb28a16d30e8a312"

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

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=48;md5=3d6981c1227c404d42d710f96a875a1b"

S = "${WORKDIR}/tensorrt"

DEPENDS = "libcublas cudnn cuda-cudart cuda-nvrtc libglvnd tegra-libraries"

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
