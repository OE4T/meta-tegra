LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"


SRC_URI = "\
    ${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${L4T_VERSION}_aarch64.tbz2;name=l4t \
    ${L4T_ALT_URI_BASE}/secureboot_R${L4T_ALT_VERSION}_aarch64.tbz2;downloadfilename=${L4T_BSP_PREFIX}_secureboot_${L4T_ALT_VERSION}.tbz2;name=sb \
    https://developer.nvidia.com/overlay-3251-agx-sku4tbz2;downloadfilename=${L4T_BSP_PREFIX}_dtb_patch_${L4T_ALT_VERSION}.tbz2;name=dtb \
"
L4T_SHA256SUM = "308e21bb708c41bada1e349fbe6a1dd9fd7294826edf7aa81073a3ed11fb0c93"
L4T_SHA256SUM_tegra210 = "6e61a77739fc9184d6eda5aa3de5a0d6b69659a78411cd2d9bcb3a59416bb8cd"
SRC_URI[l4t.sha256sum] = "${L4T_SHA256SUM}"
SB_SHA256SUM = "a795ef0e433ac1b3991f5597dd79e15e88d648170edcc25f2f2f3c1dd62c533b"
SB_SHA256SUM_tegra210 = "0fc525061d203ec2929d985ec711f477cca679cc8e4f0a4a32ca384681811808"
SRC_URI[sb.sha256sum] = "${SB_SHA256SUM}"
DTB_SHA256SUM = "5aba8449861d4ff325c5539cc3a83622383d27a808b72f85f211a7413366e311"
SRC_URI[dtb.sha256sum] = "${DTB_SHA256SUM}"

inherit l4t_bsp

S = "${TMPDIR}/work-shared/L4T-${L4T_BSP_ARCH}-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "tegra"

do_preconfigure() {
    :
}
