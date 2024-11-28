DESCRIPTION = "NVIDIA Power Hinting Service"
L4T_DEB_COPYRIGHT_MD5 = "ef1b882a6a8ed90f38e4b0288fcd1525"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-init_${PV}_arm64.deb;subdir=${BP};name=init"

MAINSUM = "7b81a016d6a0f283553b01516fa562c8c99c95bb87d88b680dcbc113e8bfa938"
MAINSUM:tegra210 = "61ada8308bd38fe6fe1793b658c83c3ef3dcc057a0d9e080dc0336e61be683c2"
INITSUM = "c5d32bc0c84ad79f9266a622aa8c09ec6a230de19a3e35edeb01116cf9506570"
INITSUM:tegra210 = "256b73c87f325ea94600a94f37a7e8fa1c69b8f7b299a616b3876a61738a3472"
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
