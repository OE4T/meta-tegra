LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"


SRC_URI = "${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${L4T_VERSION}_aarch64.tbz2;name=l4t \
	   ${L4T_URI_BASE}/secureboot_R${L4T_VERSION}_aarch64.tbz2;downloadfilename=${L4T_BSP_PREFIX}_secureboot_${L4T_VERSION}.tbz2;name=sb"
L4T_SHA256SUM = "6df03712bfbe71a076eb443cdcfbd87481e6f9feca9d33455b6b85ff7fdcb223"
L4T_SHA256SUM:tegra210 = "3e7c304b56c2c9f2ca865d560d22e8ab3d99d2e79d782342fb0fb1c4a5e7946e"
SRC_URI[l4t.sha256sum] = "${L4T_SHA256SUM}"
SB_SHA256SUM = "d6f39566b8d5628f016f2dd3103577358c3d8815a7b004a8cdbaded040d439f5"
SB_SHA256SUM:tegra210 = "52f9f6b8804f3c0db6f29da09c307d888c256e5cccb32f7f5cc5a62fddb68a53"
SRC_URI[sb.sha256sum] = "${SB_SHA256SUM}"

inherit l4t_bsp

S = "${TMPDIR}/work-shared/L4T-${L4T_BSP_ARCH}-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "tegra"

do_preconfigure() {
    :
}
