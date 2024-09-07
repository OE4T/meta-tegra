DESCRIPTION = "NVIDIA Power Hinting Service"
L4T_DEB_COPYRIGHT_MD5 = "8dc9729e1dc38aac4adb4bd6f6e3b370"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init"

MAINSUM = "7ea038eeb71cad7cdd63a21382e9d9e756792d7800e412d0a1ab0d544a9b7856"
INITSUM = "d7d3ecf6f394857b1b8ea2e33b3791f6bcfc083811f7756e7a1afade95c76b3b"
SRC_URI[init.sha256sum] = "${INITSUM}"

do_install() {
    install -d ${D}${sbindir} ${D}${sysconfdir} ${D}${libdir}
    install -m 0755 ${S}/usr/sbin/nvphs* ${S}/usr/sbin/nvsetprop ${D}${sbindir}/
    install -m 0644 ${S}/etc/nvphsd_common.conf ${D}${sysconfdir}/
    install -m 0644 ${S}/etc/nvphsd.conf.t194 ${D}${sysconfdir}/
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/libnvphsd* ${D}${libdir}/
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/libnvgov* ${D}${libdir}/
}

PACKAGES = "${PN}"
FILES:${PN} = "${sbindir} ${sysconfdir} ${libdir}"
RDEPENDS:${PN} = "bash"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
