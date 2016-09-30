LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=60ad17cc726658e8cf73578bea47b85f \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r24_Release_v2.0/BSP/Tegra210_Linux_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "aa2794530e93e7bddb90e3aa9c0521ea"
SRC_URI[sha256sum] = "bfea578cd0689e9129e88c5c6fc1d344b3f7e52e356c00ecaccfaa286a078837"
SRC_URI += "\
    file://xorg.conf \
    file://nvcamera-daemon.init \
    file://nvcamera-daemon.service \
    file://argus-daemon.init \
    file://argus-daemon.service \
"

S = "${TMPDIR}/work-shared/tegra-binaries-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "(tegra210)"
