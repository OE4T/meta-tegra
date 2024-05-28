DESCRIPTION = "CCACHE_COMPILERCHECK wrapper for CUDA"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://cuda-compiler-check.sh"

inherit native

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_compile() {
    :
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/cuda-compiler-check.sh ${D}${bindir}/cuda-compiler-check
}
