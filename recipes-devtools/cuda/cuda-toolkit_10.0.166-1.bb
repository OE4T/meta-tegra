DESCRIPTION = "Dummy recipe for bringing in CUDA tools and libraries"
LICENSE = "MIT"

CUDA_COMPONENTS = " \
    cuda-nvrtc \
    cuda-cusolver \
    cuda-cublas \
    cuda-cufft \
    cuda-curand \
    cuda-cusparse \
    cuda-npp \
    cuda-command-line-tools \
    cuda-core \
    cuda-cudart \
"
DEPENDS = "${CUDA_COMPONENTS}"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

COMPATIBLE_MACHINE = "tegra"
COMPATIBLE_MACHINE_tegra124 = "(-)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

PACKAGES = "${PN}"
ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} = "${CUDA_COMPONENTS}"
