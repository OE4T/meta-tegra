TEGRA_SRC_SUBARCHIVE = "\
    Linux_for_Tegra/source/kernel_oot_modules_src.tbz2 \
    Linux_for_Tegra/source/nvidia_kernel_display_driver_source.tbz2 \
"
TEGRA_SRC_SUBARCHIVE_OPTS = "-C ${S}"
require recipes-bsp/tegra-sources/tegra-sources-36.5.0.inc

do_unpack[depends] += "tegra-binaries:do_preconfigure"

unpack_makefile_from_bsp() {
    [ -e ${S}/Makefile ] || cp ${L4T_BSP_SHARED_SOURCE_DIR}/source/Makefile ${S}/
}
do_unpack[postfuncs] += "unpack_makefile_from_bsp"

SRC_URI += "file://0001-Makefile-update-for-OE-builds.patch \
           file://0002-Fix-nvdisplay-modules-builds.patch \
           file://0001-tegra-virt-alt-Remove-leading-from-include-path-from.patch \
           file://0001-nvidia-kernel-oot-handle-of_property_for_each_u32-ap.patch \
           "

S = "${WORKDIR}/${BPN}"

require nvidia-kernel-oot.inc
