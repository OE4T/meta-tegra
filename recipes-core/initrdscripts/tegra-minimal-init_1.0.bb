DESCRIPTION = "Minimal initramfs init script"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://init-boot.sh \
    file://platform-preboot.sh \
"

COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}"

do_install() {
    install -m 0755 ${WORKDIR}/init-boot.sh ${D}/init
    install -m 0555 -d ${D}/proc ${D}/sys
    install -m 0755 -d ${D}/dev ${D}/mnt ${D}/run ${D}/usr
    install -m 1777 -d ${D}/tmp
    mknod -m 622 ${D}/dev/console c 5 1
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/platform-preboot.sh ${D}${sysconfdir}/platform-preboot
}

RDEPENDS:${PN} = "util-linux-blkid"
FILES:${PN} = "/"
