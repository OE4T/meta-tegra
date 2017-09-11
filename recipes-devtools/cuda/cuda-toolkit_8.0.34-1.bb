DESCRIPTION = "Dummy recipe for bringing in CUDA tools and libraries"
LICENSE = "MIT"

DEPENDS = " \
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

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

inherit nopackages

# Keep the same restriction as cuda-shared-binaries-8.0.84-1.inc
# otherwise cuda-npp, cuda-cublas, cuda-nvrtc won't be available
COMPATIBLE_MACHINE = "(tegra210|tegra186)"
