LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://Tegra_Software_License_Agreement-Tegra-Linux.txt;md5=2d48d198004cfe9c3bc0df31c1b89805 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "\
    ${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${L4T_VERSION}_aarch64.tbz2 \
    https://developer.nvidia.com/downloads/embedded/L4T/overlay_mb1bct_36.x.tbz2;name=qspi \
"

SRC_URI[sha256sum] = "a6ce11c22100ab0976e419959182417c83d62f4272501bc8714f2e076e010f3b"
SRC_URI[qspi.sha256sum] = "c1b9051493c31168c58b0c3a4560d275a058c06657a580db840fe9a2600a3285"

inherit l4t_bsp dos2unix

# Override convert_crlf_to_lf task to only convert "*.dts*" files
do_convert_crlf_to_lf () {
	find ${S} -type f  -name "*.dts*" -exec dos2unix {} \;
}


S = "${L4T_BSP_SHARED_SOURCE_DIR}"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "tegra"

do_preconfigure() {
    :
}
