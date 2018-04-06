inherit update-alternatives

do_install_append() {
    mv ${D}${libdir}/libdrm.so.2.4.0 ${D}${libdir}/libdrm-freedesktop.so.2.4.0
    rm ${D}${libdir}/libdrm.so
    rm ${D}${libdir}/libdrm.so.2
    ln -sf libdrm-freedesktop.so.2.4.0 ${D}${libdir}/libdrm.so
    ln -sf libdrm-freedesktop.so.2.4.0 ${D}${libdir}/libdrm.so.2
}

do_install_append_tegra() {
    install -d ${D}${libdir}/mesa
    cp -p ${D}${libdir}/libdrm*.so.* ${D}${libdir}/mesa
}

ALTERNATIVE_${PN} = "libdrm.so.2.4.0 libdrm.so.2"
ALTERNATIVE_LINK_NAME[libdrm.so.2.4.0] = "${libdir}/libdrm.so.2.4.0"
ALTERNATIVE_LINK_NAME[libdrm.so.2] = "${libdir}/libdrm.so.2"
ALTERNATIVE_TARGET = "${libdir}/libdrm-freedesktop.so.2.4.0"
ALTERNATIVE_PRIORITY = "20"

PACKAGES =+ "${PN}-stubs-dev ${PN}-stubs"
FILES_${PN}-stubs = "${libdir}/mesa/lib*${SOLIBS}"
FILES_${PN}-stubs-dev = "${libdir}/mesa/lib*${SOLIBSDEV}"
ALLOW_EMPTY_${PN}-stubs = "1"
ALLOW_EMPTY_${PN}-stubs-dev = "1"
PRIVATE_LIBS_${PN}-stubs = "libdrm.so.2"
