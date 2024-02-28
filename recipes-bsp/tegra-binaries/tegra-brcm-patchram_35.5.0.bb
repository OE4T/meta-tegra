DESCRIPTION = "Broadcom patchram utility for Tegra modules with on-board Bluetooth interfaces"
L4T_DEB_COPYRIGHT_MD5 = "8ccbe1d1f4886f348bc2690c9e743af4"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"

require tegra-debian-libraries-common.inc

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://usr/share/doc/nvidia-tegra/LICENSE.brcm_patchram_plus.gz;md5=56c49a020a7573ba8a805f8df531b806"

MAINSUM = "917eabedf603d94f0d840673d300db92eaacb82a8a23a243030665ede5a8f367"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/brcm_patchram_plus ${D}${sbindir}
}

PACKAGES = "${PN}"
RDEPENDS:${PN} = "tegra-firmware-brcm"
