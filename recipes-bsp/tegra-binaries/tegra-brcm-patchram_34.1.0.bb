DESCRIPTION = "Broadcom patchram utility for Tegra modules with on-board Bluetooth interfaces"
L4T_DEB_COPYRIGHT_MD5 = "8ccbe1d1f4886f348bc2690c9e743af4"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://usr/share/doc/nvidia-tegra/LICENSE.brcm_patchram_plus.gz;md5=56c49a020a7573ba8a805f8df531b806"

<<<<<<<< HEAD:recipes-bsp/tegra-binaries/tegra-brcm-patchram_32.7.2.bb
MAINSUM = "e38f724861397cc9e57d677b321f6caf0e76e397558c1ad3af115203c7b17f7f"
MAINSUM:tegra210 = "5499bdfe07abeb4d2103a7b78e3e2c64dec7c28977ebcf0b2e16d940a0d19585"
========
MAINSUM = "941a93e2f9080a86bcd4a52f06cd394d45896cf67702ded4e312e8f5f733e67d"
>>>>>>>> 390a51f3 (tegra-binaries: update for 34.1.0):recipes-bsp/tegra-binaries/tegra-brcm-patchram_34.1.0.bb

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/brcm_patchram_plus ${D}${sbindir}
}

PACKAGES = "${PN}"
RDEPENDS:${PN} = "tegra-firmware-brcm"
