DESCRIPTION = "Dummy recipe for bringing in CUDA tools and libraries"
LICENSE = "MIT"

DEPENDS = " \
    cuda-command-line-tools-11-8 \
    cuda-compiler-11-8 \
    cuda-libraries-11-8 \
    cuda-nvml-11-8 \
"

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
RDEPENDS:${PN} = "cuda-command-line-tools-11-8 cuda-compiler-11-8 cuda-libraries-11-8-dev cuda-nvml-11-8-dev"
INSANE_SKIP:${PN} = "dev-deps"
BBCLASSEXTEND = "native nativesdk"
