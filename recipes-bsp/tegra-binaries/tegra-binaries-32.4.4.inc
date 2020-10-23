LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${PV}_aarch64.tbz2;name=l4t \
	   ${L4T_URI_BASE}/secureboot_R${PV}_aarch64.tbz2;downloadfilename=${L4T_BSP_PREFIX}_secureboot_${PV}.tbz2;name=sb"
L4T_SHA256SUM = "3d0dccd05159261a32229ddcb6f85fea7bad70dcad97300dbb86e234cae5edf6"
L4T_SHA256SUM_tegra210 = "ce1cc0e4771454c668da526f8d862d0058c42f2fbaab1d8cafb2f28385b288ad"
SRC_URI[l4t.sha256sum] = "${L4T_SHA256SUM}"
SB_SHA256SUM = "4cbc2f7d05bc5bbeebe0026f61a9791d43b3a8be91cd2e751d014754d782d282"
SB_SHA256SUM_tegra210 = "396a0ed1f847565077edaec0859b6ff94305193c4f5a8c358712fce2f198c06c"
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
