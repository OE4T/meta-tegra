LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed cuda features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvinfer-samples_${PV}+cuda11.4_all.deb;downloadfilename=libnvinfer-samples_${PV}+cuda11.4_all.deb;name=samples;subdir=tensorrt \
"

SRC_URI:append = " file://0001-Makefile-fix-cross-compilation-issues.patch"

SRC_URI[samples.sha256sum] = "e617ed3dfbe73e90ac07c859f154a7853b9657acdb72758d40fd23bb472325f8"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://../../../share/doc/libnvinfer-samples/copyright;md5=945a4cd6440aab48df67854e15596b7b"

S = "${WORKDIR}/tensorrt/usr/src/tensorrt/samples"

DEPENDS = "cuda-cudart cuda-profiler-api cudnn tegra-libraries-multimedia-utils tensorrt-core tensorrt-plugins libglvnd"

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
