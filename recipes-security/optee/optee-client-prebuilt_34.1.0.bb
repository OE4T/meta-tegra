DESCRIPTION = "Prebuilt Normal World Client side of the TEE"

require optee-prebuilt.inc

inherit systemd

LIC_FILES_CHKSUM += " \
    file://usr/share/doc/nvidia-tegra/LICENSE.optee_client;md5=69663ab153298557a59c67a60a743e5b \
    file://usr/share/doc/nvidia-tegra/LICENSE.nvhwkey-app;md5=6938d70d5e5d49d31049419e85bb82f8 \
    file://usr/share/doc/nvidia-tegra/LICENSE.nvluks-srv-app;md5=6938d70d5e5d49d31049419e85bb82f8 \
    file://usr/share/doc/nvidia-tegra/LICENSE.82154947-c1bc-4bdf-b89d-04f93c0ea97c.ta;md5=6938d70d5e5d49d31049419e85bb82f8 \
"

do_install() {
    install -d ${D}${libdir} ${D}${libdir}/tee-supplicant/plugins
    install -m 0644 ${S}/usr/lib/libckteec.so.0.1.0 ${D}${libdir}
    install -m 0644 ${S}/usr/lib/libteec.so.1.0.0 ${D}${libdir}
    ln -s libckteec.so.0.1.0 ${D}${libdir}/libckteec.so.0.1
    ln -s libckteec.so.0.1 ${D}${libdir}/libckteec.so.0
    ln -s libckteec.so.0 ${D}${libdir}/libckteec.so
    ln -s libteec.so.1.0.0 ${D}${libdir}/libteec.so.1.0
    ln -s libteec.so.1.0 ${D}${libdir}/libteec.so.1
    ln -s libteec.so.1 ${D}${libdir}/libteec.so
    install -m 0755 ${S}/usr/lib/tee-supplicant/plugins/f07bfc66-958c-4a15-99c0-260e4e7375dd.plugin ${D}${libdir}/tee-supplicant/plugins

    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/nvhwkey-app ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/nvluks-srv-app ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/tee-supplicant ${D}${sbindir}

    install -d ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/82154947-c1bc-4bdf-b89d-04f93c0ea97c.ta ${D}${base_libdir}/optee_armtz

    install -d ${D}${sysconfdir}/systemd/system
    install -m 0644 ${S}/etc/systemd/system/nv-tee-supplicant.service ${D}${sysconfdir}/systemd/system
    install -d ${D}${sysconfdir}/systemd/system/sysinit.target.wants/
    ln -s ../nv-tee-supplicant.service ${D}${sysconfdir}/systemd/system/sysinit.target.wants/nv-tee-supplicant.service
}

FILES:${PN} += " \
    ${libdir}/tee-supplicant/plugins \
    ${base_libdir}/optee_armtz \
    ${sysconfdir}/systemd/system \
"
INSANE_SKIP:${PN} = "already-stripped"
