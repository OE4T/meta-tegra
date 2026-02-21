L4T_DEB_COPYRIGHT_MD5 = "38ef63b8f3232378d9f652f640ee0a3f"

require tegra-debian-libraries-common.inc

DESCRIPTION = "Prebuilt OPTEE normal-world binaries"
MAINSUM = "a4d8c6ff82e432619a380b08be491eea76fb0deee8afd139dbb769868fa4eb3a"

inherit systemd

LIC_FILES_CHKSUM += " \
    file://usr/share/doc/nvidia-tegra/LICENSE.optee_test;md5=daa2bcccc666345ab8940aab1315a4fa \
    file://usr/share/doc/nvidia-tegra/LICENSE.optee_client;md5=69663ab153298557a59c67a60a743e5b \
    file://usr/share/doc/nvidia-tegra/LICENSE.nvhwkey-app;md5=6938d70d5e5d49d31049419e85bb82f8 \
    file://usr/share/doc/nvidia-tegra/LICENSE.nvluks-srv-app;md5=6938d70d5e5d49d31049419e85bb82f8 \
    file://usr/share/doc/nvidia-tegra/LICENSE.82154947-c1bc-4bdf-b89d-04f93c0ea97c.ta;md5=6938d70d5e5d49d31049419e85bb82f8 \
"
LICENSE += "& BSD-2-Clause & GPL-2.0-only"
LICENSE:${PN} = "BSD-2-Clause"
LICENSE:${PN}-base-tas = "BSD-2-Clause"
LICENSE:${PN}-test = "GPL-2.0-only & BSD-2-Clause"
LICENSE:${PN}-nvsamples = "BSD-2-Clause"

PROVIDES += "optee-client optee-test optee-nvsamples"

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
    install -m 0644 ${S}/lib/optee_armtz/*.ta ${D}${base_libdir}/optee_armtz/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/etc/systemd/system/nv-tee-supplicant.service ${D}${systemd_system_unitdir}/
}

# Put -test last here so TAs end up in the right package
PACKAGES =+ "${PN}-nvsamples-luks-srv ${PN}-nvsamples-hwkey-agent ${PN}-nvsamples ${PN}-base-tas ${PN}-test"
RPROVIDES:${PN} = "optee-client"
RCONFLICTS:${PN} = "optee-client"
RREPLACES:${PN} = "optee-client"
RPROVIDES:${PN}-base-tas = "optee-os"
RCONFLICTS:${PN}-base-tas = "optee-os"
RREPLACES:${PN}-base-tas = "optee-os"
RPROVIDES:${PN}-test = "optee-test"
RCONFLICTS:${PN}-test = "optee-test"
RREPLACES:${PN}-test = "optee-test"
RPROVIDES:${PN}-nvsamples-hwkey-agent = "optee-nvsamples-hwkey-agent"
RCONFLICTS:${PN}-nvsamples-hwkey-agent = "optee-nvsamples-hwkey-agent"
RREPLACES:${PN}-nvsamples-hwkey-agent = "optee-nvsamples-hwkey-agent"
RPROVIDES:${PN}-nvsamples-luks-srv = "optee-nvsamples-luks-srv"
RCONFLICTS:${PN}-nvsamples-luks-srv = "optee-nvsamples-luks-srv"
RREPLACES:${PN}-nvsamples-luks-srv = "optee-nvsamples-luks-srv"
RPROVIDES:${PN}-nvsamples = "optee-nvsamples"
RCONFLICTS:${PN}-nvsamples = "optee-nvsamples"
RREPLACES:${PN}-nvsamples = "optee-nvsamples"
ALLOW_EMPTY:${PN}-nvsamples = "1"
RDEPENDS:${PN}-nvsamples = "${PN}-nvsamples-hwkey-agent ${PN}-nvsamples-luks-srv"

SYSTEMD_SERVICE:${PN} = "nv-tee-supplicant.service"

FILES:${PN}-nvsamples-hwkey-agent = "\
    ${sbindir}/nvhwkey-app \
    ${nonarch_base_libdir}/optee_armtz/82154947-c1bc-4bdf-b89d-04f93c0ea97c.ta \
"
FILES:${PN}-nvsamples-luks-srv = "\
    ${sbindir}/nvluks-srv-app \
"
FILES:${PN}-base-tas = "\
    ${nonarch_base_libdir}/optee_armtz/023f8f1a-292a-432b-8fc4-de8471358067.ta \
    ${nonarch_base_libdir}/optee_armtz/f04a0fe7-1f5d-4b9b-abf7-619b85b4ce8c.ta \
    ${nonarch_base_libdir}/optee_armtz/fd02c9da-306c-48c7-a49c-bbd827ae86ee.ta \
"
FILES:${PN}-test = "\
    ${bindir}/xtest \
    ${libdir}/tee-supplicant/plugins \
    ${nonarch_base_libdir}/optee_armtz \
"
RDEPENDS:${PN}-test = "${PN}"
RDEPENDS:${PN}-nvsamples = "${PN}"
RDEPENDS:${PN} = "${PN}-base-tas"
