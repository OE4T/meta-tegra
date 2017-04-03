DESCRIPTION = "NVIDIA TensorRT (GPU Inference Engine) for deep learning"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Proprietary"
DEPENDS = "cudnn dpkg-native"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/006/linux-x64/nv-gie-repo-ubuntu1604-6-rc-cuda8.0_${PV}_arm64.deb \
           file://tensorrt-eula.txt"
SRC_URI[md5sum] = "2df905e46c8c2a5a1773659e717591c6"
SRC_URI[sha256sum] = "4d74122a3e4a56083e236d2a3ea9d860c276de4a7037266a8fbabade9556523a"

COMPATIBLE_MACHINE = "(tegra210)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

LIC_FILES_CHKSUM = "file://tensorrt-eula.txt;md5=6cc7c5300a41f51bf22094638b2721c9"

CUDAPATH ?= "/usr/local/cuda-8.0"

S = "${WORKDIR}/var/nv-gie-repo-6-rc-cuda8.0"
B = "${WORKDIR}/build"

do_configure() {
    ln -sf ${WORKDIR}/tensorrt-eula.txt ${S}/
    dpkg-deb --extract ${S}/libgie1_1.0.0-1+cuda8.0_arm64.deb ${B}
    dpkg-deb --extract ${S}/libgie-dev_1.0.0-1+cuda8.0_arm64.deb ${B}
}
do_populate_lic[depends] += "${PN}:do_configure"

do_compile() {
    chrpath -r "${CUDAPATH}/${baselib}" ${B}/usr/lib/aarch64-linux-gnu/lib*${SOLIBS}
}

do_install() {
    install -d ${D}${includedir}
    cp ${B}/usr/include/aarch64-linux-gnu/*.h ${D}${includedir}
    install -d ${D}${libdir}
    cp -d ${B}/usr/lib/aarch64-linux-gnu/* ${D}${libdir}
}

RDEPENDS_${PN} += "libstdc++"
