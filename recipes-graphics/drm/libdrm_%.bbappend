RDEPENDS_${PN}_tegra = "libdrm-nvdc"

do_install_append_tegra() {
    sed -i -e 's|^Libs: .*|& -Wl,-rpath,${libdir}/tegra|g' ${D}${libdir}/pkgconfig/libdrm.pc
}
