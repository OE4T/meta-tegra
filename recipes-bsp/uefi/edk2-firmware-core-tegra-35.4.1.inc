LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

EDK2_SRC_URI = "gitsm://github.com/NVIDIA/edk2.git;protocol=https;branch=uefi-202305.2-updates"

SRCREV_edk2 = "39cc3789d2212ecaad18e52e9cb792d2f366527f"

SRC_URI = "${EDK2_SRC_URI};name=edk2;destsuffix=edk2-tegra/edk2;nobranch=1"

S = "${WORKDIR}/edk2-tegra/edk2"
