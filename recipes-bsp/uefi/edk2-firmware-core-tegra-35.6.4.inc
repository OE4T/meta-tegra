LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

EDK2_SRC_URI = "gitsm://github.com/NVIDIA/edk2.git;protocol=https;branch=r35.6.4-updates"

SRCREV_edk2 = "dba20a0361ff53b20710b1e05c357ba0653aab5c"

SRC_URI = "${EDK2_SRC_URI};name=edk2;destsuffix=edk2-tegra/edk2;nobranch=1"

S = "${WORKDIR}/edk2-tegra/edk2"
