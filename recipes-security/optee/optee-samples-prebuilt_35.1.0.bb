DESCRIPTION = "Open Portable Trusted Execution Environment - Prebuilt sample \
  applications"

require optee-tegra-prebuilt.inc

LIC_FILES_CHKSUM += " \
    file://usr/share/doc/nvidia-tegra/LICENSE.nvhwkey-app;md5=6938d70d5e5d49d31049419e85bb82f8 \
    file://usr/share/doc/nvidia-tegra/LICENSE.nvluks-srv-app;md5=6938d70d5e5d49d31049419e85bb82f8 \
    file://usr/share/doc/nvidia-tegra/LICENSE.82154947-c1bc-4bdf-b89d-04f93c0ea97c.ta;md5=6938d70d5e5d49d31049419e85bb82f8 \
"

DEPENDS = "optee-client-tegra-prebuilt"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/nvhwkey-app ${D}${sbindir}
    install -m 0755 ${S}/usr/sbin/nvluks-srv-app ${D}${sbindir}

    install -d ${D}${nonarch_base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/82154947-c1bc-4bdf-b89d-04f93c0ea97c.ta ${D}${nonarch_base_libdir}/optee_armtz
    # XXX---
    # TA for luks-srv application is missing
    # ---XXX
}

FILES:${PN} += "${base_libdir}/optee_armtz"
INSANE_SKIP:${PN} = "already-stripped"

PROVIDES = "optee-samples"
RPROVIDES:${PN} = "optee-samples"
RCONFLICTS:${PN} = "optee-samples"
