inherit l4t_bsp

SRC_URI_NV_OOT = "git://gitlab.com/nvidia/nv-tegra/linux-nv-oot.git;protocol=https;branch=l4t/l4t-r38.4"
SRC_URI_NV_ETHERNETRM = "git://gitlab.com/nvidia/nv-tegra/kernel/nvethernetrm.git;protocol=https;branch=l4t/l4t-r38.4"
SRC_URI_UNIFIED_GPU_DISP = "git://gitlab.com/nvidia/nv-tegra/tegra/kernel-src/nv-unified-gpu-display-driver.git;protocol=https;branch=l4t/l4t-r38.4"
SRC_URI_NV_KERENL_DISPLAY = "git://gitlab.com/nvidia/nv-tegra/tegra/kernel-src/nv-kernel-display-driver.git;protocol=https;branch=l4t/l4t-r38.4"
SRC_URI_HWPM = "git://gitlab.com/nvidia/nv-tegra/linux-hwpm.git;protocol=https;branch=l4t/l4t-r38.4"
SRC_URI_T264_DTS = "git://gitlab.com/nvidia/nv-tegra/device/hardware/nvidia/t264-public-dts.git;protocol=https;branch=l4t/l4t-r38.4"
SRC_URI_T23X_DTS = "git://gitlab.com/nvidia/nv-tegra/device/hardware/nvidia/t23x-public-dts.git;protocol=https;branch=l4t/l4t-r38.4"
SRC_URI_TEGRA_DTS = "git://gitlab.com/nvidia/nv-tegra/device/hardware/nvidia/tegra-public-dts.git;protocol=https;branch=l4t/l4t-r38.4"
SRC_URI_KERENEL_DTS = "git://gitlab.com/nvidia/nv-tegra/linux/kernel-devicetree.git;protocol=https;branch=l4t/l4t-r38.4"

SRC_URI += " \
    ${SRC_URI_NV_OOT};name=nv-oot;destsuffix=${BPN}-${PV}/nvidia-oot \
    ${SRC_URI_NV_ETHERNETRM};name=nv-ethernetrm;destsuffix=${BPN}-${PV}/nvethernetrm \
    ${SRC_URI_UNIFIED_GPU_DISP};name=unified-gpu-disp;destsuffix=${BPN}-${PV}/unifiedgpudisp \
    ${SRC_URI_NV_KERENL_DISPLAY};name=nv-display;destsuffix=${BPN}-${PV}/nvdisplay \
    ${SRC_URI_HWPM};name=hwpm;destsuffix=${BPN}-${PV}/hwpm \
    ${SRC_URI_T264_DTS};name=t264-dts;destsuffix=${BPN}-${PV}/hardware/nvidia/t264/nv-public \
    ${SRC_URI_T23X_DTS};name=t23x-dts;destsuffix=${BPN}-${PV}/hardware/nvidia/t23x/nv-public \
    ${SRC_URI_TEGRA_DTS};name=tegra-dts;destsuffix=${BPN}-${PV}/hardware/nvidia/tegra/nv-public \
    ${SRC_URI_KERENEL_DTS};name=kernel-dts;destsuffix=${BPN}-${PV}/kernel-devicetree \
"

# tag: jetson_38.4
SRCREV_nv-oot = "33227347c89543042e0eda352db5de852983c8a3"
SRCREV_nv-ethernetrm = "507a71fe0be3aabf638c52013a22f1afabda0c5a"
SRCREV_unified-gpu-disp = "e75fa2f597f20ea36cae5d3de56174638643050a"
SRCREV_nv-display = "5073a1b3d2bd014175bccc9f611ce53e550c3a25"
SRCREV_hwpm = "d5799a7050d439a5be88bfb0349ffc3e60a3c9eb"
SRCREV_t264-dts = "bd958e8c5965ea4831ddab7ea522b0b7e8717aea"
SRCREV_t23x-dts = "21daec5a0cde295fb53506271a796cc2707d8c86"
SRCREV_tegra-dts = "2101ccc1b1ef8267e63286332aed34891943c7c7"
SRCREV_kernel-dts = "d488feae6501566a90743e45729608bc5f2586f9"

SRCREV_FORMAT = "nv-oot_nv-ethernetrm_unified-gpu-disp_nv-display_hwpm_t264-dts_t23x-dts_tegra-dts_kernel-dts"

PV = "38.4.0+git"

SRC_URI += " \
    file://0001-Makefile-update-for-OE-builds.patch \
    file://0002-Fix-nvdisplay-modules-builds.patch \
    file://0003-Fix-nvdisplay-conftest-gcc-14-compatibility-issues.patch \
    file://0004-Fix-unifiedgpudisp-modules-builds.patch \
    file://0005-Fix-unifiedgpudisp-conftest-gcc-14-compatibility-iss.patch \
    file://0006-conftest-work-around-stringify-issue-with.patch \
"

do_unpack[depends] += "tegra-binaries:do_preconfigure"

L4T_BSP_SHARED_SOURCE_DIR = "${TMPDIR}/work-shared/L4T-${L4T_BSP_ARCH}-${L4T_VERSION}-${PR}/sources/Linux_for_Tegra"

unpack_makefile_from_bsp() {
    cp ${L4T_BSP_SHARED_SOURCE_DIR}/source/Makefile ${UNPACKDIR}
    [ -e ${S}/Makefile ] || cp ${UNPACKDIR}/Makefile ${S}/
}
do_unpack[postfuncs] += "unpack_makefile_from_bsp"

add_nvethernetrm_symlink() {
    ln -s ../../../../../../nvethernetrm ${S}/nvidia-oot/drivers/net/ethernet/nvidia/nvethernet/nvethernetrm
}
do_unpack[postfuncs] += "add_nvethernetrm_symlink"

require nvidia-kernel-oot.inc

INSANE_SKIP:${PN}-devicetrees = "buildpaths"
