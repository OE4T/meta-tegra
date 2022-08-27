SUMMARY = "OP-TEE Client API"
DESCRIPTION = "Open Portable Trusted Execution Environment - Normal World \
  Client side of the TEE"
HOMEPAGE = "https://www.op-tee.org/"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=69663ab153298557a59c67a60a743e5b"

require optee-tegra.inc

DEPENDS = "optee-os-tadevkit-tegra"

SRC_URI += "\
    file://0001-use-install-command-line-instead-of-cp.patch \
    file://nv-tee-supplicant.service.in \
"

S = "${WORKDIR}/optee/optee_client"
B = "${WORKDIR}/build"

inherit systemd

EXTRA_OEMAKE += " \
    CROSS_COMPILE='${HOST_PREFIX}' \
    PYTHON3='${PYTHON}' \
    TA_DEV_KIT_DIR='${TA_DEV_KIT_DIR}' \
    O='${B}' \
    DESTDIR='${D}' \
"

do_compile() {
    oe_runmake -C ${S} build
    sed -e's,@sbindir@,${sbindir},g' \
        ${WORKDIR}/nv-tee-supplicant.service.in >${B}/nv-tee-supplicant.service
}
do_compile[cleandirs] = "${B}"

do_install() {
    oe_runmake -C ${S} install
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${B}/nv-tee-supplicant.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += "${systemd_system_unitdir}"
SYSTEMD_SERVICE:${PN} = "nv-tee-supplicant.service"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
