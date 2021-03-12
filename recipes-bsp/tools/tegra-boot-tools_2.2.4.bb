DESCRIPTION = "Boot-related tools for Tegra platforms"
HOMEPAGE = "https://github.com/OE4T/tegra-boot-tools"
LICENSE = "MIT & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a9217de7f233011b127382da9a035a1"

DEPENDS = "zlib systemd tegra-eeprom-tool"

SRC_URI = "https://github.com/OE4T/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
<<<<<<< HEAD:recipes-bsp/tools/tegra-boot-tools_2.2.2.bb
SRC_URI[sha256sum] = "0aa2c985015aed6dfa2051d6fd75051562be7e906698d4dc418703ac4d544243"
=======
SRC_URI[sha256sum] = "c77a1d5e1d2b77022581e32e2a79cd1fe2d158e0ca4ade332e482e92e0b544f7"
>>>>>>> c177774... tegra-boot-tools: update to v2.2.4:recipes-bsp/tools/tegra-boot-tools_2.2.4.bb

OTABOOTDEV ??= "/dev/mmcblk0boot0"
OTAGPTDEV ??= "/dev/mmcblk0boot1"

EXTRA_OECONF = "--with-systemdsystemunitdir=${systemd_system_unitdir} \
                --with-machine-name=${MACHINE} \
                --with-bootdev=${OTABOOTDEV} --with-gptdev=${OTAGPTDEV}"

inherit autotools pkgconfig systemd features_check

REQUIRED_DISTRO_FEATURES = "systemd"

SYSTEMD_PACKAGES = "${PN}-earlyboot ${PN}-lateboot"

PACKAGES =+ "libtegra-boot-tools ${PN}-earlyboot ${PN}-lateboot ${PN}-updater ${PN}-nvbootctrl ${PN}-nv-update-engine"

SYSTEMD_SERVICE_${PN}-earlyboot = "bootcountcheck.service"
SYSTEMD_SERVICE_${PN}-lateboot = "update_bootinfo.service"

FILES_libtegra-boot-tools = "${libdir}/libtegra-boot-tools${SOLIBS} ${datadir}/tegra-boot-tools"

FILES_${PN}-earlyboot = "${sbindir}/bootcountcheck"
RDEPENDS_${PN}-earlyboot = "${PN}"

RDEPENDS_${PN}-lateboot = "${PN}"

FILES_${PN}-updater = "${bindir}/tegra-bootloader-update"
RDEPENDS_${PN}-updater = "${PN}"

FILES_${PN} += "${libdir}/tmpfiles.d"
RDEPENDS_${PN} = "tegra-bootpart-config"

FILES_${PN}-nvbootctrl = "${sbindir}/nvbootctrl"
RDEPENDS_${PN}-nvbootctrl = "${PN}"
RCONFLICTS_${PN}-nvbootctrl = "tegra-redundant-boot-nvbootctrl"
FILES_${PN}-nv-update-engine = "${sbindir}/nv_update_engine"
RDEPENDS_${PN}-nv-update-engine = "${PN}-updater ${PN}"
RCONFLICTS_${PN}-nv-update-engine = "tegra-redundant-boot-base"

PACKAGE_ARCH = "${MACHINE_ARCH}"
