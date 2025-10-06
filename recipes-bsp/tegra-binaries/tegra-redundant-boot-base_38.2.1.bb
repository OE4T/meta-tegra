DESCRIPTION = "L4T bootloader update support tools"
L4T_DEB_COPYRIGHT_MD5 = "396b81964d013b4c92daf8bf82b93494"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-bootloader-utils"

require tegra-debian-libraries-common.inc

MAINSUM = "ee5c9964c516f7f8bca269386555eda8193dcce9d747dd39951127131d802c10"

do_install() {
	install -D -m 0755 -t ${D}${sbindir} ${S}/usr/sbin/nvbootctrl
}

PACKAGES = "tegra-redundant-boot-update-engine ${PN} ${PN}-dev"
FILES:tegra-redundant-boot-update-engine = "${sbindir}/nv_update_engine /opt/ota_package"
RDEPENDS:${PN} = "setup-nv-boot-control-service tegra-configs-bootloader util-linux-lsblk"
INSANE_SKIP:${PN} = "ldflags"
RDEPENDS:tegra-redundant-boot-update-engine = "${PN} util-linux-lsblk"
INSANE_SKIP:tegra-redundant-boot-update-engine = "ldflags"
ALLOW_EMPTY:${PN} = "1"
