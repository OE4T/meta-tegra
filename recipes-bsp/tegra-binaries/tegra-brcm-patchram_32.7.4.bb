DESCRIPTION = "Broadcom patchram utility for Tegra modules with on-board Bluetooth interfaces"
L4T_DEB_COPYRIGHT_MD5 = "8ccbe1d1f4886f348bc2690c9e743af4"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://usr/share/doc/nvidia-tegra/LICENSE.brcm_patchram_plus.gz;md5=56c49a020a7573ba8a805f8df531b806"

MAINSUM = "3cd3cf6ae0d86bed0cd6357facc9dd5b303463107861bf16c2a7aa34e03f84e3"
MAINSUM:tegra210 = "349cb2b2b8197107c4ae07667e0410f026db3b9bacc04b715f890e1103968226"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/brcm_patchram_plus ${D}${sbindir}
}

PACKAGES = "${PN}"
RDEPENDS:${PN} = "tegra-firmware-brcm"
