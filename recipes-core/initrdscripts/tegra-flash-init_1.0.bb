DESCRIPTION = "Minimal initramfs init script for initrd flashing"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://init-flash.sh \
    file://init-extra-pre-wipe.sh \
    file://init-extra.sh \
    file://program-boot-device.sh \
    file://initrd-flash.scheme.in \
"

S = "${UNPACKDIR}"

COMPATIBLE_MACHINE = "(tegra)"

MTD_DEV ?= "${@d.getVar('OTABOOTDEV').replace('mtdblock','mtd')}"

do_configure() {
    sed -e's,@MTD_DEV@,${MTD_DEV},g' \
        ${UNPACKDIR}/program-boot-device.sh > ${UNPACKDIR}/program-boot-device
}

do_install() {
    install -m 0755 ${UNPACKDIR}/init-flash.sh ${D}/init
    install -m 0755 ${UNPACKDIR}/init-extra-pre-wipe.sh ${D}/init-extra-pre-wipe
    install -m 0755 ${UNPACKDIR}/init-extra.sh ${D}/init-extra
    install -m 0755 -d ${D}/init-extra-pre-wipe.d
    install -m 0755 -d ${D}/init-extra.d
    install -m 0555 -d ${D}/proc ${D}/sys
    install -m 0755 -d ${D}/dev ${D}/mnt ${D}/run ${D}/usr
    install -m 1777 -d ${D}/tmp
    mknod -m 622 ${D}/dev/console c 5 1
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/program-boot-device ${D}${bindir}/program-boot-device
    install -d ${D}${sysconfdir}/initrd-flash
    install -m 0644 ${UNPACKDIR}/initrd-flash.scheme.in ${D}${sysconfdir}/initrd-flash/
}

FILES:${PN} = "/"
RDEPENDS:${PN} = "util-linux-blkdiscard tegra-flash-reboot mtd-utils e2fsprogs-mke2fs libusbgx-tegra-initrd-flash watchdog-keepalive gptfdisk tegra-firmware kmod"
RRECOMMENDS:${PN} = "kernel-module-loop \
                     kernel-module-libcomposite \
                     kernel-module-usb-f-mass-storage \
"
PACKAGE_ARCH = "${MACHINE_ARCH}"
