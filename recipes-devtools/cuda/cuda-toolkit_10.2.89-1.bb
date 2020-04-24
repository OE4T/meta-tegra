DESCRIPTION = "Dummy recipe for bringing in CUDA tools and libraries"
LICENSE = "MIT"

DEPENDS = " \
    cuda-command-line-tools \
    cuda-compiler \
    cuda-libraries \
    cuda-nvml \
"

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
RDEPENDS_${PN} = "cuda-command-line-tools cuda-compiler cuda-libraries-dev cuda-nvml-dev"
INSANE_SKIP_${PN} = "dev-deps"
BBCLASSEXTEND = "native nativesdk"
