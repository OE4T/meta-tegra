DESCRIPTION = "Minimal initramfs init script"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://init-boot.sh"

S = "${WORKDIR}"

do_install() {
    install -m 0755 ${WORKDIR}/init-boot.sh ${D}/init
    install -d ${D}/proc ${D}/sys ${D}/dev ${D}/tmp ${D}/mnt ${D}/run ${D}/usr
    mknod -m 622 ${D}/dev/console c 5 1
}

RDEPENDS_${PN} += "${VIRTUAL-RUNTIME_base-utils}"
FILES_${PN} = "/"
