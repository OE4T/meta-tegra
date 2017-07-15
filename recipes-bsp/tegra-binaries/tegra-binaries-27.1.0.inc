LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=60ad17cc726658e8cf73578bea47b85f \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r27_Release_v1.0/BSP/Tegra186_Linux_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "d59ebd3b57b0b83d1cb2951d14961a87"
SRC_URI[sha256sum] = "c3aa1ea4d85d92a7a70fc44e168e94dd64d0a7c779d48ca3f08be269f544ed38"
SRC_URI += "\
    file://xorg.conf \
    file://nvcamera-daemon.init \
    file://nvcamera-daemon.service \
    file://argus-daemon.init \
    file://argus-daemon.service \
    file://tegra186-flash-helper.sh \
"

S = "${TMPDIR}/work-shared/tegra-binaries-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "(tegra186)"
