DESCRIPTION = "Minimal initramfs init script"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://init-boot.sh \
    file://platform-preboot.sh \
"

S = "${UNPACKDIR}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${MACHINE_ARCH}"

def boot_drive(d):
    import re
    bootdev = d.getVar('TNSPEC_BOOTDEV') or 'internal'
    if bootdev not in ['internal','external']:
        m = re.match('^(mmcblk[0-9]+)p[0-9]+', bootdev) or \
            re.match('^(nvme[0-9]+n[0-9]+)p[0-9]+', bootdev) or \
            re.match('^(sd[a-z])[0-9]+', bootdev)
        if m is not None:
            return "/dev/" + m.group(1)
    return ''

TEGRA_MINIMAL_INIT_BOOTDRIVE ??= "${@boot_drive(d)}"

do_install() {
    install -m 0755 ${UNPACKDIR}/init-boot.sh ${D}/init
    install -m 0555 -d ${D}/proc ${D}/sys
    install -m 0755 -d ${D}/dev ${D}/mnt ${D}/run ${D}/usr
    install -m 1777 -d ${D}/tmp
    mknod -m 622 ${D}/dev/console c 5 1
    install -d ${D}${sysconfdir}
    install -m 0644 ${UNPACKDIR}/platform-preboot.sh ${D}${sysconfdir}/platform-preboot
    sed -i -e "s#@@TNSPEC_BOOTDEV@@#${TNSPEC_BOOTDEV}#g" \
	-e "s#@@BOOTDRIVE@@#${TEGRA_MINIMAL_INIT_BOOTDRIVE}#g" \
	${D}${sysconfdir}/platform-preboot
}

RDEPENDS:${PN} = "util-linux-blkid kmod"
FILES:${PN} = "/"
