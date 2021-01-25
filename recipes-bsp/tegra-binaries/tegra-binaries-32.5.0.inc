LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${PV}_aarch64.tbz2;name=l4t \
	   ${L4T_URI_BASE}/secureboot_R${PV}_aarch64.tbz2;downloadfilename=${L4T_BSP_PREFIX}_secureboot_${PV}.tbz2;name=sb"
L4T_SHA256SUM = "43704bdc4754d049c750ef880885fab5dfd671d61a1a3f9c8d73a7ea2e2cca50"
L4T_SHA256SUM_tegra210 = "0c8590218be814eeec2a6f8b4fd01271893e4881ef2c66d130792c1d62737642"
SRC_URI[l4t.sha256sum] = "${L4T_SHA256SUM}"
SB_SHA256SUM = "a795ef0e433ac1b3991f5597dd79e15e88d648170edcc25f2f2f3c1dd62c533b"
SB_SHA256SUM_tegra210 = "500d51a54a2475f3d2f5bd5e413b4ab849528f21a69aec52274e79cba04c9b8c"
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
