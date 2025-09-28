SRC_REPO_NV_OOT = "gitlab.com/nvidia/nv-tegra/linux-nv-oot.git;protocol=https"
SRC_REPO_NV_ETHERNETRM = "gitlab.com/nvidia/nv-tegra/kernel/nvethernetrm.git;protocol=https"
SRC_REPO_UNIFIED_GPU_DISP = "gitlab.com/nvidia/nv-tegra/tegra/kernel-src/nv-unified-gpu-display-driver.git;protocol=https"
SRC_REPO_NV_KERNEL_DISPLAY = "gitlab.com/nvidia/nv-tegra/tegra/kernel-src/nv-kernel-display-driver.git;protocol=https"
SRC_REPO_HWPM = "gitlab.com/nvidia/nv-tegra/linux-hwpm.git;protocol=https"
SRC_REPO_NVGPU = "gitlab.com/nvidia/nv-tegra/linux-nvgpu.git;protocol=https"
SRC_REPO_T264_DTS = "gitlab.com/nvidia/nv-tegra/device/hardware/nvidia/t264-public-dts.git;protocol=https"
SRC_REPO_T23X_DTS = "gitlab.com/nvidia/nv-tegra/device/hardware/nvidia/t23x-public-dts.git;protocol=https"
SRC_REPO_TEGRA_DTS = "gitlab.com/nvidia/nv-tegra/device/hardware/nvidia/tegra-public-dts.git;protocol=https"
SRC_REPO_KERNEL_DTS = "gitlab.com/nvidia/nv-tegra/linux/kernel-devicetree.git;protocol=https"

SRC_URI = " \
    git://${SRC_REPO_NV_OOT};branch=${SRCBRANCH};name=nvidia-oot;destsuffix=${BPN}-${PV}/nvidia-oot \
    git://${SRC_REPO_NV_ETHERNETRM};branch=${SRCBRANCH};name=nvethernetrm;destsuffix=${BPN}-${PV}/nvethernetrm \
    git://${SRC_REPO_UNIFIED_GPU_DISP};branch=${SRCBRANCH};name=unifiedgpudisp;destsuffix=${BPN}-${PV}/unifiedgpudisp \
    git://${SRC_REPO_NV_KERNEL_DISPLAY};branch=${SRCBRANCH};name=nvdisplay;destsuffix=${BPN}-${PV}/nvdisplay \
    git://${SRC_REPO_HWPM};branch=${SRCBRANCH};name=hwpm;destsuffix=${BPN}-${PV}/hwpm \
    git://${SRC_REPO_T264_DTS};branch=${SRCBRANCH};name=t264-dts;destsuffix=${BPN}-${PV}/hardware/nvidia/t264/nv-public \
    git://${SRC_REPO_T23X_DTS};branch=${SRCBRANCH};name=t23x-dts;destsuffix=${BPN}-${PV}/hardware/nvidia/t23x/nv-public \
    git://${SRC_REPO_TEGRA_DTS};branch=${SRCBRANCH};name=tegra-dts;destsuffix=${BPN}-${PV}/hardware/nvidia/tegra/nv-public \
    git://${SRC_REPO_KERNEL_DTS};branch=${SRCBRANCH};name=kernel-devicetree;destsuffix=${BPN}-${PV}/kernel-devicetree \
"

SRCBRANCH = "l4t/l4t-r38.4"
# tag: jetson_38.4
SRCREV_nvidia-oot = "33227347c89543042e0eda352db5de852983c8a3"
SRCREV_nvethernetrm = "507a71fe0be3aabf638c52013a22f1afabda0c5a"
SRCREV_unifiedgpudisp = "e75fa2f597f20ea36cae5d3de56174638643050a"
SRCREV_nvdisplay = "5073a1b3d2bd014175bccc9f611ce53e550c3a25"
SRCREV_hwpm = "d5799a7050d439a5be88bfb0349ffc3e60a3c9eb"
SRCREV_t264-dts = "bd958e8c5965ea4831ddab7ea522b0b7e8717aea"
SRCREV_t23x-dts = "21daec5a0cde295fb53506271a796cc2707d8c86"
SRCREV_tegra-dts = "2101ccc1b1ef8267e63286332aed34891943c7c7"
SRCREV_kernel-devicetree = "d488feae6501566a90743e45729608bc5f2586f9"

SRCREV_FORMAT = "nvidia-oot_nvethernetrm_unifiedgpudisp_nvdisplay_hwpm_t264-dts_t23x-dts_tegra-dts_kernel-devicetree"

inherit l4t_bsp

PV = "${L4T_VERSION}+git"
DEFAULT_PREFERENCE = "-1"

add_nvethernetrm_symlink() {
    ln -snf ../../../../../../nvethernetrm ${S}/nvidia-oot/drivers/net/ethernet/nvidia/nvethernet/nvethernetrm
}
do_unpack[postfuncs] += "add_nvethernetrm_symlink"

require nvidia-kernel-oot.inc
