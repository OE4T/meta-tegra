DESCRIPTION = "CUDA sample programs"
HOMEPAGE = "https://github.com/NVIDIA/cuda-samples"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bb28b97ff25ae39de442985ec577dbd8"

COMPATIBLE_MACHINE = "(tegra)"

COMPATIBLE_MACHINE = "(tegra)"

SRC_URI = " \
    git://github.com/NVIDIA/cuda-samples.git;protocol=https;branch=master \
    file://0001-Updates-for-OE-cross-builds.patch \
"
# v13.0 tag
SRCREV = "3f1c50965017932fc81e6d94a3fc9e04c105b312"

inherit cmake cuda

DEPENDS:append = " cuda-cudart cuda-crt cuda-profiler-api libcublas"

PV = "13.0"

CUDA_SAMPLES_INSTALL_PATH = "${bindir}/cuda-samples"
EXTRA_OECMAKE:append = " -DCMAKE_INSTALL_CUDA_SAMPLES=${CUDA_SAMPLES_INSTALL_PATH}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"

INSANE_SKIP:${PN} += "buildpaths"
