SUMMARY = "OP-TEE Trusted OS"
DESCRIPTION = "Open Portable Trusted Execution Environment - Trusted side of the TEE"
HOMEPAGE = "https://www.op-tee.org/"

require optee-os-l4t.inc

CVE_PRODUCT = "linaro:op-tee op-tee:op-tee_os"

DEPENDS += "optee-nvsamples"

EXTRA_OEMAKE += "\
    EARLY_TA_PATHS='${STAGING_INCDIR}/optee/early_ta/cpubl-payload-dec/0e35e2c9-b329-4ad9-a2f5-8ca9bbbd7713.stripped.elf \
                    ${STAGING_INCDIR}/optee/early_ta/luks-srv/b83d14a8-7128-49df-9624-35f14f65ca6c.stripped.elf' \
"

do_install() {
    install -d ${D}${datadir}/trusted-os
    install -m 0644 ${B}/core/*.bin ${B}/core/tee.elf ${D}${datadir}/trusted-os/

    # Install embedded TAs
    install -d ${D}${nonarch_base_libdir}/optee_armtz
    find ${B}/ta -name '*.ta' | while read name; do
        install -m 444 $name ${D}${nonarch_base_libdir}/optee_armtz/
    done
}

FILES:${PN} = "${nonarch_base_libdir}/optee_armtz"
FILES:${PN}-dev = "${datadir}/trusted-os"
INSANE_SKIP:${PN}-dev = "textrel"

PACKAGE_ARCH = "${MACHINE_ARCH}"
