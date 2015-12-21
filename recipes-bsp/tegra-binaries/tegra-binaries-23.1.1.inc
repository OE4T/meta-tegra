LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=60ad17cc726658e8cf73578bea47b85f \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r23_Release_v1.0/Tegra210_Linux_R${PV}_armhf.tbz2"
SRC_URI[md5sum] = "4773c4aff47f464f0a6eb2a8342b05e7"
SRC_URI[sha256sum] = "aacb0b1e9571df43621a6fd32101f8f52f9387ecb84e96930fb5682a68676c15"
SRC_URI += "file://xorg.conf"

S = "${TMPDIR}/work-shared/tegra-binaries-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
