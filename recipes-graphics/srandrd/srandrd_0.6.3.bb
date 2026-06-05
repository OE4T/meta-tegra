SUMMARY = "Simple RandR daemon - runs a command on monitor hotplug"
DESCRIPTION = "srandrd listens for X RandR output-change events (a monitor being \
plugged in or unplugged) and executes a command, passing the output name, event \
type and EDID to it via the environment. It is a tiny, dependency-light \
replacement for a desktop environment's display-configuration daemon."
HOMEPAGE = "https://github.com/jceb/srandrd"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e304f381f3eafc26761e58445ce1b757"

SRC_URI = "git://github.com/jceb/srandrd.git;protocol=https;branch=master \
           file://0001-Allow-running-as-root.patch \
           "
# v0.6.3
SRCREV = "b071c3d1ae1731420255504edfc0a7306f1654c5"

DEPENDS = "libx11 libxrandr libxinerama"

# The upstream Makefile assigns CFLAGS/LDFLAGS with ':=', which overrides the
# values exported into the environment by OE.
EXTRA_OEMAKE = "CC='${CC}' \
                CFLAGS='${CFLAGS} -std=c99' \
                LDFLAGS='${LDFLAGS} -lX11 -lXrandr -lXinerama'"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/srandrd ${D}${bindir}/srandrd
}
