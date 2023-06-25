DESCRIPTION = "Dummy recipe for bringing in CUDA libraries"
LICENSE = "MIT"

CUDA_COMPONENTS = " \
    cuda-cudart-11-8 \
    cuda-nvrtc-11-8 \
    libcublas-11-8 \
    libcufft-11-8 \
    libcufile-11-8 \
    libcurand-11-8 \
    libcusolver-11-8 \
    libcusparse-11-8 \
    libnpp-11-8 \
"
CUDA_COMPONENTS:append:class-target = " \
    libcudla-11-8 \
"
DEPENDS = "${CUDA_COMPONENTS}"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

COMPATIBLE_MACHINE:class-target = "tegra"
PACKAGE_ARCH:class-target = "${TEGRA_PKGARCH}"

PACKAGES = "${PN} ${PN}-dev"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "${CUDA_COMPONENTS}"
RDEPENDS:${PN}-dev = "${@' '.join(['%s-dev' % pkg for pkg in d.getVar('CUDA_COMPONENTS').split()])} cuda-nvml-11-8-dev cuda-nvcc-headers-11-8 cuda-cccl-11-8"
BBCLASSEXTEND = "native nativesdk"
