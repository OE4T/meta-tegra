DESCRIPTION = "NVIDIA Power Hinting Service"
L4T_DEB_COPYRIGHT_MD5 = "ef1b882a6a8ed90f38e4b0288fcd1525"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-init_${PV}_arm64.deb;subdir=${BP};name=init"

MAINSUM = "0afb73f595113bf02d2f66924dd351bb665941a569507fd730c66167a4823d94"
MAINSUM:tegra210 = "c2550014e670366c339662203cbc0c5d2afd92b5a6557923844a42526e3aa6ac"
INITSUM = "542a2efb8a6dbf8b96921701e2e81daa1f3090d18869de9da79a0b37c22f0c5a"
INITSUM:tegra210 = "9d77762e6bc13be948e3a84a28527e4b47fe09df78c4b7d702133bf78c7efd94"
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
