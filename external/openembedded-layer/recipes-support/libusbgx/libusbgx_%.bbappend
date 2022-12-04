PACKAGECONFIG:tegra = "examples gadget-schemes libconfig"
SYSTEMD_PACKAGES:tegra = "${PN}"
SYSTEMD_SERVICE:${PN}:tegra = "usbgx.service"
RDEPENDS:${PN}:tegra += "libusbgx-config"
FILES:${PN}-examples:tegra = "${bindir}/gadget-acm-ecm ${bindir}/gadget-export ${bindir}/gadget-ffs \
                        ${bindir}/gadget-hid ${bindir}/gadget-midi ${bindir}/gadget-ms \
                        ${bindir}/gadget-rndis-os-desc ${bindir}/gadget-uac2"
RRECOMMENDS:${PN}:append:tegra = " kernel-module-tegra-xudc"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
