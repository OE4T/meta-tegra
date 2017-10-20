require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/config.tbz2
    if [ ! -f ${B}/usr/sbin/camera_device_detect ]; then
        mkdir -p ${B}/usr/sbin
        echo "#!/bin/sh" >${B}/usr/sbin/camera_device_detect
    fi
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}/${sysconfdir}/udev/rules.d
    install -m 0644 ${B}/etc/udev/rules.d/99-tegra-devices.rules ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${B}/etc/udev/rules.d/99-tegra-mmc-ra.rules ${D}${sysconfdir}/udev/rules.d
    install -d ${D}${sbindir}
    install -m 0755 ${B}/usr/sbin/camera_device_detect ${D}${sbindir}/
}

PACKAGES = "${PN}"
FILES_${PN} = "${sysconfdir} ${sbindir}"
RDEPENDS_${PN} = "udev"
