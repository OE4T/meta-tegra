SUMMARY = "NVIDIA CUDA Deep Neural Network samples"
HOMEPAGE = "https://developer.nvidia.com/cudnn"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/src/cudnn_samples_v9/RNN_v8.0/RNN_example.h;endline=10;md5=279f10cad80a69894c566bd5319e2a03"

inherit l4t_deb_pkgfeed

L4T_DEB_GROUP = "cudnn-samples"

DEPENDS = "cudnn"

SRC_COMMON_DEBS = "\
    libcudnn9-samples_${PV}_all.deb;name=samples;subdir=cudnn \
"
SRC_URI[samples.sha256sum] = "d823a34d283bc12416e1a3606be83f50ee75d6ad59ae206a4ed037f60c79e215"
COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"

S = "${UNPACKDIR}/cudnn"

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
