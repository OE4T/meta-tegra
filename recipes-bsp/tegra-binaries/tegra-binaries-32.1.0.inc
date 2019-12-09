LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${PV}_aarch64.tbz2;name=l4t ${SRC_URI_SECUREBOOT}"
SRC_URI_SECUREBOOT = "${NVIDIA_DEVNET_MIRROR}/secureboot_r32.1.tbz2;name=sb"
# L4T 32.1 does not support Jetson Nano/TX1 secureboot
SRC_URI_SECUREBOOT_tegra210 = ""
L4T_MD5SUM = "881fedee06a7446953a064b574227980"
L4T_SHA256SUM = "e7ecaa32928484398d2492e6e9911262b4663e720b4867404d8fe1928fe483aa"
L4T_MD5SUM_tegra210 = "15fda16d83e9a189ea5534108f57af2f"
L4T_SHA256SUM_tegra210 = "e4902a952245217227486322b3e8cef47942f87f2ea91f9e67d700314ae3ade3"
SB_MD5SUM = "8c61778838533cc20a9a248882091a46"
SB_SHA256SUM = "f1b8dedf6544c0588997430333553a5b3973a0b826cf21771d84eabce50a31d6"
SRC_URI[l4t.md5sum] = "${L4T_MD5SUM}"
SRC_URI[l4t.sha256sum] = "${L4T_SHA256SUM}"
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
COMPATIBLE_MACHINE_tegra124 = "(-)"
