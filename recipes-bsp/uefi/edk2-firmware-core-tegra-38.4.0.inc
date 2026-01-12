LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

SRC_URI_EDK2 = "gitsm://github.com/NVIDIA/edk2.git;protocol=https;branch=r38.4-updates"

SRCREV_edk2 = "0e5a182ffd6a2d00cd05e668ecea181a168c353f"

SRC_URI = "${SRC_URI_EDK2};name=edk2;destsuffix=edk2-tegra/edk2"

S = "${UNPACKDIR}/edk2-tegra/edk2"
