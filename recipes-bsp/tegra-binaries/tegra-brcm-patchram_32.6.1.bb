DESCRIPTION = "Broadcom patchram utility for Tegra modules with on-board Bluetooth interfaces"
L4T_DEB_COPYRIGHT_MD5 = "8ccbe1d1f4886f348bc2690c9e743af4"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://usr/share/doc/nvidia-tegra/LICENSE.brcm_patchram_plus.gz;md5=56c49a020a7573ba8a805f8df531b806"

MAINSUM = "34254de1b1818e0d862582df9af2d1d5828ad40e340614853321be2fdf441cf2"
MAINSUM_tegra210 = "e3a4053a72a11c76d4492b76bc7237bb46585e1e9053a0fdd9ffdb07c8dd75ec"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/brcm_patchram_plus ${D}${sbindir}
}

PACKAGES = "${PN}"
RDEPENDS_${PN} = "tegra-firmware-brcm"
