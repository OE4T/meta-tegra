DESCRIPTION = "Boot-related tools for Tegra platforms"
HOMEPAGE = "https://github.com/OE4T/tegra-boot-tools"
LICENSE = "MIT & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a9217de7f233011b127382da9a035a1"

DEPENDS = "zlib systemd tegra-eeprom-tool"

SRC_URI = "https://github.com/OE4T/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
<<<<<<< HEAD:recipes-bsp/tools/tegra-boot-tools_2.0.3.bb
SRC_URI[sha256sum] = "3c0244bc06d53965a026b61e3829f0e487010ae9270a8e9d441bee7f4a579794"
=======
SRC_URI[sha256sum] = "f69851e449b80e91df9a7ab577ae3808ee36035c3850ac37b4b860973997e525"
>>>>>>> 71230986 (tegra-boot-tools: update to 2.1.0):recipes-bsp/tools/tegra-boot-tools_2.1.0.bb

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
<<<<<<< HEAD:recipes-bsp/tools/tegra-boot-tools_2.0.3.bb

PACKAGE_ARCH = "${MACHINE_ARCH}"
=======
RDEPENDS_${PN} = "tegra-bootpart-config"

FILES_${PN}-nvbootctrl = "${sbindir}/nvbootctrl"
RDEPENDS_${PN}-nvbootctrl = "${PN}"
RPROVIDES_${PN}-nvbootctrl = "tegra-redundant-boot-nvbootctrl"
RREPLACES_${PN}-nvbootctrl = "tegra-redundant-boot-nvbootctrl"
RCONFLICTS_${PN}-nvbootctrl = "tegra-redundant-boot-nvbootctrl"
FILES_${PN}-nv-update-engine = "${sbindir}/nv_update_engine"
RDEPENDS_${PN}-nv-update-engine = "${PN}-updater ${PN}"
RPROVIDES_${PN}-nv-update-engine = "tegra-redundant-boot-base"
RREPLACES_${PN}-nv-update-engine = "tegra-redundant-boot-base"
RCONFLICTS_${PN}-nv-update-engine = "tegra-redundant-boot-base"
>>>>>>> 71230986 (tegra-boot-tools: update to 2.1.0):recipes-bsp/tools/tegra-boot-tools_2.1.0.bb
