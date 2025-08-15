DESCRIPTION = "EGL external platform interface"
HOMEPAGE = "https://github.com/NVIDIA/eglexternalplatform"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cfa5a0f49cb081823fc5d965566e8298"

SRC_URI = "git://github.com/NVIDIA/eglexternalplatform.git;protocol=https;branch=master"
# corresponds to 1.1 tag
SRCREV = "7c8f8e2218e46b1a4aa9538520919747f1184d86"

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/interface/*.h ${D}${includedir}/
    install -d ${D}${datadir}/pkgconfig
    install -m 0644 ${S}/eglexternalplatform.pc ${D}${datadir}/pkgconfig/
}

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev = "${includedir} ${datadir}/pkgconfig"

