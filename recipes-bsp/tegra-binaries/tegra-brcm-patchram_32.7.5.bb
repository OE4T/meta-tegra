DESCRIPTION = "Broadcom patchram utility for Tegra modules with on-board Bluetooth interfaces"
L4T_DEB_COPYRIGHT_MD5 = "9d0203dba0bd9e7dff108bbfea124e7c"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://usr/share/doc/nvidia-tegra/LICENSE.brcm_patchram_plus.gz;md5=56c49a020a7573ba8a805f8df531b806"

MAINSUM = "dc9401be553edf3881f4696cecc92b02c3088409da28e5d4717ca520afa44bad"
MAINSUM:tegra210 = "485be42e49c838f31e32eed196a2eaef6a7133512e88c37afb4e3b4b81316934"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/brcm_patchram_plus ${D}${sbindir}
}

PACKAGES = "${PN}"
RDEPENDS:${PN} = "tegra-firmware-brcm"
