DESCRIPTION = "tar wrapper script for handling zstd suffix"
LICENSE = "MIT"

SRC_URI = "file://tar-wrapper.sh"

NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

inherit native

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -m 0755 -D ${WORKDIR}/tar-wrapper.sh ${D}${bindir}/tar
}
