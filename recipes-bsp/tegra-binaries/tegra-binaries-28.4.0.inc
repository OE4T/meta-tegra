LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2174e6214d83da8e19ab6510ffa71336 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

#SRC_URI = "https://developer.nvidia.com/embedded/L4T/r28_Release_v4.0/t186ref_release_aarch64/${@d.getVar('SOC_FAMILY').title()}_Linux_R${PV}_aarch64.tbz2"
SRC_URI = "http://172.17.0.1/${@d.getVar('SOC_FAMILY').title()}_Linux_R${PV}_aarch64.tbz2"

#"https://developer.nvidia.com/assets/embedded/secure/tools/files/jetpack-sdks/jetpack-3.3.3/JETPACK_333_b8/P3310/Tegra186_Linux_R28.4.0_aarch64.tbz2"
#https://developer.nvidia.com/embedded/L4T/r28_Release_v4.0/t186ref_release_aarch64/Tegra186_Linux_R28.4.0_aarch64.tbz2

SRC_URI[md5sum] = "6171412d91167b889fab0af3549374fc"
SRC_URI[sha256sum] = "41381c2abc32c3a91d27a66203ccdebc222be92e00f429ea73231372173a8bec"

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

COMPATIBLE_MACHINE = "(tegra186)"
