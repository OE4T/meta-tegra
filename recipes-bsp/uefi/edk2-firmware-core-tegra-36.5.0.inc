LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

SRC_URI_EDK2 = "gitsm://github.com/NVIDIA/edk2.git;protocol=https;branch=r36.5-updates"

SRCREV_edk2 = "30419afc4a6ea666434f9275057f579b46a07a5f"

SRC_URI = "${SRC_URI_EDK2};name=edk2;destsuffix=edk2-tegra/edk2"

S = "${WORKDIR}/edk2-tegra/edk2"
