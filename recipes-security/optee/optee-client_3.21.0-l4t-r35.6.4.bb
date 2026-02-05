SUMMARY = "OP-TEE Client API"
DESCRIPTION = "Open Portable Trusted Execution Environment - Normal World \
  Client side of the TEE"
HOMEPAGE = "https://www.op-tee.org/"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=69663ab153298557a59c67a60a743e5b"

require optee-l4t.inc

TEGRA_SRC_SUBARCHIVE_OPTS = "--strip-components=1 optee/optee_client"

SRC_URI += "\
    file://0001-Update-Makefile-for-OE-compatibility.patch;striplevel=3 \
    file://tee-supplicant.service.in \
    file://tee-supplicant.sh.in \
"

DEPENDS = "optee-os-tadevkit"

S = "${WORKDIR}/optee_client"
B = "${WORKDIR}/build"

inherit systemd update-rc.d

OPTEE_FS_PARENT_PATH ?= "${localstatedir}/lib/tee"

EXTRA_OEMAKE += "CFG_TEE_FS_PARENT_PATH=${OPTEE_FS_PARENT_PATH}"

do_compile() {
    oe_runmake -C ${S} build
    sed -e's,@sbindir@,${sbindir},g' \
        -e's,@sysconfdir@,${sysconfdir},g' \
        ${WORKDIR}/tee-supplicant.service.in >${B}/tee-supplicant.service
    sed -e's,@sbindir@,${sbindir},g' \
        -e's,@sysconfdir@,${sysconfdir},g' \
        -e's,@stripped_path@,${base_sbindir}:${base_bindir}:${sbindir}:${bindir},g' \
        ${WORKDIR}/tee-supplicant.sh.in >${B}/tee-supplicant.sh
}

do_install() {
    oe_runmake -C ${S} install DESTDIR="${D}"
    install -d ${D}${systemd_system_unitdir} ${D}${sysconfdir}/init.d
    install -m 0644 ${B}/tee-supplicant.service ${D}${systemd_system_unitdir}/
    install -m 0755 ${B}/tee-supplicant.sh ${D}${sysconfdir}/init.d/tee-supplicant
}

SYSTEMD_SERVICE:${PN} = "tee-supplicant.service"
INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "tee-supplicant"
INITSCRIPT_PARAMS:${PN} = "start 10 1 2 3 4 5 . stop 90 0 6 ."
