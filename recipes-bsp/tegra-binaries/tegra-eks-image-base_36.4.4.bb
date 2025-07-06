require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"

TEGRA_SOCNAME_SHORT = "${@d.getVar('SOC_FAMILY')[0:1] + d.getVar('SOC_FAMILY')[-3:]}"

do_install() {
    install -D -m 0644 ${S}/bootloader/eks_${TEGRA_SOCNAME_SHORT}.img ${D}${datadir}/l4t-eks-image/eks.img
}

PACKAGES = "${PN}-dev"
FILES:${PN}-dev = "${datadir}"
RRECOMMENDS:${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
