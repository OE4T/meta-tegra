SUMMARY = "NVIDIA® TensorRT™ is an SDK for high-performance deep learning inference on NVIDIA GPUs"
HOMEPAGE = "https://github.com/NVIDIA/TensorRT"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=71ce4a0fbbf2400c862ffbac094c7f4c"

inherit cuda cmake features_check

SRC_URI = "\
    git://github.com/NVIDIA/TensorRT.git;protocol=https;branch=main \
    file://0001-Updates-for-OE-cross-builds.patch \
    file://TensorRT-EnterpriseConfig.cmake \
"
SRCREV = "16ed0636df41e4a27d46f82d9028fc50fadc7200"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "cuda-cudart cuda-crt cuda-profiler-api cudnn tegra-libraries-multimedia-utils tensorrt-core tensorrt-plugins libglvnd nlohmann-json"

TRT_INSTALL_PATH = "/usr/src/tensorrt"

EXTRA_OECMAKE = " \
    -DBUILD_PLUGINS=OFF \
    -DBUILD_PARSERS=OFF \
    -DCMAKE_INSTALL_PREFIX=${TRT_INSTALL_PATH} \
    -DTensorRT-Enterprise_DIR=${UNPACKDIR} \
    -DTRT_SYSROOT_INCDIR=${STAGING_INCDIR} \
    -DTRT_SYSROOT_LIBDIR=${STAGING_LIBDIR} \
    -DNLOHMANN_JSON_INCLUDE_DIRS=${STAGING_INCDIR} \
    -DCUDART_LIB=${CUDA_PATH}/${baselib}/libcudart_static.a \
    -DBUILD_SAMPLES=ON \
"

CVE_PRODUCT = "nvidia:tensorrt"
