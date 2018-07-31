DESCRIPTION = "NVIDIA TensorRT (GPU Inference Engine) for deep learning"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Proprietary"
DEPENDS = "dpkg-native"

L4T_URI_BASE = "https://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/3.3/lw.xd42/JetPackL4T_33_b39"
SRC_URI = "\
    ${L4T_URI_BASE}/libnvinfer4_${PV}+cuda9.0_arm64.deb;name=lib;unpack=false \
    ${L4T_URI_BASE}/libnvinfer-dev_${PV}+cuda9.0_arm64.deb;name=dev;unpack=false \
    ${L4T_URI_BASE}/libnvinfer-samples_${PV}+cuda9.0_arm64.deb;name=samples;unpack=false \
"
SRC_URI[lib.md5sum] = "682e2d8a8f0200ea18471d03f0f35124"
SRC_URI[lib.sha256sum] = "c381a603313e7c5a30cd105275d239a0f90705da960cdba74515cb110dd79842"
SRC_URI[dev.md5sum] = "eb44e8a0eb5fb9a294407c14ea87e71a"
SRC_URI[dev.sha256sum] = "249c982ea699001e656fec5c29fbe517cc2d4446cad0f4809c5600379d6a2dc8"
SRC_URI[samples.md5sum] = "354f197403e5d54b934be7e5d16553e8"
SRC_URI[samples.sha256sum] = "b384fba508d384bf8ed133c4ea0b61cf3b8564559721c1e528a0e5593b07acb3"

COMPATIBLE_MACHINE = "(tegra186|tegra210)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

PR = "r0"

LIC_FILES_CHKSUM = "file://NvInfer.h;endline=48;md5=f38fd2aaeae3de115bacde66c2c93d2e"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"
BASEVER = "${@d.getVar('PV').split('-')[0]}"

S = "${WORKDIR}"
B = "${S}/tensorrt"

do_configure() {
    dpkg-deb --extract ${S}/libnvinfer4_${PV}+cuda9.0_arm64.deb ${B}
    dpkg-deb --extract ${S}/libnvinfer-dev_${PV}+cuda9.0_arm64.deb ${B}
    dpkg-deb --extract ${S}/libnvinfer-samples_${PV}+cuda9.0_arm64.deb ${B}
    # for the LIC_FILES_CHKSUM check
    cp ${B}/usr/include/aarch64-linux-gnu/NvInfer.h ${S}/
}
do_populate_lic[depends] += "${PN}:do_configure"

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir}
    install -m 0644 ${B}/usr/include/aarch64-linux-gnu/*.h ${D}${includedir}
    install -d ${D}${libdir}
    for lib in libnvparsers libnvinfer libnvinfer_plugin; do
        install -m 0644 ${B}/usr/lib/aarch64-linux-gnu/$lib.a ${D}${libdir}
        install -m 0644 ${B}/usr/lib/aarch64-linux-gnu/$lib.so.${BASEVER} ${D}${libdir}
	ln -s $lib.so.${BASEVER} ${D}${libdir}/$lib.so.4
	ln -s $lib.so.${BASEVER} ${D}${libdir}/$lib.so
    done
    ln -s libnvparsers.a ${D}${libdir}/libnvcaffe_parser.a
    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvcaffe_parser.so
    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvcaffe_parser.so.4
    ln -s libnvparsers.so.${BASEVER} ${D}${libdir}/libnvcaffe_parser.so.${BASEVER}
    install -d ${D}${prefix}/src
    cp --preserve=mode,timestamps --recursive ${B}/usr/src/tensorrt ${D}${prefix}/src/
}
PACKAGES =+ "${PN}-samples"
FILES_${PN}-samples = "${prefix}/src"

RDEPENDS_${PN} += "libstdc++ cudnn cuda-cublas cuda-cudart"
RDEPENDS_${PN}-samples += "bash python"
RPROVIDES_${PN}-samples = "${PN}-examples"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
INSANE_SKIP_${PN} = "textrel"
