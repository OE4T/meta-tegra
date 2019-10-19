LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${PV}_aarch64.tbz2;name=l4t \
	   ${L4T_URI_BASE}/secureboot_R${PV}_aarch64.tbz2;downloadfilename=${L4T_BSP_PREFIX}_secureboot_${PV}.tbz2;name=sb"
L4T_MD5SUM = "47a7b43f119888f7506a6ff447f43036"
L4T_SHA256SUM = "37590079ca19f7c6b0735f90a189ad7a7dc220d37cd1b72df5de703469ca0e65"
L4T_MD5SUM_tegra210 = "2d648bbc77c510c4e7e0c809996d24e8"
L4T_SHA256SUM_tegra210 = "61b71826da43ec1146bea362a019809d96eca77718ace8d745578c992e024885"
SRC_URI[l4t.md5sum] = "${L4T_MD5SUM}"
SRC_URI[l4t.sha256sum] = "${L4T_SHA256SUM}"
SB_MD5SUM = "788c0551ebad93308e56a4ff143e9e91"
SB_SHA256SUM = "cc47c9e07506f5796baf88cb944b4df52f92cd0d355477b32578f12e0f3cd7bf"
SB_MD5SUM_tegra210 = "33239f75f0c59e2e2ce1e721aeacc9ce"
SB_SHA256SUM_tegra210 = "8bc3373006251f770fae0c3b33a4f1f4835928e90f0e82facb6985df237b2df0"
SRC_URI[sb.md5sum] = "${SB_MD5SUM}"
SRC_URI[sb.sha256sum] = "${SB_SHA256SUM}"

inherit l4t_bsp

SRC_URI += "\
    file://nvargus-daemon.init \
    file://nvargus-daemon.service \
    file://nvpmodel.init \
    file://nvpmodel.service \
    file://nvphs.init \
    file://nvphs.service \
    file://nvs-service.init \
    file://nvs-service.service \
    file://nvstartup.init \
    file://nvstartup.service \
    file://tegra186-flash-helper.sh \
    file://tegra194-flash-helper.sh \
    file://tegra210-flash-helper.sh \
"

S = "${TMPDIR}/work-shared/L4T-${SOC_FAMILY}-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

do_preconfigure() {
    cp ${WORKDIR}/*-daemon.* ${WORKDIR}/nvstartup.* ${S}/
    cp ${WORKDIR}/nvpmodel.* ${WORKDIR}/nvphs.* ${WORKDIR}/nvs-service.* ${S}/
    cp ${WORKDIR}/tegra*-flash-helper.sh ${S}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "tegra"
