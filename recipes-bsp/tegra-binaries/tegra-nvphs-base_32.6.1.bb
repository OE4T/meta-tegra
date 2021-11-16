DESCRIPTION = "NVIDIA Power Hinting Service"
L4T_DEB_COPYRIGHT_MD5 = "fab0c15b4bbf7f8d5ac2bd6673d4211c"
DEPENDS = "tegra-libraries-core"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-core"

require tegra-debian-libraries-common.inc

# We're using the current version for PV, but the base -core
# package is still the original, so set SRC_SOC_DEBS accordingly
SRC_SOC_DEBS = "\
    ${L4T_DEB_TRANSLATED_BPN}_${L4T_VERSION}-${L4T_BSP_DEB_ORIG_VERSION}_arm64.deb;subdir=${BP};name=main \
    nvidia-l4t-init_${PV}_arm64.deb;subdir=${BP};name=init \
"

MAINSUM = "2c87814d6d06344a81baf7709377c5d2b1cf22b999fa136ca20531cf58f315c1"
MAINSUM:tegra210 = "d2d8941982e1b344868b0b2d2a93f6ecf886493722c2620a5864262f5db73363"
INITSUM = "97ecfa2e2a1bd9dd7537c1e272ce3b44dc56b859f1b5b0a8131549872669a93b"
INITSUM:tegra210 = "bee82489940152358039b5b7123815620447415121f03843f711a8f4158b23b4"
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
