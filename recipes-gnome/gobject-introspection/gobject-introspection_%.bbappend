do_install_append_class-target() {
    sed -i -e's,\.libs:${STAGING_DIR_HOST}/${libdir},.libs:${STAGING_DIR_HOST}${libdir}/mesa:${STAGING_DIR_HOST}${libdir},' \
        ${D}${bindir}/g-ir-scanner-qemuwrapper
}
