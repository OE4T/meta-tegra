DESCRIPTION = "Workarounds for CUDA compiler compatibility with newer toolchains"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://math-vector.h"

S = "${UNPACKDIR}"

COMPATIBLE_MACHINE:class-target = "(cuda)"
COMPATIBLE_HOST = "(x86_64|aarch64)"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    :
}

do_install:aarch64() {
    install -D -m 0644 -t ${D}${includedir}/cuda-compat-workarounds/bits ${UNPACKDIR}/math-vector.h
}

ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
