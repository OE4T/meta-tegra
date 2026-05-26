DESCRIPTION = "CUDA sample programs"
HOMEPAGE = "https://github.com/NVIDIA/cuda-samples"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bb28b97ff25ae39de442985ec577dbd8"

COMPATIBLE_MACHINE = "(tegra)"

SRC_URI = " \
    git://github.com/NVIDIA/cuda-samples.git;protocol=https;branch=master \
    file://0001-Updates-for-OE-cross-builds.patch \
"
# v13.2 tag
SRCREV = "a4526d52290b667cbc46b4a9830fbaad35be1ec2"

inherit cmake cuda

DEPENDS:append = " cuda-cudart cuda-crt cuda-profiler-api libcublas"

PV = "13.2"

CUDA_SAMPLES_INSTALL_PATH = "${bindir}/cuda-samples"
EXTRA_OECMAKE:append = " -DCMAKE_INSTALL_CUDA_SAMPLES=${CUDA_SAMPLES_INSTALL_PATH}"

do_install:append() {
    install -d ${D}${bindir}/cuda-samples
    install -m 0644 ${S}/Samples/0_Introduction/simpleSurfaceWrite/data/teapot512.pgm ${D}${bindir}/cuda-samples/
    install -m 0644 ${S}/Samples/0_Introduction/simpleSurfaceWrite/data/ref_rotated.pgm ${D}${bindir}/cuda-samples/
}

PACKAGE_ARCH = "${TEGRA_PKGARCH}"

INSANE_SKIP:${PN} += "buildpaths"
