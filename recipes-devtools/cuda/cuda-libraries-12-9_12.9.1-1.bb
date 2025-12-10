DESCRIPTION = "Dummy recipe for bringing in CUDA libraries"
LICENSE = "MIT"

CUDA_COMPONENTS = " \
    cuda-cudart-12-9 \
    cuda-nvrtc-12-9 \
    libcublas-12-9 \
    libcufft-12-9 \
    libcufile-12-9 \
    libcurand-12-9 \
    libcusolver-12-9 \
    libcusparse-12-9 \
    libnpp-12-9 \
    libnvjitlink-12-9 \
    libnvfatbin-12-9 \
"
CUDA_COMPONENTS:append:class-target = " \
    libcudla-12-9 \
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
RDEPENDS:${PN}-dev = "${@' '.join(['%s-dev' % pkg for pkg in d.getVar('CUDA_COMPONENTS').split()])} cuda-nvml-12-9-dev cuda-nvcc-headers-12-9 cuda-cccl-12-9 cuda-crt-12-9"
BBCLASSEXTEND = "native nativesdk"
