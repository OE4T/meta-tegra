LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=953d15076a1b575cf31914fafdf9ae5a \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${L4T_VERSION}_aarch64.tbz2;name=l4t \
           ${L4T_URI_BASE}/secureboot_R${L4T_VERSION}_aarch64.tbz2;downloadfilename=${L4T_BSP_PREFIX}_secureboot_${L4T_VERSION}.tbz2;name=sb"
L4T_SHA256SUM = "78c322417ad87a96d390ed3b75ec6919866a92fd14aeb1236a41ac69cb11975b"
L4T_SHA256SUM:tegra210 = "e9e532f90d3168d2af0fbe3ce56e37c2f079845c6e1b7ebffbf497008bc5a6e6"
SRC_URI[l4t.sha256sum] = "${L4T_SHA256SUM}"
SB_SHA256SUM = "86ff2b27288034908e48a42c4b53cb14bcb59edb7f4570c1dc62405f23879b3e"
SB_SHA256SUM:tegra210 = "a108189174e7e2d5874b2193f34a282208101ff25faab5d75a15a0df0dd6faba"
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
