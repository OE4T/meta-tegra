DESCRIPTION = "NVIDIA Power Hinting Service"
L4T_DEB_COPYRIGHT_MD5 = "fab0c15b4bbf7f8d5ac2bd6673d4211c"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-init_${PV}_arm64.deb;subdir=${BP};name=init"

MAINSUM = "b69e15c45a24066eaebc112f39c6876ad0ab72c22e73df7ede37e40c33b1e0d8"
MAINSUM:tegra210 = "30cfc4d9c38731165a36c85bee0a46643a7eae798903781e2cc47d02606aa79a"
INITSUM = "aabb2c405d303a166e31946a51ac39eaf96293a86b40e33a02d9c067ebc3508e"
INITSUM:tegra210 = "af336fe5295030664eef19497fe42279e6f41f2a34b989b0a54266967e4b2f36"
SRC_URI[init.sha256sum] = "${INITSUM}"

NVPHSD_MACHINE_CONF = "nvphsd.conf"
NVPHSD_MACHINE_CONF:tegra186 = "nvphsd.conf.t186"
NVPHSD_MACHINE_CONF:tegra194 = "nvphsd.conf.t194"

do_install() {
    install -d ${D}${sbindir} ${D}${sysconfdir} ${D}${libdir}
    install -m 0755 ${S}/usr/sbin/nvphs* ${S}/usr/sbin/nvsetprop ${D}${sbindir}/
    install -m 0644 ${S}/etc/nvphsd_common.conf ${D}${sysconfdir}/
    install -m 0644 ${S}/etc/${NVPHSD_MACHINE_CONF} ${D}${sysconfdir}/nvphsd.conf
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/libnvphsd* ${D}${libdir}/
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/libnvgov* ${D}${libdir}/
}

PACKAGES = "${PN}"
FILES:${PN} = "${sbindir} ${sysconfdir} ${libdir}"
RDEPENDS:${PN} = "bash"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
