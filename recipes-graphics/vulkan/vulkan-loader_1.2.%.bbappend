PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
PACKAGECONFIG:remove:tegra = "wayland"
RRECOMMENDS:${PN}:remove:tegra = "mesa-vulkan-drivers"
RDEPENDS:${PN:}:tegra = "tegra-libraries libxcb libxcb-glx0"
INSANE_SKIP:${PN}:append:tegra = " build-deps"
