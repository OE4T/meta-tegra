LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=60ad17cc726658e8cf73578bea47b85f"

SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r21_Release_v5.0/Tegra124_Linux_R${PV}_armhf.tbz2"
SRC_URI[md5sum] = "6b2df64b29eba7f643d4497a5fd454d7"
SRC_URI[sha256sum] = "a639e3e37e17ef11ea5754d7b703a133febdd6a5d3dd2e5b3924288930a45cca"

S = "${TMPDIR}/work-shared/tegra-binaries-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "(tegra124)"
