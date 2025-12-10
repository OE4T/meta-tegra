DESCRIPTION = "CUDA sample programs"
HOMEPAGE = "https://github.com/NVIDIA/cuda-samples"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bb28b97ff25ae39de442985ec577dbd8"

COMPATIBLE_MACHINE = "(tegra)"

SRC_URI = " \
    git://github.com/NVIDIA/cuda-samples.git;protocol=https;branch=master \
    file://0001-Updates-for-OE-cross-builds.patch \
"
# v12.9 tag
SRCREV = "cab7c66b4fcf35b09ea8fbc4c3c1a9272e92d997"

CUDA_VERSION = "12.9"

inherit cmake cuda

DEPENDS:remove = "cuda-libraries cuda-compiler-native cuda-cudart-native"
DEPENDS:append = " \
    cuda-libraries-12-9 cuda-compiler-12-9-native cuda-cudart-12-9-native \
    cuda-crt-12-9 cuda-profiler-api-12-9 libcublas-12-9 \
"

PV = "12.9"

CUDA_SAMPLES_INSTALL_PATH = "${bindir}/cuda-samples-12-9"
EXTRA_OECMAKE:append = " -DCMAKE_INSTALL_CUDA_SAMPLES=${CUDA_SAMPLES_INSTALL_PATH}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"

INSANE_SKIP:${PN} += "buildpaths"
