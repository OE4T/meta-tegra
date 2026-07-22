LICENSE = "LicenseRef-Proprietary"
LIC_FILES_CHKSUM = "file://Tegra_Software_License_Agreement-Tegra-Linux.txt;md5=376d20bd5275442226fcdf54e4844ddf"

SRC_URI = "\
    ${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${L4T_VERSION}_aarch64.tbz2 \
"

SRC_URI[sha256sum] = "1626626cd827de0e350b8802033b9da653c69b2290accedb9e5d01f49607e099"

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
