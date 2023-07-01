DESCRIPTION = "NVIDIA Power Hinting Service"
L4T_DEB_COPYRIGHT_MD5 = "fab0c15b4bbf7f8d5ac2bd6673d4211c"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-init_${PV}_arm64.deb;subdir=${BP};name=init"

MAINSUM = "8659b23489309907f3ac1f19b270306be37f6a4cc5ddac8cb35e96f5c3ae13bf"
MAINSUM:tegra210 = "7d816cf7bf7831f3ccde19e2bf6f9d731211b9146765271632fb2ed550522032"
INITSUM = "1745df356e76e50c28ce490acb8e8caca756469c3c4fda2e59ea13c110661f33"
INITSUM:tegra210 = "a2896e4298bb045396fe656df1f7e14002337a21b5652d8c835eebceff0dfd44"
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
