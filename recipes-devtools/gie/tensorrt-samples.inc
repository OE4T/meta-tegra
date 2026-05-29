SUMMARY = "NVIDIA® TensorRT™ is an SDK for high-performance deep learning inference on NVIDIA GPUs"
HOMEPAGE = "https://github.com/NVIDIA/TensorRT"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=99db4b09478f3c2b0a11901785687034"

inherit cuda cmake features_check

SRC_URI = "\
    git://github.com/NVIDIA/TensorRT.git;protocol=https;branch=main \
    file://0001-Updates-for-OE-cross-builds.patch \
"
SRCREV = "52399f555c2f80cb690a4a558b604e1a5f227e7c"

COMPATIBLE_MACHINE = "(tegra)"

REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "cuda-cudart cuda-crt cuda-profiler-api cudnn tegra-libraries-multimedia-utils tensorrt-core tensorrt-plugins libglvnd"

TRT_INSTALL_PATH = "/usr/src/tensorrt"

EXTRA_OECMAKE = " \
    -DBUILD_PLUGINS=OFF \
    -DBUILD_PARSERS=OFF \
    -DCMAKE_INSTALL_PREFIX=${TRT_INSTALL_PATH} \
"
