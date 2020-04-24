DESCRIPTION = "Dummy recipe for bringing in CUDA libraries"
LICENSE = "MIT"

CUDA_COMPONENTS = " \
    cuda-nvrtc \
    cuda-nvgraph \
    cuda-cusolver \
    libcublas \
    cuda-cufft \
    cuda-curand \
    cuda-cusparse \
    cuda-npp \
    cuda-cudart \
"
DEPENDS = "${CUDA_COMPONENTS}"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

COMPATIBLE_MACHINE_class-target = "tegra"
PACKAGE_ARCH_class-target = "${SOC_FAMILY_PKGARCH}"

PACKAGES = "${PN} ${PN}-dev"
ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} = "${CUDA_COMPONENTS}"
RDEPENDS_${PN}-dev = "${@' '.join(['%s-dev' % pkg for pkg in d.getVar('CUDA_COMPONENTS').split()])} cuda-nvml-dev"
BBCLASSEXTEND = "native nativesdk"
