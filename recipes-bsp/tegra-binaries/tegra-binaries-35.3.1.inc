LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "\
    ${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${L4T_VERSION}_aarch64.tbz2 \
    ${L4T_OVERLAY_URI_BASE}/overlay_xusb_${L4T_VERSION}.tbz2;name=xusb \
"

SRC_URI[sha256sum] = "80a55504a2ce9cdc0a328edb6fd0690615c4ffde9c2b32f4e64e0a1a3432a2e2"
SRC_URI[xusb.sha256sum] = "89ce71eb7d2b0df84346635d72c805e2b6832aa0e2b00360bec46d0aa0a6f40c"

inherit l4t_bsp dos2unix

# Override convert_crlf_to_lf task to only convert "*.cfg", "*.dts*" files
do_convert_crlf_to_lf () {
	find ${S} -type f "-(" -name "*.cfg" -o -name "*.dts*" "-)" -exec dos2unix {} \;
}

S = "${TMPDIR}/work-shared/L4T-${L4T_BSP_ARCH}-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "tegra"

do_preconfigure() {
    :
}
