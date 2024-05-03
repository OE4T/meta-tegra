DESCRIPTION = "L4T bootloader update support tools"
L4T_DEB_COPYRIGHT_MD5 = "1c55a704d80b8d8275c122433b1661bf"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"

require tegra-debian-libraries-common.inc

DEPENDS = "tegra-libraries-core"

MAINSUM = "438aef8ad1b9948c15a6268b914bce719cff9d391809da41a6ab2b59c26d82c0"

do_install() {
	install -d ${D}${sbindir}
	install -m 0755 ${S}/usr/sbin/nvbootctrl ${D}${sbindir}
	install -m 0755 ${S}/usr/sbin/nv_bootloader_payload_updater ${D}${sbindir}
	install -m 0755 ${S}/usr/sbin/nv_update_engine ${D}${sbindir}
	install -d ${D}/opt/ota_package
}

PACKAGES = "tegra-redundant-boot-update-engine ${PN} ${PN}-dev"
FILES:tegra-redundant-boot-update-engine = "${sbindir}/nv_update_engine ${sbindir}/nv_bootloader_payload_updater /opt/ota_package"
RDEPENDS:${PN} = "setup-nv-boot-control-service tegra-configs-bootloader util-linux-lsblk"
INSANE_SKIP:${PN} = "ldflags"
RDEPENDS:tegra-redundant-boot-update-engine = "${PN} util-linux-lsblk"
INSANE_SKIP:tegra-redundant-boot-update-engine = "ldflags"
