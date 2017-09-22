inherit update-alternatives

do_install_append() {
    mv ${D}${libdir}/libdrm.so.2.4.0 ${D}${libdir}/libdrm-freedesktop.so.2.4.0
    rm ${D}${libdir}/libdrm.so
    rm ${D}${libdir}/libdrm.so.2
    ln -sf libdrm-freedesktop.so.2.4.0 ${D}${libdir}/libdrm.so
    ln -sf libdrm-freedesktop.so.2.4.0 ${D}${libdir}/libdrm.so.2
}

ALTERNATIVE_${PN} = "libdrm.so.2.4.0 libdrm.so.2"
ALTERNATIVE_LINK_NAME[libdrm.so.2.4.0] = "${libdir}/libdrm.so.2.4.0"
ALTERNATIVE_LINK_NAME[libdrm.so.2] = "${libdir}/libdrm.so.2"
ALTERNATIVE_TARGET = "${libdir}/libdrm-freedesktop.so.2.4.0"
ALTERNATIVE_PRIORITY = "20"
