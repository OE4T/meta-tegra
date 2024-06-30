DESCRIPTION = "NVIDIA sensor HAL daemon"
L4T_DEB_COPYRIGHT_MD5 = "893ae780971ba0060468740475e46dc3"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "542a2efb8a6dbf8b96921701e2e81daa1f3090d18869de9da79a0b37c22f0c5a"
MAINSUM:tegra210 = "9d77762e6bc13be948e3a84a28527e4b47fe09df78c4b7d702133bf78c7efd94"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/nvs-service ${D}${sbindir}/
}

RDEPENDS:${PN} = "bash"
INSANE_SKIP:${PN} = "ldflags"
