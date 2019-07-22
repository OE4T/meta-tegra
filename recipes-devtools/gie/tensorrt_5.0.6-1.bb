DESCRIPTION = "NVIDIA TensorRT (GPU Inference Engine) for deep learning"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Proprietary"

inherit nvidia_devnet_downloads

SRC_URI = "\
    ${NVIDIA_DEVNET_MIRROR}/libnvinfer5_${PV}+cuda10.0_arm64.deb;name=lib;subdir=tensorrt \
    ${NVIDIA_DEVNET_MIRROR}/libnvinfer-dev_${PV}+cuda10.0_arm64.deb;name=dev;subdir=tensorrt \
    ${NVIDIA_DEVNET_MIRROR}/libnvinfer-samples_${PV}+cuda10.0_all.deb;name=samples;subdir=tensorrt \
"
SRC_URI[lib.md5sum] = "b3f083e0665dc8d3d8eb61349bb275d7"
SRC_URI[lib.sha256sum] = "b6336bccde901e64d2118dd2abb3d697d6211e422eb64daf0624f56cc6e58c83"
SRC_URI[dev.md5sum] = "9e8e0d8356ba4af730c39b35c50ae2a8"
SRC_URI[dev.sha256sum] = "9269fac7d525dbbddc07ea6aaeec03620ee18a44379d53b1aab7ec9aa868ffc3"
SRC_URI[samples.md5sum] = "dfa8bee28c87ec3c08b4063b779d63ef"
SRC_URI[samples.sha256sum] = "f518dd3110a2da48c59604cc33d836291fb5177357aa2eb2be432bd712dc04ed"

COMPATIBLE_MACHINE = "(tegra)"
COMPATIBLE_MACHINE_tegra124 = "(-)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=48;md5=f38fd2aaeae3de115bacde66c2c93d2e"

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
