LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${PV}_aarch64.tbz2"
SRC_MD5SUM = "faa4fb0cf7420616daa6d1e9085be021"
SRC_SHA256SUM = "9088802c0136a53cc8ae562f742bda3b0ff2bd4ab5d9f19c3bff3cded17ffed6"
SRC_MD5SUM_tegra210 = "52513e700d6c2a1d450887033297eea0"
SRC_SHA256SUM_tegra210 = "0200deb187d45dc9398e21a601249c3e714516e948efb52bc7e4f332b9be66d9"
SRC_URI[md5sum] = "${SRC_MD5SUM}"
SRC_URI[sha256sum] = "${SRC_SHA256SUM}"

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
