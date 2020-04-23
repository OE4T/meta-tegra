DESCRIPTION = "Dummy recipe for bringing in CUDA tools and libraries"
LICENSE = "MIT"

CUDA_COMPONENTS = " \
    cuda-nvrtc \
    cuda-cusolver \
    libcublas \
    cuda-cufft \
    cuda-curand \
    cuda-cusparse \
    cuda-npp \
    cuda-command-line-tools \
    cuda-compiler \
    cuda-libraries \
"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

COMPATIBLE_MACHINE_class-target = "tegra"
PACKAGE_ARCH_class-target = "${TEGRA_PKGARCH}"

PACKAGES = "${PN} ${PN}-dev"
ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} = "${CUDA_COMPONENTS}"
RDEPENDS_${PN}-dev = "cuda-nvml-dev"
BBCLASSEXTEND = "native nativesdk"
