LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${PV}_aarch64.tbz2;name=l4t \
	   ${L4T_URI_BASE}/secureboot_R${PV}_aarch64.tbz2;downloadfilename=${L4T_BSP_PREFIX}_secureboot_${PV}.tbz2;name=sb"
L4T_SHA256SUM = "ea7b4ee7533ad7bb1047f6045762fc155bdeda56d7d579db9592995eb70d4696"
L4T_SHA256SUM_tegra210 = "bc89cbb20286bbe7681a3ff2f43d1a87f33cdde2506ec8c0afe2e88401f24e58"
SRC_URI[l4t.sha256sum] = "${L4T_SHA256SUM}"
SB_SHA256SUM = "c856ce7c4e38cd2479caf081abe6f5c7308381512fa26cc14e1566049466642e"
SB_SHA256SUM_tegra210 = "f7f6999adb45d6d152c51af806c3394f7a01e86d9abfb28f4d0c3a3a341ad02c"
SRC_URI[sb.sha256sum] = "${SB_SHA256SUM}"

inherit l4t_bsp

S = "${TMPDIR}/work-shared/L4T-${SOC_FAMILY}-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "tegra"

do_preconfigure() {
    :
}
