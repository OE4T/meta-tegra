DESCRIPTION = "L4T bootloader update support tools"
L4T_DEB_COPYRIGHT_MD5 = "fe65a18c0cbf70d19e3d6202a8c825b6"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "f78249b26d93eb1e9851aa4558fe144125139ee165d4dfd9726cd7fe71d0e228"
MAINSUM:tegra210 = "a993495e473e9050f1bacadc5e7713bc652f28257a2acf80389e1d04206a48a7"

SRC_URI:append:tegra210 = " file://Convert-l4t_payload_updater_t210-to-Python3-R32.7.1.patch"

do_install() {
	install -d ${D}${sbindir}
	install -m 0755 ${S}/usr/sbin/nvbootctrl ${D}${sbindir}
	install -m 0755 ${S}/usr/sbin/nv_bootloader_payload_updater ${D}${sbindir}
	install -m 0755 ${S}/usr/sbin/nv_update_engine ${D}${sbindir}
	install -d ${D}/opt/ota_package
}

do_install:tegra210() {
	install -d ${D}${sbindir}
	install -m 0755 ${S}/usr/sbin/l4t_payload_updater_t210 ${D}${sbindir}
	install -d ${D}/opt/ota_package
}

PACKAGES = "tegra-redundant-boot-nvbootctrl ${PN} ${PN}-dev"
FILES:tegra-redundant-boot-nvbootctrl = "${sbindir}/nvbootctrl"
FILES:${PN} += "/opt/ota_package"
RDEPENDS:${PN} = "tegra-redundant-boot-nvbootctrl setup-nv-boot-control-service tegra-configs-bootloader"
RDEPENDS:${PN}:tegra210 = "setup-nv-boot-control-service python3-core"
INSANE_SKIP:${PN} = "ldflags"
RDEPENDS:tegra-redundant-boot-nvbootctrl = "setup-nv-boot-control"
RDEPENDS:tegra-redundant-boot-nvbootctrl:tegra210 = ""
ALLOW_EMPTY:tegra-redundant-boot-nvbootctrl:tegra210 = "1"
INSANE_SKIP:tegra-redundant-boot-nvbootctrl = "ldflags"
