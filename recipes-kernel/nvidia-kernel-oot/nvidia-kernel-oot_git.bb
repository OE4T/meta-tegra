SRC_REPO_NV_OOT = "gitlab.com/nvidia/nv-tegra/linux-nv-oot.git;protocol=https"
SRC_REPO_BUILD_NV_PUBLIC = "gitlab.com/nvidia/nv-tegra/kernel/build/nvidia-public.git;protocol=https"
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
    git://${SRC_REPO_BUILD_NV_PUBLIC};branch=${SRCBRANCH};name=build-nv-public;destsuffix=${BPN}-${PV}/build/nvidia-public \
    git://${SRC_REPO_NV_ETHERNETRM};branch=${SRCBRANCH};name=nvethernetrm;destsuffix=${BPN}-${PV}/nvethernetrm \
    git://${SRC_REPO_UNIFIED_GPU_DISP};branch=${SRCBRANCH};name=unifiedgpudisp;destsuffix=${BPN}-${PV}/unifiedgpudisp \
    git://${SRC_REPO_NV_KERNEL_DISPLAY};branch=${SRCBRANCH};name=nvdisplay;destsuffix=${BPN}-${PV}/nvdisplay \
    git://${SRC_REPO_HWPM};branch=${SRCBRANCH};name=hwpm;destsuffix=${BPN}-${PV}/hwpm \
    git://${SRC_REPO_NVGPU};branch=${SRCBRANCH};name=nvgpu;destsuffix=${BPN}-${PV}/nvgpu \
    git://${SRC_REPO_T264_DTS};branch=${SRCBRANCH};name=t264-dts;destsuffix=${BPN}-${PV}/hardware/nvidia/t264/nv-public \
    git://${SRC_REPO_T23X_DTS};branch=${SRCBRANCH};name=t23x-dts;destsuffix=${BPN}-${PV}/hardware/nvidia/t23x/nv-public \
    git://${SRC_REPO_TEGRA_DTS};branch=${SRCBRANCH};name=tegra-dts;destsuffix=${BPN}-${PV}/hardware/nvidia/tegra/nv-public \
"

SRCBRANCH = "l4t/l4t-r39.2.1"
# tag: jetson_39.2.1
SRCREV_nvidia-oot = "e71bacb7c611f880c5f341263967f13de54de3a9"
SRCREV_build-nv-public = "c47fa7443cbf767db422430121de2bf4a5bac895"
SRCREV_nvethernetrm = "92d78286257d0c536c78e140f7bae159c625edcf"
SRCREV_unifiedgpudisp = "5c61042f62d7c5310a75e34770cdde4a2644d1a2"
SRCREV_nvdisplay = "f6983e7d6013ed3f3b532fcdb32b7fd1d1d1a884"
SRCREV_hwpm = "80b966b1bdc20f896cc9625a84708cbbe4638e38"
SRCREV_nvgpu = "fc23d33512d3bf1361b201e31406c47762102429"
SRCREV_t264-dts = "a7ee111043299392ec982eca434123f4291e39c6"
SRCREV_t23x-dts = "0e155aa7767cba2595faf05a8ab14c4cee5a7a27"
SRCREV_tegra-dts = "d76c3a7751ea75592413b42c984eac7084a99592"

SRCREV_FORMAT = "nvidia-oot_build-nv-public_nvethernetrm_unifiedgpudisp_nvdisplay_hwpm_nvgpu_t264-dts_t23x-dts_tegra-dts"

inherit l4t_bsp

PV = "${L4T_VERSION}+git"
DEFAULT_PREFERENCE = "-1"

add_nvethernetrm_symlink() {
    ln -snf ../../../../../../nvethernetrm ${S}/nvidia-oot/drivers/net/ethernet/nvidia/nvethernet/nvethernetrm
}
do_unpack[postfuncs] += "add_nvethernetrm_symlink"

require nvidia-kernel-oot.inc
