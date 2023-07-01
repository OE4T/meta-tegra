DESCRIPTION = "NVIDIA sensor HAL daemon"
L4T_DEB_COPYRIGHT_MD5 = "0a282b202b51eab090698fcda604cb76"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "1745df356e76e50c28ce490acb8e8caca756469c3c4fda2e59ea13c110661f33"
MAINSUM:tegra210 = "a2896e4298bb045396fe656df1f7e14002337a21b5652d8c835eebceff0dfd44"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/nvs-service ${D}${sbindir}/
}

RDEPENDS:${PN} = "bash"
INSANE_SKIP:${PN} = "ldflags"
