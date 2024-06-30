DESCRIPTION = "L4T bootloader update support tools"
L4T_DEB_COPYRIGHT_MD5 = "5d7528c1fe500782f8b39e724c929984"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "ffe3f115eae01b2c0a67e56fba12d005ecd7253d5a1e15cebeebb824268788bf"
MAINSUM:tegra210 = "7f0acc45f285b3f7540812016889b7d1775a206396075bf3018a7a49cc8c8150"

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
