DESCRIPTION = "NVIDIA Power Hinting Service"
L4T_DEB_COPYRIGHT_MD5 = "8dc9729e1dc38aac4adb4bd6f6e3b370"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init"

MAINSUM = "3ceb07740975a191fa0b69e02d23f29e1c7cd990cbb3e21da12e8f0cec09b52f"
INITSUM = "268a9d974765ccf83dec9a0a94e2870c5c789334701bae3a3321c83795c6b431"
SRC_URI[init.sha256sum] = "${INITSUM}"

do_install() {
    install -d ${D}${sbindir} ${D}${sysconfdir} ${D}${libdir}
    install -m 0755 ${S}/usr/sbin/nvphs* ${S}/usr/sbin/nvsetprop ${D}${sbindir}/
    install -m 0644 ${S}/etc/nvphsd_common.conf ${D}${sysconfdir}/
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/nvidia/libnvphsd* ${D}${libdir}/
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/nvidia/libnvgov* ${D}${libdir}/
}

PACKAGES = "${PN}"
FILES:${PN} = "${sbindir} ${sysconfdir} ${libdir}"
RDEPENDS:${PN} = "bash"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
