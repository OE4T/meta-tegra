DESCRIPTION = "NVIDIA sensor HAL daemon"
L4T_DEB_COPYRIGHT_MD5 = "27caeb86bc9bb9f3419a7c87e9a301f6"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-init"

require tegra-debian-libraries-common.inc

MAINSUM = "732cb1886dc6cd0d8476e4554fcd9f46bf17b5ac26142ab00c8e4afb852abff5"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/nvs-service ${D}${sbindir}/
}

RDEPENDS:${PN} = "bash"
INSANE_SKIP:${PN} = "ldflags"
