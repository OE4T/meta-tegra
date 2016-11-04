LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=60ad17cc726658e8cf73578bea47b85f \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/008/linux-x64/Tegra210_Linux_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "bcffd5c9ab74d3ab268d36d79e9b3e80"
SRC_URI[sha256sum] = "cfc425118a208ca15378473358296e737bfa89a0faebcaaf8c8e06462c8d372c"
SRC_URI += "\
    file://xorg.conf \
    file://nvcamera-daemon.init \
    file://nvcamera-daemon.service \
"

S = "${TMPDIR}/work-shared/tegra-binaries-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "(tegra210)"
