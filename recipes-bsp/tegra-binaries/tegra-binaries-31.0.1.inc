LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "https://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t_prerel/4.0/m39xww/beta40/JetPackL4T_40_b190/Jetson_Linux_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "6a68a562af159cb932c5841a785e0c91"
SRC_URI[sha256sum] = "496ec9b79d45f6ea0b9dceb8349432d382cd6db372f508267ffa9d519b2205ac"

SRC_URI += "\
    file://nvargus-daemon.init \
    file://nvargus-daemon.service \
    file://nvstartup.init \
    file://nvstartup.service \
    file://tegra186-flash-helper.sh \
    file://tegra194-flash-helper.sh \
"

SRC_URI_append_tegra186 = " \
    file://nvpmodel.init \
    file://nvpmodel.service \
"

SRC_URI_append_tegra194 = " \
    file://nvpmodel.init \
    file://nvpmodel.service \
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
}

do_preconfigure_append_tegra194() {
    cp ${WORKDIR}/nvpmodel.* ${S}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "(tegra186|tegra194)"
