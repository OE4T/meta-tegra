LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"


SRC_URI = "${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${L4T_VERSION}_aarch64.tbz2;name=l4t \
	   ${L4T_URI_BASE}/secureboot_R${L4T_VERSION}_aarch64.tbz2;downloadfilename=${L4T_BSP_PREFIX}_secureboot_${L4T_VERSION}.tbz2;name=sb"
SRC_URI[l4t.sha256sum] = "670021f6a288b8ea916fd4089fbdde9ab8dd5003f23079aaf439424b35de454c"
SRC_URI[sb.sha256sum] = "a5d4b16b50e93c8f2bd2104f7dd6ec07f0f7665f58dc988e114eed174d57ed78"

inherit l4t_bsp

S = "${TMPDIR}/work-shared/L4T-${L4T_BSP_ARCH}-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "tegra"

do_preconfigure() {
    :
}
