DESCRIPTION = "Machine-specific variables for tegraflash"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://flashvars"

COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}"

INHIBIT_DEFAULT_DEPS = "1"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/tegraflash
    install -m 0644 ${S}/flashvars ${D}${datadir}/tegraflash/
}

FILES:${PN} = "${datadir}/tegraflash"
PACKAGE_ARCH = "${MACHINE_ARCH}"
