SUMMARY = "Recipe to stash patches to be applied to L4T files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

INHIBIT_DEFAULT_DEPS = "1"

SRC_URI = " \
    file://Convert-l4t_payload_updater_t210-to-Python3.patch;apply=no \
"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/l4t-patches-${PV}
    install -m 0644 ${WORKDIR}/Convert-l4t_payload_updater_t210-to-Python3.patch ${D}${datadir}/l4t-patches-${PV}/
}

inherit nopackages

