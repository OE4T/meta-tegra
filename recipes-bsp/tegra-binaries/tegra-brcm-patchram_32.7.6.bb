DESCRIPTION = "Broadcom patchram utility for Tegra modules with on-board Bluetooth interfaces"
L4T_DEB_COPYRIGHT_MD5 = "9d0203dba0bd9e7dff108bbfea124e7c"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://usr/share/doc/nvidia-tegra/LICENSE.brcm_patchram_plus.gz;md5=56c49a020a7573ba8a805f8df531b806"

MAINSUM = "941c3b8dd16be17d45823bccf328c35f795ee56e01c3d9560c6e90aa0b810a0d"
MAINSUM:tegra210 = "0404d7ddea8eda64c492f2c4e329c1f92a346d927eaa6926ec47233e70069eff"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/brcm_patchram_plus ${D}${sbindir}
}

PACKAGES = "${PN}"
RDEPENDS:${PN} = "tegra-firmware-brcm"
