DESCRIPTION = "NVIDIA TensorRT (GPU Inference Engine) for deep learning"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Proprietary"
DEPENDS = "dpkg-native"

L4T_URI_BASE = "https://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/4.1/walpdzz/JetPackL4T_4.1_b5"
SRC_URI = "\
    ${L4T_URI_BASE}/libnvinfer5_${PV}+cuda10.0_arm64.deb;name=lib;unpack=false \
    ${L4T_URI_BASE}/libnvinfer-dev_${PV}+cuda10.0_arm64.deb;name=dev;unpack=false \
    ${L4T_URI_BASE}/libnvinfer-samples_${PV}+cuda10.0_arm64.deb;name=samples;unpack=false \
"
SRC_URI[lib.md5sum] = "aa7f186068583b35c1ce9cf7e7e55052"
SRC_URI[lib.sha256sum] = "3c847f556447b9c279b1642f9bfbb2bfed93d3ad122f32d16197e70754e93759"
SRC_URI[dev.md5sum] = "54c0b98090e337fc603245436b57ee70"
SRC_URI[dev.sha256sum] = "ae185546f00cd7fc1a36b2df6e2c41b3415bf9e5750f4a5109a1e1fd33da389b"
SRC_URI[samples.md5sum] = "d2e92cca68344edc09c54c38e9a3e6bd"
SRC_URI[samples.sha256sum] = "499a16890d705f2ad7e8ddd19e9cc7ff56a0d5f6d3a954effe00f0ba177bf736"

COMPATIBLE_MACHINE = "(tegra186|tegra194)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

PR = "r0"

LIC_FILES_CHKSUM = "file://NvInfer.h;endline=48;md5=f38fd2aaeae3de115bacde66c2c93d2e"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"
BASEVER = "${@d.getVar('PV').split('-')[0]}"

S = "${WORKDIR}"
B = "${S}/tensorrt"

do_configure() {
    dpkg-deb --extract ${S}/libnvinfer5_${PV}+cuda10.0_arm64.deb ${B}
    dpkg-deb --extract ${S}/libnvinfer-dev_${PV}+cuda10.0_arm64.deb ${B}
    dpkg-deb --extract ${S}/libnvinfer-samples_${PV}+cuda10.0_arm64.deb ${B}
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
    tar -C ${B}/usr/lib/aarch64-linux-gnu -cf- . | tar -C ${D}${libdir}/ --no-same-owner -xf-
    install -d ${D}${prefix}/src
    cp --preserve=mode,timestamps --recursive ${B}/usr/src/tensorrt ${D}${prefix}/src/
}
PACKAGES =+ "${PN}-samples"
FILES_${PN}-samples = "${prefix}/src"

RDEPENDS_${PN} += "libstdc++ cudnn cuda-cublas cuda-cudart cuda-command-line-tools-libnvtoolsext tegra-libraries libglvnd"
RDEPENDS_${PN}-samples += "bash python libglvnd"
RPROVIDES_${PN}-samples = "${PN}-examples"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
INSANE_SKIP_${PN} = "textrel ldflags"
INSANE_SKIP_${PN}-samples = "ldflags"
