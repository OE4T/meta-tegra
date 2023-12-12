SUMMARY = "NVIDIA CUDA Deep Neural Network samples"
HOMEPAGE = "https://developer.nvidia.com/cudnn"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/src/cudnn_samples_v8/RNN_v8.0/RNN_example.h;endline=10;md5=b4c8c209af2f8f4b0605c51b30f587c2"

inherit l4t_deb_pkgfeed

L4T_DEB_GROUP = "cudnn-samples"

DEPENDS = "cudnn"

SRC_COMMON_DEBS = "\
    libcudnn8-samples_${PV}+cuda12.2_arm64.deb;name=samples;subdir=cudnn \
"
SRC_URI[samples.sha256sum] = "4634422d33251411e0c0129ea0814d792992f29324cc2ee1d18f76fd061d16fd"
COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"

S = "${WORKDIR}/cudnn"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${prefix}/src
    cp --preserve=mode,timestamps --recursive ${S}/usr/src/* ${D}${prefix}/src/
}

FILES:${PN} = "${prefix}/src"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

RDEPENDS:${PN} = "libcublas libcublas-dev cuda-cudart"
INSANE_SKIP:${PN} = "build-deps dev-deps ldflags staticdev"
