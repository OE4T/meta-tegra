DESCRIPTION = "Open Portable Trusted Execution Environment - Prebuilt client \
  libraries and supplicant daemon"

require optee-tegra-prebuilt.inc

inherit systemd

LIC_FILES_CHKSUM += " \
    file://usr/share/doc/nvidia-tegra/LICENSE.optee_client;md5=69663ab153298557a59c67a60a743e5b \
"

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${S}/usr/lib/libckteec.so.0.1.0 ${D}${libdir}
    install -m 0644 ${S}/usr/lib/libteec.so.1.0.0 ${D}${libdir}
    ln -s libckteec.so.0.1.0 ${D}${libdir}/libckteec.so.0.1
    ln -s libckteec.so.0.1 ${D}${libdir}/libckteec.so.0
    ln -s libckteec.so.0 ${D}${libdir}/libckteec.so
    ln -s libteec.so.1.0.0 ${D}${libdir}/libteec.so.1.0
    ln -s libteec.so.1.0 ${D}${libdir}/libteec.so.1
    ln -s libteec.so.1 ${D}${libdir}/libteec.so

    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/tee-supplicant ${D}${sbindir}

    install -d ${D}${sysconfdir}/systemd/system
    install -m 0644 ${S}/etc/systemd/system/nv-tee-supplicant.service ${D}${sysconfdir}/systemd/system
    install -d ${D}${sysconfdir}/systemd/system/sysinit.target.wants/
    ln -s ../nv-tee-supplicant.service ${D}${sysconfdir}/systemd/system/sysinit.target.wants/nv-tee-supplicant.service
}

FILES:${PN} += "${sysconfdir}/systemd/system"

PROVIDES = "optee-client-tegra"
RPROVIDES:${PN} = "optee-client-tegra"
RCONFLICTS:${PN} = "optee-client-tegra"
RPROVIDES:${PN}-dev = "optee-client-tegra"
RCONFLICTS:${PN}-dev = "optee-client-tegra"
