require u-boot-tegra-common-${PV}.inc

SUMMARY = "U-Boot bootloader fw_printenv/setenv utilities"
DEPENDS = "mtd-utils"

INSANE_SKIP_${PN} = "already-stripped"

SRC_URI += "\
           file://0001-Override-HOSTCC.patch \
           file://fw_env.config \
           "

EXTRA_OEMAKE_class-target = 'CROSS_COMPILE=${TARGET_PREFIX} CC="${CC} ${CFLAGS} ${LDFLAGS}" HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" V=1'


inherit uboot-config

do_compile () {
       oe_runmake ${UBOOT_MACHINE}
       oe_runmake env
}

do_install () {
       install -d ${D}${base_sbindir}
       install -d ${D}${sysconfdir}
       install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_printenv
       install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_setenv
       install -m 0644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}

do_install_class-cross () {
       install -d ${D}${bindir_cross}
       install -m 755 ${S}/tools/env/fw_printenv ${D}${bindir_cross}/fw_printenv
       install -m 755 ${S}/tools/env/fw_printenv ${D}${bindir_cross}/fw_setenv
}

SYSROOT_DIRS_append_class-cross = " ${bindir_cross}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
BBCLASSEXTEND = "cross"

PROVIDES = "u-boot-fw-utils"
RPROVIDES_${PN} = "u-boot-fw-utils"
