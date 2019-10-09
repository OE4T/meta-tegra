RDEPENDS_${PN}_tegra = "libdrm-nvdc"

# Ensure libdrm uses its helpers and doesn't call back into libdrm-nvdc
LDFLAGS_append_tegra = " -Wl,-Bsymbolic-functions"

do_install_append_tegra() {
    sed -i -e 's|^Libs: .*|& -Wl,-rpath,${libdir}/tegra|g' ${D}${libdir}/pkgconfig/libdrm.pc
}
