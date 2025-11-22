SUMMARY = "Library to support popular graphics image formats"
SECTION = "libs"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://license-gplv2.txt;md5=1fbed70be9d970d3da399f33dae9cc51"

SRC_URI = "${SOURCEFORGE_MIRROR}/freeimage/FreeImage3180.zip"
SRC_URI[sha256sum] = "f41379682f9ada94ea7b34fe86bf9ee00935a3147be41b6569c9605a53e438fd"

S = "${WORKDIR}/FreeImage"

CXXFLAGS += "-std=c++14"

# Avoid the following error when using with mnistCUDNN:
#undefined reference to `png_init_filter_functions_neon'
TARGET_CFLAGS += "-DPNG_ARM_NEON_OPT=0"

do_configure:prepend() {
	# Don't strip to avoid [already-stripped]
	sed -i 's/$(CC) -s /$(CC) /' ${S}/Makefile.gnu
}

do_install() {
	install -d ${D}${libdir}
	install -d ${D}${includedir}
	oe_runmake INSTALLDIR="${D}${libdir}" INCDIR="${D}${includedir}" install
}

INSANE_SKIP:${PN} += "dev-so"

FILES:${PN} += "${libdir}"
FILES_SOLIBSDEV = "{libdir}/lib${BP}${SOLIBSDEV}"
