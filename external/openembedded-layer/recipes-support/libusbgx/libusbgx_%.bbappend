PACKAGES:prepend:tegra = "${PN}-examples "
FILES:${PN}-examples = "${bindir}/gadget-acm-ecm ${bindir}/gadget-export ${bindir}/gadget-ffs \
                        ${bindir}/gadget-hid ${bindir}/gadget-midi ${bindir}/gadget-ms \
                        ${bindir}/gadget-rndis-os-desc ${bindir}/gadget-uac2"
RRECOMMENDS:${PN}:append:tegra = " kernel-module-tegra-xudc"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
