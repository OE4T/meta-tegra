LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"


SRC_URI = "${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${L4T_VERSION}_aarch64.tbz2;name=l4t \
	   ${L4T_URI_BASE}/secureboot_R${L4T_VERSION}_aarch64.tbz2;downloadfilename=${L4T_BSP_PREFIX}_secureboot_${L4T_VERSION}.tbz2;name=sb"
L4T_SHA256SUM = "7ef9c08e1e33a59ca1275a8ca480f657791e01b1742244898a7e88aaafc48fb2"
L4T_SHA256SUM_tegra210 = "5fa9f47ea3f2e16f3696211631c8110cb63c4fc70195c5034823085cf90d3f67"
SRC_URI[l4t.sha256sum] = "${L4T_SHA256SUM}"
SB_SHA256SUM = "6979b7743a26c780a4058b0302f21d892cc380dc6376d66f6146930722ff78d4"
SB_SHA256SUM_tegra210 = "c0e5cf2e51f0f8c4f95b95f1b55fe58bc1cd7cab9fa0bd6082a0c50df635dad6"
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
