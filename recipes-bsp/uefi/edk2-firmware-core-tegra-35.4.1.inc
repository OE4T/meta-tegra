LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

EDK2_SRC_URI = "gitsm://github.com/NVIDIA/edk2.git;protocol=https;branch=main-edk2-stable202208-updates"

SRCREV_edk2 = "7ba0a31b503cd0bf82a97e90f460ad7586ba80c4"

SRC_URI = "${EDK2_SRC_URI};name=edk2;destsuffix=edk2-tegra/edk2;nobranch=1"

S = "${WORKDIR}/edk2-tegra/edk2"
