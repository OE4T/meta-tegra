LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=60ad17cc726658e8cf73578bea47b85f \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "http://developer2.download.nvidia.com/embedded/L4T/r28_Release_v1.0/BSP/${@d.getVar('SOC_FAMILY').title()}_Linux_R${PV}_aarch64.tbz2"

SRC_URI += "\
    file://xorg.conf \
    file://nvcamera-daemon.init \
    file://nvcamera-daemon.service \
    file://argus-daemon.init \
    file://argus-daemon.service \
"
SRC_URI_append_tegra186 = " file://tegra186-flash-helper.sh"

S = "${TMPDIR}/work-shared/L4T-${SOC_FAMILY}-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

do_preconfigure() {
    cp ${WORKDIR}/xorg.conf ${WORKDIR}/*-daemon.* ${S}
}

do_preconfigure_append_tegra186() {
    cp ${WORKDIR}/tegra186-flash-helper.sh ${S}
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "(tegra186|tegra210)"

python __anonymous () {
    soc = d.getVar('SOC_FAMILY') or ""
    if soc == "tegra186":
        d.setVarFlag("SRC_URI", "md5sum", "d8a1b64780de38d41e2d26c54b87e2ab")
        d.setVarFlag("SRC_URI", "sha256sum", "8c0634da2a47011dceb14bb4639ad8abcf89b0b67d8bd9cd58edbd251ccb321c")
    elif soc == "tegra210":
        d.setVarFlag("SRC_URI", "md5sum", "a2686c154006f8fdf232bf9f2e7d01e6")
        d.setVarFlag("SRC_URI", "sha256sum", "0c0ab92bcba1f3be984b2c4311f9326c249a758247897af5792130de26a3fe8d")
}
