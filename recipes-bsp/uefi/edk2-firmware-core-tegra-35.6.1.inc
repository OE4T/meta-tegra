LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

EDK2_SRC_URI = "gitsm://github.com/NVIDIA/edk2.git;protocol=https;branch=r35.6.1-edk2-stable202208-updates"

SRCREV_edk2 = "ce382c7493990f56c49b6da3fcff6d2b9d3126fd"

SRC_URI = "${EDK2_SRC_URI};name=edk2;destsuffix=edk2-tegra/edk2;nobranch=1"

S = "${WORKDIR}/edk2-tegra/edk2"
