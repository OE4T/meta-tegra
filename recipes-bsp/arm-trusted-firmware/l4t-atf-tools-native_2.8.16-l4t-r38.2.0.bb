DESCRIPTION = "Trusted Firmware-A fiptool and sptool for packaging (from L4T sources)"

require arm-trusted-firmware-2.8.16-l4t-r38.2.0.inc

B = "${S}"

DEPENDS += "openssl-native"

inherit native

EXTRA_OEMAKE = "V=1 HOSTCC='${BUILD_CC}' OPENSSL_DIR=${STAGING_DIR_NATIVE}${prefix_native}"

do_compile () {
    # This is still needed to have the native fiptool executing properly by
    # setting the RPATH
    sed -i '/^LDOPTS/ s,$, \$\{BUILD_LDFLAGS},' ${S}/tools/fiptool/Makefile
    sed -i '/^INCLUDE_PATHS/ s,$, \$\{BUILD_CFLAGS},' ${S}/tools/fiptool/Makefile

    oe_runmake fiptool
}

do_install () {
    install -D -p -m 0755 -t ${D}${bindir} ${S}/tools/sptool/sptool.py ${B}/tools/fiptool/fiptool
}
