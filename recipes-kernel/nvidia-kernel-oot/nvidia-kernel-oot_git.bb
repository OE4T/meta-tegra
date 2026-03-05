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

SRCBRANCH = "l4t/l4t-r36.4.4"
# tag: jetson_36.4.4
SRCREV_nvidia-oot = "11816976938bf47c9c1c3b06e20ae3a82c78dba4"
SRCREV_nvethernetrm = "035347c85eb946ef5f89b23a78ec25faeab18489"
SRCREV_nvdisplay = "276f19772385b9284832966832ffaa6443dc4f68"
SRCREV_hwpm = "bee07a35a5b770ddf4859655a7c7ea80d041b85c"
SRCREV_nvgpu = "91aa42fedb1bbca0b5be8b81d113fc227657652b"
SRCREV_t23x-dts = "e68291edcd280aee24f3c5946c8ca2f0c246a83d"
SRCREV_tegra-dts = "0bbc9b54343c6e915764b2410fb3952b58e7d624"
SRCREV_kernel-devicetree = "db812ba3c59e4e7707cf9d0fbb236a19ccb4cbb2"

SRCREV_FORMAT = "nvidia-oot_nvethernetrm_nvdisplay_hwpm_nvgpu_t23x-dts_tegra-dts_kernel-devicetree"

inherit l4t_bsp

PV = "${L4T_VERSION}+git"
DEFAULT_PREFERENCE = "-1"

add_nvethernetrm_symlink() {
    ln -snf ../../../../../../nvethernetrm ${S}/nvidia-oot/drivers/net/ethernet/nvidia/nvethernet/nvethernetrm
}
do_unpack[postfuncs] += "add_nvethernetrm_symlink"

require nvidia-kernel-oot.inc
