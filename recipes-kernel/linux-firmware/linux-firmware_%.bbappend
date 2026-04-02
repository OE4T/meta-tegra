do_install::append:tegra() {
    install -d ${D}${nonarch_base_libdir}/firmware
    ln -s rtl_bt/rtl8822cu_fw.bin ${D}${nonarch_base_libdir}/firmware/rtl8822cu_fw
    ln -s rtl_bt/rtl8822cu_config.bin ${D}${nonarch_base_libdir}/firmware/rtl8822cu_config
}

FILES:${PN}-rtl8822:append:tegra = " \
    ${nonarch_base_libdir}/firmware/rtl8822cu_config \
    ${nonarch_base_libdir}/firmware/rtl8822cu_fw \
"
