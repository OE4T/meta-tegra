DESCRIPTION = "tar wrapper script for handling zstd suffix"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
 
SRC_URI = "file://tar-wrapper.sh"

NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

inherit_defer native

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -m 0755 -D ${UNPACKDIR}/tar-wrapper.sh ${D}${bindir}/tar
}
