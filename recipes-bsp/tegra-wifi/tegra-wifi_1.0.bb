DESCRIPTION = "Configuration files for WiFi on Tegra platforms"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://tegra-wifi.rules \
"

COMPATIBLE_MACHINE = "tegra"

S = "${WORKDIR}"

do_install() {
    if [ -s ${S}/tegra-wifi.rules ]; then
	install -d ${D}${nonarch_base_libdir}/udev/rules.d
	install -m 0644 ${S}/tegra-wifi.rules ${D}${nonarch_base_libdir}/udev/rules.d/99-tegra-wifi.rules
    fi
}

ALLOW_EMPTY_${PN} = "1"
FILES_${PN} += "${nonarch_base_libdir}/udev/rules.d"
PACKAGE_ARCH = "${MACHINE_ARCH}"
