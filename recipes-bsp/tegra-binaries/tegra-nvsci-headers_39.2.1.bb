SUMMARY = "NvSci header files (build-time only)"
HOMEPAGE = "https://developer.nvidia.com/embedded/jetpack"
LICENSE = "LicenseRef-Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/jetson_sipl_api/Tegra_Software_License_Agreement-Tegra-Linux.txt;md5=376d20bd5275442226fcdf54e4844ddf"

COMPATIBLE_MACHINE = "(tegra)"

inherit l4t_bsp

SRC_URI = "${L4T_URI_BASE}/Jetson_SIPL_API_R${L4T_VERSION}_aarch64.tbz2"
SRC_URI[sha256sum] = "c08ea0e4e63e8b50220bafcf723d9702f5a15dc9e93dc0bd44485df6c0d7c3ce"

S = "${UNPACKDIR}"

NVSCI_HEADER_SRC = "${S}/usr/src/jetson_sipl_api/sipl/include/nvsci"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${includedir}
    install -m 0644 ${NVSCI_HEADER_SRC}/*.h ${D}${includedir}/
}

FILES:${PN} = "${includedir}/"
