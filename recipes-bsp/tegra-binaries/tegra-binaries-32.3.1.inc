LICENSE = "Proprietary"
HOMEPAGE = "https://developer.nvidia.com/embedded/linux-tegra"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${PV}_aarch64.tbz2;name=l4t \
	   ${L4T_URI_BASE}/secureboot_R${PV}_aarch64.tbz2;downloadfilename=${L4T_BSP_PREFIX}_secureboot_${PV}.tbz2;name=sb"
L4T_MD5SUM = "667462228a705bf3a64e83d0731c192a"
L4T_SHA256SUM = "91bd6f7508ce0e03248747c45fc43992e1f145f6b663fc1a6dba5dba07e7add4"
L4T_MD5SUM_tegra210 = "99e0fe768c0c7f6b613141d91b7f3d50"
L4T_SHA256SUM_tegra210 = "4c8373045d11aa8ad4836a06a76b1de55004485be76bf62fa95f72e7d23c1bf6"
SRC_URI[l4t.md5sum] = "${L4T_MD5SUM}"
SRC_URI[l4t.sha256sum] = "${L4T_SHA256SUM}"
SB_MD5SUM = "6b5b65cad7da45c5f31f0ffc69caca74"
SB_SHA256SUM = "3fea6fb85c8d2b6bf78679a87dbb0f8567f755947ab3849d211d6add921257ef"
SB_MD5SUM_tegra210 = "24f18b3730c348ac0b0f14cea3056256"
SB_SHA256SUM_tegra210 = "6151e15d47a0b92a6851d422000cf442bae3801c68241c12d1a198037f592203"
SRC_URI[sb.md5sum] = "${SB_MD5SUM}"
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
