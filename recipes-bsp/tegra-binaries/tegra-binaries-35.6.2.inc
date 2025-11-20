LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://Tegra_Software_License_Agreement-Tegra-Linux.txt;md5=376d20bd5275442226fcdf54e4844ddf \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "\
    ${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${L4T_VERSION}_aarch64.tbz2 \
    https://developer.nvidia.com/downloads/embedded/L4T/overlay_mb1bct_35.x.tbz2;name=qspi \
"

SRC_URI[sha256sum] = "bbe751b411e137e6f1c22b86959c048525eba527d26fe92e0b9d3e163a1b4938"
SRC_URI[qspi.sha256sum] = "21b0c97461f29c56b837e57f9c7374b0aefe9242191ccc42e699d54aa94b26ca"

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
