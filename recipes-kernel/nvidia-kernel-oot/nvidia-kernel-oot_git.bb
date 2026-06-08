SRC_REPO_NV_OOT = "gitlab.com/nvidia/nv-tegra/linux-nv-oot.git;protocol=https"
SRC_REPO_NV_ETHERNETRM = "gitlab.com/nvidia/nv-tegra/kernel/nvethernetrm.git;protocol=https"
SRC_REPO_UNIFIED_GPU_DISP = "gitlab.com/nvidia/nv-tegra/tegra/kernel-src/nv-unified-gpu-display-driver.git;protocol=https"
SRC_REPO_NV_KERNEL_DISPLAY = "gitlab.com/nvidia/nv-tegra/tegra/kernel-src/nv-kernel-display-driver.git;protocol=https"
SRC_REPO_HWPM = "gitlab.com/nvidia/nv-tegra/linux-hwpm.git;protocol=https"
SRC_REPO_NVGPU = "gitlab.com/nvidia/nv-tegra/tegra/kernel-src/linux-nvgpu.git;protocol=https"
SRC_REPO_T264_DTS = "gitlab.com/nvidia/nv-tegra/device/hardware/nvidia/t264-public-dts.git;protocol=https"
SRC_REPO_T23X_DTS = "gitlab.com/nvidia/nv-tegra/device/hardware/nvidia/t23x-public-dts.git;protocol=https"
SRC_REPO_TEGRA_DTS = "gitlab.com/nvidia/nv-tegra/device/hardware/nvidia/tegra-public-dts.git;protocol=https"

SRC_URI = " \
    git://${SRC_REPO_NV_OOT};branch=${SRCBRANCH};name=nvidia-oot;destsuffix=${BPN}-${PV}/nvidia-oot \
    git://${SRC_REPO_NV_ETHERNETRM};branch=${SRCBRANCH};name=nvethernetrm;destsuffix=${BPN}-${PV}/nvethernetrm \
    git://${SRC_REPO_UNIFIED_GPU_DISP};branch=${SRCBRANCH};name=unifiedgpudisp;destsuffix=${BPN}-${PV}/unifiedgpudisp \
    git://${SRC_REPO_NV_KERNEL_DISPLAY};branch=${SRCBRANCH};name=nvdisplay;destsuffix=${BPN}-${PV}/nvdisplay \
    git://${SRC_REPO_HWPM};branch=${SRCBRANCH};name=hwpm;destsuffix=${BPN}-${PV}/hwpm \
    git://${SRC_REPO_NVGPU};branch=${SRCBRANCH};name=nvgpu;destsuffix=${BPN}-${PV}/nvgpu \
    git://${SRC_REPO_T264_DTS};branch=${SRCBRANCH};name=t264-dts;destsuffix=${BPN}-${PV}/hardware/nvidia/t264/nv-public \
    git://${SRC_REPO_T23X_DTS};branch=${SRCBRANCH};name=t23x-dts;destsuffix=${BPN}-${PV}/hardware/nvidia/t23x/nv-public \
    git://${SRC_REPO_TEGRA_DTS};branch=${SRCBRANCH};name=tegra-dts;destsuffix=${BPN}-${PV}/hardware/nvidia/tegra/nv-public \
"

SRCBRANCH = "l4t/l4t-r39.2"
# tag: jetson_39.2_GA
SRCREV_nvidia-oot = "100424630afc3e3d9561d8c4a0f6be6f9f1af7f7"
SRCREV_nvethernetrm = "cf2ae53fdf4ee85333f3b3907d1337d6546db64f"
SRCREV_unifiedgpudisp = "a662a636f19939ce9d11ac25a873fab8b2dedb36"
SRCREV_nvdisplay = "c66bad7cc07f605a59a46cf7e6585433c88ad161"
SRCREV_hwpm = "80b966b1bdc20f896cc9625a84708cbbe4638e38"
SRCREV_nvgpu = "fb2c5dedeb1843eab91e8fd7e976fdc277d58aa3"
SRCREV_t264-dts = "664a726551f62958a5a57b6538be01b9a62cd178"
SRCREV_t23x-dts = "3897df11a86397704db8685071de46559cfb3c6e"
SRCREV_tegra-dts = "d76c3a7751ea75592413b42c984eac7084a99592"

SRCREV_FORMAT = "nvidia-oot_nvethernetrm_unifiedgpudisp_nvdisplay_hwpm_nvgpu_t264-dts_t23x-dts_tegra-dts_kernel-devicetree"

inherit l4t_bsp

PV = "${L4T_VERSION}+git"
DEFAULT_PREFERENCE = "-1"

add_nvethernetrm_symlink() {
    ln -snf ../../../../../../nvethernetrm ${S}/nvidia-oot/drivers/net/ethernet/nvidia/nvethernet/nvethernetrm
}
do_unpack[postfuncs] += "add_nvethernetrm_symlink"

require nvidia-kernel-oot.inc
