DESCRIPTION = "NVIDIA TensorRT Samples for deep learning"
LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed cuda

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

PREFIX = "NoDLA-"
PREFIX_tegra194 = "DLA-"

L4T_DEB_GROUP = "tensorrt"

SRC_SOC_DEBS = "\
    libnvinfer-samples_${PV}+cuda10.2_all.deb;downloadfilename=${PREFIX}libnvinfer-samples_${PV}+cuda10.2_all.deb;name=samples;subdir=tensorrt \
"

SRC_URI_append = " file://0001-Makefile-fix-cross-compilation-issues.patch"

SAMPSHA256SUM = "3a5162474cce7e191d78703b196bc8bdd5a6908d7537200194f1e80d72cf45a0"
SAMPSHA256SUM_tegra194 = "87d32bb231c19717c4690e05c770eb51d3d5ad4bea8fbd0ee46ba4d1a386ad9f"

SRC_URI[samples.sha256sum] = "${SAMPSHA256SUM}"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://../../../share/doc/libnvinfer-samples/copyright;md5=ea60fab79a29c78a6958fc75552ffdc0"

S = "${WORKDIR}/tensorrt/usr/src/tensorrt/samples"

DEPENDS = "cuda-cudart cudnn tegra-libraries tensorrt-core tensorrt-plugins libglvnd"

EXTRA_OEMAKE = ' \
    CUDA_INSTALL_DIR="${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}" \
    CUDNN_INSTALL_DIR="${STAGING_DIR_HOST}/usr/lib" \ 
    TRT_LIB_DIR="${STAGING_DIR_HOST}/usr/lib" \
    TARGET="${TARGET_ARCH}" BUILD_TYPE="release" \
'

TARGET_CC_ARCH += "${LDFLAGS} -L${STAGING_DIR_HOST}/usr/lib"

do_configure() {
    :
}

do_install() {
    install -d ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_algorithm_selector ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_char_rnn ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_dynamic_reshape ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_fasterRCNN ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_googlenet ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_int8 ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_int8_api ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_mlp ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_mnist ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_mnist_api ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_nmt ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_onnx_mnist ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_reformat_free_io ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_ssd ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_uff_faster_rcnn ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_uff_mask_rcnn ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_uff_mnist ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_uff_plugin_v2_ext ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_uff_ssd ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/trtexec ${D}${prefix}/src/tensorrt/bin

    install -d ${D}${prefix}/src/tensorrt/data
    cp -R --preserve=mode,timestamps ${S}/../data/* ${D}${prefix}/src/tensorrt/data
}

PACKAGES =+ "${PN}-trtexec"

FILES_${PN} += "${prefix}/src/tensorrt/bin ${prefix}/src/tensorrt/data"
FILES_${PN}-trtexec += "${prefix}/src/tensorrt/bin/trtexec"

RDEPENDS_${PN} += "tegra-libraries bash python3 python3-pillow python3-numpy libglvnd cudnn cuda-cudart libcublas"
