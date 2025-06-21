DESCRIPTION = "Utility to force reboot into recovery mode"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://reboot-recovery.c"

COMPATIBLE_MACHINE = "(tegra)"

S = "${UNPACKDIR}"
B = "${WORKDIR}/build"

do_compile() {
    $CC $CFLAGS $LDFLAGS -o ${B}/reboot-recovery ${S}/reboot-recovery.c
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/reboot-recovery ${D}${bindir}/
}

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
