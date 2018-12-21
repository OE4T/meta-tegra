LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "https://developer.download.nvidia.com/embedded/L4T/r31_Release_v1.0/BSP/Jetson_Linux_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "d1d7e1fa49c0102ee705e581a188d34c"
SRC_URI[sha256sum] = "c968e74a410592976595710eefc3b5fdb123d0d1e4baaf1a8019a1003b89f561"

SRC_URI += "\
    file://nvargus-daemon.init \
    file://nvargus-daemon.service \
    file://nvstartup.init \
    file://nvstartup.service \
    file://tegra186-flash-helper.sh \
    file://tegra194-flash-helper.sh \
    file://xavier-bup-fix.patch \
"

SRC_URI_append_tegra186 = " \
    file://nvpmodel.init \
    file://nvpmodel.service \
    file://flashvars \
"

SRC_URI_append_tegra194 = " \
    file://nvpmodel.init \
    file://nvpmodel.service \
    file://flashvars \
"

S = "${TMPDIR}/work-shared/L4T-${SOC_FAMILY}-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

do_preconfigure() {
    cp ${WORKDIR}/*-daemon.* ${WORKDIR}/nvstartup.* ${S}/
    cp ${WORKDIR}/tegra186-flash-helper.sh ${S}/
    cp ${WORKDIR}/tegra194-flash-helper.sh ${S}/
}

do_preconfigure_append_tegra186() {
    cp ${WORKDIR}/nvpmodel.* ${S}/
    cp ${WORKDIR}/flashvars ${S}/
}

do_preconfigure_append_tegra194() {
    cp ${WORKDIR}/nvpmodel.* ${S}/
    cp ${WORKDIR}/flashvars ${S}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "(tegra186|tegra194)"
