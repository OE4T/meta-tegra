LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

SRC_URI_EDK2 = "gitsm://github.com/NVIDIA/edk2.git;protocol=https;branch=r36.3.0-updates"

SRCREV_edk2 = "36cdf6e1e3a3b5a74f90cb7a5ae2eaf348617a95"

SRC_URI = "${SRC_URI_EDK2};name=edk2;destsuffix=edk2-tegra/edk2"

S = "${WORKDIR}/edk2-tegra/edk2"
