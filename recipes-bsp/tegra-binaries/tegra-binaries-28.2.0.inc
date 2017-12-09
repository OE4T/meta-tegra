LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=60ad17cc726658e8cf73578bea47b85f \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "http://developer2.download.nvidia.com/embedded/L4T/r28_Release_v2.0/BSP/${@d.getVar('SOC_FAMILY').title()}_Linux_R${PV}_aarch64.tbz2"

SRC_URI += "\
    file://nvcamera-daemon.init \
    file://nvcamera-daemon.service \
    file://argus-daemon.init \
    file://argus-daemon.service \
    file://nvstartup.init \
    file://nvstartup.service \
"
SRC_URI_append_tegra186 = " \
    file://tegra186-flash-helper.sh \
    file://nvpmodel.init \
    file://nvpmodel.service \
"

S = "${TMPDIR}/work-shared/L4T-${SOC_FAMILY}-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

do_preconfigure() {
    cp ${WORKDIR}/*-daemon.* ${WORKDIR}/nvstartup.* ${S}/
}

do_preconfigure_append_tegra186() {
    cp ${WORKDIR}/tegra186-flash-helper.sh ${S}/
    cp ${WORKDIR}/nvpmodel.* ${S}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "(tegra186)"

python __anonymous () {
    soc = d.getVar('SOC_FAMILY') or ""
    if soc == "tegra186":
        d.setVarFlag("SRC_URI", "md5sum", "4e1f5502d700873a8675fef2b15e98ca")
        d.setVarFlag("SRC_URI", "sha256sum", "a303f0c57b009284fdfff7f0b4ea0963127c1d728c357981897afeb934e018f8")
    elif soc == "tegra210":
        d.setVarFlag("SRC_URI", "md5sum", "a2686c154006f8fdf232bf9f2e7d01e6")
        d.setVarFlag("SRC_URI", "sha256sum", "0c0ab92bcba1f3be984b2c4311f9326c249a758247897af5792130de26a3fe8d")
}

PR = "rc0"
