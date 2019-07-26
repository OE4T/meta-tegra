DESCRIPTION = "NVIDIA TensorRT (GPU Inference Engine) for deep learning"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Proprietary"

inherit nvidia_devnet_downloads

SRC_URI = "\
    ${NVIDIA_DEVNET_MIRROR}/libnvinfer5_${PV}+cuda10.0_arm64.deb;name=lib;subdir=tensorrt \
    ${NVIDIA_DEVNET_MIRROR}/libnvinfer-dev_${PV}+cuda10.0_arm64.deb;name=dev;subdir=tensorrt \
    ${NVIDIA_DEVNET_MIRROR}/libnvinfer-samples_${PV}+cuda10.0_all.deb;name=samples;subdir=tensorrt \
"
SRC_URI[lib.md5sum] = "9da0093178ae3dde92942e74274e8e3a"
SRC_URI[lib.sha256sum] = "6c652464897c10b33adf69dec5004ed40a5f59348f2b560469dcbe58fcc0fc25"
SRC_URI[dev.md5sum] = "1a580a0b8b1aad0a497c722fbd4e77c2"
SRC_URI[dev.sha256sum] = "b14706016fb0a0bbe91e91cc5174a59345fa553a0a39ae1ffc63a91cabd01230"
SRC_URI[samples.md5sum] = "c8cc2db854381b7f92a5e7604da66e36"
SRC_URI[samples.sha256sum] = "97dbeda8c97508586bc5344923fe3b32f79173c5a2ca7993148a3de0363cfe6a"

COMPATIBLE_MACHINE = "(tegra)"
COMPATIBLE_MACHINE_tegra124 = "(-)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=48;md5=1755df325a6e1ac8515b1e469efe07a7"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"
BASEVER = "${@d.getVar('PV').split('-')[0]}"

S = "${WORKDIR}/tensorrt"

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
    tar -C ${S}/usr/lib/aarch64-linux-gnu -cf- . | tar -C ${D}${libdir}/ --no-same-owner -xf-
    install -d ${D}${prefix}/src
    cp --preserve=mode,timestamps --recursive ${S}/usr/src/tensorrt ${D}${prefix}/src/
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
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
