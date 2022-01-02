DEPENDS:append:tegra = " libdrm-nvdc"
RDEPENDS:${PN}:tegra = "libdrm-nvdc"

# Ensure libdrm uses its helpers and doesn't call back into libdrm-nvdc
LDFLAGS:append:tegra = " -Wl,-Bsymbolic-functions"

pkg_postinst:${PN}:tegra() {
    ln -sf libdrm_nvdc.so $D${libdir}/libdrm.so.2
}
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
