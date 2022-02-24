DESCRIPTION = "NVIDIA TensorRT Samples for deep learning"
LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed cuda features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvinfer-samples_${PV}+cuda10.2_all.deb;downloadfilename=libnvinfer-samples_${PV}+cuda10.2_all.deb;name=samples;subdir=tensorrt \
"

SRC_URI:append = " file://0001-Makefile-fix-cross-compilation-issues.patch"

SRC_URI[samples.sha256sum] = "711ba173603270a836d844b71edd6da22322be7fd5e66ad683a5a34014514478"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://../../../share/doc/libnvinfer-samples/copyright;md5=713c2de2adb0f371a903b9fe20431bab"

S = "${WORKDIR}/tensorrt/usr/src/tensorrt/samples"

DEPENDS = "cuda-cudart cudnn tegra-libraries-multimedia-utils tensorrt-core tensorrt-plugins libglvnd"

EXTRA_OEMAKE = ' \
    CUDA_INSTALL_DIR="${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}" \
    CUDNN_INSTALL_DIR="${STAGING_DIR_HOST}/usr/lib" \
    TRT_LIB_DIR="${STAGING_DIR_HOST}/usr/lib" \
    TARGET="${TARGET_ARCH}" BUILD_TYPE="release" \
'

TARGET_CC_ARCH += "${LDFLAGS}"

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
    install -m 0755 ${S}/../bin/sample_io_formats ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_mnist ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_mnist_api ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_nmt ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_onnx_mnist ${D}${prefix}/src/tensorrt/bin
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

FILES:${PN} += "${prefix}/src/tensorrt/bin ${prefix}/src/tensorrt/data"
FILES:${PN}-trtexec += "${prefix}/src/tensorrt/bin/trtexec"
