FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI_append_tegra = " \
    file://usb-gadget.target \
"

do_install_append_tegra() {
    sed -i -e's,^WantedBy=.*,WantedBy=usb-gadget.target,' ${D}${systemd_system_unitdir}/usbgx.service
    install -m 0644 ${WORKDIR}/usb-gadget.target ${D}${systemd_system_unitdir}/
}

SYSTEMD_SERVICE_${PN}_append_tegra = " usb-gadget.target"
PACKAGES_prepend_tegra = "${PN}-examples "
FILES_${PN}-examples = "${bindir}/gadget-acm-ecm ${bindir}/gadget-export ${bindir}/gadget-ffs \
                        ${bindir}/gadget-hid ${bindir}/gadget-midi ${bindir}/gadget-ms \
                        ${bindir}/gadget-rndis-os-desc ${bindir}/gadget-uac2"
RRECOMMENDS_${PN}_append_tegra = " kernel-module-tegra-xudc"
PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
