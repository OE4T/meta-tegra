LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

EDK2_SRC_URI = "gitsm://github.com/NVIDIA/edk2.git;protocol=https;branch=r36.2-updates"

SRCREV_edk2 = "f57ebb80644056eef33508ff09ea1945ef1321c4"

SRC_URI = "${EDK2_SRC_URI};name=edk2;destsuffix=edk2-tegra/edk2;nobranch=1"

S = "${WORKDIR}/edk2-tegra/edk2"
