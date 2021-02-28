LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

# XXX--
# Handle yet another break in the naming convention
BSP_XXX_PREFIX = "${L4T_BSP_PREFIX}"
BSP_XXX_PREFIX_tegra210 = "Jetson-210"
# -- XXX

SRC_URI = "${L4T_URI_BASE}/${BSP_XXX_PREFIX}_Linux_R${L4T_VERSION}_aarch64.tbz2;name=l4t \
	   ${L4T_ALT_URI_BASE}/secureboot_R${L4T_ALT_VERSION}_aarch64.tbz2;downloadfilename=${L4T_BSP_PREFIX}_secureboot_${L4T_ALT_VERSION}.tbz2;name=sb"
L4T_SHA256SUM = "180542ff70d10676a02191fa104edd8ccf8ce080bb8ef7e2296c2ed66e3fb7db"
L4T_SHA256SUM_tegra210 = "d3556102f5634c80a4ee3b8752d86a02431e30ff9fa865c31b7b955b427fe4d4"
SRC_URI[l4t.sha256sum] = "${L4T_SHA256SUM}"
SB_SHA256SUM = "a795ef0e433ac1b3991f5597dd79e15e88d648170edcc25f2f2f3c1dd62c533b"
SB_SHA256SUM_tegra210 = "0fc525061d203ec2929d985ec711f477cca679cc8e4f0a4a32ca384681811808"
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
