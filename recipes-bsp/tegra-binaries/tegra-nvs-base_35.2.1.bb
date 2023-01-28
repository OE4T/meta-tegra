DESCRIPTION = "NVIDIA sensor HAL daemon"
L4T_DEB_COPYRIGHT_MD5 = "0a282b202b51eab090698fcda604cb76"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"

require tegra-debian-libraries-common.inc

MAINSUM = "36b53071e126e9ddf6017e745f47165727ff1c7eb1eedea53a0aec41363dc044"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/nvs-service ${D}${sbindir}/
}

RDEPENDS:${PN} = "bash"
INSANE_SKIP:${PN} = "ldflags"
