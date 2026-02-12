SRC_REPO_NV_OOT = "gitlab.com/nvidia/nv-tegra/linux-nv-oot.git;protocol=https"
SRC_REPO_NV_ETHERNETRM = "gitlab.com/nvidia/nv-tegra/kernel/nvethernetrm.git;protocol=https"
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
    git://${SRC_REPO_NV_KERNEL_DISPLAY};branch=${SRCBRANCH};name=nvdisplay;destsuffix=${BPN}-${PV}/nvdisplay \
    git://${SRC_REPO_HWPM};branch=${SRCBRANCH};name=hwpm;destsuffix=${BPN}-${PV}/hwpm \
    git://${SRC_REPO_NVGPU};branch=${SRCBRANCH};name=nvgpu;destsuffix=${BPN}-${PV}/nvgpu \
    git://${SRC_REPO_T23X_DTS};branch=${SRCBRANCH};name=t23x-dts;destsuffix=${BPN}-${PV}/hardware/nvidia/t23x/nv-public \
    git://${SRC_REPO_TEGRA_DTS};branch=${SRCBRANCH};name=tegra-dts;destsuffix=${BPN}-${PV}/hardware/nvidia/tegra/nv-public \
    git://${SRC_REPO_KERNEL_DTS};branch=${SRCBRANCH};name=kernel-devicetree;destsuffix=${BPN}-${PV}/kernel-devicetree \
"

SRCBRANCH = "l4t/l4t-r36.5"
# tag: jetson_36.5
SRCREV_nvidia-oot = "b05da1f94ae5a2dbfe928ef7ab387035a2b77a20"
SRCREV_nvethernetrm = "22e582e01d1c9c258ac56873f1aa0822acb695f3"
SRCREV_nvdisplay = "a3da679668071cff718a33922b6d302bd18ebaa1"
SRCREV_hwpm = "ebfe0e9c4b4d96e07232ceb37f67168a1234ffeb"
SRCREV_nvgpu = "a4145c7b6e05af9b5ee4b8da246e3b3820023dca"
SRCREV_t23x-dts = "ed1b0f6b113bb050c8cda1ccb411a163f8e2799f"
SRCREV_tegra-dts = "8ba5d53ef1e1753f9f2a5b1f7b7b5fc5039de68e"
SRCREV_kernel-devicetree = "19952c8e25702e9de23500c3b1fb351bf4380446"

SRCREV_FORMAT = "nvidia-oot_nvethernetrm_nvdisplay_hwpm_nvgpu_t23x-dts_tegra-dts_kernel-devicetree"

inherit l4t_bsp

PV = "${L4T_VERSION}+git"
DEFAULT_PREFERENCE = "-1"

add_nvethernetrm_symlink() {
    ln -snf ../../../../../../nvethernetrm ${S}/nvidia-oot/drivers/net/ethernet/nvidia/nvethernet/nvethernetrm
}
do_unpack[postfuncs] += "add_nvethernetrm_symlink"

require nvidia-kernel-oot.inc
