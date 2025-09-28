LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

SRC_URI_EDK2 = "gitsm://github.com/NVIDIA/edk2.git;protocol=https;branch=r38.2-updates"

SRCREV_edk2 = "e7ea3db10d499e900ad8636fb017e41b4762548d"

SRC_URI = "${SRC_URI_EDK2};name=edk2;destsuffix=edk2-tegra/edk2"

S = "${UNPACKDIR}/edk2-tegra/edk2"
