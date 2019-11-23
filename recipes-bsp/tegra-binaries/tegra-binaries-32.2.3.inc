LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${PV}_aarch64.tbz2;name=l4t \
	   ${L4T_URI_BASE}/secureboot_R${PV}_aarch64.tbz2;downloadfilename=${L4T_BSP_PREFIX}_secureboot_${PV}.tbz2;name=sb"
L4T_MD5SUM = "7226aeff2e4d41800e3af956d7f0587b"
L4T_SHA256SUM = "3e56179b0270a89a745cef3b1fa80af6499947f79ced8de701a85b1c56acac3b"
L4T_MD5SUM_tegra210 = "a4a62847f60aedbbc331a1c58af1ab1e"
L4T_SHA256SUM_tegra210 = "89636f32741c388e46b7876198332457db1845dd6c52779960e0cec5511d6c02"
SRC_URI[l4t.md5sum] = "${L4T_MD5SUM}"
SRC_URI[l4t.sha256sum] = "${L4T_SHA256SUM}"
SB_MD5SUM = "a5de7dd3d77e6f708e71ca6d12fdac27"
SB_SHA256SUM = "696f2fd5cb02d9808927d96253a598e6d8c5777d06c32f25cb2714032f67d39e"
SB_MD5SUM_tegra210 = "23a89c95e467da4b36c76005bfb91379"
SB_SHA256SUM_tegra210 = "5b9b62bcbd200c994fcc8b3ebbfccae623c011922079d911786dabedfb2b1576"
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
