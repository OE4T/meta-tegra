FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

PACKAGE_ARCH_tegra210 = "${MACHINE_ARCH}"
PACKAGE_ARCH_tegra186 = "${MACHINE_ARCH}"

RCONFDEPS = ""
RCONFDEPS_tegra210 = "tegra-configs-alsa"
RCONFDEPS_tegra186 = "tegra-configs-alsa"
RDEPENDS_${PN} += "${RCONFDEPS}"
