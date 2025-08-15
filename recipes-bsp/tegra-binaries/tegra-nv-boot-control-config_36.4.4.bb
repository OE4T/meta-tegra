require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"

OTABOOTDEV ??= "/dev/mmcblk0boot0"
OTAGPTDEV ??= "/dev/mmcblk0boot1"

do_compile() {
    cat > ${B}/nv_boot_control.template <<EOF
TNSPEC @TNSPEC@
COMPATIBLE_SPEC @COMPATIBLE_SPEC@
TEGRA_BOOT_STORAGE @BOOT_STORAGE@
TEGRA_CHIPID ${NVIDIA_CHIP}
TEGRA_OTA_BOOT_DEVICE ${OTABOOTDEV}
TEGRA_OTA_GPT_DEVICE ${OTAGPTDEV}
EOF

}

do_install() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${B}/nv_boot_control.template ${D}${sysconfdir}/
    ln -sf /run/nv_boot_control/nv_boot_control.conf ${D}${sysconfdir}/
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

RDEPENDS:${PN} = "util-linux-findmnt util-linux-lsblk"
