LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2174e6214d83da8e19ab6510ffa71336 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r28_Release_v2.0/GA/BSP/${@d.getVar('SOC_FAMILY').title()}_Linux_R${PV}_aarch64.tbz2"

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

COMPATIBLE_MACHINE = "(tegra186|tegra210)"

python __anonymous () {
    soc = d.getVar('SOC_FAMILY') or ""
    if soc == "tegra186":
        d.setVarFlag("SRC_URI", "md5sum", "22bbd0002db06bb26bf5de2d17cea8eb")
        d.setVarFlag("SRC_URI", "sha256sum", "62f57b4c03fedde1fe0a1635bd0828b334308eca71bd21f25ae5757f48fb3a76")
    elif soc == "tegra210":
        d.setVarFlag("SRC_URI", "md5sum", "0b78d620654add9f9147f99b1bb317de")
        d.setVarFlag("SRC_URI", "sha256sum", "d6a9be43401bbfd13201b3b96708ba7c3b0be0d40fa52236b7b0a5cd7d30aafd")
}

