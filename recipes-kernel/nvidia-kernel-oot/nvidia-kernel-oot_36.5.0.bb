TEGRA_SRC_SUBARCHIVE = "\
    Linux_for_Tegra/source/kernel_oot_modules_src.tbz2 \
    Linux_for_Tegra/source/nvidia_kernel_display_driver_source.tbz2 \
"
TEGRA_SRC_SUBARCHIVE_OPTS = "-C ${S}"
require recipes-bsp/tegra-sources/tegra-sources-36.5.0.inc

do_unpack[depends] += "tegra-binaries:do_preconfigure"
do_unpack[dirs] += "${S}"

unpack_makefile_from_bsp() {
    cp ${L4T_BSP_SHARED_SOURCE_DIR}/source/Makefile ${UNPACKDIR}
    [ -e ${S}/Makefile ] || cp ${UNPACKDIR}/Makefile ${S}/
}
do_unpack[postfuncs] += "unpack_makefile_from_bsp"

SRC_URI += "file://0001-Reapply-OE-patches-to-main-Makefile.patch \
            file://0002-conftest-fix-compatibility-with-gcc-14.patch;patchdir=nvdisplay \
            file://0003-Update-for-OE-builds.patch;patchdir=nvdisplay; \
            file://0004-tegra-virt-alt-Remove-leading-from-include-path-from.patch;patchdir=nvidia-oot \
            file://0005-conftest-work-around-stringify-issue-with-__assign_s.patch;patchdir=nvidia-oot \
            "

require nvidia-kernel-oot.inc
