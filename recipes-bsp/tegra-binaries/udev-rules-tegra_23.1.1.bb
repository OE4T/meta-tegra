require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/config.tbz2
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}/${sysconfdir}/udev/rules.d
    install -m 0644 ${B}/etc/udev/rules.d/99-tegra-devices.rules ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${B}/etc/udev/rules.d/99-tegra-mmc-ra.rules ${D}${sysconfdir}/udev/rules.d
}

PACKAGES = "${PN}"
FILES_${PN} = "${sysconfdir}"
RDEPENDS_${PN} = "udev"
