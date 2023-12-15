PACKAGECONFIG:tegra = "examples gadget-schemes libconfig"
SYSTEMD_PACKAGES:tegra = "${PN}"
SYSTEMD_SERVICE:${PN}:tegra = "usbgx.service"
RDEPENDS:${PN}:tegra += "libusbgx-config"
FILES:${PN}-examples:tegra = "${bindir}/gadget-acm-ecm ${bindir}/gadget-export ${bindir}/gadget-ffs \
                        ${bindir}/gadget-hid ${bindir}/gadget-midi ${bindir}/gadget-ms \
                        ${bindir}/gadget-rndis-os-desc ${bindir}/gadget-uac2"
RRECOMMENDS:${PN}:append:tegra = " \
    kernel-module-tegra-xudc \
    kernel-module-usb-f-accessory \
    kernel-module-usb-f-acm \
    kernel-module-usb-f-ecm \
    kernel-module-usb-f-ecm-subset \
    kernel-module-usb-f-eem \
    kernel-module-usb-f-fs \
    kernel-module-usb-f-mass-storage \
    kernel-module-usb-f-ncm \
    kernel-module-usb-f-obex \
    kernel-module-usb-f-rndis \
    kernel-module-usb-f-serial \
    kernel-module-bridge \
"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
