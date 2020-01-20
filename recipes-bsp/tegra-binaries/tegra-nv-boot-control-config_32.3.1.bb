require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"

OTABOOTDEV ??= "/dev/mmcblk0boot0"
OTAGPTDEV ??= "/dev/mmcblk0boot1"

do_compile() {
	:
}

do_install() {
	install -d ${D}${sysconfdir}
	sed -e 's,^TNSPEC.*$,TNSPEC @TNSPEC@,' \
	    -e '/TEGRA_CHIPID/d' -e '/TEGRA_OTA_BOOT_DEVICE/d' -e '/TEGRA_OTA_GPT_DEVICE/d' \
	    -e '$ a TEGRA_CHIPID ${NVIDIA_CHIP}' \
	    -e '$ a TEGRA_OTA_BOOT_DEVICE ${OTABOOTDEV}' \
	    -e '$ a TEGRA_OTA_GPT_DEVICE ${OTAGPTDEV}' \
	    ${S}/bootloader/nv_boot_control.conf >${D}${sysconfdir}/nv_boot_control.template
	chmod 0644 ${D}${sysconfdir}/nv_boot_control.template
        install -d ${D}${localstatedir}/lib/nvbootctrl
        ln -sf ${localstatedir}/lib/nvbootctrl/nv_boot_control.conf ${D}${sysconfdir}/
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
