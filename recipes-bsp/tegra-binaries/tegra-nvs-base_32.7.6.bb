DESCRIPTION = "NVIDIA sensor HAL daemon"
L4T_DEB_COPYRIGHT_MD5 = "893ae780971ba0060468740475e46dc3"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "c5d32bc0c84ad79f9266a622aa8c09ec6a230de19a3e35edeb01116cf9506570"
MAINSUM:tegra210 = "256b73c87f325ea94600a94f37a7e8fa1c69b8f7b299a616b3876a61738a3472"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/nvs-service ${D}${sbindir}/
}

RDEPENDS:${PN} = "bash"
INSANE_SKIP:${PN} = "ldflags"
