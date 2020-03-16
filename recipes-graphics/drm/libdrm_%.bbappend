RDEPENDS_${PN}_tegra = "libdrm-nvdc"

# Ensure libdrm uses its helpers and doesn't call back into libdrm-nvdc
LDFLAGS_append_tegra = " -Wl,-Bsymbolic-functions"

pkg_postinst_${PN}() {
    ln -sf libdrm_nvdc.so $D${libdir}/libdrm.so.2
}
PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
