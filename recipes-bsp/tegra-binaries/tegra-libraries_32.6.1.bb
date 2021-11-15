DESCRIPTION = "Dummy transition recipe for tegra-libraries. Recipes that currently depend \
on tegra-libraries should be modified to depend on specific tegra-libraries-<group> recipes instead."
LICENSE = "MIT"

TEGRA_LIBRARIES = "\
    tegra-libraries-camera \
    tegra-libraries-core \
    tegra-libraries-cuda \
    tegra-libraries-eglcore \
    tegra-libraries-gbm \
    tegra-libraries-glescore \
    tegra-libraries-glxcore \
    tegra-libraries-multimedia \
    tegra-libraries-multimedia \
    tegra-libraries-multimedia-utils \
    tegra-libraries-multimedia-utils \
    tegra-libraries-multimedia-v4l \
    tegra-libraries-omx \
    tegra-libraries-vulkan \
    tegra-libraries-winsys \
"

DEPENDS = "${TEGRA_LIBRARIES}"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${L4T_BSP_PKGARCH}"

PACKAGES = "${PN} ${PN}-dev"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "${TEGRA_LIBRARIES}"
