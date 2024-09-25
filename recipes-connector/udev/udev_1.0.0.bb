SUMMARY = "Adds Connectorv2 specific udev rules"
DESCRIPTION = "Adds udev rules for USB peripherals of Connectorv2"
LICENSE = "CLOSED"

SRC_URI = "file://99-usb-whitelisting.rules"

do_install() {
    install -d ${D}/etc/udev/rules.d

    install -m 644 ${WORKDIR}/99-usb-whitelisting.rules ${D}/etc/udev/rules.d/
}