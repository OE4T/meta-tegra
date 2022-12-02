DESCRIPTION = "NVIDIA sensor HAL daemon"
L4T_DEB_COPYRIGHT_MD5 = "0a282b202b51eab090698fcda604cb76"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "aabb2c405d303a166e31946a51ac39eaf96293a86b40e33a02d9c067ebc3508e"
MAINSUM:tegra210 = "af336fe5295030664eef19497fe42279e6f41f2a34b989b0a54266967e4b2f36"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/nvs-service ${D}${sbindir}/
}

RDEPENDS:${PN} = "bash"
INSANE_SKIP:${PN} = "ldflags"
