DESCRIPTION = "NVIDIA sensor HAL daemon"
L4T_DEB_COPYRIGHT_MD5 = "1d9a81a4a5115c509c05a2219bd840b9"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"

require tegra-debian-libraries-common.inc

MAINSUM = "5db517ec85098d94969b9f0a60091dfb40d708176a908d9cd5400960ddd5a399"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/nvs-service ${D}${sbindir}/
}

RDEPENDS:${PN} = "bash"
INSANE_SKIP:${PN} = "ldflags"
