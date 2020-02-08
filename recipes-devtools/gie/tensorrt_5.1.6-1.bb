DESCRIPTION = "NVIDIA TensorRT (GPU Inference Engine) for deep learning"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Proprietary"

inherit nvidia_devnet_downloads container-runtime-csv

SUBDIR = ""
SUBDIR_tegra194 = "P2888/"
PREFIX = "${@d.getVar('SUBDIR').replace('/', '-')}"

SRC_URI = "\
    ${NVIDIA_DEVNET_MIRROR}/${SUBDIR}libnvinfer5_${PV}+cuda10.0_arm64.deb;downloadfilename=${PREFIX}libnvinfer5_${PV}+cuda10.0_arm64.deb;name=lib;subdir=tensorrt \
    ${NVIDIA_DEVNET_MIRROR}/${SUBDIR}libnvinfer-dev_${PV}+cuda10.0_arm64.deb;downloadfilename=${PREFIX}libnvinfer-dev_${PV}+cuda10.0_arm64.deb;name=dev;subdir=tensorrt \
    ${NVIDIA_DEVNET_MIRROR}/${SUBDIR}libnvinfer-samples_${PV}+cuda10.0_all.deb;downloadfilename=${PREFIX}libnvinfer-samples_${PV}+cuda10.0_all.deb;name=samples;subdir=tensorrt \
"
LIBMD5SUM = "dca1e2dadeae2186b57a11861fac7652"
LIBSHA256SUM = "1d39cf496f7d041f0005ebfdc7bdb0ab0b0216aa2bd0def0fbfc3868a37a54da"
DEVMD5SUM = "0e0c0c6d427847d5994f04fbce0401d2"
DEVSHA256SUM = "b0df180df752b2ea843964aff48255b7da994e4a49e09bcc9f03fdf4e49e1fb0"
SAMPMD5SUM = "e8f021ea1fad99d99f0f551d7ea3146a"
SAMPSHA256SUM = "741bb9de4b9e44844d0408e5ab22d025bcd208391f9405e390981ade661ff02b"

LIBMD5SUM_tegra194 = "9da0093178ae3dde92942e74274e8e3a"
LIBSHA256SUM_tegra194 = "6c652464897c10b33adf69dec5004ed40a5f59348f2b560469dcbe58fcc0fc25"
DEVMD5SUM_tegra194 = "1a580a0b8b1aad0a497c722fbd4e77c2"
DEVSHA256SUM_tegra194 = "b14706016fb0a0bbe91e91cc5174a59345fa553a0a39ae1ffc63a91cabd01230"
SAMPMD5SUM_tegra194 = "c8cc2db854381b7f92a5e7604da66e36"
SAMPSHA256SUM_tegra194 = "97dbeda8c97508586bc5344923fe3b32f79173c5a2ca7993148a3de0363cfe6a"

SRC_URI[lib.md5sum] = "${LIBMD5SUM}"
SRC_URI[lib.sha256sum] = "${LIBSHA256SUM}"
SRC_URI[dev.md5sum] = "${DEVMD5SUM}"
SRC_URI[dev.sha256sum] = "${DEVSHA256SUM}"
SRC_URI[samples.md5sum] = "${SAMPMD5SUM}"
SRC_URI[samples.sha256sum] = "${SAMPSHA256SUM}"

COMPATIBLE_MACHINE = "(tegra)"
COMPATIBLE_MACHINE_tegra124 = "(-)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=48;md5=1755df325a6e1ac8515b1e469efe07a7"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"
BASEVER = "${@d.getVar('PV').split('-')[0]}"

S = "${WORKDIR}/tensorrt"

CONTAINER_CSV_FILES = "${libdir}/*.so* /usr/src/*"

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
RDEPENDS_${PN}-samples += "tegra-libraries bash python libglvnd cudnn cuda-cudart cuda-cublas"
RPROVIDES_${PN}-samples = "${PN}-examples"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
INSANE_SKIP_${PN} = "textrel ldflags"
INSANE_SKIP_${PN}-samples = "ldflags"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
