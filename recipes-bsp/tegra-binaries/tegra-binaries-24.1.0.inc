LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=60ad17cc726658e8cf73578bea47b85f \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r24_Release_v1.0/24.1_64bit/t210ref_release_aarch64/Tegra210_Linux_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "27dba660d61196d39f38375b3773d272"
SRC_URI[sha256sum] = "b94e17dcb357d0ddcfd847db014e94343b8f8b0323d2bb9ec7f373fd4d26430f"
SRC_URI += "file://xorg.conf"

S = "${TMPDIR}/work-shared/tegra-binaries-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
