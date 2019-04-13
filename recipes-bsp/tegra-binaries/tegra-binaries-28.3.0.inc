LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2174e6214d83da8e19ab6510ffa71336 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

INCNAME = "tegra186"
INCNAME_tegra210 = "tegra210"
require tegra-binaries-${INCNAME}-28.3.0.inc

SRC_URI += "\
    file://nvcamera-daemon.init \
    file://nvcamera-daemon.service \
    file://argus-daemon.init \
    file://argus-daemon.service \
    file://nvstartup.init \
    file://nvstartup.service \
    file://tegra186-flash-helper.sh \
"

SRC_URI_append_tegra186 = " \
    file://nvpmodel.init \
    file://nvpmodel.service \
"

S = "${TMPDIR}/work-shared/L4T-${SOC_FAMILY}-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

do_preconfigure() {
    cp ${WORKDIR}/*-daemon.* ${WORKDIR}/nvstartup.* ${S}/
    cp ${WORKDIR}/tegra186-flash-helper.sh ${S}/
}

do_preconfigure_append_tegra186() {
    cp ${WORKDIR}/nvpmodel.* ${S}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "(tegra186|tegra210)"
