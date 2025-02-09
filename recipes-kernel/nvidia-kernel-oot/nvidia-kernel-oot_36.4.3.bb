TEGRA_SRC_SUBARCHIVE = "\
    Linux_for_Tegra/source/kernel_oot_modules_src.tbz2 \
    Linux_for_Tegra/source/nvidia_kernel_display_driver_source.tbz2 \
"
TEGRA_SRC_SUBARCHIVE_OPTS = "-C ${UNPACKDIR}/${BPN}"
require recipes-bsp/tegra-sources/tegra-sources-36.4.3.inc

do_unpack[depends] += "tegra-binaries:do_preconfigure"
do_unpack[dirs] += "${UNPACKDIR}/${BPN}"

unpack_makefile_from_bsp() {
    cp ${L4T_BSP_SHARED_SOURCE_DIR}/source/Makefile ${UNPACKDIR}
    [ -e ${S}/Makefile ] || cp ${UNPACKDIR}/Makefile ${S}/
}
do_unpack[postfuncs] += "unpack_makefile_from_bsp"

SRC_URI += "file://0001-Makefile-update-for-OE-builds.patch \
            file://0002-Fix-nvdisplay-modules-builds.patch \
            file://0003-Fix-nvdisplay-conftest-gcc-14-compatibility-issues.patch \
            file://0004-tegra-virt-alt-Remove-leading-from-include-path-from.patch \
            file://0005-conftest-work-around-stringify-issue-with-__assign_s.patch \
           "

S = "${WORKDIR}/${BPN}"

require nvidia-kernel-oot.inc
