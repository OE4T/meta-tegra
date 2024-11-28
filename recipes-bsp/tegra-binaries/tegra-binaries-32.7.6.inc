LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=953d15076a1b575cf31914fafdf9ae5a \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${L4T_VERSION}_aarch64.tbz2;name=l4t \
           ${L4T_URI_BASE}/secureboot_R${L4T_VERSION}_aarch64.tbz2;downloadfilename=${L4T_BSP_PREFIX}_secureboot_${L4T_VERSION}.tbz2;name=sb"
L4T_SHA256SUM = "8818e8219beaf6876e71b25fdc72f96efe911046fa7a56f4db319d7c415fad0e"
L4T_SHA256SUM:tegra210 = "865717eed7076babd3c193fc39bc7df41cc457b554059d374c624c42bcde0212"
SRC_URI[l4t.sha256sum] = "${L4T_SHA256SUM}"
SB_SHA256SUM = "6794cc91a04c6c5fdaa30081efec5ba115ddd6d22a9ebdc10fac21f78b5e5242"
SB_SHA256SUM:tegra210 = "c158a8b8f6c41166391a5bfe45ed38a7fac88eef776c03ac670ac12002ebdcd1"
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
