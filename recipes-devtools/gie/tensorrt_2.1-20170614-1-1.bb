DESCRIPTION = "NVIDIA TensorRT (GPU Inference Engine) for deep learning"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Proprietary"
DEPENDS = "dpkg-native"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/013/linux-x64/nv-gie-repo-ubuntu1604-ga-cuda8.0-trt2.1-20170614_1-1_arm64.deb"
SRC_URI[md5sum] = "968729da55ada3393fa98c8403108528"
SRC_URI[sha256sum] = "f5c20ec08badea5e799c49d33696b5af5c71dfa87891d6f31dc47522eb12b32c"

COMPATIBLE_MACHINE = "(tegra186|tegra210)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

LIC_FILES_CHKSUM = "file://NvInfer.h;endline=48;md5=c019325b2fa9ba0eb1b404d686a45685"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"

S = "${WORKDIR}/var/nv-gie-repo-ga-cuda8.0-trt2.1-20170614"
B = "${WORKDIR}/build"

do_configure() {
    dpkg-deb --extract ${S}/libnvinfer3_3.0.2-1+cuda8.0_arm64.deb ${B}
    dpkg-deb --extract ${S}/libnvinfer-dev_3.0.2-1+cuda8.0_arm64.deb ${B}
    # for the LIC_FILES_CHKSUM check
    cp ${B}/usr/include/aarch64-linux-gnu/NvInfer.h ${S}/
}
do_populate_lic[depends] += "${PN}:do_configure"

do_compile() {
    mv ${B}/usr/include/aarch64-linux-gnu/NvUtils.h ${B}/usr/src/tensorrt/samples/sampleCharRNN/
}

do_install() {
    install -d ${D}${includedir}
    install -m 0644 ${B}/usr/include/aarch64-linux-gnu/*.h ${D}${includedir}
    install -d ${D}${libdir}
    cp --preserve=mode,timestamps --no-dereference ${B}/usr/lib/aarch64-linux-gnu/* ${D}${libdir}
    install -d ${D}${prefix}/src
    cp --preserve=mode,timestamps --recursive ${B}/usr/src/tensorrt ${D}${prefix}/src/
}
PACKAGES =+ "${PN}-examples"
FILES_${PN}-examples = "${prefix}/src"

RDEPENDS_${PN} += "libstdc++ cudnn cuda-cublas cuda-cudart"
INSANE_SKIP_${PN} = "textrel"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
