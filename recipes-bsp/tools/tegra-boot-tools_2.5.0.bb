DESCRIPTION = "Boot-related tools for Tegra platforms"
HOMEPAGE = "https://github.com/OE4T/tegra-boot-tools"
LICENSE = "MIT & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a9217de7f233011b127382da9a035a1"

DEPENDS = "zlib util-linux-libuuid systemd tegra-eeprom-tool"

SRC_URI = "https://github.com/OE4T/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "722ab8bbf2ab55c19239e8b358b4473e8b44d6be8d77bafdea00d66f710f2acd"

OTABOOTDEV ??= "/dev/mmcblk0boot0"
OTAGPTDEV ??= "/dev/mmcblk0boot1"

EXTRA_OECONF = "--with-systemdsystemunitdir=${systemd_system_unitdir} \
                --with-machine-name=${MACHINE} \
                --with-bootdev=${OTABOOTDEV} --with-gptdev=${OTAGPTDEV}"

inherit autotools pkgconfig systemd features_check

REQUIRED_DISTRO_FEATURES = "systemd"

SYSTEMD_PACKAGES = "${PN}-earlyboot ${PN}-lateboot"

PACKAGES =+ "libtegra-boot-tools ${PN}-earlyboot ${PN}-lateboot ${PN}-updater ${PN}-nvbootctrl ${PN}-nv-update-engine"

SYSTEMD_SERVICE:${PN}-earlyboot = "bootcountcheck.service"
SYSTEMD_SERVICE:${PN}-lateboot = "update_bootinfo.service"

FILES:libtegra-boot-tools = "${libdir}/libtegra-boot-tools${SOLIBS} ${datadir}/tegra-boot-tools"

FILES:${PN}-earlyboot = "${sbindir}/bootcountcheck"
RDEPENDS:${PN}-earlyboot = "${PN}"

RDEPENDS:${PN}-lateboot = "${PN}"

FILES:${PN}-updater = "${bindir}/tegra-bootloader-update"
RDEPENDS:${PN}-updater = "${PN} tegra-bootpart-config"

FILES:${PN} += "${libdir}/tmpfiles.d"

FILES:${PN}-nvbootctrl = "${sbindir}/nvbootctrl"
RDEPENDS:${PN}-nvbootctrl = "${PN}"
RCONFLICTS:${PN}-nvbootctrl = "tegra-redundant-boot-nvbootctrl"
FILES:${PN}-nv-update-engine = "${sbindir}/nv_update_engine"
RDEPENDS:${PN}-nv-update-engine = "${PN}-updater ${PN}"
RCONFLICTS:${PN}-nv-update-engine = "tegra-redundant-boot-base"

PACKAGE_ARCH = "${MACHINE_ARCH}"
