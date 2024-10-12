LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed cuda features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvinfer-samples_${PV}+cuda12.2_all.deb;downloadfilename=libnvinfer-samples_${PV}+cuda12.2_all.deb;name=samples;subdir=tensorrt \
"

SRC_URI:append = " file://0001-Makefile-fix-cross-compilation-issues.patch"

SRC_URI[samples.sha256sum] = "57a3633cf1242a157138395da3e57ead9bcf1fc15c5653ad5422b1bd7a80aae0"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://../../../share/doc/libnvinfer-samples/copyright;md5=32ccc6a9bbc79616807b9bc252844b2f"

S = "${WORKDIR}/tensorrt/usr/src/tensorrt/samples"

DEPENDS = "cuda-cudart cuda-crt cuda-profiler-api cudnn tegra-libraries-multimedia-utils tensorrt-core tensorrt-plugins libglvnd"

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
