DESCRIPTION = "NVIDIA OP-TEE sample applications for Jetson platforms"
HOMEPAGE = "https://developer.nvidia.com/embedded"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6938d70d5e5d49d31049419e85bb82f8"

require optee-l4t.inc
TEGRA_SRC_SUBARCHIVE_OPTS = "--strip-components=1 optee/samples"

SRC_URI += " file://0001-Update-makefiles-for-OE-builds.patch"

DEPENDS += "optee-os-tadevkit optee-client"
DEPENDS:append:libc-musl = " argp-standalone"

LDADD:append:libc-musl = " -L${STAGING_LIBDIR} -largp"

export LDADD

S = "${UNPACKDIR}/samples"
B = "${WORKDIR}/build"

EXTRA_OEMAKE += "CROSS_COMPILE=${HOST_PREFIX}"

do_compile() {
    oe_runmake -C ${S} all
}
do_compile[cleandirs] = "${B}"

do_install() {
    install -d ${D}${nonarch_base_libdir}/optee_armtz
    install -m 0644 ${B}/ta/hwkey-agent/82154947-c1bc-4bdf-b89d-04f93c0ea97c.ta ${D}${nonarch_base_libdir}/optee_armtz
    oe_runmake -C ${S}/hwkey-agent/host install DESTDIR="${D}"

    install -D -m 0755 ${B}/early_ta/luks-srv/b83d14a8-7128-49df-9624-35f14f65ca6c.stripped.elf -t ${D}${includedir}/optee/early_ta/luks-srv
    install -D -m 0755 ${B}/early_ta/cpubl-payload-dec/0e35e2c9-b329-4ad9-a2f5-8ca9bbbd7713.stripped.elf -t ${D}${includedir}/optee/early_ta/cpubl-payload-dec
    oe_runmake -C ${S}/luks-srv/host install DESTDIR="${D}"
}

PACKAGES =+ "${PN}-luks-srv ${PN}-hwkey-agent"
FILES:${PN}-hwkey-agent = "${nonarch_base_libdir}/optee_armtz/82154947-c1bc-4bdf-b89d-04f93c0ea97c.ta ${sbindir}/nvhwkey-app"
FILES:${PN}-luks-srv = "${sbindir}/nvluks-srv-app"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "${PN}-luks-srv ${PN}-hwkey-agent"
INHIBIT_SYSROOT_STRIP = "1"
INSANE_SKIP:${PN} = "already-stripped"
