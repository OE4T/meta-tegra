DESCRIPTION = "Encrypted keyblob image"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"

DEPENDS = "tegra-eks-image-base"

do_compile() {
    cp ${STAGING_DATADIR}/l4t-eks-image/eks.img ${B}/eks.img
}

do_install() {
    install -D -m 0644 ${B}/eks.img -t ${D}${datadir}/tegraflash
}

PACKAGES = "${PN}-dev"
FILES:${PN}-dev = "${datadir}"
RRECOMMENDS:${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
